package com.example.comicwave;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.comicwave.adapters.EpisodeAdapter;
import com.example.comicwave.helpers.DateHelper;
import com.example.comicwave.helpers.NumberHelper;
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.ComicDetails;
import com.example.comicwave.models.Episode;
import com.example.comicwave.repositories.ComicRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ComicDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView detailImage, detailFavoriteButton, detailWhereYouLeftOffImage;
    private TextView detailTitle, detailGenres, detailViews, detailFavorites, detailRating, detailDescription, detailAuthor,
            detailRatingText, detailFavoriteText, detailWhereYouLeftOffTitle, detailWhereYouLeftOffReleaseDate, detailEpisodeText;
    private Button detailSortByButton;
    private LinearLayout detailWhereYouLeftOffLayout;
    private String comicId;
    private ComicDetails comic;
    private ArrayList<Episode> episodes;
    private RecyclerView detailEpisodesList;
    String selected;
    private EpisodeAdapter adapter;
    private void initComponents() {
        toolbar = findViewById(R.id.detailToolbar);
        toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailGenres = findViewById(R.id.detailGenres);
        detailViews = findViewById(R.id.detailViews);
        detailFavorites = findViewById(R.id.detailFavorites);
        detailRating = findViewById(R.id.detailRating);
        detailDescription = findViewById(R.id.detailDescription);
        detailAuthor = findViewById(R.id.detailAuthor);
        detailFavoriteText = findViewById(R.id.detailFavoriteText);
        detailRatingText = findViewById(R.id.detailRatingText);
        detailFavoriteButton = findViewById(R.id.detailFavoriteButton);


        detailWhereYouLeftOffLayout = findViewById(R.id.detailWhereYouLeftOffLayout);
        detailWhereYouLeftOffImage = findViewById(R.id.detailWhereYouLeftOffImage);
        detailWhereYouLeftOffTitle = findViewById(R.id.detailWhereYouLeftOffTitle);
        detailWhereYouLeftOffReleaseDate = findViewById(R.id.detailWhereYouLeftOffReleaseDate);

        detailSortByButton = findViewById(R.id.detailSortByButton);
        selected = detailSortByButton.getText().toString();

        detailEpisodeText = findViewById(R.id.detailEpisodeText);
        detailEpisodesList = findViewById(R.id.detailEpisodesList);
        episodes = new ArrayList<>();
        adapter = new EpisodeAdapter(episodes);
        detailEpisodesList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        detailEpisodesList.setAdapter(adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comic_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        fetchData();
    }

    private void populateUI(Comic comic, Boolean isFavorited, Double userRating) {
        detailTitle.setText(comic.getTitle());
        detailGenres.setText(String.join(", ", comic.getGenres()));
        detailViews.setText(NumberHelper.format(comic.getTotalViews()));
        detailFavorites.setText(NumberHelper.format(comic.getTotalFavorites()));
        detailRating.setText(String.valueOf(comic.getRating()));
        detailDescription.setText(comic.getDescription());
        detailAuthor.setText(String.format("Written by: %s", comic.getAuthor()));

        Glide.with(this).load(comic.getImageUrl()).into(detailImage);

        if (isFavorited) {
            detailFavoriteButton.setImageResource(R.drawable.heart_icon_filled);
            detailFavoriteText.setText(String.valueOf("In Favorites"));
        }
        if (userRating != 0.0) {
            detailRatingText.setText(String.valueOf(userRating));
        }

        detailSortByButton.setOnClickListener(e -> {
            selected = detailSortByButton.getText().toString();
            PopupMenu menu = new PopupMenu(this, detailSortByButton);
            menu.getMenuInflater().inflate(R.menu.episodes_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                if (item != null && item.getTitle().equals(selected)) {
                    return true;
                }
                selected = item.getTitle().toString();
                detailSortByButton.setText(selected);
                Collections.sort(episodes, new Comparator<Episode>() {
                    @Override
                    public int compare(Episode o1, Episode o2) {
                        if (selected.equals("Oldest")) {
                            return o1.getEpisodeNumber() - o2.getEpisodeNumber();
                        } else {
                            return o2.getEpisodeNumber() - o1.getEpisodeNumber();
                        }
                    }
                });
                adapter.notifyDataSetChanged();
                return true;
            });
            menu.show();
        });

    }

    private void populateWhereYouLeftOff(Episode episode) {
        detailWhereYouLeftOffLayout.setVisibility(View.VISIBLE);
        Log.d("ComicDetails", episode.getEpisodeId());
        Glide.with(this)
                .load(episode.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(detailWhereYouLeftOffImage);
        detailWhereYouLeftOffTitle.setText(String.format("%d. %s", episode.getEpisodeNumber(), episode.getTitle()));
        detailWhereYouLeftOffReleaseDate.setText(DateHelper.format(episode.getReleaseDate()));
    }

    private void fetchData() {
        comicId = getIntent().getStringExtra("comicId");

        // Get Comic Details
        ComicRepository.getComicDetailsByID(comicId, FirebaseAuth.getInstance().getCurrentUser().getUid(), result -> {
            if (result != null) {
                comic = result;
                populateUI(comic.getComic(), comic.getFavorited(), comic.getRating());
            } else {
                Toast.makeText(this, "Comic not found!", Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(this::finish, 500);
            }
        });

        ComicRepository.getWhereYouLeftOff(comicId, FirebaseAuth.getInstance().getCurrentUser().getUid(), result -> {
            if (result == null) {
                Log.d("ComicDetails", "No Episodes");
                detailWhereYouLeftOffLayout.setVisibility(View.GONE);
            }
            else {
                populateWhereYouLeftOff(result);
            }
        });

        ComicRepository.getAllEpisodes(comicId, result -> {
            episodes.clear();
            episodes.addAll(result);
            detailEpisodeText.setText(String.format("Episodes (%d)", episodes.size()));
            adapter.notifyDataSetChanged();
        });


    }
}