package com.example.comicwave;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicwave.adapters.EpisodeAdapter;
import com.example.comicwave.adapters.EpisodeContentAdapter;
import com.example.comicwave.helpers.AnimationHelper;
import com.example.comicwave.models.Episode;
import com.example.comicwave.repositories.ComicRepository;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class EpisodeContent extends AppCompatActivity {

    private FlexboxLayout contentTopNavbar, contentBottomNavbar;
    private TextView contentEpisodeTitle;
    private RecyclerView episodeContents;
    private ImageView contentBackButton, contentPrevButton, contentNextButton;
    private String comicId, episodeId;
    private Episode episode;
    private EpisodeContentAdapter episodeAdapter;
    private void initComponents() {
        contentTopNavbar = findViewById(R.id.contentTopNavbar);
        contentBottomNavbar = findViewById(R.id.contentBottomNavbar);
        contentEpisodeTitle = findViewById(R.id.contentEpisodeTitle);
        episodeContents = findViewById(R.id.episodeContents);
        contentBackButton = findViewById(R.id.contentBackButton);
        contentPrevButton = findViewById(R.id.contentPrevButton);
        contentNextButton = findViewById(R.id.contentNextButton);
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
    }

    private void initializeScrollingEffect() {
        episodeContents.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
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
        });
    }
}