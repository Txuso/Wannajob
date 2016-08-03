package com.example.txuso.wannajob.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.adapter.RVUserAdapter;
import com.example.txuso.wannajob.data.model.classes.WannajobBidUser;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JobBidWannajobersActivity extends AppCompatActivity {

    Firebase mFirebaseBidRef;
    Firebase mFirebaseUserRef;

    RVUserAdapter adapter;
    private List<WannajobBidUser> users;

    WannajobBidUser item;
    String jobId;


    @Bind(R.id.activity_job_bid_wannajobers_recycler_view)
    RecyclerView rv;


    @Bind(R.id.activity_job_bid_swiper_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;


    public static Intent newIntent (@NonNull Context context, String jobId) {
        Intent myUserIntent = new Intent(context, JobBidWannajobersActivity.class);
        myUserIntent.putExtra("jobID", jobId);
        return myUserIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_bid_wannajobers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFirebaseBidRef = new Firebase("https://wannajob.firebaseio.com/bid");
        mFirebaseUserRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
        jobId = getIntent().getExtras().getString("jobID");
        ButterKnife.bind(this);

        users = new ArrayList<>();
        adapter = new RVUserAdapter(users, getApplicationContext());
        rv.setAdapter(adapter);
        mFirebaseBidRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> bid = (Map<String, Object>) dataSnapshot.getValue();
                if (bid.get("jobId").equals(jobId)) {
                    mFirebaseUserRef.child(bid.get("userId").toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> wannaUser = (Map<String, Object>) dataSnapshot.getValue();
                            fetchUserInfo(wannaUser, dataSnapshot.getKey(), (long)bid.get("number"));
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
    public void fetchUserInfo(final Map<String, Object> wannaUser, final String userId, long bidNumber) {
        swipeRefreshLayout.setRefreshing(true);

        if (wannaUser.get("rating") != null) {
            double ratingDouble = Double.parseDouble(wannaUser.get("rating").toString());

            item = new WannajobBidUser(wannaUser.get("name").toString(), wannaUser.get("description").toString(), wannaUser.get("image").toString(), ratingDouble, bidNumber, userId, jobId);
            users.add(item);
            adapter = new RVUserAdapter(users, JobBidWannajobersActivity.this);

            adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent showUser = UserProfileActivity.showOtherUserProfileIntent(getApplicationContext(), userId);
                    startActivity(showUser);
                }
            });
            if (adapter != null) {
                ButterKnife.bind(JobBidWannajobersActivity.this);
                rv.setAdapter(adapter);
                rv.setHasFixedSize(true);
                swipeRefreshLayout.setRefreshing(false);
                rv.setLayoutManager(new LinearLayoutManager(this));

            }
        }
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
