package com.example.txuso.wannajob.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.txuso.wannajob.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import com.example.txuso.wannajob.misc.things.ImageManager;

public class ShowJobVacanciesActivity extends AppCompatActivity {
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
    String jobID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_jobs_vacancies);
        extras = getIntent().getExtras();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jobID = extras.getString("jobID");
        final String fromId = extras.getString("fromID");
        final String toId = extras.getString("toID");
        final String to = extras.getString("to");

        final Intent chat = new Intent(this, ChatActivity.class);



        Button cancelB = (Button) findViewById(R.id.cancel_button);
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");
        image = (ImageView) findViewById(R.id.image2);
//       image.setBackground(getDrawable(R.drawable.job));
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar3));
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar2);
        jobName = (TextInputLayout) findViewById(R.id.output_job_name2);
        jobDescription = (TextInputLayout) findViewById(R.id.output_job_description2);
        jobSalary = (TextInputLayout) findViewById(R.id.output_job_salary2);
        jobDuration = (TextInputLayout) findViewById(R.id.output_job_duration2);
        jobCategory = (TextInputLayout) findViewById(R.id.output_job_category2);
        //collapsingToolbarLayout.setTitle(extras.getString("jobName"));
        final Firebase mFirebaseRef2 = new Firebase("https://wannajob.firebaseio.com/wannaJobs");


        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder2 = new AlertDialog.Builder(ShowJobVacanciesActivity.this);
                builder2.setMessage(getString(R.string.want_to_cancel_job_dialog));
                builder2.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseRef2.child(jobID).removeValue();
                        finish();
                    }
                });
                builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }

                });

                builder2.show();
            }
        });

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        mFirebaseRef.child("wannaJobs").child(jobID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();
                collapsingToolbarLayout.setTitle(job.get("name").toString());
               // Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.get("jobImage").toString()),100,100);

                //  Picasso.with(getApplicationContext()).load()
               // BitmapDrawable ob = new BitmapDrawable(getResources(), pic);
                Bitmap pic = ImageManager.decodeBase64(job.get("jobImage").toString());
                Drawable d = new BitmapDrawable(getResources(), pic);

                setPalette(pic);

                image.setBackground(d);

                jobName.getEditText().setText(job.get("name").toString());
                jobDescription.getEditText().setText(job.get("description").toString());
                jobSalary.getEditText().setText(job.get("salary").toString());
                jobDuration.getEditText().setText(job.get("jobDuration").toString());
                jobCategory.getEditText().setText(job.get("category").toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void setPalette(Bitmap bd) {

        Palette.from(bd).generate(new Palette.PaletteAsyncListener() {
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
