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

}
