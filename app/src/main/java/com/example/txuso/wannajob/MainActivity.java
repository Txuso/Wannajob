package com.example.txuso.wannajob;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import wannajob.classes.ImageManager;
import wannajob.classes.Job;
import wannajob.classes.RVUserAdapter;
import wannajob.classes.RoundedImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SwipeRefreshLayout.OnRefreshListener  {
    Firebase mFirebaseRef;
    private List<JobListItem> jobs;
    private RecyclerView rv;
    RVUserAdapter adapter;

    double latitude;
    double longitude;

    ProgressDialog progress;
    JobListItem item;
    private SwipeRefreshLayout swipeRefreshLayout;





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
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");

        latitude = 40.4167754;
        longitude = -3.7037902;
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        fetchJobs();
                                    }
                                }
        );

        /**
         * The floating button that allows the creation of jobs
         */
        AddFloatingActionButton createJob = (AddFloatingActionButton) findViewById(R.id.createJobButton);
        createJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newJobIntent = new Intent(MainActivity.this, CreateJobActivity.class);
                newJobIntent.putExtra("userID", extras.getString("userID"));
                newJobIntent.putExtra("latitude", latitude);
                newJobIntent.putExtra("longitude", longitude);

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




     //   progress = ProgressDialog.show(MainActivity.this, "Wait please",
       //         "Retrieving jobs around you", true);

        //initializeData();

    }

    private void fetchJobs() {
        swipeRefreshLayout.setRefreshing(true);

        jobs = new ArrayList<>();
        adapter = new RVUserAdapter(jobs);
        adapter.clearContent();
        rv.setAdapter(adapter);



        mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();


                double latitude2 = Double.parseDouble(job.get("latitude").toString());
                double longitude2 = Double.parseDouble(job.get("longitude").toString());
                double distance = distance(latitude, longitude, latitude2, longitude2, 'K');

                if (distance <= 50) {

                    Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.get("jobImage").toString()), 100, 100);
                    Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 300);
                    BitmapDrawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);

                    item = new JobListItem(dataSnapshot.getKey(), job.get("name").toString(), Integer.parseInt(job.get("salary").toString()), ima);
                    adapter = new RVUserAdapter(jobs);

                    jobs.add(item);
                    rv.setAdapter(adapter);

                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent showJob = new Intent(MainActivity.this, ShowJob.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            startActivity(showJob);
                        }
                    });

                }
                swipeRefreshLayout.setRefreshing(false);
                //     progress.dismiss();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
    }
}
