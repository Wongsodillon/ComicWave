package com.example.comicwave.fragments.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.comicwave.R;
import com.example.comicwave.adapters.ComicSliderAdapter;
import com.example.comicwave.adapters.FavoritesSliderAdapter;
import com.example.comicwave.adapters.ViewingHistoryAdapter;
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.ViewingHistory;
import com.example.comicwave.repositories.ComicRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView homeContinueReadingSlider, homeFavoritesSlider;
    private ArrayList<ViewingHistory> continueReadingComics;
    private ArrayList<Favorites> favoriteComics;
    private ViewingHistoryAdapter continueReadingAdapter;
    private FavoritesSliderAdapter favoritesAdapter;
    private ImageView homeFeaturedImage;
    private TextView homeFeaturedTitle, homeFeaturedGenres;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeFeaturedImage = view.findViewById(R.id.homeFeaturedImage);
        homeFeaturedTitle = view.findViewById(R.id.homeFeaturedTitle);
        homeFeaturedGenres = view.findViewById(R.id.homeFeaturedGenres);

        homeContinueReadingSlider = view.findViewById(R.id.homeContinueReadingSlider);
        homeFavoritesSlider = view.findViewById(R.id.homeFavoritesSlider);

        continueReadingComics = new ArrayList<>();
        continueReadingAdapter = new ViewingHistoryAdapter(continueReadingComics, R.layout.item_slider);
        homeContinueReadingSlider.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeContinueReadingSlider.setAdapter(continueReadingAdapter);

        favoriteComics = new ArrayList<>();
        favoritesAdapter = new FavoritesSliderAdapter(favoriteComics, R.layout.item_slider);
        homeFavoritesSlider.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeFavoritesSlider.setAdapter(favoritesAdapter);

        fetchData();

        return view;
    }

    private void fetchData() {
        ComicRepository.getFeaturedComic(comic -> {
            Glide.with(this).load(comic.getImageUrl()).into(homeFeaturedImage);
            homeFeaturedTitle.setText(comic.getTitle());
            homeFeaturedGenres.setText(String.join(", ", comic.getGenres()));
        });

        ComicRepository.getViewingHistory(FirebaseAuth.getInstance().getCurrentUser().getUid(), comics -> {
            continueReadingComics.clear();
            continueReadingComics.addAll(comics);
            continueReadingAdapter.notifyDataSetChanged();
        });

        ComicRepository.getFavoriteComics(FirebaseAuth.getInstance().getCurrentUser().getUid(), comics -> {
            favoriteComics.clear();
            favoriteComics.addAll(comics);
            favoritesAdapter.notifyDataSetChanged();
        });
    }
}
