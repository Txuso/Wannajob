package com.example.txuso.wannajob.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.model.classes.UserOpinion;
import com.example.txuso.wannajob.data.model.classes.UserOpinionListItem;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.Firebase;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;

public class EvaluateUserActivity extends AppCompatActivity {

    @Bind(R.id.activity_show_job_user_rating)
    RatingBar rating;

    @Bind(R.id.activity_evaluate_user_input_job_evaluation)
    TextInputLayout opinionText;

    String jobName;
    Bundle extras;
    String jobId;
    String toId;
    Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_user);
        extras = getIntent().getExtras();
        jobName = extras.getString("jobName");
        jobId = extras.getString("jobID");
        toId = extras.getString("toID");
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/userOpinion");


    }

    /**
     * Action when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        super.onResume();
            super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.evaluate_user, menu);
        return super.onCreateOptionsMenu(menu);
    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home: {
                return true;
            }
            case R.id.action_evaluate_user:{
                AlertDialog.Builder builder2 = new AlertDialog.Builder(EvaluateUserActivity.this);
                builder2.setMessage(getString(R.string.want_to_send_this_evaluation));
                builder2.setPositiveButton(getString(R.string.dialog_yes),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserOpinion opinion = new UserOpinion(
                                UserManager.getUserName(getApplicationContext()), jobName,
                                opinionText.getEditText().getText().toString(),rating.getNumStars(),
                                UserManager.getUserPhoto(getApplicationContext()),toId,
                                UserManager.getUserId(getApplicationContext()));
                        mFirebaseRef.push().setValue(opinion);

                    }
                });
                builder2.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }

                });

                builder2.show();
                return true;
            }

        }
            return super.onOptionsItemSelected(item);
        }
}
