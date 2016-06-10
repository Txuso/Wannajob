package com.example.txuso.wannajob.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.txuso.wannajob.data.model.classes.JobListItem;
import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.misc.things.ImageManager;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.example.txuso.wannajob.misc.things.GPSTracker;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.example.txuso.wannajob.data.adapter.RVUserAdapter;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SwipeRefreshLayout.OnRefreshListener  {
    Firebase mFirebaseRef;
    private List<JobListItem> jobs;
    private List<JobListItem> wordJobs;
    RVUserAdapter adapter;
    JobListItem item;
    GPSTracker gps;
    Bitmap pic;
    double latitude;
    double longitude;
    int categoryID;
    private MenuItem mSearchAction;
    /**
     * Extra data from the login containing the ID of the loged user
     */
    View headerView;

    @Bind(R.id.content_main_recycler_view)
    RecyclerView rv;

    @Bind(R.id.app_bar_main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_user_favorite_jobs_swiper_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.app_bar_main_create_job_floating_action_button)
    FloatingActionButton createJob;

    @Bind(R.id.activity_main_drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.activity_main_nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");

       // Intent service = new Intent(this, NotificationHandler.class);
        //service.putExtra("userID", extras.getString("userID"));
        //this.startService(service);

        latitude = UserManager.getUserLatitude(this);
        longitude = UserManager.getUserLongitude(this);

        swipeRefreshLayout.setOnRefreshListener(this);
        //we get data from the Facebook account
        if (latitude == 0 || longitude == 0) {
            gps = new GPSTracker(MainActivity.this);
            if (gps.canGetLocation()) {

                latitude = (long)gps.getLatitude();
                mFirebaseRef.child("wannajobUsers").child(UserManager.getUserId(this)).child("latitude").setValue(latitude);
                UserManager.setUserLatitude(this,latitude);

                longitude = (long)gps.getLongitude();
                mFirebaseRef.child("wannajobUsers").child(UserManager.getUserId(this)).child("longitude").setValue(longitude);
                UserManager.setUserLongitude(this,longitude);

            } else
                gps.showSettingsAlert();
        }


        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fetchJobs(latitude, longitude, UserManager.NOT_CATEGORY_FILTER);

                                    }
                                }
        );

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20) {
                    toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();

                } else if (dy < -5) {
                    toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                }
            }
        });

        /**
         * The floating button that allows the creation of jobs
         */
        createJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newJobIntent = new Intent(MainActivity.this, CreateJobActivity.class);

                startActivityForResult(newJobIntent, 3);
            }
        });

        /**
         * The drawer layout settings
         */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /**
         * We set the navigation drawer that will be displayed to the user
         */
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * We get the header so that the user can click on it and
         * go to his/her profiles when it is clicked
         */
        headerView = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.nav_header_profile);
        com.example.txuso.wannajob.misc.RoundedImageView userImage = (com.example.txuso.wannajob.misc.RoundedImageView) headerView.findViewById(R.id.nav_header_main_user_photo);
        TextView headerName = (TextView) headerView.findViewById(R.id.nav_header_main_user_name);
        headerName.append(new StringBuffer(UserManager.getUserName(getApplicationContext())));
        Picasso
                .with(getApplicationContext())
                .load(UserManager.getUserPhoto(this))
                .fit()
                .into(userImage);
         //   headerJobs.append(new StringBuffer(countJobs+""));
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProf = UserProfileActivity.showMyUserProfileIntent(MainActivity.this);
                startActivity(userProf);
            }
        });
        // rv.setLayoutManager(llm);
      //  rv.setHasFixedSize(true);
    }


    private void fetchJobs(final double latitude, final double longitude, final int categoryID) {
        swipeRefreshLayout.setRefreshing(true);

        jobs = new ArrayList<>();
        adapter = new RVUserAdapter(jobs, getApplicationContext());
        //rv.setAdapter(adapter);

        if (categoryID == UserManager.NOT_CATEGORY_FILTER) {
            mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                    final Job job = dataSnapshot.getValue(Job.class);

                    double latitude2 = job.getLatitude();
                    double longitude2 = job.getLongitude();
                    double distance = GPSTracker.distance(latitude, longitude, latitude2, longitude2, 'K');

                    if (distance <= 25) {

                        //pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.getJobImage()), 100, 100);
                        //  Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 250);
                       // Drawable ima = new BitmapDrawable(getApplicationContext().getResources(), pic);
                        item = new JobListItem(dataSnapshot.getKey(), job.getName(), job.getSalary(), job.getCreatorID(), job.getDescription());
                        item.setImageUrl(job.getJobImage());
                        item.setDistance(distance);
                        adapter = new RVUserAdapter(jobs, getApplicationContext());
                        jobs.add(item);

                    }

                    jobs = adapter.sortListByDistance();
                    adapter = new RVUserAdapter(jobs, getApplicationContext());
                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            Intent showJob = new Intent(MainActivity.this, ShowJobActivity.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            showJob.putExtra("toID", job.getCreatorID());
                            showJob.putExtra("to", job.getName());
                            //showJob.putExtra("image", byteArray);
                            startActivity(showJob);
                        }
                    });
                    rv.setAdapter(adapter);
                    rv.setHasFixedSize(true);
                    swipeRefreshLayout.setRefreshing(false);
                    rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /*
                final Job job = dataSnapshot.getValue(Job.class);


                double latitude2 = job.getLatitude();
                double longitude2 = job.getLongitude();
                double distance = GPSTracker.distance(latitude, longitude, latitude2, longitude2, 'K');


                if (distance <= 25 && !adapter.findJob(dataSnapshot.getKey())) {

                    Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.getJobImage()), 100, 100);
                    Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 300);
                    BitmapDrawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);

                    item = new JobListItem(dataSnapshot.getKey(), job.getName(), job.getSalary(), ima, job.getCreatorID(), job.getDescription());
                    adapter = new RVUserAdapter(jobs);


                    jobs.add(item);
                    rv.setAdapter(adapter);

                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent showJob = new Intent(MainActivity.this, ShowJobActivity.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            showJob.putExtra("toID", job.getCreatorID());
                            showJob.putExtra("to", job.getName());
                            startActivity(showJob);
                        }
                    });

                }

                rv.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                */

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
        } else {
            mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                    final Job job = dataSnapshot.getValue(Job.class);

                    double latitude2 = job.getLatitude();
                    double longitude2 = job.getLongitude();
                    double distance = GPSTracker.distance(latitude, longitude, latitude2, longitude2, 'K');

                    if (distance <= 25 && Character.getNumericValue(job.getCategory().charAt(0)) == categoryID) {
                        //pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.getJobImage()), 100, 100);
                        //  Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 250);
                        //Drawable ima = new BitmapDrawable(getApplicationContext().getResources(), pic);

                        item = new JobListItem(dataSnapshot.getKey(), job.getName(), job.getSalary(), job.getCreatorID(), job.getDescription());
                        item.setImageUrl(job.getJobImage());
                        item.setDistance(distance);
                        adapter = new RVUserAdapter(jobs, getApplicationContext());
                        jobs.add(item);

                    }

                    jobs = adapter.sortListByDistance();
                    adapter = new RVUserAdapter(jobs, getApplicationContext());
                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            Intent showJob = new Intent(MainActivity.this, ShowJobActivity.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            showJob.putExtra("toID", job.getCreatorID());
                            showJob.putExtra("to", job.getName());
                            //showJob.putExtra("image", byteArray);
                            startActivity(showJob);
                        }
                    });
                    rv.setAdapter(adapter);
                    rv.setHasFixedSize(true);
                    swipeRefreshLayout.setRefreshing(false);
                    rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /*
                final Job job = dataSnapshot.getValue(Job.class);


                double latitude2 = job.getLatitude();
                double longitude2 = job.getLongitude();
                double distance = GPSTracker.distance(latitude, longitude, latitude2, longitude2, 'K');


                if (distance <= 25 && !adapter.findJob(dataSnapshot.getKey())) {

                    Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.getJobImage()), 100, 100);
                    Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 300);
                    BitmapDrawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);

                    item = new JobListItem(dataSnapshot.getKey(), job.getName(), job.getSalary(), ima, job.getCreatorID(), job.getDescription());
                    adapter = new RVUserAdapter(jobs);


                    jobs.add(item);
                    rv.setAdapter(adapter);

                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent showJob = new Intent(MainActivity.this, ShowJobActivity.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            showJob.putExtra("toID", job.getCreatorID());
                            showJob.putExtra("to", job.getName());
                            startActivity(showJob);
                        }
                    });

                }

                rv.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                */

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
    }

    /**
     * Action when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        super.onResume();
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
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQueryHint(getString(R.string.search_key_word));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearchRequested();
                wordJobs = adapter.findJobsByWord(query);
                adapter = new RVUserAdapter(wordJobs, getApplicationContext());
                adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent showJob = new Intent(MainActivity.this, ShowJobActivity.class);
                        showJob.putExtra("jobID", jobs.get(position).getJobID());
                        showJob.putExtra("toID", jobs.get(position).getCreatorID());
                        showJob.putExtra("to", jobs.get(position).getName());
                        //showJob.putExtra("image", byteArray);
                        startActivity(showJob);
                    }
                });
                rv.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        menu.add(0, 0, 0, getString(R.string.sort_by_salary)).setShortcut('3', 'c');
        menu.add(1, 1, 1, getString(R.string.sort_by_distance)).setShortcut('3', 'c');
        menu.add(2, 2, 2, getString(R.string.reset_filters)).setShortcut('3', 'c');

        return super.onCreateOptionsMenu(menu);

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

            case R.id.action_show_job_map: {
                Intent showJobMap = new Intent(MainActivity.this, ShowJobMapActivity.class);
                showJobMap.putExtra("longitude", longitude);
                showJobMap.putExtra("latitude", latitude);
                startActivity(showJobMap);
            }

            case 0:{
                wordJobs = adapter.sortListBySalary();
                adapter = new RVUserAdapter(wordJobs, getApplicationContext());
                adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        Intent showJob = new Intent(MainActivity.this, ShowJobActivity.class);
                        showJob.putExtra("jobID", jobs.get(position).getJobID());
                        showJob.putExtra("toID", jobs.get(position).getCreatorID());
                        showJob.putExtra("to", jobs.get(position).getName());
                        //showJob.putExtra("image", byteArray);
                        startActivity(showJob);
                    }
                });
                rv.setAdapter(adapter);

                return true;
            }
            case 1:{
                wordJobs = adapter.sortListByDistance();
                adapter = new RVUserAdapter(wordJobs, getApplicationContext());
                adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent showJob = new Intent(MainActivity.this, ShowJobActivity.class);
                        showJob.putExtra("jobID", jobs.get(position).getJobID());
                        showJob.putExtra("toID", jobs.get(position).getCreatorID());
                        showJob.putExtra("to", jobs.get(position).getName());
                        startActivity(showJob);
                    }
                });
                rv.setAdapter(adapter);
                return true;

            }
            case 2:
                adapter = new RVUserAdapter(jobs, getApplicationContext());
                adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        Intent showJob = new Intent(MainActivity.this, ShowJobActivity.class);
                        showJob.putExtra("jobID", jobs.get(position).getJobID());
                        showJob.putExtra("toID", jobs.get(position).getCreatorID());
                        showJob.putExtra("to", jobs.get(position).getName());
                        //showJob.putExtra("image", byteArray);
                        startActivity(showJob);
                    }
                });
                rv.setAdapter(adapter);
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
     *  - Share wannajob_login_image activity
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorites) {
            Intent favorites = new Intent(MainActivity.this,UserFavoriteJobsActivity.class);
            startActivity(favorites);
        } else if (id == R.id.nav_options) {
            callDiscoveryPreferences();
        } else if (id == R.id.nav_share) {
            Intent shareW = new Intent(MainActivity.this, ShareWannajobActivity.class);
            startActivity(shareW);
        } else if (id == R.id.nav_categories) {
            Intent showCategories = new Intent(MainActivity.this, JobCategoryActivity.class);
            startActivityForResult(showCategories, 2);
        } else if (id == R.id.nav_create_job) {
            Intent createNewJob = new Intent(MainActivity.this, CreateJobActivity.class);
            startActivity(createNewJob);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onRefresh() {
        fetchJobs(latitude,longitude, UserManager.NOT_CATEGORY_FILTER);
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    public void callDiscoveryPreferences () {
        Intent discoPref = new Intent(MainActivity.this, DiscoveryPreferencesActivity.class);
        startActivityForResult(discoPref, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1){

            latitude = data.getDoubleExtra("latitude", latitude);
            longitude = data.getDoubleExtra("longitude", longitude);
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            fetchJobs(latitude, longitude, UserManager.NOT_CATEGORY_FILTER);

                                        }
                                    }
            );
        } else if (resultCode == RESULT_OK && requestCode == 2){
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
            categoryID = data.getIntExtra("categoryId", categoryID);
                                            fetchJobs(UserManager.getUserLatitude(getApplicationContext()), UserManager.getUserLongitude(getApplicationContext()), categoryID);

                                        }
                                    }
            );
        } else if (resultCode == RESULT_OK && requestCode == 3){
            fetchJobs(latitude,longitude, UserManager.NOT_CATEGORY_FILTER);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
