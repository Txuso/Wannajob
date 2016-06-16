package com.example.txuso.wannajob.activities;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.adapter.RVUserAdapter;

public class EvaluateUserActivity extends AppCompatActivity {

    String fromId;
    String toId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_user);
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
                return true;
            }

        }
            return super.onOptionsItemSelected(item);
        }
}
