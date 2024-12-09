package com.example.comicwave.fragments.home;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.comicwave.ComicDetailsActivity;
import com.example.comicwave.R;
import com.example.comicwave.adapters.ComicSliderAdapter;
import com.example.comicwave.adapters.FavoritesSliderAdapter;
import com.example.comicwave.adapters.ViewingHistoryAdapter;
import com.example.comicwave.fragments.favorites.FavoritesFragment;
import com.example.comicwave.fragments.schedule.ScheduleFragment;
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.ViewingHistory;
import com.example.comicwave.repositories.ComicRepository;
import com.example.comicwave.repositories.UserRepository;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HomeFragment extends Fragment {
    private RecyclerView homeContinueReadingSlider, homeFavoritesSlider, homeMostFavoritesSlider, homeGenresSlider, homeGenresSlider2;
    private ArrayList<ViewingHistory> continueReadingComics;
    private ArrayList<Favorites> favoriteComics;
    private ArrayList<Comic> mostFavoritesComics, comicGenres, comicGenres2;
    private ViewingHistoryAdapter continueReadingAdapter;
    private FavoritesSliderAdapter favoritesAdapter;
    private ComicSliderAdapter mostFavoritesAdapter, comicGenresAdapter, comicGenresAdapter2;
    private ImageView homeFeaturedImage, homeFavoritesMore;
    private TextView homeFeaturedTitle, homeFeaturedGenres, homeContinueReadingText, homeMostFavoritesText, homeGenresText, homeGenresText2;
    private LinearLayout homeContinueReadingLayout, homeFavoritesSliderLayout, homeMostFavoritesLayout, homeGenresLayout, homeGenresLayout2;
    private FlexboxLayout homeFavoritesText;
    private FrameLayout homeFeaturedLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initComponents(view);
        setupAdapters();
        fetchData();

        return view;
    }

    private void initComponents(View view) {
        homeFeaturedLayout = view.findViewById(R.id.homeFeaturedLayout);
        homeFeaturedImage = view.findViewById(R.id.homeFeaturedImage);
        homeFeaturedTitle = view.findViewById(R.id.homeFeaturedTitle);
        homeFeaturedGenres = view.findViewById(R.id.homeFeaturedGenres);



        homeContinueReadingSlider = view.findViewById(R.id.homeContinueReadingSlider);
        homeFavoritesSlider = view.findViewById(R.id.homeFavoritesSlider);
        homeContinueReadingLayout = view.findViewById(R.id.homeContinueReadingLayout);
        homeFavoritesSliderLayout = view.findViewById(R.id.homeFavoritesSliderLayout);
        homeFavoritesText = view.findViewById(R.id.homeFavoritesText);

        homeMostFavoritesLayout = view.findViewById(R.id.homeMostFavoritesLayout);
        homeMostFavoritesSlider = view.findViewById(R.id.homeMostFavoritesSlider);
        homeMostFavoritesText = view.findViewById(R.id.homeMostFavoritesText);
        homeContinueReadingText = view.findViewById(R.id.homeContinueReadingText);

        homeFavoritesMore = view.findViewById(R.id.homeFavoritesMore);
        homeFavoritesMore.setOnClickListener(e -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ScheduleFragment())
                    .commit();
            BottomNavigationView navigationBar = requireActivity().findViewById(R.id.bottom_navigation);
            navigationBar.setSelectedItemId(R.id.navigation_favorites);
        });

        homeGenresText = view.findViewById(R.id.homeGenresText);
        homeGenresLayout = view.findViewById(R.id.homeGenresLayout);
        homeGenresSlider = view.findViewById(R.id.homeGenresSlider);

        homeGenresText2 = view.findViewById(R.id.homeGenresText2);
        homeGenresLayout2 = view.findViewById(R.id.homeGenresLayout2);
        homeGenresSlider2 = view.findViewById(R.id.homeGenresSlider2);
    }
    private void setupAdapters() {
        continueReadingComics = new ArrayList<>();
        continueReadingAdapter = new ViewingHistoryAdapter(continueReadingComics, R.layout.item_slider);
        homeContinueReadingSlider.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeContinueReadingSlider.setAdapter(continueReadingAdapter);

        favoriteComics = new ArrayList<>();
        favoritesAdapter = new FavoritesSliderAdapter(favoriteComics, R.layout.item_slider);
        homeFavoritesSlider.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeFavoritesSlider.setAdapter(favoritesAdapter);

        mostFavoritesComics = new ArrayList<>();
        mostFavoritesAdapter = new ComicSliderAdapter(mostFavoritesComics, R.layout.item_best_comics);
        homeMostFavoritesSlider.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeMostFavoritesSlider.setAdapter(mostFavoritesAdapter);

        comicGenres = new ArrayList<>();
        comicGenresAdapter = new ComicSliderAdapter(comicGenres, R.layout.item_slider);
        homeGenresSlider.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeGenresSlider.setAdapter(comicGenresAdapter);

        comicGenres2 = new ArrayList<>();
        comicGenresAdapter2 = new ComicSliderAdapter(comicGenres2, R.layout.item_slider);
        homeGenresSlider2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeGenresSlider2.setAdapter(comicGenresAdapter2);
    }

    private void fetchData() {
        ComicRepository.getFeaturedComic(comic -> {
            Glide.with(this).load(comic.getImageUrl()).into(homeFeaturedImage);
            homeFeaturedTitle.setText(comic.getTitle());
            homeFeaturedGenres.setText(String.join(", ", comic.getGenres()));
            homeFeaturedLayout.setOnClickListener(e -> {
                Intent i = new Intent(getContext(), ComicDetailsActivity.class);
                i.putExtra("comicId", comic.getId());
                getContext().startActivity(i);
            });
        });

        continueReadingAdapter.setLoading(true);
        UserRepository.getViewingHistory(FirebaseAuth.getInstance().getCurrentUser().getUid(), comics -> {
            continueReadingComics.clear();
            if (comics != null && !comics.isEmpty()) {
                continueReadingComics.addAll(comics);
                Log.d("HomeFragment", "Viewing history size: " + continueReadingComics.size());
            }
            continueReadingAdapter.setLoading(false);
            if (continueReadingComics.isEmpty()) {
                homeContinueReadingLayout.setVisibility(View.GONE);
            } else {
                homeContinueReadingText.setVisibility(View.VISIBLE);
                homeContinueReadingLayout.setVisibility(View.VISIBLE);
            }
            continueReadingAdapter.notifyDataSetChanged();
        });

        favoritesAdapter.setLoading(true);
        UserRepository.getFavoriteComics(5, comics -> {
            favoriteComics.clear();
            if (comics != null && !comics.isEmpty()) {
                favoriteComics.addAll(comics);
                Log.d("HomeFragment", "Favorites size: " + favoriteComics.size());
            }
            favoritesAdapter.setLoading(false);
            if (favoriteComics.isEmpty()) {
                homeFavoritesSliderLayout.setVisibility(View.GONE);
            } else {
                homeFavoritesText.setVisibility(View.VISIBLE);
                homeFavoritesSliderLayout.setVisibility(View.VISIBLE);
            }
            homeFavoritesText.setVisibility(favoriteComics.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            favoritesAdapter.notifyDataSetChanged();
        });

        mostFavoritesAdapter.setLoading(true);
        ComicRepository.getMostFavorited(comics -> {
            mostFavoritesComics.clear();
            mostFavoritesComics.addAll(comics);
            mostFavoritesAdapter.setLoading(false);
            if (mostFavoritesComics.isEmpty()) {
                homeMostFavoritesLayout.setVisibility(View.GONE);
            } else {
                homeMostFavoritesText.setVisibility(View.VISIBLE);
                homeMostFavoritesLayout.setVisibility(View.VISIBLE);
            }
            homeMostFavoritesText.setVisibility(mostFavoritesComics.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            mostFavoritesAdapter.notifyDataSetChanged();
        });

        ArrayList<String> genresToShow = new ArrayList<>(Arrays.asList("Romance", "Drama", "Kingdom"));

        Collections.shuffle(genresToShow);

        String genre1 = genresToShow.get(0);
        String genre2 = genresToShow.get(1);

        comicGenresAdapter.setLoading(true);
        ComicRepository.getComicsByGenre(genre1, results -> {
            comicGenres.clear();
            comicGenres.addAll(results);
            comicGenresAdapter.setLoading(false);
            if (results.isEmpty()) {
                homeGenresLayout.setVisibility(View.GONE);
            } else {
                homeGenresText.setText(String.format("%s comics", genre1));
                homeGenresLayout.setVisibility(View.VISIBLE);
            }
            homeGenresText.setVisibility(results.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            comicGenresAdapter.notifyDataSetChanged();
        });

        comicGenresAdapter2.setLoading(true);
        ComicRepository.getComicsByGenre(genre2, results -> {
            comicGenres2.clear();
            comicGenres2.addAll(results);
            comicGenresAdapter2.setLoading(false);
            if (results.isEmpty()) {
                homeGenresLayout2.setVisibility(View.GONE);
            } else {
                homeGenresText2.setText(String.format("%s comics", genre2));
                homeGenresLayout2.setVisibility(View.VISIBLE);
            }
            homeGenresText2.setVisibility(results.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            comicGenresAdapter2.notifyDataSetChanged();
        });
    }
}
