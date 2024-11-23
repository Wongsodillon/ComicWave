package com.example.comicwave;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicwave.adapters.ComicSliderAdapter;
import com.example.comicwave.adapters.FavoritesSliderAdapter;
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.User;
import com.example.comicwave.repositories.ComicRepository;
import com.example.comicwave.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView homeContinueReadingSlider, homeFavoritesSlider;
    private ArrayList<Comic> continueReadingComics;
    private ArrayList<Favorites> favoriteComics;
    private ComicSliderAdapter continueReadingAdapter;
    private FavoritesSliderAdapter favoritesAdapter;
    private ImageView homeFeaturedImage;
    private TextView homeFeaturedTitle, homeFeaturedGenres;
    private void initComponents() {

        homeFeaturedImage = findViewById(R.id.homeFeaturedImage);
        homeFeaturedTitle = findViewById(R.id.homeFeaturedTitle);
        homeFeaturedGenres = findViewById(R.id.homeFeaturedGenres);

        homeContinueReadingSlider = findViewById(R.id.homeContinueReadingSlider);
        LinearLayoutManager continueReadingManager = new LinearLayoutManager(this);
        continueReadingManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        homeContinueReadingSlider.setLayoutManager(continueReadingManager);

        continueReadingComics = new ArrayList<>();
        continueReadingAdapter = new ComicSliderAdapter(continueReadingComics, R.layout.item_slider);
        homeContinueReadingSlider.setAdapter(continueReadingAdapter);

        homeFavoritesSlider = findViewById(R.id.homeFavoritesSlider);
        LinearLayoutManager favoritesManager = new LinearLayoutManager(this);
        favoritesManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        homeFavoritesSlider.setLayoutManager(favoritesManager);

        favoriteComics = new ArrayList<>();
        favoritesAdapter = new FavoritesSliderAdapter(favoriteComics, R.layout.item_slider);
        homeFavoritesSlider.setAdapter(favoritesAdapter);
    }


    private void fetchData() {
        ComicRepository.getFeaturedComic(comic -> {
            Glide.with(this).load(comic.getImageUrl()).into(homeFeaturedImage);
            homeFeaturedTitle.setText(comic.getTitle());
            homeFeaturedGenres.setText(String.join(", ", comic.getGenres()));
        });

        ComicRepository.getContinueWatching(FirebaseAuth.getInstance().getCurrentUser().getUid(), comics -> {
            continueReadingComics.clear();
            continueReadingComics.addAll(comics);
            continueReadingAdapter.notifyDataSetChanged();
        });

        ComicRepository.getFavoriteComics(FirebaseAuth.getInstance().getCurrentUser().getUid(), comics -> {
            favoriteComics.clear();
            favoriteComics.addAll(comics);
            Log.d("Favorites", "Updating adapter with " + favoriteComics.size() + " items.");
            favoritesAdapter.notifyDataSetChanged();
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initComponents();
        fetchData();
    }

}