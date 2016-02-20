package com.example.txuso.wannajob;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.media.Image;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import wannajob.classes.CharacterCountErrorWatcher;
import wannajob.classes.ImageManager;
import wannajob.classes.Job;

public class CreateJobActivity extends AppCompatActivity {

    AlertDialog levelDialog;
    Firebase mFirebaseRef;
    Bundle extra;

    TextInputLayout jobName;
    TextInputLayout jobDescription;
    TextInputLayout jobSalary;
    TextInputLayout jobDuration;
    AppCompatButton jobCategoryB;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        extra = getIntent().getExtras();
        this.latitude = (double)extra.get("latitude");
        this.longitude = (double)extra.get("longitude");
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");

        AppCompatButton createJobB = (AppCompatButton) findViewById(R.id.createJobButton);

        jobName = (TextInputLayout) findViewById(R.id.input_job_name);
        jobDescription = (TextInputLayout) findViewById(R.id.input_job_description);
        jobSalary = (TextInputLayout) findViewById(R.id.input_job_salary);
        jobDuration = (TextInputLayout) findViewById(R.id.input_job_duration);
        jobCategoryB = (AppCompatButton) findViewById(R.id.btn_categories);

        jobDuration.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(jobDuration, 1,30));
        jobName.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(jobName, 1, 30));
        jobDescription.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(jobDescription, 1, 200));

        jobCategoryB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {" Education ", " Hard ", " Easy ", " Others"};

                // Creating and Building the Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateJobActivity.this);
                builder.setTitle("Choose the Category");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        jobCategoryB.setText(items[item]);
                        levelDialog.dismiss();
                    }
                });
                levelDialog = builder.create();
                levelDialog.show();
            }
        });

        createJobB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkConditions()) {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.job);
                    String im = ImageManager.encodeTobase64(bm);


                    Job newJob = new Job(jobName.getEditText().getText().toString(),
                            jobDescription.getEditText().getText().toString(),
                            Integer.parseInt(jobSalary.getEditText().getText().toString()),
                            jobCategoryB.getText().toString(),
                            extra.getString("userID"), new SimpleDateFormat("yyyy/MM/dd").format(new Date()), im,
                            jobDuration.getEditText().getText().toString(),latitude, longitude);
                    Toast.makeText(getApplicationContext(), jobDuration.getEditText().getText().toString(), Toast.LENGTH_SHORT).show();


                    mFirebaseRef.child("wannaJobs").push().setValue(newJob);
                    Toast.makeText(getApplicationContext(), R.string.job_created_dialog, Toast.LENGTH_SHORT).show();
                    finish();

                }
                else Toast.makeText(getApplicationContext(), R.string.data_wrong_dialog , Toast.LENGTH_SHORT).show();
            }
        });


    }

    public boolean checkConditions () {
        return (jobName.getEditText().getText().toString().trim().equals("") ||
                jobDescription.getEditText().getText().toString().trim().equals("") ||
                jobSalary.getEditText().getText().toString().trim().equals("") ||
                jobCategoryB.getText().toString().equals(R.string.job_category) ||
                jobDuration.getEditText().getText().toString().trim().equals(""));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(CreateJobActivity.this);
                builder2.setMessage("Do you want to exit without saving the changes?");
                builder2.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                builder2.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });

                builder2.show();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
