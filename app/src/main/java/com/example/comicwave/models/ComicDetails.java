package com.example.comicwave.models;

import java.util.ArrayList;

public class ComicDetails {
    private Comic comic;
    private ArrayList<Episode> episodes;
    private Boolean isFavorited;
    private Double rating;
    public ComicDetails(Comic comic, ArrayList<Episode> episodes, Boolean isFavorited, Double rating) {
        this.comic = comic;
        this.episodes = episodes;
        this.isFavorited = isFavorited;
        this.rating = rating;
    }
    public ComicDetails(Comic comic, Boolean isFavorited, Double rating) {
        this.comic = comic;
        this.isFavorited = isFavorited;
        this.rating = rating;
    }

    public Comic getComic() {
        return comic;
    }

    public void setComic(Comic comic) {
        this.comic = comic;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public Boolean getFavorited() {
        return isFavorited;
    }

    public void setFavorited(Boolean favorited) {
        isFavorited = favorited;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
