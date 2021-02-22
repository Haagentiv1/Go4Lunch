package com.example.go4lunch.api;

import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";


    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }


    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, List<String> likes, Timestamp userCreationTimestamp, String chosenRestaurant , Timestamp chosenRestaurantTimeStamp){
        User userToCreate = new User(uid,username, urlPicture, likes, userCreationTimestamp, chosenRestaurant,chosenRestaurantTimeStamp);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Query getAllUsers() {
        return UserHelper.getUsersCollection();
    }

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateChosenRestaurant(String uid,String chosenRestaurant){
        return UserHelper.getUsersCollection().document(uid).update("chosen_restaurant",chosenRestaurant);
    }

    public static Task<Void> updateChosenRestaurantTimestamp(String uid, String chosenRestaurantTimestamp){
        return UserHelper.getUsersCollection().document(uid).update("chosen_restaurant_timestamp", chosenRestaurantTimestamp);
    }

    public static Task<Void> updateUsername(String username, String uid){
        return UserHelper.getUsersCollection().document(uid).update("username",username);
    }

    public static Task<Void> updateNotification(String uid, Boolean notification){
        return UserHelper.getUsersCollection().document(uid).update("notification",notification);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid){
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
