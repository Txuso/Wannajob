package com.example.txuso.wannajob.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.firebase.FirebaseStorageService;
import com.example.txuso.wannajob.data.firebase.JobFirebaseService;
import com.example.txuso.wannajob.misc.things.UserManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.txuso.wannajob.misc.things.CharacterCountErrorWatcher;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateJobActivity extends AppCompatActivity {


    String jobId;
    AlertDialog levelDialog;
    private Uri mImageCaptureUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    JobFirebaseService jService = new JobFirebaseService();
    FirebaseStorageService sService = new FirebaseStorageService(CreateJobActivity.this);

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    String category = "";

    String imageURL = "";

    @Bind(R.id.input_job_name)
    TextInputLayout jobName;

    @Bind(R.id.input_job_description)
    TextInputLayout jobDescription;

    @Bind(R.id.input_job_salary)
    TextInputLayout jobSalary;

    @Bind(R.id.confirm_wannajober_dialog)
    TextInputLayout jobDuration;

    @Bind(R.id.btn_categories)
    AppCompatButton jobCategoryB;

    @Bind(R.id.app_bar_main_create_job_floating_action_button)
    AppCompatButton createJobB;

    @Bind(R.id.job_photo_button)
    android.support.v7.widget.AppCompatImageButton imageP;

    @Bind(R.id.in_init_date)
    EditText initDate;

    @Bind(R.id.activity_create_job_checkbox)
    CheckBox doItNowCheckbox;

    private int mYear, mMonth, mDay, mHour, mMinute;

    double latitude;
    double longitude;

    Date dateFinish;
    Date dateInit;

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
        jobId = jService.getNewJobKey();

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
                        jobCategoryB.setText(items[item].substring(2,items[item].length()));
                        category = items[item];

                    }
                });
                builder.setPositiveButton("ACEPTAR CATEGORÍA",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int item) {
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
                    Job newJob;
                    if (imageURL.equals("")) {
                        imageURL = getCategoryImage(Integer.parseInt(category.substring(0,1)));
                    }
                    if (doItNowCheckbox.isChecked()) {
                        newJob = new Job(jobName.getEditText().getText().toString(),
                                jobDescription.getEditText().getText().toString(),
                                Integer.parseInt(jobSalary.getEditText().getText().toString()),
                                category,
                                UserManager.getUserId(getApplicationContext()), new SimpleDateFormat("yyyy/MM/dd").format(new Date()), imageURL,
                                jobDuration.getEditText().getText().toString(),latitude, longitude, "", true);
                    } else {
                            newJob = new Job(jobName.getEditText().getText().toString(),
                                    jobDescription.getEditText().getText().toString(),
                                    Integer.parseInt(jobSalary.getEditText().getText().toString()),
                                    category,
                                    UserManager.getUserId(getApplicationContext()), new SimpleDateFormat("yyyy/MM/dd").format(new Date()), imageURL,
                                    jobDuration.getEditText().getText().toString(),latitude, longitude, "",
                                    initDate.getText().toString());

                    }
                    Toast.makeText(getApplicationContext(), jobDuration.getEditText().getText().toString(), Toast.LENGTH_SHORT).show();
                    jService.createJob(jobId, newJob);
                    Intent intent = new Intent(CreateJobActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
                builder2.setMessage(getString(R.string.exit_without_saving));
                builder2.setPositiveButton(getString(R.string.yes_dialog),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                builder2.setNegativeButton(getString(R.string.no_dialog), new DialogInterface.OnClickListener() {
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
                        Uri cameraUri = Uri.fromFile(file);
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,cameraUri );
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.dialog_complete_action_using)), PICK_FROM_FILE);
                }
            }
        } );

        return builder.create();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        Bitmap bitmap;
        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            CropImage.activity(mImageCaptureUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        } else if (requestCode == PICK_FROM_CAMERA) {

            CropImage.activity(mImageCaptureUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
//                bitmap = BitmapFactory.decodeFile(getRealPathFromURI(resultUri));
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                    byte[] data2 = baos.toByteArray();

                    imageP.setImageBitmap(bitmap);
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://project-6871569626797643888.appspot.com");

                    // Create a reference to 'images/mountains.jpg'
                    StorageReference mountainImagesRef = storageRef.child("images/"+jobId +".jpg");
                    final ProgressDialog progress = ProgressDialog.show(this, "Procesando imagen",
                            "La imagen está siendo procesada, espere un momento.", true);
                    UploadTask uploadTask = mountainImagesRef.putBytes(data2);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            progress.dismiss();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            if (downloadUrl.toString() != null) {
                                imageURL = downloadUrl.toString();
                                progress.dismiss();

                            }
                        }
                    });
                } catch (IOException e) {

                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        //imageURL = sService.uploadTaskImage(jobId, bitmap);
        //imageURL = ImageManager.encodeTobase64(bitmap);
        // Create a storage reference from our app

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

    @OnClick(R.id.in_init_date)
    public void chooseInitDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateInit = c.getTime();
                        initDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @OnClick(R.id.activity_create_job_checkbox)
    public void createNowCheckboxClicked() {
        if (doItNowCheckbox.isChecked()) {
            initDate.setVisibility(View.GONE);
        } else {
            initDate.setVisibility(View.VISIBLE);
        }
    }

    public String getCategoryImage(int categoryId) {
        String image = "";
        switch (categoryId) {
            case 0: {
                image = "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2Fcategories%2Felectricista.jpg?alt=media&token=f2974317-95e9-4fa5-8845-509071b7e2b1";
            } break;
            case 1: {
                image = "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2Fcategories%2Fpaquetes.jpg?alt=media&token=70d04596-4080-4cfd-af95-b2e841fb233d";
            } break;
            case 2: {
                image = "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2Fcategories%2Ftaking_out_the_trash.jpg?alt=media&token=ee693c3d-461a-4751-8576-4e703c45610e";
            } break;
            case 3: {
                image = "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2Fcategories%2Fclases.jpg?alt=media&token=f4beca47-8659-484a-94e5-7f11b7cffce3";
            } break;
            case 4: {
                image = "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2Fcategories%2Ffregar_platos.jpg?alt=media&token=1c10a989-7292-4b7f-a569-919be8b6045b";
            } break;
            case 5: {
                image = "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2Fcategories%2Flimpieza.jpg?alt=media&token=e4cd20a5-111b-4947-a9ea-6e0ad57a6c43";
            } break;
            case 6: {
                image = "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2Fcategories%2Fhacer_cola.jpg?alt=media&token=063ac79b-5746-42a7-9e44-6b7da7aee0f4";
            } break;
            case 7: {
                image = "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2Fcategories%2Fpaseo_perro.jpeg?alt=media&token=70616e52-2ba4-4404-b063-41d27a058e01";
            } break;
        }
        return image;
    }
}
