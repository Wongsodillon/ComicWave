package com.example.comicwave.models;

public class Notification {
    private boolean enabled;
    private boolean newFavorites;
    private boolean trendingComics;

    public Notification() {}

    public Notification(boolean enabled, boolean newFavorites, boolean trendingComics) {
        this.enabled = enabled;
        this.newFavorites = newFavorites;
        this.trendingComics = trendingComics;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isNewFavorites() {
        return newFavorites;
    }

    public void setNewFavorites(boolean newFavorites) {
        this.newFavorites = newFavorites;
    }

    public boolean isTrendingComics() {
        return trendingComics;
    }

    public void setTrendingComics(boolean trendingComics) {
        this.trendingComics = trendingComics;
    }
}
