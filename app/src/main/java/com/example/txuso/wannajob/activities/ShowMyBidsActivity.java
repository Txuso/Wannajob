package com.example.txuso.wannajob.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.adapter.RVJobAdapter;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.example.txuso.wannajob.data.model.classes.JobListItem;
import com.example.txuso.wannajob.misc.things.GPSTracker;
import com.example.txuso.wannajob.misc.things.ImageManager;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowMyBidsActivity extends AppCompatActivity {

    Firebase mFirebaseRef;
    Firebase mFirebaseJobsRef;
    private List<JobListItem> jobs;
    JobListItem item;
    RVJobAdapter adapter;

    @Bind(R.id.activity_show_my_job_recycler_view)
    RecyclerView rv;

    @Bind(R.id.activity_show_my_job_swiper_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_bids);
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com");
        mFirebaseJobsRef = new Firebase("https://wannajob.firebaseio.com/");
        jobs = new ArrayList<>();
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mFirebaseRef.child("bid").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> bid = (Map<String, Object>) dataSnapshot.getValue();

                if (bid.get("userId").toString().equals(UserManager.getUserId(getApplicationContext()))) {
                    mFirebaseJobsRef.child("wannaJobs").child(bid.get("jobId").toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();
                            long longSalary = (long)job.get("salary");
                            int salary = (int)longSalary;
                            item = new JobListItem(dataSnapshot.getKey(), job.get("name").toString(), salary, job.get("creatorID").toString(), job.get("description").toString());
                            item.setImageUrl(job.get("jobImage").toString());
                            jobs.add(item);
                            adapter = new RVJobAdapter(jobs, getApplicationContext());
                            adapter.SetOnItemClickListener(new RVJobAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    Intent showJob = new Intent(ShowMyBidsActivity.this, ShowJobActivity.class);
                                    showJob.putExtra("jobID", jobs.get(position).getJobID());
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
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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