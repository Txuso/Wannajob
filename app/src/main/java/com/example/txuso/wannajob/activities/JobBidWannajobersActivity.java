package com.example.txuso.wannajob.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.misc.things.UserManager;

public class JobBidWannajobersActivity extends AppCompatActivity {


    public static Intent newIntent (@NonNull Context context, String jobId) {
        Intent myUserIntent = new Intent(context, JobBidWannajobersActivity.class);
        myUserIntent.putExtra("jobID", jobId);
        return myUserIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_bid_wannajobers);
    }
}
