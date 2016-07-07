package com.example.txuso.wannajob.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.txuso.wannajob.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JobMatchActivity extends Activity {


    @Bind(R.id.activity_job_match_evaluate_button)
    TextView evaluateButton;

    String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_job_match);
        ButterKnife.bind(this);
        jobId = getIntent().getExtras().getString("jobID");

    }

    @OnClick (R.id.activity_job_match_evaluate_button)
    public void onEvaluateClick() {
        Intent evaluateIntent = new Intent(JobMatchActivity.this, EvaluateUserActivity.class);
        startActivity(evaluateIntent);
    }
}
