package com.example.txuso.wannajob;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;

import wannajob.classes.GPSTracker;
import wannajob.classes.ImageManager;
import wannajob.classes.Job;
import wannajob.classes.RVUserAdapter;
import wannajob.classes.RoundedImageView;

public class ShowJobMap extends FragmentActivity {

    private GoogleMap mMap;
    Geocoder gc;
    Bundle extras;
    Double longitude;
    Double latitude;
    Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_job_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        extras = getIntent().getExtras();
        longitude = extras.getDouble("longitude");
        latitude = extras.getDouble("latitude");

        // the Geocoder will help us to get locations information such us name...
        gc = new Geocoder(getApplicationContext());
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");
        mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final Job job = dataSnapshot.getValue(Job.class);

                double latitude2 = job.getLatitude();
                double longitude2 = job.getLongitude();
                double distance = GPSTracker.distance(latitude, longitude, latitude2, longitude2, 'K');


                if (distance <= 50)
                    mMap.addMarker(new MarkerOptions().position(new LatLng(job.getLatitude(), job.getLongitude())).title(job.getName()));


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TODO
//                adapter.removeJob(dataSnapshot.getKey());
                //              rv.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("It's Me!"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location location) {
                        //We draw a marker in our current position



                    }
                });

            }
        }
    }




}
