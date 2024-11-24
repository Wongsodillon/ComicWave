package com.example.comicwave.models;

import com.google.firebase.Timestamp;

public class ViewingHistory {
    private String comicId;
    private String comicTitle;
    private String imageUrl;
    private String lastViewedEpisodeId;
    private String nextEpisodeId;
    private Timestamp lastViewedTimestamp;

    public ViewingHistory() {}

    public ViewingHistory(String comicId, String comicTitle, String imageUrl, String lastViewedEpisodeId, String nextEpisodeId) {
        this.comicId = comicId;
        this.comicTitle = comicTitle;
        this.imageUrl = imageUrl;
        this.lastViewedEpisodeId = lastViewedEpisodeId;
        this.nextEpisodeId = nextEpisodeId;
        this.lastViewedTimestamp = Timestamp.now();
    }

    public String getLastViewedEpisodeId() {
        return lastViewedEpisodeId;
    }

    public void setLastViewedEpisodeId(String lastViewedEpisodeId) {
        this.lastViewedEpisodeId = lastViewedEpisodeId;
    }

    public String getNextEpisodeId() {
        return nextEpisodeId;
    }

    public void setNextEpisodeId(String nextEpisodeId) {
        this.nextEpisodeId = nextEpisodeId;
    }

    public Timestamp getLastViewedTimestamp() {
        return lastViewedTimestamp;
    }

    public void setLastViewedTimestamp(Timestamp lastViewedTimestamp) {
        this.lastViewedTimestamp = lastViewedTimestamp;
    }

    public String getComicId() {
        return comicId;
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public String getComicTitle() {
        return comicTitle;
    }

    public void setComicTitle(String comicTitle) {
        this.comicTitle = comicTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
