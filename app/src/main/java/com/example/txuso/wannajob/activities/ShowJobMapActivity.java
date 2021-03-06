package com.example.txuso.wannajob.activities;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.txuso.wannajob.misc.things.GPSTracker;
import com.example.txuso.wannajob.data.model.classes.Job;

import java.util.HashMap;
import java.util.Map;

public class ShowJobMapActivity extends AppCompatActivity {

    private SupportMapFragment mMap;
    Geocoder gc;
    Bundle extras;
    Double longitude;
    Double latitude;
    GoogleMap map;
    Firebase mFirebaseRef;
    HashMap<Marker, String> jobMarkerId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_job_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        setUpMapIfNeeded();

        extras = getIntent().getExtras();
        longitude = extras.getDouble("longitude");
        latitude = extras.getDouble("latitude");

        // the Geocoder will help us to get locations information such us name...
        gc = new Geocoder(getApplicationContext());
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");
        mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();

                double latitude2 = Double.parseDouble(job.get("latitude").toString());
                double longitude2 = Double.parseDouble(job.get("longitude").toString());
                double distance = GPSTracker.distance(latitude, longitude, latitude2, longitude2, 'K');

                if (distance <= 50 && job.get("selectedUserID").toString().equals("")) {
                    Marker m = map.addMarker(new MarkerOptions().position(new LatLng(latitude2,
                            longitude2)).title(job.get("name").toString()));
                    jobMarkerId.put(m, dataSnapshot.getKey());

                }
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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    // Check if we were successful in obtaining the map.
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent showJob = new Intent(ShowJobMapActivity.this, ShowJobActivity.class);
                            showJob.putExtra("jobID", jobMarkerId.get(marker));
                            startActivity(showJob);
                            return false;
                        }
                    });
                }
            });





        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
