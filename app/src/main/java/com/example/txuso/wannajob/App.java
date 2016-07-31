package com.example.txuso.wannajob;

import android.app.Application;
import android.graphics.Bitmap;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.squareup.picasso.Picasso;


/**
 * Created by Daniel Redondo on 01/02/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        if (BuildConfig.DEBUG) {
            // Logging

        }

    }
}
