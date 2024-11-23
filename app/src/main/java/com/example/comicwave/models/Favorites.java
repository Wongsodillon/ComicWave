package com.example.comicwave.models;

import com.google.firebase.Timestamp;

public class Favorites {
    private String comicId;
    private String title;
    private String imageUrl;
    private Timestamp timestamp;

    public Favorites() {}

    public Favorites(String comicId, String title, String imageUrl, Timestamp timestamp) {
        this.comicId = comicId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getComicId() {
        return comicId;
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
