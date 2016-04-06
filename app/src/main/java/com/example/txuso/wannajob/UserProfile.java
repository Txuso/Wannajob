package com.example.txuso.wannajob;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import wannajob.classes.ImageManager;
import wannajob.classes.WannajobUser;

public class UserProfile extends AppCompatActivity {

    Bundle extras;
    String userID;
    Firebase firebaseRef;
    ImageView image;
    TextInputLayout userName;
    TextInputLayout userAge;

    CollapsingToolbarLayout collapsingToolbarLayout;
    android.support.v7.widget.Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        extras = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            /*
        userID = extras.getString("userID");
        firebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_user);
        image = (ImageView) findViewById(R.id.user_image);
        userName = (TextInputLayout) findViewById(R.id.user_name);
        userAge = (TextInputLayout) findViewById(R.id.user_age);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        firebaseRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();

                collapsingToolbarLayout.setTitle(user.get("name").toString());
                // Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.get("jobImage").toString()),100,100);

                //  Picasso.with(getApplicationContext()).load()
                // BitmapDrawable ob = new BitmapDrawable(getResources(), pic);
                Bitmap pic = ImageManager.decodeBase64(user.get("image").toString());
                Drawable d = new BitmapDrawable(getResources(), pic);
                setPalette(pic);
                image.setBackground(d);

                userName.getEditText().setText(user.get("name").toString());
                userAge.getEditText().setText(user.get("age").toString());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        createEvent();



    }

    private void setPalette(Bitmap bd) {

        Palette.from(bd).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });

    }



    public void createEvent () {
        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 3, 12, 7, 30);// set(int year, int month, int day, int hourOfDay, int minute)
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 3, 12, 8, 30);
        endMillis = endTime.getTimeInMillis();

        TimeZone tz = TimeZone.getDefault();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Jazzercise");
        values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,  tz.getID());
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());


        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        startActivity(intent);
    }

*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }

    }

}
