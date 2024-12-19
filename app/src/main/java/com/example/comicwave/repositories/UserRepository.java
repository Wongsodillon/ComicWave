package com.example.comicwave.repositories;

import android.util.Log;
import android.widget.Toast;

import com.example.comicwave.SignUpActivity;
import com.example.comicwave.adapters.ReadListAdapter;
import com.example.comicwave.database.ComicWaveDB;
import com.example.comicwave.interfaces.OnChangeListener;
import com.example.comicwave.interfaces.OnFinishListener;
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.ReadList;
import com.example.comicwave.models.User;
import com.example.comicwave.models.ViewingHistory;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.Time;

import java.util.ArrayList;
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

    public static void validateOldPassword(String password, OnFinishListener<Boolean> callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onFinish(false);
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onFinish(true);
                    } else {
                        callback.onFinish(false);
                    }
                });
    }

    public static void updatePassword(String newPassword, OnFinishListener<Boolean> listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnSuccessListener(e -> {
                        listener.onFinish(true);
                    })
                    .addOnFailureListener(e -> {
                        listener.onFinish(false);
                    });
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
    public static void getFavoriteComics(OnFinishListener<ArrayList<Favorites>> listener) {
        CollectionReference favoriteRef = userRef.document(mAuth.getCurrentUser().getUid()).collection("favorites");
        ArrayList<Favorites> favorites = new ArrayList<>();
        favoriteRef.get().addOnSuccessListener(snapshots -> {
            if (snapshots.isEmpty()) {
                listener.onFinish(null);
                return;
            }
            for (DocumentSnapshot snapshot : snapshots) {
                Favorites favorite = documentToFavorites(snapshot);
                favorites.add(favorite);
                Log.d("Favorite New", favorite.getComicId());
            }
            listener.onFinish(favorites);
        }).addOnFailureListener(e -> {
            listener.onFinish(null);
        });
    }

    public static Task<QuerySnapshot> getFavoriteComics(Integer limit, OnFinishListener<ArrayList<Favorites>> listener) {
        CollectionReference favoriteRef = userRef.document(mAuth.getCurrentUser().getUid()).collection("favorites");
        ArrayList<Favorites> favorites = new ArrayList<>();
        Task<QuerySnapshot> task = favoriteRef
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get().addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot snapshot : snapshots) {
                        Favorites favorite = documentToFavorites(snapshot);
                        favorites.add(favorite);
                        Log.d("Favorite New", favorite.getComicId());
                    }
                    listener.onFinish(favorites);
                }).addOnFailureListener(e -> {
                    listener.onFinish(null);
                });
        return task;
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

    public static void getReadList(OnFinishListener<ArrayList<ReadList>> listener) {
        CollectionReference readListsRef = userRef.document(mAuth.getCurrentUser().getUid()).collection("readlists");
        ArrayList<ReadList> readLists = new ArrayList<>();
        readListsRef.get().addOnSuccessListener(snapshots -> {
            for (DocumentSnapshot snapshot : snapshots) {
                readLists.add(documentToReadList(snapshot));
            }
            listener.onFinish(readLists);
        }).addOnFailureListener(e -> {
            listener.onFinish(null);
        });

    }

    public static void addToReadList(String comicId, String title, String imageUrl, OnFinishListener<Boolean> listener) {
        DocumentReference docs = userRef.document(mAuth.getCurrentUser().getUid());
        ReadList readList = new ReadList(comicId, title, imageUrl);
        HashMap<String, Object> readListData = readList.getMappedData();
        docs.collection("readlists")
                .document(comicId)
                .set(readListData)
                .addOnCompleteListener(e -> {
                   if (e.isSuccessful()) {
                       listener.onFinish(true);
                   } else {
                       listener.onFinish(false);
                   }
                });
    }

    public static void removeFromReadList(String comicId, OnFinishListener<Boolean> listener) {
        DocumentReference docs = userRef.document(mAuth.getCurrentUser().getUid());
        docs.collection("readlists").document(comicId)
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
    public static Task<QuerySnapshot> getViewingHistory(String userId, OnFinishListener<ArrayList<ViewingHistory>> listener) {
        CollectionReference viewingHistoryRef = userRef.document(userId).collection("viewingHistory");
        ArrayList<ViewingHistory> histories = new ArrayList<>();
        Task<QuerySnapshot> task = viewingHistoryRef
                .orderBy("lastViewedTimestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) {
                        Log.d("View History", "No viewing history found for user: " + userId);
                        listener.onFinish(histories);
                    } else {
                        for (DocumentSnapshot snapshot : snapshots) {
                            ViewingHistory vh = documentToViewingHistory(snapshot);
                            Log.d("View History", vh.getComicTitle());
                            histories.add(vh);
                        }
                        listener.onFinish(histories);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("View History", "Error fetching data", e);
                });
        return task;
    }

    public static void updateViewingHistory(String comicId, String lastViewedEpisodeId, OnFinishListener<Boolean> callback) {
        CollectionReference viewingHistoryRef = userRef.document(mAuth.getCurrentUser().getUid()).collection("viewingHistory");
        HashMap<String, Object> viewingHistoryData = new HashMap<>();

        ComicRepository.comicRef.document(comicId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Comic comic = ComicRepository.documentToComic(snapshot);
                Log.d("ViewingHistory", "Comic title fetched: " + comic.getTitle());

                viewingHistoryData.put("comicTitle", comic.getTitle());
                viewingHistoryData.put("imageUrl", comic.getImageUrl());

                viewingHistoryData.put("lastViewedTimestamp", Timestamp.now());
                viewingHistoryData.put("lastViewedEpisodeId", lastViewedEpisodeId);
                viewingHistoryData.put("nextEpisodeId", lastViewedEpisodeId);

                viewingHistoryRef.document(comicId).set(viewingHistoryData)
                        .addOnSuccessListener(e -> {
                            callback.onFinish(true);
                        })
                        .addOnFailureListener(e -> {
                            callback.onFinish(false);
                        });
            } else {
                Log.w("ViewingHistory", "Comic document not found.");
                callback.onFinish(false);
            }
        }).addOnFailureListener(e -> {
            Log.e("ViewingHistory", "Failed to fetch comic details: " + e.getMessage());
            callback.onFinish(false);
        });
    }


    public static void checkIfComicIsFavorited(String comicId, String userId, OnFinishListener<Boolean> callback) {
        DocumentReference favoriteDocs = userRef.document(userId).collection("favorites").document(comicId);
        favoriteDocs.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                callback.onFinish(true);
            }
            else {
                callback.onFinish(false);
            }
        }).addOnFailureListener(e -> {
            Log.e("ComicDetails", "Error checking favorite status: " + comicId);
            callback.onFinish(false);
        });
    }
    public static void checkUserRating(String comicId, String userId, OnFinishListener<Double> callback) {
        DocumentReference ratingDoc = userRef.document(userId).collection("ratings").document(comicId);
        ratingDoc.get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Double rating = snapshot.getDouble("rating");
                        callback.onFinish(rating != null ? rating : 0.0);
                    } else {
                        callback.onFinish(0.0);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ComicRepository", "Error fetching rating for comic: " + comicId, e);
                    callback.onFinish(0.0);
                });
    }

    public static void checkIfReadListed(String comicId, String userId, OnFinishListener<Boolean> callback) {
        DocumentReference favoriteDocs = userRef.document(userId).collection("readlists").document(comicId);
        favoriteDocs.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                callback.onFinish(true);
            }
            else {
                callback.onFinish(false);
            }
        }).addOnFailureListener(e -> {
            Log.e("ComicDetails", "Error checking favorite status: " + comicId);
            callback.onFinish(false);
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
    public static ReadList documentToReadList(DocumentSnapshot docs) {
        return new ReadList(
                docs.getId(),
                (String) docs.get("title"),
                (String) docs.get("imageUrl"),
                (Timestamp) docs.get("createdAt")
        );
    }
    public static ViewingHistory documentToViewingHistory(DocumentSnapshot docs) {
        ViewingHistory viewingHistory = new ViewingHistory();
        viewingHistory.setLastViewedTimestamp(docs.getTimestamp("lastViewedTimestamp"));
        viewingHistory.setComicId(docs.getId());
        viewingHistory.setImageUrl((String) docs.get("imageUrl"));
        viewingHistory.setComicTitle((String) docs.get("comicTitle"));
        viewingHistory.setLastViewedEpisodeId((String) docs.get("lastViewedEpisodeId"));
        viewingHistory.setNextEpisodeId((String) docs.get("nextEpisodeId"));
        return viewingHistory;
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
