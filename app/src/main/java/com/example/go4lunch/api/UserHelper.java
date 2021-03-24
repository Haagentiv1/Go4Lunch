
package com.example.go4lunch.api;

import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";


    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }


    // --- CREATE ---


    public static Task<Void> createUser(String uid, String username, String urlPicture, List<String> likes, Timestamp userCreationTimestamp, String chosenRestaurant , String chosenRestaurantName, Timestamp chosenRestaurantTimeStamp, String chosenRestaurantAddress){
        User userToCreate = new User(uid,username, urlPicture, likes, userCreationTimestamp, chosenRestaurant,chosenRestaurantName,chosenRestaurantTimeStamp,chosenRestaurantAddress);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Query getAllUsers() {
        return UserHelper.getUsersCollection();
    }

    public static Query getAllUsersByRestaurant(String restaurantID){
        return UserHelper.getUsersCollection().whereEqualTo("chosenRestaurant",restaurantID);
    }

    public static Task<QuerySnapshot> getUsers(){
        return UserHelper.getUsersCollection().get();
    }

    public static Query getUsersWithReservedRestaurant(){
        return UserHelper.getUsersCollection().whereNotEqualTo("chosenRestaurant",null);
    }



   

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }



    // --- UPDATE ---
    public static Task<Void> updateChosenRestaurantAddress(String uid, String chosenRestaurantAddress){
       return UserHelper.getUsersCollection().document(uid).update("chosenRestaurantAddress",chosenRestaurantAddress);
    }

    public static Task<Void> updateChosenRestaurant(String uid,String chosenRestaurant){
        return UserHelper.getUsersCollection().document(uid).update("chosenRestaurant",chosenRestaurant);
    }

    public static Task<Void> updateChosenRestaurantName(String uid, String restaurantName){
        return UserHelper.getUsersCollection().document(uid).update("chosenRestaurantName", restaurantName);
    }

    public static Task<Void> updateUserLikes(String uid,String restaurantId){
        return UserHelper.getUsersCollection().document(uid).update("likes", FieldValue.arrayUnion(restaurantId));
    }

    public static Task<Void> updateChosenRestaurantTimestamp(String uid, Timestamp chosenRestaurantTimestamp){
        return UserHelper.getUsersCollection().document(uid).update("chosenRestaurantTimestamp", chosenRestaurantTimestamp);
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

    public static Task<Void> deleteLike(String uid, String restaurantId){
       return UserHelper.getUsersCollection().document(uid).update("likes",FieldValue.arrayRemove(restaurantId));
    }


}
