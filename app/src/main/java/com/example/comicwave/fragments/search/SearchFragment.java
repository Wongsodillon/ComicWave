package com.example.comicwave.fragments.search;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comicwave.R;
import com.example.comicwave.adapters.SearchResultAdapter;
import com.example.comicwave.models.Comic;
import com.example.comicwave.repositories.ComicRepository;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchViewModel mViewModel;
    private TextInputEditText searchSearchField;
    private RecyclerView searchResults;
    private ArrayList<Comic> comics;
    private SearchResultAdapter adapter;
    private void initComponents(View view) {
        comics = new ArrayList<>();
        searchSearchField = view.findViewById(R.id.searchSearchField);
        searchResults = view.findViewById(R.id.searchResults);

        adapter = new SearchResultAdapter(comics, R.layout.item_search);
        searchResults.setAdapter(adapter);
        searchResults.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initComponents(view);

        searchSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    searchResults.setVisibility(View.GONE);
                    comics.clear();
                    adapter.notifyDataSetChanged();
                }
                else {
                    searchResults.setVisibility(View.VISIBLE);
                    ComicRepository.searchComics(query, results -> {
                        if (!isAdded() || isRemoving()) return;
                        comics.clear();
                        comics.addAll(results);
                        adapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

}