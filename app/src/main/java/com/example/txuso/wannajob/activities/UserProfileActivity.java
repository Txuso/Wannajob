package com.example.txuso.wannajob.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.adapter.RVJobAdapter;
import com.example.txuso.wannajob.data.adapter.RVUserOpinionAdapter;
import com.example.txuso.wannajob.data.firebase.FirebaseStorageService;
import com.example.txuso.wannajob.data.firebase.UserFirebaseService;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.example.txuso.wannajob.data.model.classes.JobListItem;
import com.example.txuso.wannajob.data.model.classes.UserOpinionListItem;
import com.example.txuso.wannajob.misc.RoundedImageView;
import com.example.txuso.wannajob.misc.things.GPSTracker;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SwipeRefreshLayout.OnRefreshListener  {

    String userID;
    double latitude;
    double longitude;
    private Uri mImageCaptureUri;
    private List<JobListItem> jobs;
    private List<UserOpinionListItem> opinions;
    int numberJobsCounter;
    int numberOpinionsCounter;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    Firebase mFirebaseRef;
    Firebase mUserJobsRef;
    Firebase mUserOpinionRef;


    UserFirebaseService uService;
    FirebaseStorageService sService = new FirebaseStorageService(UserProfileActivity.this);


    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    private SupportMapFragment mMap;

    String imageURL = "";

    @Bind(R.id.user_name)
    TextView userName;

    @Bind(R.id.user_description)
    TextView userDescription;

    @Bind(R.id.user_menu_photo)
    RoundedImageView userPhoto;

    @Bind(R.id.edit_profile_textview)
    TextView editProfileTextView;

    @Bind(R.id.activity_user_profile_recycler_view)
    RecyclerView rv;

    @Bind(R.id.activity_user_profile_show_jobs)
    LinearLayout myJobsLayout;

    @Bind(R.id.activity_user_profile_show_jobs_number)
    TextView jobNumber;

    @Bind(R.id.activity_user_profile_show_opinions_number)
    TextView opinionNumber;

    @Bind(R.id.activity_user_profile_show_opinions)
    LinearLayout myOpinionsLayout;

    @Bind(R.id.activity_user_profile_swiper_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    RVJobAdapter adapter;
    RVUserOpinionAdapter rvUserOpinionAdapter;
    JobListItem item;

    private static final String EXTRA_USER_ID = "EXTRA_USER_ID";

    public static Intent showMyUserProfileIntent (@NonNull Context context) {
        Intent myUserIntent = new Intent(context, UserProfileActivity.class);
        myUserIntent.putExtra(EXTRA_USER_ID, UserManager.getUserId(context));
        return  myUserIntent;
    }

    public static Intent showOtherUserProfileIntent (@NonNull Context context, String userId) {
        Intent myUserIntent = new Intent(context, UserProfileActivity.class);
        myUserIntent.putExtra(EXTRA_USER_ID, userId);
        return  myUserIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uService = new UserFirebaseService(UserProfileActivity.this);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        numberJobsCounter = 0;
        numberOpinionsCounter = 0;
        userID = getIntent().getExtras().getString(EXTRA_USER_ID);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
        mUserJobsRef = new Firebase("https://wannajob.firebaseio.com/");
        mUserOpinionRef = new Firebase("https://wannajob.firebaseio.com/userOpinion");
        if (!userID.equals(UserManager.getUserId(this))) {
            editProfileTextView.setVisibility(View.GONE);
            mFirebaseRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> wannaUser = (Map<String, Object>) dataSnapshot.getValue();
                    latitude = (Double) wannaUser.get("latitude");
                    longitude = (Double) wannaUser.get("longitude");
                    userName.setText(wannaUser.get("name").toString() + " - " + wannaUser.get("age").toString());
                    userDescription.setText(wannaUser.get("description").toString());
                    Picasso.with(getApplicationContext()).
                            load(wannaUser.get("image").toString()).
                            placeholder(R.drawable.person_placeholder).
                            fit().
                            into(userPhoto);
                    setUpMapIfNeeded();

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        } else {

            latitude = UserManager.getUserLatitude(this);
            longitude = UserManager.getUserLongitude(this);
            //Bitmap pic = ImageManager.decodeBase64(UserManager.getUserPhoto(this));
            userName.setText(UserManager.getUserName(this) + " - " + UserManager.getUserAge(this));

            userDescription.setText(UserManager.getUserDescription(this));

            Picasso.with(getApplicationContext()).
                    load(UserManager.getUserPhoto(getApplicationContext())).
                    placeholder(R.drawable.person_placeholder).
                    fit().
                    into(userPhoto);
            setUpMapIfNeeded();


        }


        mUserJobsRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();
                if (job.get("creatorID").toString().equals(userID)) {
                    numberJobsCounter++;
                    jobNumber.setText(numberJobsCounter + "");
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

        mUserOpinionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();
                if (job.get("userID").toString().equals(userID)) {
                    numberOpinionsCounter++;
                    opinionNumber.setText(numberOpinionsCounter + "");
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


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.user_map);
        fetchMyJobs(latitude, longitude);

            /*

    public void createEvent () {
        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 3, 12, 7, 30);// set(int year, int month, int day, int hourOfDay, int minute)
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 3, 12, 8, 30);
        endMillis = endTime.getTimeInMillis();

        TimeZone tz = TimeZone.getDefault();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Jazzercise");
        values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,  tz.getID());
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());


        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        startActivity(intent);
    }

*/
    }


    @OnClick(R.id.edit_profile_textview)
    public void editProfile() {

        final Dialog myDialog = new Dialog(UserProfileActivity.this);
        myDialog.getWindow();
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.edit_profile_dialog);
        final EditText nameE = (EditText) myDialog.findViewById(R.id.edit_profile_dialog_name);
        final EditText descriptionE = (EditText) myDialog.findViewById(R.id.edit_profile_dialog_description);
        final EditText ageE = (EditText) myDialog.findViewById(R.id.edit_profile_dialog_age);
        LinearLayout saveChanges = (LinearLayout) myDialog.findViewById(R.id.edit_profile_dialog_save_changes_layout);

        nameE.setText(UserManager.getUserName(getApplicationContext()));
        descriptionE.setText(UserManager.getUserDescription(getApplicationContext()));
        ageE.setText(UserManager.getUserAge(getApplicationContext()));

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uService.updateUserData(userID, nameE.getEditableText().toString(), descriptionE.getText().toString(), ageE.getEditableText().toString(), getApplicationContext());
                userName.setText(UserManager.getUserName(getApplicationContext()) + " - " + UserManager.getUserAge(getApplicationContext()));
                userDescription.setText(UserManager.getUserDescription(getApplicationContext()));
                myDialog.cancel();
            }
        });


        myDialog.setCancelable(true);
        myDialog.show();
    }

    @OnClick(R.id.user_menu_photo)
    public void changePhoto() {
        pickImageChooser().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            case R.id.action_logout: {

            } break;
            case 0:{
                UserManager.setIsUserLogged(getApplicationContext(), false);
                Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.user_map));
            mMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("It's Me!"));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                }
            });
            // Check if we were successful in obtaining the map.

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Logout").setShortcut('3', 'c');
        return super.onCreateOptionsMenu(menu);

    }

    public AlertDialog pickImageChooser () {
        final String [] items = new String [] {getString(R.string.from_camera), getString(R.string.from_sdcard)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.select_image));
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, getString(R.string.dialog_complete_action_using)), PICK_FROM_FILE);
                }
            }
        } );

        return builder.create();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        Bitmap bitmap   = null;
        String path     = "";

        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            path = getRealPathFromURI(mImageCaptureUri); //from Gallery

            if (path == null)
                path = mImageCaptureUri.getPath(); //from File Manager

            if (path != null)
                bitmap  = BitmapFactory.decodeFile(path);
        } else {
            path    = mImageCaptureUri.getPath();
            bitmap  = BitmapFactory.decodeFile(path);
        }
        sService.uploadUserOrTaskImage(userID, bitmap, getApplicationContext());
        userPhoto.setImageBitmap(bitmap);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri, proj, null, null,null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onRefresh() {
     //   fetchMyJobs(latitude,longitude);
        swipeRefreshLayout.setRefreshing(false);

    }

    private void fetchMyJobs(final double latitude, final double longitude) {
        swipeRefreshLayout.setRefreshing(true);

        jobs = new ArrayList<>();
        adapter = new RVJobAdapter(jobs, getApplicationContext());
        //rv.setAdapter(adapter);
        mUserJobsRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                    final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();

                    double latitude2 = Double.parseDouble(job.get("latitude").toString());
                    double longitude2 = Double.parseDouble(job.get("longitude").toString());
                    double distance = GPSTracker.distance(latitude, longitude, latitude2, longitude2, 'K');

                    if (job.get("creatorID").toString().equals(userID)) {

                        item = new JobListItem(dataSnapshot.getKey(),
                                job.get("name").toString(),
                                Integer.parseInt(job.get("salary").toString()),
                                        job.get("creatorID").toString(),
                                        job.get("description").toString());
                        item.setImageUrl(job.get("jobImage").toString());
                        item.setDistance(distance);
                        adapter = new RVJobAdapter(jobs, getApplicationContext());
                        jobs.add(item);

                    }

                    jobs = adapter.sortListByDistance();
                    adapter = new RVJobAdapter(jobs, getApplicationContext());
                    adapter.SetOnItemClickListener(new RVJobAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            Intent showJob = new Intent(UserProfileActivity.this, ShowJobActivity.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            showJob.putExtra("toID", job.get("creatorID").toString());
                            showJob.putExtra("to", job.get("name").toString());
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


    @OnClick(R.id.activity_user_profile_show_jobs)
    public void showMyJobs() {
       fetchMyJobs(latitude, longitude);
    }

    @OnClick(R.id.activity_user_profile_show_opinions)
    public void showMyOpinions() {
        opinions = new ArrayList<>();
        //
        //rv.setAdapter(adapter);
        mUserOpinionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> opinion = (Map<String, Object>) dataSnapshot.getValue();
                if (opinion.get("userID").toString().equals(UserManager.getUserId(UserProfileActivity.this))) {
                    UserOpinionListItem userOpinion = new UserOpinionListItem(opinion.get("name").toString(),
                            opinion.get("jobName").toString(),
                            opinion.get("opinion").toString(),
                            Integer.parseInt(opinion.get("stars").toString()),
                            opinion.get("imageUrl").toString(),
                            opinion.get("fromId").toString());
                    opinions.add(userOpinion);

                }
                rvUserOpinionAdapter = new RVUserOpinionAdapter(opinions, getApplicationContext());
                rvUserOpinionAdapter.SetOnItemClickListener(new RVUserOpinionAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent showJob = showOtherUserProfileIntent(getApplicationContext(), opinions.get(position).getUserID());
                        //showJob.putExtra("image", byteArray);
                        startActivity(showJob);
                    }
                });
                rv.setAdapter(rvUserOpinionAdapter);
                rv.setHasFixedSize(true);
                swipeRefreshLayout.setRefreshing(false);
                rv.setLayoutManager(new LinearLayoutManager(UserProfileActivity.this));

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

