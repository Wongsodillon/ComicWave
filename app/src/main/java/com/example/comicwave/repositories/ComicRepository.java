package com.example.comicwave.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ComicRepository {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference comicRef = db.collection("comic");
}
