package com.example.txuso.wannajob.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.adapter.RVUserAdapter;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.example.txuso.wannajob.data.model.classes.JobListItem;
import com.example.txuso.wannajob.misc.RoundedImageView;
import com.example.txuso.wannajob.misc.things.GPSTracker;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.txuso.wannajob.misc.things.ImageManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SwipeRefreshLayout.OnRefreshListener  {

    String userID;
    Firebase mFirebaseRef;
    double latitude;
    double longitude;
    private Uri mImageCaptureUri;
    private List<JobListItem> jobs;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    private GoogleMap mMap;

    String imageURL = "";

    @Bind(R.id.user_name)
    TextView userName;

    @Bind(R.id.user_description)
    TextView userDescription;

    @Bind(R.id.user_menu_photo)
    RoundedImageView userPhoto;

    @Bind(R.id.toolbar_user_profile)
    android.support.v7.widget.Toolbar toolbar;

    @Bind(R.id.edit_profile_textview)
    TextView editProfileTextView;

    @Bind(R.id.activity_user_profile_recycler_view)
    RecyclerView rv;

    @Bind(R.id.activity_user_profile_show_jobs_layout)
    LinearLayout myJobsLayout;

    @Bind(R.id.activity_user_profile_swiper_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    RVUserAdapter adapter;
    JobListItem item;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        userID = UserManager.getUserId(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.user_map);

        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
        latitude = UserManager.getUserLatitude(this);
        longitude = UserManager.getUserLongitude(this);
        //Bitmap pic = ImageManager.decodeBase64(UserManager.getUserPhoto(this));
        userName.setText(UserManager.getUserName(this) + " - " + UserManager.getUserAge(this));

        userDescription.setText(UserManager.getUserDescription(this));

        Picasso.with(getApplicationContext()).load(UserManager.getUserPhoto(getApplicationContext())).placeholder(R.drawable.photo_placeholder).fit().into(userPhoto);

        setUpMapIfNeeded();
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
                mFirebaseRef.child(userID).child("name").setValue(nameE.getEditableText().toString());
                mFirebaseRef.child(userID).child("description").setValue(descriptionE.getText().toString());
                mFirebaseRef.child(userID).child("age").setValue(ageE.getEditableText().toString());

                UserManager.setUserName(getApplicationContext(), nameE.getEditableText().toString());
                UserManager.setUserAge(getApplicationContext(), ageE.getEditableText().toString());
                UserManager.setUserDescription(getApplicationContext(),descriptionE.getEditableText().toString());

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
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.user_map))
                    .getMap();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("It's Me!"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));

            mMap.setMyLocationEnabled(true);

            // Check if we were successful in obtaining the map.

        }
    }

    public AlertDialog pickImageChooser () {
        final String [] items = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
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

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
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
        //String imageURL = ImageManager.getImageUrl(jobs)
        StorageReference storageRef = storage.getReferenceFromUrl("gs://project-6871569626797643888.appspot.com");

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/"+userID+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data2 = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data2);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl.toString() != null) {
                    imageURL = downloadUrl.toString();
                    UserManager.setUserPhoto(getApplicationContext(), imageURL);
                    mFirebaseRef.child(userID).child("image").setValue(UserManager.getUserPhoto(getApplicationContext()));
                }
            }
        });
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
        fetchMyJobs(latitude,longitude);
        swipeRefreshLayout.setRefreshing(false);

    }

    private void fetchMyJobs(final double latitude, final double longitude) {
        swipeRefreshLayout.setRefreshing(true);

        jobs = new ArrayList<>();
        adapter = new RVUserAdapter(jobs, getApplicationContext());
        //rv.setAdapter(adapter);
            mFirebaseRef.child("wannaJobs").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                    final Job job = dataSnapshot.getValue(Job.class);

                    double latitude2 = job.getLatitude();
                    double longitude2 = job.getLongitude();
                    double distance = GPSTracker.distance(latitude, longitude, latitude2, longitude2, 'K');

                    if (job.getCreatorID().equals(userID)) {

                        item = new JobListItem(dataSnapshot.getKey(), job.getName(), job.getSalary(), job.getCreatorID(), job.getDescription());
                        item.setDistance(distance);
                        adapter = new RVUserAdapter(jobs, getApplicationContext());
                        jobs.add(item);

                    }

                    jobs = adapter.sortListByDistance();
                    adapter = new RVUserAdapter(jobs, getApplicationContext());
                    adapter.SetOnItemClickListener(new RVUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            Intent showJob = new Intent(UserProfileActivity.this, ShowJobActivity.class);
                            showJob.putExtra("jobID", jobs.get(position).getJobID());
                            showJob.putExtra("toID", job.getCreatorID());
                            showJob.putExtra("to", job.getName());
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

    @OnClick(R.id.activity_user_profile_show_jobs_layout)
    public void showMyJobs() {
        fetchMyJobs(latitude, longitude);
    }
}

