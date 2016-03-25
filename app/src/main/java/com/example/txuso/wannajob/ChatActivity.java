package com.example.txuso.wannajob;

import android.app.ListActivity;
        import android.database.DataSetObserver;
        import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
        import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
        import android.view.View;
        import android.view.inputmethod.EditorInfo;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;
        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.ValueEventListener;
        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.TimeZone;
import wannajob.chat.ChatListAdapter;
import wannajob.chat.IndividualChat;


public class ChatActivity extends AppCompatActivity {
    private Firebase mFirebaseRef;
    private Firebase tFirebaseRef;

    String from = "";
    String to = "";
    String fromID = "";
    String toID = "";
    String encounterID = "";
    private static final String FIREBASE_URL = "https://wannajob.firebaseio.com/IndividualChat";
    private static final String WANNAJOBUSER_URL = "https://wannajob.firebaseio.com/wannajobUsers";

    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //we get extra data from the previous activity
        Bundle extra = getIntent().getExtras();
        from = extra.getString("from");
        fromID = extra.getString("fromID");
        toID = extra.getString("toID");
        encounterID = extra.getString("encounterID");

        //we set the the firebase root to the encounter between the users
        mFirebaseRef = new Firebase(FIREBASE_URL).child(encounterID);
        tFirebaseRef = new Firebase(WANNAJOBUSER_URL).child(toID);
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        // Tell our list adapter that we only want 50 messages at a time
        listView = (ListView)findViewById(R.id.list);

        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limit(100), this, R.layout.chat_message, fromID, toID);
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(ChatActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
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

    // Method to send a message and store the message in Firebase
    private void sendMessage() {
        // The input edit text in which the user types the message
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // We notify the receptor user that he/she has a new message in order to send a notification
            tFirebaseRef.child("newMessage").setValue(true);
            tFirebaseRef.child("newMessageValue").setValue(from + "^" + fromID + "^" + encounterID);
            // we get the current time
            String localTime = getCurrentTime();
            //we create the IndividualChat instance with the required data
            IndividualChat chat = new IndividualChat(input, from, to, fromID, toID, false, localTime);
            // We write the data in Firebase with push command in order to create a unique ID.
            mFirebaseRef.push().setValue(chat);
            //we erase the already message from the edit text
            inputText.setText("");
        }
    }

    public String getCurrentTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("KK:mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
        String localTime = date.format(currentLocalTime);

        return localTime;
    }


}