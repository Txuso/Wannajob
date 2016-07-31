package com.example.txuso.wannajob.activities;

import android.app.Activity;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.example.txuso.wannajob.misc.things.DialogUtils;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditJobActivity extends AppCompatActivity {
    Firebase mFirebaseRef;
    StorageReference storageRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String jobID;
    String imageURL = "";
    private Uri mImageCaptureUri;
    Job newJob;
    String category = "";
    AlertDialog levelDialog;
    Bitmap bitmap;


    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    @Bind(R.id.activity_edit_job_photo)
    ImageView jobPhoto;

    @Bind(R.id.activity_edit_job_name)
    TextInputLayout jobName;

    @Bind(R.id.activity_edit_job_description)
    TextInputLayout jobDescription;

    @Bind(R.id.activity_edit_job_duration)
    android.support.design.widget.TextInputLayout jobDuration;

    @Bind(R.id.activity_edit_job_money)
    android.support.design.widget.TextInputLayout jobMoney;

    @Bind(R.id.activity_edit_job_category)
    android.support.v7.widget.AppCompatButton jobCategory;

    @Bind(R.id.activity_edit_job_edit_button)
    android.support.v7.widget.AppCompatButton editJobButton;

    @Bind(R.id.activity_edit_job_eliminate_button)
    android.support.v7.widget.AppCompatButton eliminateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        jobID = getIntent().getExtras().getString("jobID");
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannaJobs");
        storageRef = storage.getReferenceFromUrl("gs://project-6871569626797643888.appspot.com/images");
        imageURL = getIntent().getExtras().getString("imageURL");
        ButterKnife.bind(this);

        jobPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageChooser().show();
            }
        });
        Picasso
                .with(getApplicationContext())
                .load(imageURL)
                .placeholder(R.drawable.photo_placeholder)
                .fit()
                .into(jobPhoto);

        mFirebaseRef.child(jobID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();
                //newJob = job;
                jobName.getEditText().setText(job.get("name").toString());
                jobCategory.setText(job.get("category").toString());
                jobDescription.getEditText().setText(job.get("description").toString());
                jobDuration.getEditText().setText(job.get("jobDuration").toString());
                jobMoney.getEditText().setText(job.get("salary").toString());


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        editJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkConditions()) {
                    if (!imageURL.equals("")) {
                        mFirebaseRef.child(jobID).child("jobImage").setValue(imageURL);
                    }
                    mFirebaseRef.child(jobID).child("name").setValue(jobName.getEditText().getText().toString());
                    mFirebaseRef.child(jobID).child("category").setValue(jobCategory.getText().toString());
                    mFirebaseRef.child(jobID).child("description").setValue(jobDescription.getEditText().getText().toString());
                    mFirebaseRef.child(jobID).child("jobDuration").setValue(jobDuration.getEditText().getText().toString());
                    mFirebaseRef.child(jobID).child("salary").setValue(Integer.parseInt(jobMoney.getEditText().getText().toString()));

                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);

                    Toast.makeText(getApplicationContext(), R.string.job_created_dialog, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else Toast.makeText(getApplicationContext(), R.string.data_wrong_dialog , Toast.LENGTH_SHORT).show();
            }
        });

        jobCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = getResources().getStringArray(R.array.categories_list);

                // Creating and Building the Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EditJobActivity.this);
                builder.setTitle(getString(R.string.job_category));
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        jobCategory.setText(items[item].substring(1,items[item].length()));
                        category = items[item];
                        levelDialog.dismiss();
                    }
                });
                levelDialog = builder.create();
                levelDialog.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean checkConditions () {
        return (jobName.getEditText().getText().toString().trim().equals("") ||
                jobDescription.getEditText().getText().toString().trim().equals("") ||
                jobMoney.getEditText().getText().toString().trim().equals("") ||
                category.equals(R.string.job_category) ||
                jobDuration.getEditText().getText().toString().trim().equals(""));
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

        bitmap = null;
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
        //imageURL = sService.uploadTaskImage(jobId, bitmap);
        //imageURL = ImageManager.encodeTobase64(bitmap);
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://project-6871569626797643888.appspot.com/images");

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/"+jobID +".jpg");

        deletePreviousImage(mountainImagesRef);
        uploadNewImage(mountainImagesRef);


        jobPhoto.setBackground(null);
        jobPhoto.setImageBitmap(Bitmap.createScaledBitmap(bitmap,100, 100, false));
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

    public void deletePreviousImage(StorageReference imagesRef) {
        imagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

    }

    public void uploadNewImage(StorageReference imagesRef) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data2 = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data2);
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
    }

    @OnClick(R.id.activity_edit_job_eliminate_button)
    public void eliminateJob() {

        DialogUtils.buildAlertDialog(EditJobActivity.this)
                .setMessage(getString(R.string.eliminate_job_dialog))
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        mFirebaseRef.child(jobID).removeValue();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://project-6871569626797643888.appspot.com");
                        StorageReference desertRef = storageRef.child("images/"+jobID +".jpg");

                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                            }
                        });
                        Intent main = new Intent(EditJobActivity.this, MainActivity.class);
                        startActivity(main);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
}
