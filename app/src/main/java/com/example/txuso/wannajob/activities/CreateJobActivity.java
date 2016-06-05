package com.example.txuso.wannajob.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.txuso.wannajob.misc.things.CharacterCountErrorWatcher;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateJobActivity extends AppCompatActivity {


    String jobId;
    AlertDialog levelDialog;
    Firebase mFirebaseRef;
    Firebase newTandRef;
    private Uri mImageCaptureUri;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    Bitmap bm;
    String category = "";

    String imageURL = "";

    @Bind(R.id.input_job_name)
    TextInputLayout jobName;

    @Bind(R.id.input_job_description)
    TextInputLayout jobDescription;

    @Bind(R.id.input_job_salary)
    TextInputLayout jobSalary;

    @Bind(R.id.input_job_duration)
    TextInputLayout jobDuration;

    @Bind(R.id.btn_categories)
    AppCompatButton jobCategoryB;

    @Bind(R.id.app_bar_main_create_job_floating_action_button)
    AppCompatButton createJobB;

    @Bind(R.id.job_photo_button)
    android.support.v7.widget.AppCompatImageButton imageP;

    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        imageURL = "";
        imageP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageChooser().show();
            }
        });

        this.latitude = UserManager.getUserLatitude(this);
        this.longitude = UserManager.getUserLongitude(this);
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannaJobs/");
        newTandRef = mFirebaseRef.push();
        jobId = newTandRef.getKey();



        jobDuration.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(jobDuration, 1,30));
        jobName.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(jobName, 1, 30));
        jobDescription.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(jobDescription, 1, 200));

        jobCategoryB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = getResources().getStringArray(R.array.categories_list);

                // Creating and Building the Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateJobActivity.this);
                builder.setTitle(getString(R.string.job_category));
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        jobCategoryB.setText(items[item].substring(1,items[item].length()));
                        category = items[item];
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
                    Job newJob = new Job(jobName.getEditText().getText().toString(),
                            jobDescription.getEditText().getText().toString(),
                            Integer.parseInt(jobSalary.getEditText().getText().toString()),
                            category,
                            UserManager.getUserId(getApplicationContext()), new SimpleDateFormat("yyyy/MM/dd").format(new Date()), imageURL,
                            jobDuration.getEditText().getText().toString(),latitude, longitude);
                    Toast.makeText(getApplicationContext(), jobDuration.getEditText().getText().toString(), Toast.LENGTH_SHORT).show();
                    newTandRef.setValue(newJob);

                    Toast.makeText(getApplicationContext(), R.string.job_created_dialog, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else Toast.makeText(getApplicationContext(), R.string.data_wrong_dialog , Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkConditions () {
        return (imageURL.equals("") ||
                jobName.getEditText().getText().toString().trim().equals("") ||
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
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://project-6871569626797643888.appspot.com");

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/"+jobId +".jpg");

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
                }
            }
        });

        //imageURL = ImageManager.encodeTobase64(bitmap);
        imageP.setBackground(null);
        imageP.setImageBitmap(Bitmap.createScaledBitmap(bitmap,100, 100, false));
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
