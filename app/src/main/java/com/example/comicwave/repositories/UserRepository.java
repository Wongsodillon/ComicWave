package com.example.comicwave.repositories;

import android.util.Log;
import android.widget.Toast;

import com.example.comicwave.SignUpActivity;
import com.example.comicwave.database.ComicWaveDB;
import com.example.comicwave.interfaces.OnChangeListener;
import com.example.comicwave.interfaces.OnFinishListener;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static CollectionReference userRef = FirebaseFirestore.getInstance().collection("user");
    public static User CURRENT_USER = null;
    public static void login(String email, String password, OnFinishListener<Boolean> listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    listener.onFinish(task.isSuccessful());
                });
    }
    public static void getCurrentUser(OnFinishListener<User> listener) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            CURRENT_USER = null;
            listener.onFinish(null);
            return;
        }

        if (CURRENT_USER == null || !CURRENT_USER.getUserId().equals(firebaseUser.getUid())) {
            getUserByID(firebaseUser.getUid(), user -> {
                CURRENT_USER = user;
                listener.onFinish(CURRENT_USER);
            });
        } else {
            listener.onFinish(CURRENT_USER);
        }
    }

    public static User documentToUser(DocumentSnapshot userDoc) {
        String id = userDoc.getId();
        String name = userDoc.getString("name");
        String email = userDoc.getString("email");
        return new User(id, email, name);
    }

    public static void getUserByID(String id, OnFinishListener<User> listener) {
        DocumentReference userDoc = userRef.document(id);
        userDoc.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                listener.onFinish(documentToUser(snapshot));
            } else {
                listener.onFinish(null);
            }
        }).addOnFailureListener(e -> {
            listener.onFinish(null);
        });
    }
    public static void addToFavorites(String comicId, String title, String imageUrl, OnFinishListener<Boolean> listener) {
        DocumentReference docs = userRef.document(mAuth.getCurrentUser().getUid());
        Favorites favorites = new Favorites(comicId, title, imageUrl);
        HashMap<String, Object> favoritesData = favorites.getMappedData();
        docs.collection("favorites")
                .document(comicId)
                .set(favoritesData)
                .addOnCompleteListener(e -> {
                    if (e.isSuccessful()) {
                        listener.onFinish(true);
                    } else {
                        listener.onFinish(false);
                    }
                });
    }
    public static void removeFromFavorites(String comicId, OnFinishListener<Boolean> listener) {
        DocumentReference docs = userRef.document(mAuth.getCurrentUser().getUid());
        docs.collection("favorites").document(comicId)
                .delete()
                .addOnSuccessListener(e -> {
                    listener.onFinish(true);
                })
                .addOnFailureListener(e -> {
                    listener.onFinish(false);
                });
    }

    public static void addRating(String comicId, double rating, OnFinishListener<Boolean> listener) {
        DocumentReference docs = userRef.document(mAuth.getCurrentUser().getUid())
                .collection("ratings")
                .document(comicId);
        DocumentReference comicDocs = ComicRepository.comicRef.document(comicId);
        docs.get().addOnSuccessListener(task -> {
            if (task.exists() && rating == 0.0) {
                docs.delete()
                        .addOnSuccessListener(e -> {
                            listener.onFinish(true);
                        }).addOnFailureListener(e -> {
                            listener.onFinish(false);
                        });
            }
            else {
                HashMap<String, Object> ratingData = new HashMap<>();
                ratingData.put("createdAt", Timestamp.now());
                ratingData.put("rating", rating);
                docs.set(ratingData).addOnCompleteListener(e -> {
                    listener.onFinish(true);
                }).addOnSuccessListener(e -> {
                    listener.onFinish(false);
                });
            }
        }).addOnFailureListener(e -> {
            listener.onFinish(false);
        });
    }

    public static void signUp(String fullName, String email, String password, OnFinishListener<Boolean> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fillUserInfo(fullName, fillTask -> {
                            if (fillTask) {
                                listener.onFinish(true);
                            }
                            else {
                                listener.onFinish(false);
                            }
                        });
                    }
                    else {
                        Log.d("ErrorSignUp", fullName + " " + email);
                        listener.onFinish(false);
                    }
                });
    }

    public static void fillUserInfo(String name, OnFinishListener<Boolean> listener) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Log.e("fillUserInfo", "No authenticated user found");
            listener.onFinish(false);
            return;
        }
        User user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), name);
        Map<String, Object> userData = user.getMappedData();
        userRef.document(firebaseUser.getUid()).set(userData)
                .addOnSuccessListener(task -> {
                    listener.onFinish(true);
                })
                .addOnFailureListener(task -> {
                    listener.onFinish(false);
                });
    }

    public static void isUniqueEmail(String email, OnFinishListener<Boolean> listener) {
        userRef.whereEqualTo("email", email.toLowerCase()).get()
                .addOnSuccessListener(task -> {
                    listener.onFinish(task.isEmpty());
                }).addOnFailureListener(task -> {
                    Log.d("UniqueEmail", "Error", task);
                    listener.onFinish(false);
                });
    }
    public static void logout() {
        mAuth.signOut();
        CURRENT_USER = null;
    }

}
