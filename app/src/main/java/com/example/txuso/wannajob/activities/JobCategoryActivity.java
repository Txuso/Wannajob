package com.example.txuso.wannajob.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.adapter.CategoryGridAdapter;
import com.example.txuso.wannajob.misc.things.UserManager;

import java.util.ArrayList;

public class JobCategoryActivity extends AppCompatActivity {
    ArrayList<CategoryGridAdapter.Item> gridArray = new ArrayList<CategoryGridAdapter.Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_category);
        final GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new CategoryGridAdapter(this,gridArray));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Intent intent = getIntent();
                intent.putExtra("categoryId", gridArray.get(position).categoryId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
