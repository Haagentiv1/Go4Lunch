package com.example.go4lunch.models;

import androidx.annotation.Nullable;

import java.util.List;

public class User {

    private String uid;
    private String username;
    private String userEmail;
    @Nullable private String urlPicture;
    private Boolean isNotification;
    private List<String> likes;

    public User(String uid, String username, String urlPicture, String restaurant, List<String> likes){ }

    public User(String uid, String username, String urlPicture){
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isNotification = false;
    }

    // --- GETTERS ---

    public String getUid(){ return uid; }
    public String getUsername(){ return username; }
    public String getUrlPicture() {return urlPicture; }
    public Boolean getIsNotification() { return isNotification; }

    // --- SETTERS ---

    public void setUid(String uid) { this.uid = uid; }
    public void setUsername(String username) { this.username = username; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setNotification(Boolean notification) { isNotification = notification; }
}
