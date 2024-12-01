package com.example.comicwave.fragments.favorites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comicwave.R;
import com.example.comicwave.adapters.FavoritesAdapter;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.repositories.ComicRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private ArrayList<Favorites> favorites;
    private TabLayout favoritesTab;
    private FavoritesAdapter favoritesAdapter;
    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView);
        favoritesTab = view.findViewById(R.id.favoritesTab);
        favorites = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(favorites, R.layout.item_favorites);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), 3);
        favoritesRecyclerView.setLayoutManager(gridLayoutManager);
        favoritesRecyclerView.setAdapter(favoritesAdapter);
        fetchData();
        return view;
    }

    private void fetchData() {
        favoritesAdapter.setLoading(true);
        ComicRepository.getFavoriteComics(FirebaseAuth.getInstance().getCurrentUser().getUid(), comics -> {
            favorites.clear();
            favorites.addAll(comics);
            favoritesAdapter.setLoading(false);
            favoritesAdapter.notifyDataSetChanged();
        });
    }

}