package com.example.txuso.wannajob;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.io.ByteArrayOutputStream;
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
    JobListItem item;
    GPSTracker gps;
    Bitmap pic;
    double latitude;
    double longitude;

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

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        //we get data from the Facebook account
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()){

            latitude = gps.getLatitude();
            mFirebaseRef.child("wannajobUsers").child(extras.getString("userID")).child("latitude").setValue(latitude);
            longitude = gps.getLongitude();
            mFirebaseRef.child("wannajobUsers").child(extras.getString("userID")).child("longitude").setValue(longitude);
        }
        else
            gps.showSettingsAlert();


        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fetchJobs(latitude, longitude);

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
                newJobIntent.putExtra("longitude",longitude);

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
                userProf.putExtra("userID", extras.getString("userID"));
                startActivity(userProf);
            }
        });

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

    }

    private void fetchJobs(final double latitude, final double longitude) {
        swipeRefreshLayout.setRefreshing(true);

        jobs = new ArrayList<>();
        adapter = new RVUserAdapter(jobs);
        adapter.clearContent();
        rv.setAdapter(adapter);


        mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final Job job = dataSnapshot.getValue(Job.class);


                double latitude2 = job.getLatitude();
                double longitude2 = job.getLongitude();
                double distance = distance(latitude, longitude, latitude2, longitude2, 'K');


                if (distance <= 50) {


                    pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.getJobImage()), 100, 100);
                    Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 250);
                    Drawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);

                    item = new JobListItem(dataSnapshot.getKey(), job.getName(), job.getSalary(), ima);
                    item.setDistance(distance);

                    adapter = new RVUserAdapter(jobs);

                    jobs.add(item);

                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            pic.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                            byte[] byteArray = stream.toByteArray();

                            Intent showJob = new Intent(MainActivity.this, ShowJob.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            showJob.putExtra("fromID", extras.getString("userID"));
                            showJob.putExtra("toID", job.getCreatorID());
                            showJob.putExtra("to", job.getName());
                            //showJob.putExtra("image", byteArray);
                            startActivity(showJob);
                        }
                    });

                }

                jobs = adapter.sortListByDistance();
                adapter = new RVUserAdapter(jobs);
                rv.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Job job = dataSnapshot.getValue(Job.class);


                double latitude2 = job.getLatitude();
                double longitude2 = job.getLongitude();
                double distance = distance(latitude, longitude, latitude2, longitude2, 'K');


                if (distance <= 50 && !adapter.findJob(dataSnapshot.getKey())) {

                    Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.getJobImage()), 100, 100);
                    Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 300);
                    BitmapDrawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);

                    item = new JobListItem(dataSnapshot.getKey(), job.getName(), job.getSalary(), ima);
                    adapter = new RVUserAdapter(jobs);

                    jobs.add(item);
                    rv.setAdapter(adapter);

                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent showJob = new Intent(MainActivity.this, ShowJob.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            showJob.putExtra("fromID", extras.getString("userID"));
                            showJob.putExtra("toID", job.getCreatorID());
                            showJob.putExtra("to", job.getName());
                            startActivity(showJob);
                        }
                    });

                }

                swipeRefreshLayout.setRefreshing(false);

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
        menu.add(0, 0, 0, getString(R.string.sort_by_salary)).setShortcut('3', 'c');
        menu.add(1, 1, 1, getString(R.string.sort_by_distance)).setShortcut('3', 'c');

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home: {

                return true;
            }
            case 0:{
                jobs = adapter.sortListBySalary();
                adapter = new RVUserAdapter(jobs);
                rv.setAdapter(adapter);

                return true;
            }
            case 1:{
                jobs = adapter.sortListByDistance();
                adapter = new RVUserAdapter(jobs);
                rv.setAdapter(adapter);
            }


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
            Intent messages = new Intent(MainActivity.this,UserMessages.class);
            messages.putExtra("userID", extras.getString("userID"));
            startActivity(messages);

        } else if (id == R.id.nav_options) {
            callDiscoveryPreferences();
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


        swipeRefreshLayout.setRefreshing(false);

    }

    public void callDiscoveryPreferences () {
        Intent discoPref = new Intent(MainActivity.this, DiscoveryPreferences.class);
        discoPref.putExtra("userID", extras.getString("userID"));
        startActivityForResult(discoPref, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1){

            latitude = data.getDoubleExtra("latitude", latitude);
            longitude = data.getDoubleExtra("longitude", longitude);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            fetchJobs(latitude, longitude);

                                        }
                                    }
            );
        }


    }

}
