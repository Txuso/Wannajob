package com.example.txuso.wannajob.data.firebase;

import android.content.Context;

import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.Firebase;

/**
 * Created by Txuso on 10/06/16.
 */
public class UserFirebaseService {

    Firebase mFirebaseRef;

    public UserFirebaseService() {
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
    }

    public void updateUserData(String userID, String name, String description, String age, Context context) {
        mFirebaseRef.child(userID).child("name").setValue(name);
        mFirebaseRef.child(userID).child("description").setValue(description);
        mFirebaseRef.child(userID).child("age").setValue(age);

        UserManager.setUserName(context, name);
        UserManager.setUserAge(context, age);
        UserManager.setUserDescription(context,description);
    }

    public void updateLatitudeLongitude (String userID, Double latitude, Double longitude) {
        mFirebaseRef.child(userID).child("latitude").setValue(latitude);
        mFirebaseRef.child(userID).child("longitude").setValue(longitude);

    }

    public void updateLatitudeLongitudeDistance (String userID, Double latitude, Double longitude, int distance, Context context) {
        updateLatitudeLongitude(userID, latitude, longitude);
        mFirebaseRef.child(userID).child("distance").setValue(distance);
        UserManager.setUserLatitude(context, latitude);
        UserManager.setUserLongitude(context, longitude);

    }

}
