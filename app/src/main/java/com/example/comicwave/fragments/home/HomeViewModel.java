package com.example.comicwave.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comicwave.models.Comic;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.ViewingHistory;
import com.example.comicwave.repositories.ComicRepository;
import com.example.comicwave.repositories.UserRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<Comic> featuredComic = new MutableLiveData<>();
    private final MutableLiveData<List<ViewingHistory>> continueReadingComics = new MutableLiveData<>();
    private final MutableLiveData<List<Favorites>> favoriteComics = new MutableLiveData<>();
    private final MutableLiveData<List<Comic>> mostFavoritedComics = new MutableLiveData<>();
    private final MutableLiveData<List<Comic>> genre1Comics = new MutableLiveData<>();
    private final MutableLiveData<List<Comic>> genre2Comics = new MutableLiveData<>();

    public LiveData<Comic> getFeaturedComic() {
        return featuredComic;
    }

    public LiveData<List<ViewingHistory>> getContinueReadingComics() {
        return continueReadingComics;
    }

    public LiveData<List<Favorites>> getFavoriteComics() {
        return favoriteComics;
    }

    public LiveData<List<Comic>> getMostFavoritedComics() {
        return mostFavoritedComics;
    }

    public LiveData<List<Comic>> getGenre1Comics() {
        return genre1Comics;
    }

    public LiveData<List<Comic>> getGenre2Comics() {
        return genre2Comics;
    }

    // Fetch the featured comic
    public void fetchFeaturedComic() {
        ComicRepository.getFeaturedComic(comic -> featuredComic.setValue(comic));
    }

    // Fetch continue reading comics
    public void fetchContinueReadingComics(String userId) {
        UserRepository.getViewingHistory(userId, comics -> continueReadingComics.setValue(comics));
    }

    // Fetch favorite comics
    public void fetchFavoriteComics(int limit) {
        UserRepository.getFavoriteComics(limit, comics -> favoriteComics.setValue(comics));
    }

    // Fetch most favorited comics
    public void fetchMostFavoritedComics() {
        ComicRepository.getMostFavorited(mostFavoritedComics::setValue);
    }

    // Fetch comics by genre
    public void fetchComicsByGenre(String genre, boolean isFirstGenre) {
        ComicRepository.getComicsByGenre(genre, results -> {
            if (isFirstGenre) {
                genre1Comics.setValue(results);
            } else {
                genre2Comics.setValue(results);
            }
        });
    }
}
