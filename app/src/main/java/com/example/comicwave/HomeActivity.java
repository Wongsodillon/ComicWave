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
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.User;
import com.example.comicwave.repositories.ComicRepository;
import com.example.comicwave.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView homeContinueReadingSlider;
    private ArrayList<Comic> continueReadingComics;
    private ComicSliderAdapter continueReadingAdapter;
    private ImageView homeFeaturedImage;
    private TextView homeFeaturedTitle, homeFeaturedGenres;
    private void initComponents() {

        homeFeaturedImage = findViewById(R.id.homeFeaturedImage);
        homeFeaturedTitle = findViewById(R.id.homeFeaturedTitle);
        homeFeaturedGenres = findViewById(R.id.homeFeaturedGenres);

        homeContinueReadingSlider = findViewById(R.id.homeContinueReadingSlider);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        homeContinueReadingSlider.setLayoutManager(linearLayoutManager);

        continueReadingComics = new ArrayList<>();
        continueReadingAdapter = new ComicSliderAdapter(continueReadingComics, R.layout.item_slider);
        homeContinueReadingSlider.setAdapter(continueReadingAdapter);

//        DocumentReference sourceDocRef = db.collection("comic").document("TiX6DUlwWe8d9tzwvnfW");
//            sourceDocRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful() && task.getResult() != null) {
//                    Map<String, Object> documentData = task.getResult().getData();
//                    if (documentData != null) {
//                        for (int i = 0; i < 1; i++) {
//                            db.collection("comic").add(documentData).addOnCompleteListener(addTask -> {
//                                if (addTask.isSuccessful()) {
//                                    String newDocId = addTask.getResult().getId();
//                                    Log.d("New ID", newDocId);
//                                } else {
//                                    Log.d("New ID", "Failed to duplicate document " + task.getResult().getId());
//                                }
//                            });
//                        }
//                    }
//                }
//            });
    }


    private void fetchData() {
        ComicRepository.getContinueWatching(FirebaseAuth.getInstance().getCurrentUser().getUid(), comics -> {
            continueReadingComics.clear();
            continueReadingComics.addAll(comics);
            continueReadingAdapter.notifyDataSetChanged();
        });

        ComicRepository.getFeaturedComic(comic -> {
            Glide.with(this).load(comic.getImageUrl()).into(homeFeaturedImage);
            homeFeaturedTitle.setText(comic.getTitle());
            homeFeaturedGenres.setText(String.join(", ", comic.getGenres()));
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