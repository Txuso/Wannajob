package com.example.txuso.wannajob.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.txuso.wannajob.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JobMatchActivity extends Activity {


    @Bind(R.id.activity_job_match_evaluate_button)
    TextView evaluateButton;

    @Bind(R.id.activity_job_match_call_text)
    TextView callText;



    String jobName;
    Bundle extras;
    String jobId;
    String number;
    String toId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_job_match);
        ButterKnife.bind(this);
        extras = getIntent().getExtras();
        jobId = getIntent().getExtras().getString("jobID");
        jobName = extras.getString("jobName");
        jobId = extras.getString("jobID");
        toId = extras.getString("toID");
        number = extras.getString("number");
        callText.append( " \n" + number);


    }

    @OnClick (R.id.activity_job_match_evaluate_button)
    public void onEvaluateClick() {
        Intent evaluateIntent = new Intent(JobMatchActivity.this, EvaluateUserActivity.class);
        evaluateIntent.putExtra("jobID", jobId);
        evaluateIntent.putExtra("jobName", jobName);
        evaluateIntent.putExtra("toID", toId);
        startActivity(evaluateIntent);
    }
}
