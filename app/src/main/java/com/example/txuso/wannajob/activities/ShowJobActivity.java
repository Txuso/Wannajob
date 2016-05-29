package com.example.txuso.wannajob.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.model.classes.Bid;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import com.example.txuso.wannajob.misc.things.ImageManager;

public class ShowJobActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView image;
    TextView jobName;
    TextInputLayout jobDescription;
    TextView jobSalary;
    TextInputLayout jobDuration;
    TextInputLayout jobCategory;
    android.support.v7.widget.Toolbar toolbar;
    Bundle extras;
    Firebase mFirebaseRef;
    String jobID;
    Bitmap pic;
    int val;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_job);
        extras = getIntent().getExtras();


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.activity_show_job_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jobID = extras.getString("jobID");
        final String fromId = UserManager.getUserId(this);
        final String toId = extras.getString("toID");
        final String to = extras.getString("to");

        final Intent chat = new Intent(this, ChatActivity.class);
        /*


        //Button messageB = (Button) findViewById(R.id.send_message_button);
        Button itIsMineB = (Button) findViewById(R.id.activity_show_job_bet_button);
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");
        image = (ImageView) findViewById(R.id.activity_show_job_image);
//       image.setBackground(getDrawable(R.drawable.job));
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.activity_show_job_toolbar));
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_show_job_collapsing_toolbar);
        jobName = (TextView) findViewById(R.id.output_job_name);
        jobDescription = (TextInputLayout) findViewById(R.id.output_job_description);
        jobSalary = (TextView) findViewById(R.id.output_job_salary);
        jobDuration = (TextInputLayout) findViewById(R.id.output_job_duration);
        jobCategory = (TextInputLayout) findViewById(R.id.output_job_category);
        //collapsingToolbarLayout.setTitle(extras.getString("jobName"));
        final Firebase mFirebaseRef2 = new Firebase("https://wannajob.firebaseio.com/bid");

        itIsMineB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog myDialog = new Dialog(ShowJobActivity.this);
                myDialog.getWindow();
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                myDialog.setContentView(R.layout.bid_dialog);
                final TextView bidNumberText = (TextView)myDialog.findViewById(R.id.bid_dialog_bid_number);
                NumberPicker numberPicker = (NumberPicker)myDialog.findViewById(R.id.bid_dialog_bid_number2);
                int maxValue = Integer.parseInt(jobSalary.getText().toString());
                numberPicker.setMaxValue(maxValue);
                bidNumberText.setText("Tu Puja: " + maxValue + " €");
                numberPicker.setMinValue(1);
                numberPicker.setValue(maxValue);
                val = maxValue;
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        bidNumberText.setText("Tu Puja: " + newVal+" €");
                        val = newVal;
                    }
                });

                myDialog.findViewById(R.id.bid_dialog_bid_text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bid newBid = new Bid(val, jobID, fromId);
                        mFirebaseRef.child("bid").push().setValue(newBid);
                        myDialog.cancel();
                    }
                });

                myDialog.show();
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
                pic = ImageManager.decodeBase64(job.get("jobImage").toString());

                setPalette(pic);

                image.setImageBitmap(pic);

                jobName.setText(job.get("name").toString());
                jobDescription.getEditText().setText(job.get("description").toString().substring(1,job.get("description").toString().length()));
                jobSalary.setText(job.get("salary").toString() + "" +
                        "" +
                        "" +
                        "" +
                        " €/ 20€");
                jobDuration.getEditText().setText(job.get("jobDuration").toString());
                jobCategory.getEditText().setText(job.get("category").toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        */

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

    @Override
    public void onBackPressed() {
        super.onResume();
        finish();

    }

}

