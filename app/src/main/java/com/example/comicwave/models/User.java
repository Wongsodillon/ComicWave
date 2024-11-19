package com.example.comicwave.models;

import java.util.HashMap;

public class User {
    private String userId, name, email;
    public User(String userId, String email, String name) {
        this.userId = userId;
        this.name = name;
        this.email  = email;
    }

    public HashMap<String, Object> getMappedData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.name);
        map.put("email", this.email);
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

}
