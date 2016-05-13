package com.example.txuso.wannajob.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.misc.RoundedImageView;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.txuso.wannajob.misc.things.ImageManager;

public class UserProfileActivity extends AppCompatActivity {

    String userID;
    Firebase firebaseRef;
    double latitude;
    double longitude;
    private Uri mImageCaptureUri;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;


    private GoogleMap mMap;

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

        firebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
        latitude = UserManager.getUserLatitude(this);
        longitude = UserManager.getUserLongitude(this);
        Bitmap pic = ImageManager.decodeBase64(UserManager.getUserPhoto(this));
        userName.setText(UserManager.getUserName(this) + " - " + UserManager.getUserAge(this));
        userDescription.setText(UserManager.getUserDescription(this));
        BitmapDrawable ob = new BitmapDrawable(getResources(), pic);
        userPhoto.setImageDrawable(ob);

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
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.edit_profile_dialog);
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

        userPhoto.setImageBitmap(bitmap);

        String imageFile = ImageManager.encodeTobase64(bitmap);
        firebaseRef.child(userID).child("image").setValue(imageFile);

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
}

