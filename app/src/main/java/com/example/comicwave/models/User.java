package com.example.comicwave.models;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class User {
    private String userId;
    private String name;
    private String email;
    private Timestamp lastUpdated;
    private Notification notifications;

    public User(String userId, String email, String name) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.lastUpdated = Timestamp.now();
        this.notifications = new Notification(false, false, false);
    }

    public HashMap<String, Object> getMappedData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.name);
        map.put("email", this.email);
        map.put("notification", new HashMap<String, Object>() {{
            put("enabled", notifications.isEnabled());
            put("trendingComics", notifications.isTrendingComics());
            put("newFavorites", notifications.isNewFavorites());
        }});
        map.put("lastUpdated", this.lastUpdated);
        return map;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Notification getNotifications() {
        return notifications;
    }

    public void setNotifications(Notification notifications) {
        this.notifications = notifications;
    }
}
