package com.example.txuso.wannajob.data.intent;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.activities.ChatActivity;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Txuso on 23/03/16.
 */
public class NotificationHandler extends IntentService {

    Firebase mFirebaseRef;
    Bundle extras;
    String id;
    private static final String FIREBASE_URL = "https://blazing-fire-2203.firebaseio.com/wannajobUsers";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationHandler(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        extras = intent.getExtras();
        id = extras.getString("userID");
        mFirebaseRef = new Firebase(FIREBASE_URL);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mFirebaseRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        final Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();
                        if (dataSnapshot.getKey().equals(id))
                            if (!user.get("newMessageValue").toString().equals(" ^ ^ ")) {

                                NotificationManager notiMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                String value = user.get("newMessageValue").toString();
                                String[] parts = value.split("\\^");
                                String name = parts[0];
                                String fromID = parts[1];
                                String encounterID = parts[2];

                                /*
                                Notification notification = new Notification.Builder(getApplicationContext())
                                        .setContentText("You have a new message")
                                        .setSmallIcon(R.drawable.com_facebook_button_like_icon)
                                        .setWhen(System.currentTimeMillis())
                                        .build();
                                        */

                                android.support.v4.app.NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getApplicationContext())
                                                .setSmallIcon(R.drawable.com_facebook_button_like_icon)
                                                .setContentTitle("My notification")
                                                .setContentText("Hello World!");

                                Intent launchAct = new Intent(NotificationHandler.this, ChatActivity.class);
                                launchAct.putExtra("to", name);
                                launchAct.putExtra("toID", fromID);
                                launchAct.putExtra("from", user.get("name").toString());
                                launchAct.putExtra("fromID", id);
                                launchAct.putExtra("encounterID", encounterID);
                                launchAct.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                mFirebaseRef.child(id).child("newMessage").setValue(false);
                                PendingIntent i = PendingIntent.getActivity(NotificationHandler.this, 0, launchAct, PendingIntent.FLAG_UPDATE_CURRENT);
//                                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                mBuilder.setContentIntent(i);
                                notiMan.notify(9999, mBuilder.build());


                            }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        }, 0, 10000);
    }
}
