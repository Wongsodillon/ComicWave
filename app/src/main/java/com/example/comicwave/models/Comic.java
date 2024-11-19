package com.example.comicwave.models;

import java.util.HashMap;
import java.util.Vector;

public class Comic {
    private String id;
    private String title;
    private Vector<String> genres;
    private String description;
    private String author;
    private String status;
    private String posterUrl;
    private String schedule;
    private String language;
    private Integer totalViews;
    private Double rating;
    private Integer ratingCount;
    private Integer totalFavorites;
    private Vector<Episode> episodes;
    private WeeklyMetrics weeklyMetrics;
    private HashMap<String, UserInteractions> userInteractions;

}
