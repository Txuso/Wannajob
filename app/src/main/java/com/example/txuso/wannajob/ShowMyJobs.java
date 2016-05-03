package com.example.txuso.wannajob;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wannajob.classes.ImageManager;
import wannajob.classes.Job;
import wannajob.classes.RVUserAdapter;
import wannajob.classes.RoundedImageView;

public class ShowMyJobs extends AppCompatActivity {

    private List<JobListItem> jobs;
    Firebase mFirebaseRef;
    RVUserAdapter adapter;
    Bundle extras;
    String userID;
    JobListItem item;
    private RecyclerView rv;
    LinearLayoutManager llm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_jobs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");

        extras = getIntent().getExtras();
        userID = extras.getString("userID");

        jobs = new ArrayList<>();
        adapter = new RVUserAdapter(jobs);

        mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();

                if (job.get("creatorID").toString().equals(userID)) {
                    Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.get("jobImage").toString()), 100, 100);
                    Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 250);
                    Drawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);

                    item = new JobListItem(dataSnapshot.getKey(), job.get("name").toString(), Integer.parseInt(job.get("salary").toString()), ima, job.get("creatorID").toString(), job.get("description").toString());
                    jobs.add(item);
                    adapter = new RVUserAdapter(jobs);
                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            Intent showJob = new Intent(ShowMyJobs.this, ShowJobVacancies.class);
                            showJob.putExtra("jobID", dataSnapshot.getKey());
                            showJob.putExtra("fromID", extras.getString("userID"));
                            showJob.putExtra("toID", job.get("creatorID").toString());
                            showJob.putExtra("to", job.get("name").toString());
                            startActivity(showJob);
                        }
                    });

                    //                  jobs.add(job);
                }
                rv.setAdapter(adapter);

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
        rv = (RecyclerView)findViewById(R.id.my_jobs_rv);
        llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

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
}