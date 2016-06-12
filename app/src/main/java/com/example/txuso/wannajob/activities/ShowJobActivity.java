package com.example.txuso.wannajob.activities;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.model.classes.Bid;

import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.txuso.wannajob.misc.things.ImageManager;
import com.google.android.gms.common.data.DataBufferObserver;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowJobActivity extends AppCompatActivity  {
    Bundle extras;
    Firebase mFirebaseRef;
    Firebase mFirebaseRefBids;
    String jobID;
    Bitmap pic;
    String creatorID = "";
    int val;
    long bidNumber = 0;
    long viewNumber = 0;
    private GoogleMap mMap;
    boolean isMine = false;

    @Bind(R.id.activity_show_job_my_bid)
    LinearLayout bidLayout;

    @Bind(R.id.activity_show_job_my_bid_text)
    TextView bidText;

    @Bind(R.id.activity_show_job_app_bar_layout)
    android.support.design.widget.AppBarLayout appBarLayout;

    @Bind(R.id.activity_show_job_collapsing_toolbar)
    android.support.design.widget.CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.activity_show_job_image)
    ImageView jobImage;

    @Bind(R.id.activity_show_job_toolbar)
    android.support.v7.widget.Toolbar toolbar;

    @Bind(R.id.activity_show_job_scroll)
    android.support.v4.widget.NestedScrollView nestedScrollView;

    @Bind(R.id.activity_show_job_like_button)
    LinearLayout likeButton;

    @Bind(R.id.activity_show_job_job_name)
    TextView jobName;

    @Bind(R.id.activity_show_job_money_layout)
    LinearLayout jobMoneyLayout;

    @Bind(R.id.activity_show_job_money)
    TextView jobMoney;

    @Bind(R.id.activity_show_job_views_layout)
    LinearLayout jobViewsLayout;

    @Bind(R.id.activity_show_job_views)
    TextView jobViews;

    @Bind(R.id.activity_show_job_bid_layout)
    LinearLayout jobBidsLayout;

    @Bind(R.id.activity_show_job_bids)
    TextView jobBids;

    @Bind(R.id.activity_show_job_description)
    TextView jobDescription;

    @Bind(R.id.activity_show_job_user_photo)
    com.example.txuso.wannajob.misc.RoundedImageView userPhoto;

    @Bind(R.id.activity_show_job_user_rating)
    RatingBar userRating;

    @Bind(R.id.activity_show_job_user_name)
    TextView userName;

    @Bind(R.id.activity_show_job_bet_button)
    Button betButton;

    String fromId;

    StorageReference storageRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_job);
        ButterKnife.bind(this);
        extras = getIntent().getExtras();

        storageRef = storage.getReferenceFromUrl("gs://project-6871569626797643888.appspot.com/images");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_show_job_user_map);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fromId = UserManager.getUserId(this);

        jobID = extras.getString("jobID");
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");
        mFirebaseRefBids = new Firebase("https://wannajob.firebaseio.com/");
        checkIfBid();

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        mFirebaseRef.child("wannaJobs").child(jobID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();
                collapsingToolbarLayout.setTitle(job.get("name").toString());
                creatorID = job.get("creatorID").toString();
                if (creatorID.equals(fromId)) {
                    likeButton.setVisibility(View.GONE);
                    isMine = true;
                    betButton.setBackgroundResource(R.color.colorAccent);
                    betButton.setText(R.string.show_job_activity_edit_job);
                }
                if (!UserManager.isUserfavoriteJob(getApplicationContext(), jobID)) {
                    likeButton.setBackgroundResource(R.drawable.rounded_corner_button_layout_gray);
                }
                // Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.get("jobImage").toString()),100,100);

                //  Picasso.with(getApplicationContext()).load()
                // BitmapDrawable ob = new BitmapDrawable(getResources(), pic);

                if (job.get("bidNumber")  != null)
                    bidNumber = (long) job.get("bidNumber");
                else
                    bidNumber = 0;
                if (job.get("viewNumber") != null)
                    viewNumber = (long) job.get("viewNumber");
                else
                    viewNumber = 0;

               // Bitmap image =ImageManager.fromURLToBitmap(getApplicationContext(), job.get("jobImage").toString());
//                setPalette(image);

//                Picasso.with(getApplicationContext()).load(job.get("jobImage").toString()).fit().placeholder(R.drawable.photo_placeholder).into(jobImage);
                int color = getResources().getColor(R.color.colorGray);
                collapsingToolbarLayout.setStatusBarScrimColor(color);

                jobName.setText(job.get("name").toString());
                jobDescription.setText(job.get("description").toString());
                jobBids.setText(bidNumber + "");

                jobViews.setText(viewNumber + "");
                jobMoney.setText(job.get("salary").toString() + "€");
                setUpMapIfNeeded((double)job.get("latitude"), (double)job.get("longitude"));
                Picasso
                        .with(getApplicationContext())
                        .load(job.get("jobImage").toString())
                        .placeholder(R.drawable.photo_placeholder)
                        .fit()
                        .into(jobImage);

                //jobDuration.getEditText().setText(job.get("jobDuration").toString());
                //jobCategory.getEditText().setText(job.get("category").toString());
                mFirebaseRef.child("wannajobUsers").child(job.get("creatorID").toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();
                        userName.setText(user.get("name").toString());
                        Picasso.with(getApplicationContext()).load(user.get("image").toString()).centerCrop().placeholder(R.drawable.photo_placeholder).fit().into(userPhoto);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showUserProf = UserProfileActivity.showOtherUserProfileIntent(ShowJobActivity.this, creatorID);
                startActivity(showUserProf);
            }
        });

        betButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMine) {
                    Intent createJobActivity = new Intent(ShowJobActivity.this, CreateJobActivity.class);
                    startActivity(createJobActivity);
                } else {
                    final Dialog myDialog = new Dialog(ShowJobActivity.this);
                    myDialog.getWindow();
                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    myDialog.setContentView(R.layout.bid_dialog);
                    final TextView bidNumberText = (TextView)myDialog.findViewById(R.id.bid_dialog_bid_number);
                    NumberPicker numberPicker = (NumberPicker)myDialog.findViewById(R.id.bid_dialog_bid_number2);
                    int maxValue = Integer.parseInt(jobMoney.getText().subSequence(0,jobMoney.getText().length()-1).toString());
                    numberPicker.setMaxValue(maxValue);
                    bidNumberText.setText(getString(R.string.your_bid) + maxValue + " €");
                    numberPicker.setMinValue(1);
                    numberPicker.setValue(maxValue);

                    val = maxValue;
                    numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            bidNumberText.setText(getString(R.string.your_bid) + newVal+" €");
                            val = newVal;
                        }
                    });

                    myDialog.findViewById(R.id.bid_dialog_bid_text).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bid newBid = new Bid(val, jobID, fromId);
                            mFirebaseRef.child("bid").push().setValue(newBid);
                            bidNumber++;
                            mFirebaseRef.child("wannaJobs").child(jobID).child("bidNumber").setValue(bidNumber);
                            bidLayout.setVisibility(View.VISIBLE);
                            bidText.setText(getString(R.string.your_bid) + val + " €");
                            myDialog.cancel();
                        }
                    });

                    myDialog.show();
                }

            }
        });


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
        updateViews();
    }

    @OnClick(R.id.activity_show_job_like_button)
    public void onLikePressed() {
        if (UserManager.isUserfavoriteJob(getApplicationContext(), jobID)) {
            UserManager.deleteUserFavoriteJob(this, jobID);
            mFirebaseRef.child("wannajobUsers").child(fromId).child("likes").child(jobID).removeValue();
            likeButton.setBackgroundResource(R.drawable.rounded_corner_button_layout_gray);
            Toast.makeText(getApplicationContext(), getString(R.string.job_added_to_favorite), Toast.LENGTH_SHORT).show();


        } else {
            Map<String, Object> likes = new HashMap<>();
            likes.put(jobID, true);
            mFirebaseRef.child("wannajobUsers").child(fromId).child("likes").updateChildren(likes);
            UserManager.addUserFavoriteJob(this, jobID);
            likeButton.setBackgroundResource(R.drawable.rounded_corner_button_layout_red);

        }

    }

    public void updateViews() {
        mFirebaseRef.child("wannaJobs").child(jobID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();
                long viewN = 0;
                if (job.get("viewNumber") != null)
                    viewN = (long) job.get("viewNumber");
                else
                    viewN = 0;
                viewN++;
                mFirebaseRef.child("wannaJobs").child(jobID).child("viewNumber").setValue(viewN);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setUpMapIfNeeded(double latitude, double longitude) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_show_job_user_map))
                    .getMap();
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("It's Me!"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location location) {
                        //We draw a marker in our current position



                    }
                });

            }
        }
    }

    public void checkIfBid() {

        mFirebaseRefBids.child("bid").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> bid = (Map<String, Object>) dataSnapshot.getValue();
                if (bid.get("userId").toString().equals(fromId) && bid.get("jobId").toString().equals(jobID)) {
                    bidLayout.setVisibility(View.VISIBLE);
                    bidText.setText(getString(R.string.your_bid) + bid.get("number").toString() + " €");
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

