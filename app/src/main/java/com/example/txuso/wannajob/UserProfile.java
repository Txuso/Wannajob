package com.example.txuso.wannajob;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wannajob.classes.ImageManager;
import wannajob.classes.WannajobUser;

public class UserProfile extends AppCompatActivity {

    Bundle extras;
    String userID;
    Firebase firebaseRef;
    double latitude;
    double longitude;
    private static int RESULT_LOAD_IMAGE = 2;

    private GoogleMap mMap;

    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_description)
    TextView userDescription;
    @Bind(R.id.user_menu_photo)
    com.example.txuso.wannajob.RoundedImageView userPhoto;
    @Bind(R.id.toolbar_user_profile)
    android.support.v7.widget.Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        extras = getIntent().getExtras();
        ButterKnife.bind(this);
        userID = extras.getString("userID");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.user_map);

        firebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");


        firebaseRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();

                // Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.get("jobImage").toString()),100,100);

                //  Picasso.with(getApplicationContext()).load()
                // BitmapDrawable ob = new BitmapDrawable(getResources(), pic);
                latitude = (double) user.get("latitude");
                longitude = (double) user.get("longitude");

                Bitmap pic = ImageManager.decodeBase64(user.get("image").toString());
                userName.setText(user.get("name").toString() + " - " + user.get("age"));
                userDescription.setText(user.get("description").toString());
                BitmapDrawable ob = new BitmapDrawable(getResources(), pic);
                userPhoto.setImageDrawable(ob);



                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("It's Me!"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        setUpMapIfNeeded();
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

    @OnClick(R.id.user_menu_photo)
    public void changePhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColum = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColum, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColum[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap selectedIm = BitmapFactory.decodeFile(picturePath);

            try {

                Bitmap image = ImageManager.decodeUri(this, selectedImage, 375);
                userPhoto.setImageBitmap(image);

                String imageFile = ImageManager.encodeTobase64(selectedIm);
                firebaseRef.child(userID).child("image").setValue(imageFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.user_map))
                    .getMap();
            mMap.setMyLocationEnabled(true);

            // Check if we were successful in obtaining the map.

        }
    }
}

