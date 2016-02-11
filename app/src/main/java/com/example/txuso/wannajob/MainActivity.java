package com.example.txuso.wannajob;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wannajob.classes.GPSTracker;
import wannajob.classes.Job;
import wannajob.classes.RVUserAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Firebase mFirebaseRef;
    private List<Job> jobs;
    private RecyclerView rv;

    GPSTracker gps;
    double latitude;
    double longitude;


    /**
     * Extra data from the login containing the ID of the loged user
     */
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        extras = getIntent().getExtras();
        Firebase.setAndroidContext(this);
        latitude = 0;
        longitude = 0;
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");


        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()){

                //latitude = gps.getLatitude();
            latitude = 40.4167754;
            longitude = -3.7037902;
                mFirebaseRef.child("wannajobUsers").child(extras.getString("userID")).child("latitude").setValue(latitude);
                //longitude = gps.getLongitude();
                mFirebaseRef.child("wannajobUsers").child(extras.getString("userID")).child("longitude").setValue(longitude);


        }
        else
            gps.showSettingsAlert();

        /**
         * The floating button that allows the creation of jobs
         */
        AddFloatingActionButton createJob = (AddFloatingActionButton) findViewById(R.id.createJobButton);
        createJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newJobIntent = new Intent(MainActivity.this, CreateJobActivity.class);
                newJobIntent.putExtra("userID", extras.getString("userID"));
                startActivity(newJobIntent);
            }
        });

        /**
         * The drawer layout settings
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /**
         * We set the navigation drawer that will be displayed to the user
         */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * We get the header so that the user can click on it and
         * go to his/her profiles when it is clicked
         */
        View headerview = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerview.findViewById(R.id.nav_profile);
        TextView headerName = (TextView) headerview.findViewById(R.id.user_drawer_text);
        TextView headerAge = (TextView) headerview.findViewById(R.id.published_jobs_text);
        headerName.append(new StringBuffer(extras.getString("name")));

         //   headerJobs.append(new StringBuffer(countJobs+""));
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProf = new Intent(MainActivity.this, UserProfile.class);
                startActivity(userProf);
            }
        });

        rv = (RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        //initializeData();

/*
        mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();

                double latitude2 = Double.parseDouble(job.get("latitude").toString());
                double longitude2 = Double.parseDouble(job.get("longitude").toString());
                double distance = distance(latitude, longitude, latitude2, longitude2, 'K');


                if (distance <= 50) {
                    Toast.makeText(getApplicationContext(), job.get("name").toString() + " " + Integer.parseInt(job.get("salary").toString()) + " " + job.get("jobImage").toString(), Toast.LENGTH_SHORT).show();
                  //  jobs.add(new Job(job.get("name").toString(), Integer.parseInt(job.get("salary").toString()), job.get("jobImage").toString()));

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();

                double latitude2 = Double.parseDouble(job.get("latitude").toString());
                double longitude2 = Double.parseDouble(job.get("longitude").toString());
                double distance = distance(latitude, longitude, latitude2, longitude2, 'K');

                if (distance < 50) {

                  //  jobs.add(new Job(job.get("name").toString(), Integer.parseInt(job.get("salary").toString()), job.get("jobImage").toString()));

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
        */

        initializeData();
        RVUserAdapter adapter = new RVUserAdapter(jobs);
        adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent showJob = new Intent(MainActivity.this, ShowJob.class);
                startActivity(showJob);
            }
        });
        rv.setAdapter(adapter);


    }

    /**
     * Action when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        super.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param item the navigation drawer
     * @return boolean with the clicked option
     * Here we open the following activities:
     *  - Messages activity
     *  - Discovery preferences activity
     *  - Share Wannajob activity
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_messages) {

        } else if (id == R.id.nav_options) {
            Intent discoPref = new Intent(MainActivity.this, DiscoveryPreferences.class);
            discoPref.putExtra("userID", extras.getString("userID"));
            startActivity(discoPref);
        } else if (id == R.id.nav_share) {
            Intent shareW = new Intent(MainActivity.this, ShareWannajob.class);
            startActivity(shareW);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void initializeData(){
        jobs = new ArrayList<>();
        jobs.add(new Job("Ayudar en Mudanza", 12, ""));
        jobs.add(new Job("Repartir Muestras", 30, ""));
        /*
        jobs.add(new Job("Esto y lo Otro", 24 , ""));
        jobs.add(new Job("Ayudar en Mudanza", 12, ""));
        jobs.add(new Job("Hacerme cositis", 30, ""));
        jobs.add(new Job("Pasear Perros", 24 , "")); jobs.add(new Job("Cocinarme la comida", 12, ""));
        jobs.add(new Job("Pasear Perros", 30, ""));
        jobs.add(new Job("Ayudar en Mudanza", 24 , "")); jobs.add(new Job("Putun Putun", 12, ""));
        jobs.add(new Job("Putun Putun", 30, ""));
        jobs.add(new Job("Ayudar en Mudanza", 24, ""));
*/


    }

    private void initializeAdapter(){
        RVUserAdapter adapter = new RVUserAdapter(jobs);
        rv.setAdapter(adapter);
    }

    //This method calculates the exact distance between two points and it returns the number depending on the unit
    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
