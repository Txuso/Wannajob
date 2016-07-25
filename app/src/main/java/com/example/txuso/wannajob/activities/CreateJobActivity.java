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

    @Bind(R.id.in_finish_date)
    EditText finishDate;
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
                if (!checkConditions() && checkDates()) {
                    Job newJob;
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
                                    initDate.getText().toString(), finishDate.getText().toString());

                    }
                    Toast.makeText(getApplicationContext(), jobDuration.getEditText().getText().toString(), Toast.LENGTH_SHORT).show();
                    jService.createJob(jobId, newJob);
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);


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

    public boolean checkDates() {
        boolean areDatesRight = true;
        if (!initDate.getText().toString().equals("") ||
                !finishDate.getText().toString().equals("")) {
            if (dateFinish.getTime() - dateInit.getTime() > 0){
                return true;
            } else {
                return false;
            }
        } else {
            return areDatesRight;
        }

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

        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
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
                    final ProgressDialog progress = ProgressDialog.show(this, "Uploading Picture",
                            "Your picture is being uploaded", true);
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

    @OnClick(R.id.in_finish_date)
    public void chooseFinishDate() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        finishDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        c.set(year, monthOfYear, dayOfMonth, 0, 0);
                        dateFinish = c.getTime();
                        if (dateInit != null) {
                            if (dateFinish.getTime() - dateInit.getTime() > 0) {
                                Toast.makeText(getApplicationContext(), "maquinon!", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Choose a date later than " + dateInit.getTime(), Toast.LENGTH_SHORT).show();
                            }

                        }



                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    @OnClick(R.id.activity_create_job_checkbox)
    public void createNowCheckboxClicked() {
        if (doItNowCheckbox.isChecked()) {
            initDate.setVisibility(View.GONE);
            finishDate.setVisibility(View.GONE);
        } else {
            initDate.setVisibility(View.VISIBLE);
            finishDate.setVisibility(View.VISIBLE);
        }
    }

}
