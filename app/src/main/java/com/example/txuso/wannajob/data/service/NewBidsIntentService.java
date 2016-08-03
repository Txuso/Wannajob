package com.example.txuso.wannajob.data.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.activities.JobBidWannajobersActivity;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class NewBidsIntentService extends IntentService {

    private static final String USER_ID = "userId";
    Firebase mFirebaseRef;
    public NewBidsIntentService() {
        super("NewBidsIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startNotificationService(@NonNull Context context, String userId) {
        Intent intent = new Intent(context, NewBidsIntentService.class);
        intent.putExtra(USER_ID, userId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            handleActionBid(intent.getExtras().getString(USER_ID));
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBid(final String userId) {
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mFirebaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> wannaUser = (Map<String, Object>) dataSnapshot.getValue();
                        if (wannaUser.get("newBidMessage") != null) {
                            if (!wannaUser.get("newBidMessage").toString().equals(" ^ ^ ")) {

                                NotificationManager notificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                String value = wannaUser.get("newBidMessage").toString();
                                String[] parts = value.split("\\^");
                                String name = parts[0];
                                String fromID = parts[1];
                                String jobId = parts[2];

                                Intent launchAct = new Intent(NewBidsIntentService.this,
                                        JobBidWannajobersActivity.class);
                                launchAct.putExtra("jobID", jobId);
                                launchAct.putExtra("userId", fromID);
                                launchAct.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(
                                        NewBidsIntentService.this,
                                        0,
                                        launchAct,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                                Notification notification = new Notification.Builder(getApplicationContext())
                                        .setContentTitle("Wannajob")
                                        .setContentText("You have a new bid from " + name)
                                        .setSmallIcon(R.mipmap.wannajob_logo)
                                        .setStyle(new Notification.BigTextStyle()
                                                .bigText("You have a new bid from " + name))
                                        .setWhen(System.currentTimeMillis())
                                        .setContentIntent(pendingIntent)
                                        .build();
                                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                mFirebaseRef.child(UserManager.getUserId(getApplicationContext()))
                                        .child("newBidMessage").setValue(" ^ ^ ");
                                notificationManager.notify(9999, notification);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }, 0, 10000);



    }

}
