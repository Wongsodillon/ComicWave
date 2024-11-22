package com.example.comicwave.models;

import com.google.firebase.Timestamp;

public class FavoriteEpisodes {
    private String comicTitle;
    private Timestamp createdAt;
    private Integer episodeNumber;
    private String imageUrl;
    private String title;

    public FavoriteEpisodes() {}
    public FavoriteEpisodes(String comicTitle, Integer episodeNumber, String imageUrl, String title) {
        this.comicTitle = comicTitle;
        this.createdAt = Timestamp.now();
        this.episodeNumber = episodeNumber;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getComicTitle() {
        return comicTitle;
    }
    public void setComicTitle(String comicTitle) {
        this.comicTitle = comicTitle;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public Integer getEpisodeNumber() {
        return episodeNumber;
    }
    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
