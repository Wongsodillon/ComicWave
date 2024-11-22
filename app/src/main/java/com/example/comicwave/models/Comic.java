package com.example.comicwave.models;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Comic {
    private String id;
    private String title;
    private List<String> genres;
    private String description;
    private String author;
    private String status;
    private String imageUrl;
    private String schedule;
    private String language;
    private Integer totalViews;
    private Double rating;
    private Integer ratingCount;
    private Integer totalFavorites;
    private Vector<Episode> episodes;
    private WeeklyMetrics weeklyMetrics;
    private HashMap<String, UserInteractions> userInteractions;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Integer totalViews) {
        this.totalViews = totalViews;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Integer getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Integer totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Vector<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Vector<Episode> episodes) {
        this.episodes = episodes;
    }

    public WeeklyMetrics getWeeklyMetrics() {
        return weeklyMetrics;
    }

    public void setWeeklyMetrics(WeeklyMetrics weeklyMetrics) {
        this.weeklyMetrics = weeklyMetrics;
    }

    public HashMap<String, UserInteractions> getUserInteractions() {
        return userInteractions;
    }

    public void setUserInteractions(HashMap<String, UserInteractions> userInteractions) {
        this.userInteractions = userInteractions;
    }
}
