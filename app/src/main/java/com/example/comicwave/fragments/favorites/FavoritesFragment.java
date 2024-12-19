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
import android.widget.TextView;

import com.example.comicwave.R;
import com.example.comicwave.adapters.FavoritesAdapter;
import com.example.comicwave.adapters.ReadListAdapter;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.ReadList;
import com.example.comicwave.repositories.ComicRepository;
import com.example.comicwave.repositories.UserRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private ArrayList<Favorites> favoritesData;
    private ArrayList<ReadList> readListData;
    private TabLayout favoritesTab;
    private FavoritesAdapter favoritesAdapter;
    private ReadListAdapter readListAdapter;
    private TextView favoritesEmptyMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView);
        favoritesTab = view.findViewById(R.id.favoritesTab);

        favoritesData = new ArrayList<>();
        readListData = new ArrayList<>();

        favoritesAdapter = new FavoritesAdapter(favoritesData, R.layout.item_favorites);
        readListAdapter = new ReadListAdapter(readListData, R.layout.item_favorites);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), 3);
        favoritesRecyclerView.setLayoutManager(gridLayoutManager);

        favoritesRecyclerView.setAdapter(favoritesAdapter);

        favoritesEmptyMessage = view.findViewById(R.id.favoritesEmptyMessage);

        favoritesTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    favoritesRecyclerView.setAdapter(favoritesAdapter);
                    fetchFavoritesData();
                } else if (tab.getPosition() == 1) {
                    favoritesRecyclerView.setAdapter(readListAdapter);
                    fetchReadListData();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        fetchFavoritesData();

        return view;
    }

    private void fetchFavoritesData() {
        favoritesAdapter.setLoading(true);
        UserRepository.getFavoriteComics(comics -> {
            if (!isAdded() || isRemoving()) return;
            favoritesData.clear();
            favoritesData.addAll(comics);
            favoritesAdapter.setLoading(false);
            if (comics.isEmpty()) {
                favoritesRecyclerView.setVisibility(View.GONE);
                favoritesEmptyMessage.setText("You have no favorite comics!");
                favoritesEmptyMessage.setVisibility(View.VISIBLE);
            }
            else {
                favoritesRecyclerView.setVisibility(View.VISIBLE);
                favoritesEmptyMessage.setVisibility(View.GONE);
            }
            favoritesAdapter.notifyDataSetChanged();
        });
    }

    private void fetchReadListData() {
        readListAdapter.setLoading(true);
        UserRepository.getReadList(comics -> {
            if (!isAdded() || isRemoving()) return;
            readListData.clear();
            readListData.addAll(comics);
            readListAdapter.setLoading(false);
            if (comics.isEmpty()) {
                favoritesRecyclerView.setVisibility(View.GONE);
                favoritesEmptyMessage.setText("Your Read List is still empty!");
                favoritesEmptyMessage.setVisibility(View.VISIBLE);
            }
            else {
                favoritesRecyclerView.setVisibility(View.VISIBLE);
                favoritesEmptyMessage.setVisibility(View.GONE);
            }
            readListAdapter.notifyDataSetChanged();
        });
    }
}
