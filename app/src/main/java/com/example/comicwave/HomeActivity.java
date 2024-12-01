package com.example.comicwave;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.comicwave.fragments.favorites.FavoritesFragment;
import com.example.comicwave.fragments.home.HomeFragment;
import com.example.comicwave.fragments.schedule.ScheduleFragment;
import com.example.comicwave.fragments.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
        BottomNavigationView navigationBar = findViewById(R.id.bottom_navigation);

        navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            }
            else if (item.getItemId() == R.id.navigation_schedule) {
                loadFragment(new ScheduleFragment());
                return true;
            }
            else  if (item.getItemId() == R.id.navigation_search) {
                loadFragment(new SearchFragment());
                return true;
            } else if (item.getItemId() == R.id.navigation_favorites) {
                loadFragment(new FavoritesFragment());
                return true;
            }
            else {
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}