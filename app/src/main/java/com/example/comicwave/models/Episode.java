package com.example.comicwave.models;

import com.google.firebase.Timestamp;

import java.util.Vector;

public class Episode {
    private String episodeId;
    private String title;
    private Integer episodeNumber;
    private Vector<String> content;
    private Timestamp releaseDate;
    private String thumbnailUrl;
}
