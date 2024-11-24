package com.example.comicwave.fragments.schedule;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comicwave.R;
import com.example.comicwave.adapters.ComicScheduleAdapter;
import com.example.comicwave.adapters.ComicSliderAdapter;
import com.example.comicwave.models.Comic;
import com.example.comicwave.repositories.ComicRepository;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment {

    private Handler debounceHandler = new Handler(Looper.getMainLooper());
    private static final long DEBOUNCE_DELAY = 100;

    private RecyclerView scheduleRecyclerView;
    private ArrayList<Comic> comics;
    private ComicScheduleAdapter comicScheduleAdapter;
    private TabLayout scheduleTab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        scheduleRecyclerView = view.findViewById(R.id.scheduleRecyclerView);
        comics = new ArrayList<>();
        comicScheduleAdapter = new ComicScheduleAdapter(comics, R.layout.item_schedule);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), 3);
        scheduleRecyclerView.setLayoutManager(gridLayoutManager);
        scheduleRecyclerView.setAdapter(comicScheduleAdapter);

        scheduleTab = view.findViewById(R.id.scheduleTab);

        scheduleTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String schedule = getScheduleFromTabPosition(tab.getPosition());
                debounceFetchData(schedule);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        String initialSchedule = getScheduleFromTabPosition(scheduleTab.getSelectedTabPosition());
        debounceFetchData(initialSchedule);

        return view;
    }

    private String getScheduleFromTabPosition(int position) {
        switch (position) {
            case 0: return "Monday";
            case 1: return "Tuesday";
            case 2: return "Wednesday";
            case 3: return "Thursday";
            case 4: return "Friday";
            case 5: return "Saturday";
            case 6: return "Sunday";
            case 7: return "Daily";
            default: return "";
        }
    }

    private void debounceFetchData(String day) {
        debounceHandler.removeCallbacksAndMessages(null);
        debounceHandler.postDelayed(() -> fetchData(day), DEBOUNCE_DELAY);
    }

    private void fetchData(String day) {
        ComicRepository.getComicsBySchedule(day, c -> {
            comics.clear();
            comics.addAll(c);
            comicScheduleAdapter.notifyDataSetChanged();
        });
    }
}