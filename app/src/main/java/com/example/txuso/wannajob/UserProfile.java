package com.example.txuso.wannajob;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import wannajob.classes.ImageManager;
import wannajob.classes.WannajobUser;

public class UserProfile extends AppCompatActivity {

    Bundle extras;
    String userID;
    Firebase firebaseRef;
    ImageView image;
    TextInputLayout userName;
    TextInputLayout userAge;

    CollapsingToolbarLayout collapsingToolbarLayout;
    android.support.v7.widget.Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        extras = getIntent().getExtras();
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userID = extras.getString("userID");
        firebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_user);
        image = (ImageView) findViewById(R.id.user_image);
        userName = (TextInputLayout) findViewById(R.id.user_name);
        userAge = (TextInputLayout) findViewById(R.id.user_age);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        firebaseRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();

                collapsingToolbarLayout.setTitle(user.get("name").toString());
                // Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.get("jobImage").toString()),100,100);

                //  Picasso.with(getApplicationContext()).load()
                // BitmapDrawable ob = new BitmapDrawable(getResources(), pic);
                Bitmap pic = ImageManager.decodeBase64(user.get("image").toString());
                Drawable d = new BitmapDrawable(getResources(), pic);
                setPalette(pic);
                image.setBackground(d);

                userName.getEditText().setText(user.get("name").toString());
                 userAge.getEditText().setText(user.get("age").toString());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }

    private void setPalette(Bitmap bd) {

        Palette.from(bd).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
