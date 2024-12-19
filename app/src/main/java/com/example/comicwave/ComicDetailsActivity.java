package com.example.comicwave;

import android.content.Intent;
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
import com.example.comicwave.fragments.ratingSheet.RatingSheetFragment;
import com.example.comicwave.helpers.DateHelper;
import com.example.comicwave.helpers.EpisodeHelper;
import com.example.comicwave.helpers.NumberHelper;
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.ComicDetails;
import com.example.comicwave.models.Episode;
import com.example.comicwave.repositories.ComicRepository;
import com.example.comicwave.repositories.UserRepository;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ComicDetailsActivity extends AppCompatActivity implements RatingSheetFragment.RatingSheetListener {

    private Toolbar toolbar;
    private ImageView detailImage, detailWhereYouLeftOffImage, detailFavoriteIcon, detailReadlistIcon, detailRatingIcon;
    private TextView detailTitle, detailGenres, detailViews, detailFavorites, detailRating, detailDescription, detailAuthor,
            detailRatingText, detailFavoriteText, detailWhereYouLeftOffTitle, detailReadlistText,
            detailWhereYouLeftOffReleaseDate,  detailEpisodeText;
    private Button detailSortByButton;
    private LinearLayout detailWhereYouLeftOffLayout, detailRatingButton, detailReadlistButton, detailShareButton, detailFavoriteButton;
    private FlexboxLayout detailEpisodeLayout, detailIcons;
    private ShimmerFrameLayout detailEpisodeSkeletonLayout, detailDescriptionSkeleton;
    private String comicId;
    private ComicDetails comic;
    private ArrayList<Episode> episodes;
    private RecyclerView detailEpisodesList;
    String selected;
    Boolean isFavorited = false;
    Boolean isReadListed = false;
    double userRating;
    private EpisodeAdapter adapter;
    private boolean isActive = false;
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
        detailIcons = findViewById(R.id.detailIcons);
        detailDescriptionSkeleton = findViewById(R.id.detailDescriptionSkeleton);

        detailWhereYouLeftOffLayout = findViewById(R.id.detailWhereYouLeftOffLayout);
        detailWhereYouLeftOffImage = findViewById(R.id.detailWhereYouLeftOffImage);
        detailWhereYouLeftOffTitle = findViewById(R.id.detailWhereYouLeftOffTitle);
        detailWhereYouLeftOffReleaseDate = findViewById(R.id.detailWhereYouLeftOffReleaseDate);

        detailSortByButton = findViewById(R.id.detailSortByButton);
        selected = detailSortByButton.getText().toString();

        detailEpisodeLayout = findViewById(R.id.detailEpisodeLayout);
        detailEpisodeSkeletonLayout = findViewById(R.id.detailEpisodeSkeletonLayout);

        detailEpisodeText = findViewById(R.id.detailEpisodeText);
        detailEpisodesList = findViewById(R.id.detailEpisodesList);
        episodes = new ArrayList<>();
        adapter = new EpisodeAdapter(episodes);
        detailEpisodesList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        detailEpisodesList.setAdapter(adapter);

        detailFavoriteButton = findViewById(R.id.detailFavoriteButton);
        detailRatingButton = findViewById(R.id.detailRatingButton);
        detailShareButton = findViewById(R.id.detailShareButton);
        detailFavoriteIcon = findViewById(R.id.detailFavoriteIcon);
        detailRatingIcon = findViewById(R.id.detailRatingIcon);
        detailReadlistText = findViewById(R.id.detailReadlistText);
        detailReadlistIcon = findViewById(R.id.detailReadlistIcon);
        detailReadlistButton = findViewById(R.id.detailReadlistButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
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

    private void populateUI(Comic comic, Boolean isFavorited, Double userRating, Boolean isReadListed) {
        detailTitle.setText(comic.getTitle());
        detailGenres.setText(String.join(", ", comic.getGenres()));
        detailViews.setText(NumberHelper.format(comic.getTotalViews()));
        detailFavorites.setText(NumberHelper.format(comic.getTotalFavorites()));
        detailRating.setText(String.valueOf(comic.getRating()));
        detailDescription.setText(comic.getDescription());
        detailDescriptionSkeleton.setVisibility(View.GONE);
        detailAuthor.setText(String.format("Written by: %s", comic.getAuthor()));

        Glide.with(this).load(comic.getImageUrl()).into(detailImage);

        detailIcons.setVisibility(View.VISIBLE);

        if (isFavorited) {
            detailFavoriteIcon.setImageResource(R.drawable.heart_icon_filled);
            detailFavoriteText.setText(String.valueOf("In Favorites"));
        }

        if (isReadListed) {
            detailReadlistIcon.setImageResource(R.drawable.added_icon);
            detailReadlistText.setText("Added");
        }

        if (userRating != 0.0) {
            detailRatingIcon.setImageResource(R.drawable.star_icon);
            detailRatingText.setText(String.format("%.1f/5", userRating));
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
                            adapter.setIsAscending(true);
                            return o1.getEpisodeNumber() - o2.getEpisodeNumber();
                        } else {
                            adapter.setIsAscending(false);
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
    private void initializeButtons() {
        detailFavoriteIcon.setImageResource(isFavorited ? R.drawable.heart_icon_filled : R.drawable.heart_icon_outline);
        detailFavoriteText.setText(isFavorited ? "In Favorites" : "Favorite");

        detailFavoriteButton.setOnClickListener(e -> {
            isFavorited = !isFavorited;
            detailFavoriteIcon.setImageResource(isFavorited ? R.drawable.heart_icon_filled : R.drawable.heart_icon_outline);
            detailFavoriteText.setText(isFavorited ? "In Favorites" : "Favorite");
            if (isFavorited) {
                UserRepository.addToFavorites(comicId, comic.getComic().getTitle(), comic.getComic().getImageUrl(), success -> {
                    if (success) {
                        Log.d("Favorite", "Added to favorites");
                    } else {
                        Log.d("Favorite", "Failed to add to favorites");
                        isFavorited = false;
                        detailFavoriteIcon.setImageResource(R.drawable.heart_icon_outline);
                        detailFavoriteText.setText("Favorite");
                    }
                });
            } else {
                UserRepository.removeFromFavorites(comicId, success -> {
                    if (success) {
                        Log.d("Favorite", "Removed from favorites");
                    } else {
                        Log.d("Favorite", "Failed to remove from favorites");
                        isFavorited = true;
                        detailFavoriteIcon.setImageResource(R.drawable.heart_icon_filled);
                        detailFavoriteText.setText("In Favorites");
                    }
                });
            }
        });

        detailReadlistButton.setOnClickListener(e -> {
            isReadListed = !isReadListed;
            detailReadlistIcon.setImageResource(isReadListed ? R.drawable.added_icon : R.drawable.add_icon);
            detailReadlistText.setText(isReadListed ? "Added" : "Readlist");
            if (isReadListed) {
                UserRepository.addToReadList(comicId, comic.getComic().getTitle(), comic.getComic().getImageUrl(), success -> {
                    if (success) {
                        Log.d("ReadList", "Added");
                    } else {
                        Log.d("ReadList", "Failed to add to favorites");
                        isReadListed = false;
                        detailReadlistIcon.setImageResource(R.drawable.add_icon);
                        detailReadlistText.setText("Readlist");
                    }
                });
            } else {
                UserRepository.removeFromReadList(comicId, success -> {
                    if (success) {
                        Log.d("ReadList", "Removed from favorites");
                    } else {
                        Log.d("ReadList", "Failed to remove from favorites");
                        isReadListed = true;
                        detailReadlistIcon.setImageResource(R.drawable.added_icon);
                        detailReadlistText.setText("Added");
                    }
                });
            }
        });

        detailRatingButton.setOnClickListener(e -> {
            Log.d("ComicDetails", "Rated CLick");
            RatingSheetFragment ratingSheetFragment = RatingSheetFragment.newInstance();
            Bundle args = new Bundle();
            args.putString("comicTitle", comic.getComic().getTitle());
            args.putString("comicImage", comic.getComic().getImageUrl());
            args.putDouble("userRating", comic.getRating());
            ratingSheetFragment.setArguments(args);
            ratingSheetFragment.show(getSupportFragmentManager(), ratingSheetFragment.getTag());
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
        detailWhereYouLeftOffLayout.setOnClickListener(e -> {
            Intent i = new Intent(this, EpisodeContentActivity.class);
            i.putExtra("comicId", comicId);
            i.putExtra("episodeId", episode.getEpisodeId());
            i.putExtra("nexts", EpisodeHelper.getAvailableNexts(episodes, episode.getEpisodeNumber()-1, true));
            i.putExtra("prevs", episode.getEpisodeNumber() - 1);
            startActivity(i);
        });
    }

    private void refetchWhereYouLeftOff() {
        ComicRepository.getWhereYouLeftOff(comicId, FirebaseAuth.getInstance().getCurrentUser().getUid(), result -> {
            if (!isActive) return;
            if (result == null) {
                Log.d("ComicDetails", "No Episodes");
                detailWhereYouLeftOffLayout.setVisibility(View.GONE);
            }
            else {
                populateWhereYouLeftOff(result);
            }
        });
    }

    private void fetchData() {
        comicId = getIntent().getStringExtra("comicId");

        // Get Comic Details
        ComicRepository.getComicDetailsByID(comicId, FirebaseAuth.getInstance().getCurrentUser().getUid(), result -> {
            if (!isActive) return;
            if (result != null) {
                comic = result;
                isFavorited = comic.getFavorited();
                userRating = comic.getRating();
                isReadListed = comic.getReadListed();
                populateUI(comic.getComic(), isFavorited, userRating, isReadListed);
                initializeButtons();
            } else {
                Toast.makeText(this, "Comic not found!", Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(this::finish, 500);
            }
        });
        refetchWhereYouLeftOff();
        ComicRepository.getAllEpisodes(comicId, result -> {
            if (!isActive) return;
            episodes.clear();
            episodes.addAll(result);
            detailEpisodeLayout.setVisibility(View.VISIBLE);
            detailEpisodeSkeletonLayout.setVisibility(View.GONE);
            detailEpisodeText.setText(String.format("Episodes (%d)", episodes.size()));
            adapter.notifyDataSetChanged();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refetchWhereYouLeftOff();
    }

    @Override
    public void onRatingSubmitted(double rating) {
        if (rating == 0.0 && userRating == 0.0) {
            return;
        }
        UserRepository.addRating(comic.getComic().getId(), rating, isSuccess -> {
            if (isSuccess && rating == 0.0) {
                detailRatingIcon.setImageResource(R.drawable.star_outline_icon);
                detailRatingText.setText("Rating");
            } else if (isSuccess && rating != 0.0) {
                detailRatingIcon.setImageResource(R.drawable.star_icon);
                detailRatingText.setText(String.format("%.1f/5", rating));
            } else {
                Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT);
            }
        });
    }
}