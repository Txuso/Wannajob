package com.example.txuso.wannajob;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wannajob.chat.Chat;
import wannajob.chat.FirebaseListAdapter;
import wannajob.chat.WannajobEncounter;
import wannajob.chat.WannajobEncounterItem;
import wannajob.classes.ImageManager;
import wannajob.classes.RVMessageAdapter;
import wannajob.classes.RVUserAdapter;
import wannajob.classes.RoundedImageView;

public class UserMessages extends AppCompatActivity {

    Bundle extras;
    String userID;
    Firebase myFirebaseRef;
    private RecyclerView rv;
    private List<WannajobEncounterItem> messages;
    RVMessageAdapter adapter;
    WannajobEncounterItem item;
    String encounterID = "";
    String from = "";
    String to = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_messages);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        extras = getIntent().getExtras();
        userID = extras.getString("userID");

        myFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");

        rv = (RecyclerView)findViewById(R.id.rv2);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        messages = new ArrayList<>();
        adapter = new RVMessageAdapter(messages);
        adapter.clearContent();
        rv.setAdapter(adapter);

        myFirebaseRef.child("wannajobEncounter").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Map<String, Object> wEncounter = (Map<String, Object>) dataSnapshot.getValue();
                if (wEncounter.get("author").toString().equals(userID)) {
                    from = wEncounter.get("author").toString();
                    to = wEncounter.get("receptor").toString();
                    encounterID = dataSnapshot.getKey();

                    myFirebaseRef.child("wannajobUsers").child(to).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();

                            Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(user.get("image").toString()), 80, 80);
                            Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 300);
                            BitmapDrawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);
                            item = new WannajobEncounterItem(from, to, wEncounter.get("receptorName").toString(),wEncounter.get("date").toString(),ima,encounterID );
                            messages.add(item);
                            adapter = new RVMessageAdapter(messages);
                            rv.setAdapter(adapter);
                            adapter.SetOnItemClickListener(new RVMessageAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent chatIntent = new Intent(UserMessages.this, ChatActivity.class);
                                    chatIntent.putExtra("fromID", from);
                                    chatIntent.putExtra("toID",  to);
                                    chatIntent.putExtra("from", messages.get(position).getReceptorName());
                                    chatIntent.putExtra("encounterID", messages.get(position).getEncounterID());
                                    startActivity(chatIntent);

                                }
                            });



                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                else if (wEncounter.get("receptor").toString().equals(userID)){
                    from = wEncounter.get("receptor").toString();
                    to = wEncounter.get("authoer").toString();

                    myFirebaseRef.child("wannajobUsers").child(to).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();

                            Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(user.get("image").toString()), 80, 80);
                            Bitmap picRounded = RoundedImageView.getCroppedBitmap(pic, 300);
                            BitmapDrawable ima = new BitmapDrawable(getApplicationContext().getResources(), picRounded);
                            item = new WannajobEncounterItem(from, to, wEncounter.get("receptorName").toString(),wEncounter.get("date").toString(),ima, encounterID);
                            messages.add(item);
                            encounterID = dataSnapshot.getKey();
                            adapter = new RVMessageAdapter(messages);
                            rv.setAdapter(adapter);
                            adapter.SetOnItemClickListener(new RVMessageAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent chatIntent = new Intent(UserMessages.this, ChatActivity.class);
                                    chatIntent.putExtra("fromID", from);
                                    chatIntent.putExtra("toID",  to);
                                    chatIntent.putExtra("from", messages.get(position).getReceptorName());
                                    chatIntent.putExtra("encounterID", messages.get(position).getEncounterID());
                                    startActivity(chatIntent);
                                }
                            });

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
