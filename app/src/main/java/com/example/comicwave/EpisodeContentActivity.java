package com.example.comicwave;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicwave.adapters.EpisodeContentAdapter;
import com.example.comicwave.helpers.AnimationHelper;
import com.example.comicwave.models.Episode;
import com.example.comicwave.repositories.ComicRepository;
import com.example.comicwave.repositories.UserRepository;
import com.google.android.flexbox.FlexboxLayout;

public class EpisodeContentActivity extends AppCompatActivity {

    private FlexboxLayout contentTopNavbar, contentBottomNavbar, contentPrevButton, contentNextButton;;
    private TextView contentEpisodeTitle;
    private RecyclerView episodeContents;
    private ImageView contentBackButton;
    private String comicId, episodeId;
    private Episode episode;
    private EpisodeContentAdapter episodeAdapter;
    private ProgressBar contentLoading;
    private View contentOverlay;
    private boolean loading = false;
    private void initComponents() {
        contentTopNavbar = findViewById(R.id.contentTopNavbar);
        contentBottomNavbar = findViewById(R.id.contentBottomNavbar);
        contentEpisodeTitle = findViewById(R.id.contentEpisodeTitle);
        episodeContents = findViewById(R.id.episodeContents);
        contentBackButton = findViewById(R.id.contentBackButton);
        contentPrevButton = findViewById(R.id.contentPrevButton);
        contentNextButton = findViewById(R.id.contentNextButton);
        contentLoading = findViewById(R.id.contentLoading);
        contentOverlay = findViewById(R.id.contentOverlay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_episode_content);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initComponents();
        fetchData();
    }

    private void populateUI() {
        contentEpisodeTitle.setText(String.format("Episode %d: %s", episode.getEpisodeNumber(), episode.getTitle()));
        contentEpisodeTitle.setSelected(true);
        episodeAdapter = new EpisodeContentAdapter(episode.getContent());
        episodeContents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        episodeContents.setAdapter(episodeAdapter);

        contentBackButton.setOnClickListener(e -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        Log.d("EpisodeContent", getIntent().getIntExtra("nexts", 0) + "");
        if (getIntent().getIntExtra("nexts", 0) > 0) {
            contentNextButton.setVisibility(View.VISIBLE);
            contentNextButton.setOnClickListener(e -> {
                toggleLoading();
                ComicRepository.getNextEpisodeID(comicId, episode.getEpisodeNumber(), id -> {
                    Intent i = new Intent(this, EpisodeContentActivity.class);
                    i.putExtra("episodeId", id);
                    i.putExtra("comicId", episode.getComicId());
                    i.putExtra("nexts", getIntent().getIntExtra("nexts", 0) - 1);
                    i.putExtra("prevs", getIntent().getIntExtra("prevs", 0) + 1);
                    toggleLoading();
                    this.startActivity(i);
                    finish();
                });
            });
        }
        if (getIntent().getIntExtra("prevs", 0) > 0) {
            contentPrevButton.setVisibility(View.VISIBLE);
            contentPrevButton.setOnClickListener(e -> {
                toggleLoading();
                ComicRepository.getPrevEpisodeID(comicId, episode.getEpisodeNumber(), id -> {
                    Log.d("EpisodeContent", id);
                    Intent i = new Intent(this, EpisodeContentActivity.class);
                    i.putExtra("episodeId", id);
                    i.putExtra("comicId", episode.getComicId());
                    i.putExtra("nexts", getIntent().getIntExtra("nexts", 0) + 1);
                    i.putExtra("prevs", getIntent().getIntExtra("prevs", 0) - 1);
                    toggleLoading();
                    this.startActivity(i);
                    finish();
                });
            });
        }
    }

    private void initializeScrollingEffect() {
        episodeContents.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (!episodeContents.canScrollVertically(1)) {
                    Log.d("EpisodeContent", "Reached Bottom");
                    AnimationHelper.fadeIn(contentTopNavbar);
                    AnimationHelper.fadeIn(contentBottomNavbar);
                    return;
                }
                if (scrollY > oldScrollY) {
                    AnimationHelper.fadeOut(contentTopNavbar);
                    AnimationHelper.fadeOut(contentBottomNavbar);
                }
                else if (scrollY < oldScrollY - 3) {
                    AnimationHelper.fadeIn(contentTopNavbar);
                    AnimationHelper.fadeIn(contentBottomNavbar);
                }
            }
        });
    }

    private void toggleLoading() {
        if (!loading) {
            contentOverlay.setVisibility(View.VISIBLE);
            contentLoading.setVisibility(View.VISIBLE);
        } else {
            contentOverlay.setVisibility(View.INVISIBLE);
            contentLoading.setVisibility(View.INVISIBLE);
        }
        loading = !loading;
    }

    private void updateViewingHistory() {
        UserRepository.updateViewingHistory(episode.getComicId(), episode.getEpisodeId(), isSuccess -> {
            if (isSuccess) {
                Log.d("EpisodeContent", "Viewing history updated successfully.");
            } else {
                Log.w("EpisodeContent", "Failed to update viewing history.");
            }
        });
    }

    private void fetchData() {
        comicId = getIntent().getStringExtra("comicId");
        episodeId = getIntent().getStringExtra("episodeId");
        ComicRepository.getEpisodeByID(comicId, episodeId, result -> {
            if (result == null) {
                getOnBackPressedDispatcher().onBackPressed();
            }
            episode = result;
            populateUI();
            initializeScrollingEffect();
            updateViewingHistory();
        });
    }
}