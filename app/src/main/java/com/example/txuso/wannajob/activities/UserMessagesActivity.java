package com.example.txuso.wannajob.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.txuso.wannajob.data.model.classes.WannajobEncounterItem;
import com.example.txuso.wannajob.data.adapter.CustomJobListViewAdapter;
import com.example.txuso.wannajob.misc.things.ImageManager;
import com.example.txuso.wannajob.misc.things.RoundedImageView;

public class UserMessagesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String userID;
    Firebase myFirebaseRef;
    private RecyclerView rv;
    List<WannajobEncounterItem> messages;
    CustomJobListViewAdapter adapter;
    WannajobEncounterItem item;
    String encounterID = "";
    String from = "";
    String to = "";
    ListView listView;
    ProgressDialog progress;


    BitmapDrawable ima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorite_jobs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userID = UserManager.getUserId(this);

        myFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");

        messages = new ArrayList<>();

        myFirebaseRef.child("wannajobEncounter").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot1, String s) {
                final Map<String, Object> wEncounter = (Map<String, Object>) dataSnapshot1.getValue();
                if (wEncounter.get("author").toString().equals(userID)) {
                    from = wEncounter.get("author").toString();
                    to = wEncounter.get("receptor").toString();

                    myFirebaseRef.child("wannajobUsers").child(to).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();

                            Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(user.get("image").toString()), 80, 80);
                            Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 250);

                            BitmapDrawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);
                            item = new WannajobEncounterItem(from, to, wEncounter.get("receptorName").toString(), wEncounter.get("date").toString(), ima, dataSnapshot1.getKey());
                            messages.add(item);
                            listView.invalidateViews();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });



                } else if (wEncounter.get("receptor").toString().equals(userID)) {
                    from = wEncounter.get("receptor").toString();
                    to = wEncounter.get("author").toString();

                    myFirebaseRef.child("wannajobUsers").child(to).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();

                            Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(user.get("image").toString()), 80, 80);
                            Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 250);

                            BitmapDrawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);
                            item = new WannajobEncounterItem(from, to, wEncounter.get("receptorName").toString(), wEncounter.get("date").toString(), ima, dataSnapshot1.getKey());
                            messages.add(item);
                            listView.invalidateViews();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
        /*

        listView = (ListView) findViewById(R.id.JobEncounters);
        adapter = new CustomJobListViewAdapter(this,
                R.layout.message_item, messages);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(this);
        */
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
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Toast.makeText(getApplicationContext(), messages.get(position).getEncounterID(), Toast.LENGTH_SHORT).show();

        Intent chatIntent = new Intent(UserMessagesActivity.this, ChatActivity.class);
        chatIntent.putExtra("fromID", from);
        chatIntent.putExtra("toID",  to);
        chatIntent.putExtra("from", messages.get(position).getReceptorName());
        chatIntent.putExtra("encounterID", messages.get(position).getEncounterID());
        startActivity(chatIntent);

    }
}
