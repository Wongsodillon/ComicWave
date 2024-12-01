package com.example.comicwave.fragments.home;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView homeContinueReadingSlider, homeFavoritesSlider, homeMostFavoritesSlider;
    private ArrayList<ViewingHistory> continueReadingComics;
    private ArrayList<Favorites> favoriteComics;
    private ArrayList<Comic> mostFavoritesComics;
    private ViewingHistoryAdapter continueReadingAdapter;
    private FavoritesSliderAdapter favoritesAdapter;
    private ComicSliderAdapter mostFavoritesAdapter;
    private ImageView homeFeaturedImage, homeFavoritesMore;
    private TextView homeFeaturedTitle, homeFeaturedGenres, homeContinueReadingText, homeMostFavoritesText;
    private LinearLayout homeContinueReadingLayout, homeFavoritesSliderLayout, homeMostFavoritesLayout;
    private FlexboxLayout homeFavoritesText;

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
    }

    private void fetchData() {
        ComicRepository.getFeaturedComic(comic -> {
            Glide.with(this).load(comic.getImageUrl()).into(homeFeaturedImage);
            homeFeaturedTitle.setText(comic.getTitle());
            homeFeaturedGenres.setText(String.join(", ", comic.getGenres()));
        });

        continueReadingAdapter.setLoading(true);
        ComicRepository.getViewingHistory(FirebaseAuth.getInstance().getCurrentUser().getUid(), comics -> {
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
        ComicRepository.getFavoriteComics(FirebaseAuth.getInstance().getCurrentUser().getUid(), 5, comics -> {
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
    }
}
