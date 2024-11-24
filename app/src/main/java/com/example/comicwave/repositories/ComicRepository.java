package com.example.comicwave.repositories;

import android.util.Log;

import com.example.comicwave.interfaces.OnFinishListener;
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.Favorites;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComicRepository {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference comicRef = db.collection("comic");

    public static void getContinueWatching(String userId, OnFinishListener<ArrayList<Comic>> listener) {
        DocumentReference user = UserRepository.userRef.document(userId);
        ArrayList<Comic> comics = new ArrayList<>();
        comicRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot document : querySnapshot) {
                Comic comic = documentToComic(document);
                if (comic != null) {
                    comics.add(comic);
                }
            }

            listener.onFinish(comics);

        }).addOnFailureListener(e -> {

        });
    }

    public static void getFeaturedComic(OnFinishListener<Comic> listener) {
        DocumentReference docs = comicRef.document("9ITWVdbiycSJpF4oLrXS");
        docs.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Comic comic = documentToComic(snapshot);
                listener.onFinish(comic);
            }
        });
    }

    public static void getFavoriteComics(String userId, OnFinishListener<ArrayList<Favorites>> listener) {
        CollectionReference favoriteRef = UserRepository.userRef.document(userId).collection("favorites");
        ArrayList<Favorites> favorites = new ArrayList<>();
        favoriteRef.get().addOnSuccessListener(snapshots -> {
            for (DocumentSnapshot snapshot : snapshots) {
                Favorites favorite = documentToFavorites(snapshot);
                favorites.add(favorite);
                Log.d("Favorite New", favorite.getComicId());
            }
            listener.onFinish(favorites);
        }).addOnFailureListener(e -> {
        });
    }

    public static Favorites documentToFavorites(DocumentSnapshot docs) {
        Favorites favorites = new Favorites(
                docs.getId(),
                (String) docs.get("title"),
                (String) docs.get("imageUrl"),
                (Timestamp) docs.get("createdAt")
        );
        return favorites;
    }

    public static void getComicsBySchedule(String schedule, OnFinishListener<ArrayList<Comic>> listener) {
        ArrayList<Comic> comics = new ArrayList<>();
        comicRef.whereEqualTo("schedule", schedule).get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot snapshot : snapshots) {
                        Comic comic = documentToComic(snapshot);
                        comics.add(comic);
                        Log.d("Schedule New", comic.getId());
                    }
                    listener.onFinish(comics);
                })
                .addOnFailureListener(e -> {

                });
    }

    public static Comic documentToComic(DocumentSnapshot docs) {
        Comic comic = new Comic();
        comic.setId(docs.getId());
        comic.setTitle((String) docs.get("title"));
        comic.setImageUrl((String) docs.get("imageUrl"));
        comic.setDescription((String) docs.get("description"));
        List<String> genres = (List<String>) docs.get("genres");
        comic.setGenres(genres != null ? new ArrayList<>(genres) : new ArrayList<>());
        comic.setAuthor((String) docs.get("author"));
        comic.setRating(docs.getDouble("averageRating"));
        comic.setSchedule((String) docs.get("schedule"));
        comic.setTotalFavorites(docs.getDouble("totalFavorites").intValue());
        comic.setTotalViews(docs.getDouble("totalViews").intValue());
        return comic;
    }

}
