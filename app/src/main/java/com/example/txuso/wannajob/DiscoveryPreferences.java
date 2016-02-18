package com.example.txuso.wannajob;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.IOException;
import java.util.List;

import wannajob.classes.GPSTracker;

public class DiscoveryPreferences extends AppCompatActivity {
    int progressValue = 50;
    Double longitude;
    Double latitude;
    Geocoder gc;
    GPSTracker gps;
    TextInputLayout search;
    Firebase tandemRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_preferences);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tandemRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
        AppCompatButton myLocButton = (AppCompatButton) findViewById(R.id.myLocButton);
        search = (TextInputLayout) findViewById(R.id.input_loc_name);
        gc = new Geocoder(this.getApplicationContext());

        myLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPSTracker(DiscoveryPreferences.this);
                if (gps.canGetLocation()) {

                    tandemRef.child(getIntent().getExtras().getString("userID")).child("latitude").setValue(gps.getLatitude());
                    tandemRef.child(getIntent().getExtras().getString("userID")).child("longitude").setValue(gps.getLongitude());

                    Toast.makeText(getApplicationContext(), getString(R.string.you_find_jobs_own_location) + " " + progressValue + " " + getString(R.string.from_you), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    gps.showSettingsAlert();
                }
            }
        });


        SeekBar bar = (SeekBar)findViewById(R.id.seekBar);
        bar.setMax(50);
        bar.setProgress(50);

        final TextView dist = (TextView)findViewById(R.id.input_distance_text);
        dist.setText(getString(R.string.job_distance) + "      50 km");

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
                dist.setText(getString(R.string.job_distance) + "      " + progress + " km");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, R.string.save_changes).setShortcut('3', 'c');
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(DiscoveryPreferences.this);
                builder2.setMessage("Do you want to save the changes?");
                builder2.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String place = search.getEditText().getText().toString();
                        if (!place.trim().equals("")) {
                            try {
                                List<Address> loc = gc.getFromLocationName(place, 1);
                                Address location = loc.get(0);
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();

                                tandemRef.child(getIntent().getExtras().getString("userID")).child("latitude").setValue(latitude);
                                tandemRef.child(getIntent().getExtras().getString("userID")).child("longitude").setValue(longitude);
                                tandemRef.child(getIntent().getExtras().getString("userID")).child("distance").setValue(progressValue);

                                Toast.makeText(getApplicationContext(), getString(R.string.you_find_encounters) + " " + location.getLocality() + " " + getString(R.string.and_around) + " " + progressValue + " " + getString(R.string.from_you), Toast.LENGTH_LONG).show();
                                Intent intent = getIntent();
                                intent.putExtra("distance", progressValue + "");
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), getString(R.string.enter_location), Toast.LENGTH_SHORT).show();
                    }
                });
                builder2.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }

                });

                builder2.show();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            case 0: {
                String place = search.getEditText().getText().toString();
                if (!place.trim().equals("")) {
                    try {
                        List<Address> loc = gc.getFromLocationName(place, 1);
                        Address location = loc.get(0);
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();

                        tandemRef.child(getIntent().getExtras().getString("userID")).child("latitude").setValue(latitude);
                        tandemRef.child(getIntent().getExtras().getString("userID")).child("longitude").setValue(longitude);
                        tandemRef.child(getIntent().getExtras().getString("userID")).child("distance").setValue(progressValue);

                        Toast.makeText(getApplicationContext(), getString(R.string.you_find_encounters) + " " + location.getLocality() + " " + getString(R.string.and_around) + " " + progressValue + " " + getString(R.string.from_you), Toast.LENGTH_LONG).show();
                        Intent intent = getIntent();
                        intent.putExtra("distance", progressValue + "");
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_location), Toast.LENGTH_SHORT).show();

                return true;}
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
