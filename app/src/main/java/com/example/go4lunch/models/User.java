package com.example.go4lunch.models;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.util.List;

public class User {

    private String uid;
    private String username;
    @Nullable private String urlPicture;
    private List<String> likes;
    private Timestamp userCreationTimestamp;
    private String chosenRestaurant;
    private Timestamp chosenRestaurantTimestamp;
    public User(){ }




    public User(String uid, String username, String urlPicture, List<String> likes, Timestamp userCreationTimestamp, String chosenRestaurant, Timestamp chosenRestaurantTimestamp){
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.likes = likes;
        this.userCreationTimestamp = userCreationTimestamp;
        this.chosenRestaurant = chosenRestaurant;
        this.userCreationTimestamp = chosenRestaurantTimestamp;
    }

    // --- GETTERS ---

    public String getUid(){ return uid; }
    public String getUsername(){ return username; }
    public String getUrlPicture() {return urlPicture; }
    public List<String> getLikes() { return likes; }
    public Timestamp getUserCreationTimestamp() { return userCreationTimestamp; }
    public String getChosenRestaurant() { return chosenRestaurant; }
    public Timestamp getChosenRestaurantTimestamp() { return chosenRestaurantTimestamp; }


    // --- SETTERS ---

    public void setUid(String uid) { this.uid = uid; }
    public void setUsername(String username) { this.username = username; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setLikes(List<String> likes) { this.likes = likes; }
    public void setUserCreationTimestamp(Timestamp userCreationTimestamp) { this.userCreationTimestamp = userCreationTimestamp; }
    public void setChosenRestaurant(String chosenRestaurant) { this.chosenRestaurant = chosenRestaurant; }
    public void setChosenRestaurantTimestamp(Timestamp chosenRestaurantTimestamp) { this.chosenRestaurantTimestamp = chosenRestaurantTimestamp; }

}
