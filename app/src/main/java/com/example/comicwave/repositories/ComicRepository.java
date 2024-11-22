package com.example.comicwave.repositories;

import android.util.Log;

import com.example.comicwave.interfaces.OnFinishListener;
import com.example.comicwave.models.Comic;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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

    public static Comic documentToComic(DocumentSnapshot docs) {
        Comic comic = new Comic();
        comic.setId(docs.getId());
        comic.setTitle((String) docs.get("title"));
        comic.setImageUrl((String) docs.get("imageUrl"));
        List<String> genres = (List<String>) docs.get("genres");
        if (genres != null) {
            comic.setGenres(new ArrayList<>(genres));
        } else {
            comic.setGenres(new ArrayList<>());
        }
        return comic;
    }

}
