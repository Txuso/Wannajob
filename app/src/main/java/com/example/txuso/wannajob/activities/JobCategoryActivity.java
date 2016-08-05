package com.example.txuso.wannajob.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView.setAdapter(new CategoryGridAdapter(this,gridArray));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Intent mainA = MainActivity.newIntent(getApplicationContext(), gridArray.get(position).categoryId);
                mainA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainA);

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
