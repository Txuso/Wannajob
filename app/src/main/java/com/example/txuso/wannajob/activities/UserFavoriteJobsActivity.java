package com.example.txuso.wannajob.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.adapter.RVUserAdapter;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.example.txuso.wannajob.data.model.classes.JobListItem;
import com.example.txuso.wannajob.data.model.classes.WannajobUser;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserFavoriteJobsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    String userID;
    Firebase mFirebaseRef;
    Firebase mFirebaseRef2;

    String encounterID = "";
    String from = "";
    String to = "";
    ListView listView;
    ProgressDialog progress;

    @Bind(R.id.activity_user_favorite_swiper_refresh_layout_recycler_view)
    RecyclerView rv;

    @Bind(R.id.activity_user_favorite_jobs_swiper_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    BitmapDrawable ima;
    private List<JobListItem> jobs;
    JobListItem item;

    RVUserAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorite_jobs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        userID = UserManager.getUserId(this);
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");
        mFirebaseRef2 = new Firebase("https://wannajob.firebaseio.com/");

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fetchJobs();
                                    }
                                }
        );
    }

    private void fetchJobs() {
        swipeRefreshLayout.setRefreshing(true);

        jobs = new ArrayList<>();
        adapter = new RVUserAdapter(jobs, getApplicationContext());
        rv.setAdapter(adapter);

        mFirebaseRef.child("wannajobUsers").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final Map<String, Object> job = (Map<String, Object>) snapshot.getValue();
                HashMap<String, Boolean> userLikes = (HashMap<String, Boolean>) job.get("likes");
                if (userLikes != null) {
                    Iterator entries = userLikes.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry thisEntry = (Map.Entry) entries.next();
                        final Object key = thisEntry.getKey();

                        mFirebaseRef2.child("wannaJobs").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                                final Job job = dataSnapshot.getValue(Job.class);
                                if (key.equals(dataSnapshot.getKey())) {
                                    item = new JobListItem(dataSnapshot.getKey(), job.getName(), job.getSalary(), job.getCreatorID(), job.getDescription());
                                    item.setImageUrl(job.getJobImage());
                                    adapter = new RVUserAdapter(jobs, getApplicationContext());
                                    jobs.add(item);

                                    adapter = new RVUserAdapter(jobs, getApplicationContext());
                                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {

                                            Intent showJob = new Intent(UserFavoriteJobsActivity.this, ShowJobActivity.class);
                                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                                            showJob.putExtra("toID", job.getCreatorID());
                                            showJob.putExtra("to", job.getName());
                                            //showJob.putExtra("image", byteArray);
                                            startActivityForResult(showJob, 1);
                                        }
                                    });
                                    if (adapter != null) {
                                        ButterKnife.bind(UserFavoriteJobsActivity.this);
                                        rv.setAdapter(adapter);
                                        rv.setHasFixedSize(true);
                                        swipeRefreshLayout.setRefreshing(false);
                                        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                    }
                                }

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
                }


            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });




        mFirebaseRef.child("wannajobUsers").child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot likeDataSnapshot, String s) {

                for (final DataSnapshot like : likeDataSnapshot.child("likes").getChildren()) {
                    Toast.makeText(getApplicationContext(), like.getKey()+""  , Toast.LENGTH_LONG).show();

                }
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

        //rv.setAdapter(adapter);

}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1){
            fetchJobs();
        }
    }

}
