package com.example.comicwave.repositories;

import android.util.Log;
import android.view.View;

import com.example.comicwave.ComicDetailsActivity;
import com.example.comicwave.helpers.DateHelper;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ComicRepository {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference comicRef = db.collection("comic");
    private static HashMap<String, ComicDetails> cachedComicDetails = new HashMap<>();
    private static HashMap<String, ArrayList<Comic>> cachedScheduleData = new HashMap<>();
    private static ArrayList<Comic> mostFavoritedComics = new ArrayList<>();
    private static ArrayList<Comic> genresComics1 = new ArrayList<>();
    private static ArrayList<Comic> genresComics2 = new ArrayList<>();
    public static void getMostFavorited(OnFinishListener<ArrayList<Comic>> listener) {
        if (!mostFavoritedComics.isEmpty()) {
            listener.onFinish(mostFavoritedComics);
            return;
        }
        ArrayList<Comic> comics = new ArrayList<>();
        comicRef
                .limit(10)
                .orderBy("totalFavorites", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot document : querySnapshot) {
                Comic comic = documentToComic(document);
                if (comic != null) {
                    comics.add(comic);
                }
            }
            mostFavoritedComics.clear();
            mostFavoritedComics.addAll(comics);
            listener.onFinish(comics);

        }).addOnFailureListener(e -> {

        });
    }

    public static void getComicsByGenre(String genre, OnFinishListener<ArrayList<Comic>> listener) {
        ArrayList<Comic> comics = new ArrayList<>();
        comicRef.whereArrayContains("genres", genre)
                .limit(7)
                .get()
                .addOnSuccessListener(snapshots -> {
                   if (snapshots.isEmpty()) {
                       listener.onFinish(null);
                   }
                   for (DocumentSnapshot snapshot : snapshots) {
                       Comic comic = documentToComic(snapshot);
                       comics.add(comic);
                   }
                   Collections.shuffle(comics);
                   listener.onFinish(comics);
                })
                .addOnFailureListener(e -> {
                    listener.onFinish(null);
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
    public static void getFeaturedComic(OnFinishListener<Comic> listener) {
        String today = DateHelper.whatDayIsToday();
        Log.d("ComicRepository", today);
        comicRef.whereEqualTo("schedule", today)
                .get()
                .addOnSuccessListener(snapshots -> {
                    Random random = new Random();
                    int randomIndex = random.nextInt(snapshots.size());

                    Comic comic = documentToComic(snapshots.getDocuments().get(randomIndex));
                    listener.onFinish(comic);
                }).addOnFailureListener(e -> {
                    listener.onFinish(null);
                });
    }
    public static void getComicDetailsByID(String comicId, String userId, OnFinishListener<ComicDetails> listener) {
        DocumentReference docs = comicRef.document(comicId);
        docs.get().addOnSuccessListener(snapshot -> {
            if (snapshot != null && snapshot.exists()) {
                Comic comic = documentToComic(snapshot);
                UserRepository.checkIfComicIsFavorited(comicId, userId, isFavorited -> {
                    UserRepository.checkUserRating(comicId, userId, userRating -> {
                        UserRepository.checkIfReadListed(comicId, userId, isReadListed -> {
                            ComicDetails comicDetails = new ComicDetails(comic, isFavorited, userRating, isReadListed);
                            cachedComicDetails.put(comicId, comicDetails);
                            listener.onFinish(comicDetails);
                        });
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
                                    Episode episode = documentToEpisode(episodeSnapshot, comicId);
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
                           Episode episode = documentToEpisode(snapshot, comicId);
                           episodes.add(episode);
                           Log.d("ComicDetails", episode.getTitle());
                       }
                       listener.onFinish(episodes);
                   }
                }).addOnFailureListener(e -> {

                });
    }
    public static void getEpisodeByID(String comicId, String episodeId, OnFinishListener<Episode> listener) {
        comicRef.document(comicId).collection("episode")
                .document(episodeId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        listener.onFinish(documentToEpisode(snapshot, comicId));
                    } else {
                        listener.onFinish(null);
                    }
                }).addOnFailureListener(e -> {
                    listener.onFinish(null);
                });
    }

    public static void updateFavorites(String comicId, Integer value) {
        comicRef.document(comicId)
                .update("totalFavorites", FieldValue.increment(value))
                .addOnSuccessListener(e -> {
                    Log.d("Favorites", comicId + " " + value);
                })
                .addOnFailureListener(e -> {

                });
    }

    public static void updateTotalViews(String comicId) {
        comicRef.document(comicId)
                .update("totalViews", FieldValue.increment(1))
                .addOnCompleteListener(e -> {
                    Log.d("EpisodeContent", "totalViews Updated");
                });
    }

    public static void getNextEpisodeID(String comicId, Integer episodeNumber, OnFinishListener<String> listener) {
        comicRef.document(comicId).collection("episode")
                .whereEqualTo("episodeNumber", episodeNumber + 1)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) {
                        listener.onFinish(null);
                    }
                    else {
                        listener.onFinish(snapshots.getDocuments().get(0).getId());
                    }
                });
    }

    public static void getPrevEpisodeID(String comicId, Integer episodeNumber, OnFinishListener<String> listener) {
        comicRef.document(comicId).collection("episode")
                .whereEqualTo("episodeNumber", episodeNumber - 1)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) {
                        listener.onFinish(null);
                    }
                    else {
                        listener.onFinish(snapshots.getDocuments().get(0).getId());
                    }
                });
    }

    public static Episode documentToEpisode(DocumentSnapshot docs, String comicId) {
        Episode episode = new Episode();
        episode.setEpisodeId(docs.getId());
        episode.setEpisodeNumber(docs.getDouble("episodeNumber").intValue());
        episode.setImageUrl((String) docs.get("imageUrl"));
        episode.setReleaseDate((Timestamp) docs.get("releaseDate"));
        episode.setTitle((String) docs.get("title"));
        List<String> contents = (List<String>) docs.get("content");
        episode.setComicId(comicId);
        episode.setContent(contents != null ? new ArrayList<>(contents) : new ArrayList<>());
        return episode;
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
