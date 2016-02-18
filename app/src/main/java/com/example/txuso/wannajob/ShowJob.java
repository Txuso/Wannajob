package com.example.txuso.wannajob;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import wannajob.classes.ImageManager;
import wannajob.classes.Job;

public class ShowJob extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView image;
    TextInputLayout jobName;
    TextInputLayout jobDescription;
    TextInputLayout jobSalary;
    TextInputLayout jobDuration;
    TextInputLayout jobCategory;

    android.support.v7.widget.Toolbar toolbar;
    Bundle extras;
    Firebase mFirebaseRef;
    Job jobShow;
    String jobID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_job);
        extras = getIntent().getExtras();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jobID = extras.getString("jobID");


        Button messageB = (Button) findViewById(R.id.send_message_button);
        Button itIsMineB = (Button) findViewById(R.id.request_button);
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");
        image = (ImageView) findViewById(R.id.image);
//       image.setBackground(getDrawable(R.drawable.job));
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2));
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        jobName = (TextInputLayout) findViewById(R.id.output_job_name);
        jobDescription = (TextInputLayout) findViewById(R.id.output_job_description);
        jobSalary = (TextInputLayout) findViewById(R.id.output_job_salary);
        jobDuration = (TextInputLayout) findViewById(R.id.output_job_duration);
        jobCategory = (TextInputLayout) findViewById(R.id.output_job_category);
        //collapsingToolbarLayout.setTitle(extras.getString("jobName"));

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        setPalette();

        mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();

                if (jobID.equals(dataSnapshot.getKey())) {
                    jobShow = dataSnapshot.getValue(Job.class);
                    collapsingToolbarLayout.setTitle(jobShow.getName());
                  //  Bitmap pic = ImageManager.decodeBase64(job.get("jobImage").toString());
                   // BitmapDrawable ob = new BitmapDrawable(getResources(), pic);

                    //image.setBackground(ob);

                    jobName.getEditText().setText(jobShow.getName());
                    jobDescription.getEditText().setText(jobShow.getDescription());
                    jobSalary.getEditText().setText(jobShow.getSalary()+" €");
                    jobDuration.getEditText().setText(jobShow.getJobDuration());
                    jobCategory.getEditText().setText(jobShow.getCategory());

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

    private void setPalette() {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });

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
