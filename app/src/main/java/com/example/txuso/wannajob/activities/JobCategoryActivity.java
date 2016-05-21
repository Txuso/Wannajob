package com.example.txuso.wannajob.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.adapter.CategoryGridAdapter;

public class JobCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_category);
        final GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new CategoryGridAdapter(this));

        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
