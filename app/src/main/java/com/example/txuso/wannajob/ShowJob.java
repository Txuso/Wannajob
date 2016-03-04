package com.example.txuso.wannajob;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import wannajob.chat.WannajobEncounter;
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
        final String fromId = extras.getString("fromID");
        final String toId = extras.getString("toID");
        final String to = extras.getString("to");

        final Intent chat = new Intent(this, ChatActivity.class);



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
        final Firebase mFirebaseRef2 = new Firebase("https://wannajob.firebaseio.com/wannajobEncounter");


        messageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //we put some extra data on the next activity
                chat.putExtra("from", to);
                chat.putExtra("to", to);
                chat.putExtra("fromID", fromId);
                chat.putExtra("toID", toId);
                //we get the current time
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
                //we create the TandemEncounter instance
                WannajobEncounter enc = new WannajobEncounter(fromId, toId, to, timeStamp, false);
                //we create a new instance of TandemEncounter to get later the ID
                Firebase newTandRef = mFirebaseRef2.push();
                //we store the new TandemEncounter on the Firebase root
                newTandRef.setValue(enc);
                //we get the created TandemEncounter's ID
                String encounterID = newTandRef.getKey();
                //we put the extra data on the chat activity that is about to be launched
                chat.putExtra("encounterID", encounterID);
                //we start the new activity
                startActivity(chat);

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
