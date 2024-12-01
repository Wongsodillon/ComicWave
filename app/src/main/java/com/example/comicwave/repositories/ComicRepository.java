package com.example.comicwave.repositories;

import android.util.Log;
import android.view.View;

import com.example.comicwave.ComicDetailsActivity;
import com.example.comicwave.interfaces.OnChangeListener;
import com.example.comicwave.interfaces.OnFinishListener;
import com.example.comicwave.models.Comic;
import com.example.comicwave.models.ComicDetails;
import com.example.comicwave.models.Episode;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.ViewingHistory;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ComicRepository {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference comicRef = db.collection("comic");
    private static HashMap<String, ComicDetails> cachedComicDetails = new HashMap<>();
    private static HashMap<String, ArrayList<Comic>> cachedScheduleData = new HashMap<>();
    public static void getAllComics(String userId, OnFinishListener<ArrayList<Comic>> listener) {
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

    public static void searchComics(String query, OnFinishListener<ArrayList<Comic>> listener) {
        ArrayList<Comic> comics = new ArrayList<>();
        comicRef
//                .orderBy("title")
//                .startAt(query)
//                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot snapshot: snapshots) {
                        Comic comic = documentToComic(snapshot);
                        if (comic.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            comics.add(comic);
                        }
                    }
                    listener.onFinish(comics);
                });
    }

    public static void getViewingHistory(String userId, OnFinishListener<ArrayList<ViewingHistory>> listener) {
        CollectionReference viewingHistoryRef = UserRepository.userRef.document(userId).collection("viewingHistory");
        ArrayList<ViewingHistory> histories = new ArrayList<>();
        viewingHistoryRef.whereNotEqualTo("nextEpisodeId", "")
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
            listener.onFinish(null);
        });
    }

    public static void getFavoriteComics(String userId, Integer limit, OnFinishListener<ArrayList<Favorites>> listener) {
        CollectionReference favoriteRef = UserRepository.userRef.document(userId).collection("favorites");
        ArrayList<Favorites> favorites = new ArrayList<>();
        favoriteRef
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
    }

    public static void checkIfComicIsFavorited(String comicId, String userId, OnFinishListener<Boolean> callback) {
        DocumentReference favoriteDocs = UserRepository.userRef.document(userId).collection("favorites").document(comicId);
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
        DocumentReference ratingDoc = UserRepository.userRef.document(userId).collection("ratings").document(comicId);
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

    public static void getComicDetailsByID(String comicId, String userId, OnFinishListener<ComicDetails> listener) {
        DocumentReference docs = comicRef.document(comicId);
        docs.get().addOnSuccessListener(snapshot -> {
            if (snapshot != null && snapshot.exists()) {
                Comic comic = documentToComic(snapshot);
                checkIfComicIsFavorited(comicId, userId, isFavorited -> {
                    checkUserRating(comicId, userId, userRating -> {
                        ComicDetails comicDetails = new ComicDetails(comic, isFavorited, userRating);
                        cachedComicDetails.put(comicId, comicDetails);
                        listener.onFinish(comicDetails);
                    });
                });
            } else {
                listener.onFinish(null);
            }
        }).addOnFailureListener(e -> {
            Log.e("ComicDetails", "Error fetching comic: " + comicId);
            listener.onFinish(null);
        });
    }
    public static void getWhereYouLeftOff(String comicId, String userId, OnFinishListener<Episode> listener) {
        DocumentReference docs = UserRepository.userRef.document(userId).collection("viewingHistory").document(comicId);
        docs.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String episodeId = snapshot.getString("nextEpisodeId");
                if (episodeId != null) {
                    comicRef.document(comicId).collection("episode")
                            .document(episodeId)
                            .get()
                            .addOnSuccessListener(episodeSnapshot -> {
                                if (episodeSnapshot != null) {
                                    Episode episode = documentToEpisode(episodeSnapshot);
                                    Log.d("ComicDetails", episode.getTitle());
                                    listener.onFinish(episode);
                                } else {
                                    listener.onFinish(null);
                                }
                            }).addOnFailureListener(e -> {
                                listener.onFinish(null);
                            });
                }
            } else {
                listener.onFinish(null);
            }
        }).addOnFailureListener(e -> {
            listener.onFinish(null);
        });
    }

    public static void getAllEpisodes(String comicId, OnFinishListener<ArrayList<Episode>> listener) {
        comicRef.document(comicId).collection("episode")
                .orderBy("episodeNumber")
                .get()
                .addOnSuccessListener(snapshots -> {
                   if (snapshots.isEmpty()) {
                       listener.onFinish(null);
                   } else {
                       ArrayList<Episode> episodes = new ArrayList<>();
                       for (DocumentSnapshot snapshot : snapshots) {
                           Episode episode = documentToEpisode(snapshot);
                           episodes.add(episode);
                           Log.d("ComicDetails", episode.getTitle());
                       }
                       listener.onFinish(episodes);
                   }
                }).addOnFailureListener(e -> {

                });
    }
    public static void updateRating(String comicId, double rating, OnFinishListener<Boolean> listener) {

    }
    public static Episode documentToEpisode(DocumentSnapshot docs) {
        Episode episode = new Episode();
        episode.setEpisodeId(docs.getId());
        episode.setEpisodeNumber(docs.getDouble("episodeNumber").intValue());
        episode.setImageUrl((String) docs.get("imageUrl"));
        episode.setReleaseDate((Timestamp) docs.get("releaseDate"));
        episode.setTitle((String) docs.get("title"));
        List<String> contents = (List<String>) docs.get("content");
        episode.setContent(contents != null ? new ArrayList<>(contents) : new ArrayList<>());
        return episode;
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
        if (cachedScheduleData != null && cachedScheduleData.containsKey(schedule)) {
            listener.onFinish(cachedScheduleData.get(schedule));
        }
        ArrayList<Comic> comics = new ArrayList<>();
        comicRef.whereEqualTo("schedule", schedule).get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot snapshot : snapshots) {
                        Comic comic = documentToComic(snapshot);
                        comics.add(comic);
                        Log.d("Schedule New", comic.getId());
                    }
                    cachedScheduleData.put(schedule, comics);
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
        comic.setDescription((String) docs.get("description"));
        comic.setAuthor((String) docs.get("author"));
        comic.setRating(docs.getDouble("averageRating"));
        comic.setSchedule((String) docs.get("schedule"));
        comic.setTotalFavorites(docs.getDouble("totalFavorites").intValue());
        comic.setTotalViews(docs.getDouble("totalViews").intValue());
        return comic;
    }

}
