package com.example.txuso.wannajob;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import wannajob.classes.GPSTracker;
import wannajob.classes.ImageManager;
import wannajob.classes.WannajobUser;


public class LoginActivity extends Activity implements View.OnClickListener {

    Intent intent;

    //Signin button
    private SignInButton signInButton;

    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    boolean isLoged = false;
    /**
     * Boolean attribute that is on when the app is resumed
     */
    boolean isResumed = false;

    /**
     * Firebase object
     */
    Firebase mFirebaseRef;

    /**
     * UiLifecycleHelper helps to create automatically open, save and restore the Active Session
     */
    private UiLifecycleHelper uiHelper;

    /**
     * Callback controls the session
     */
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onFacebookSessionStateChange(session, state, exception);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * We set the the uihelper and the firebase context.
         */
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        /**
         * We create the firebase object with the reference to the users
         */
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());


        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(this);


        /**
         *The Facebook login button which a component within the GUI
         */
        LoginButton mFacebookLoginButton  = (LoginButton) findViewById(R.id.login_with_facebook);

        /**
         * Data that will be gathered from the user's Facebook account is set here
         */
        mFacebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_birthday"));
        android.support.v7.widget.AppCompatButton testB =  (android.support.v7.widget.AppCompatButton) findViewById(R.id.testUserButton);

        intent = new Intent(LoginActivity.this, MainActivity.class);

        testB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.job);
                String im = ImageManager.encodeTobase64(bm);

                WannajobUser newU = new WannajobUser("Rodolfo", "40",im);
                Firebase newTandRef = mFirebaseRef.push();
                newTandRef.setValue(newU);
                String logedUserID = newTandRef.getKey();
                intent.putExtra("userID", logedUserID);
                intent.putExtra("name", "Rodolfo");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

    }
    /**
     *  Handle any changes to the Facebook session
     <  */
    private void onFacebookSessionStateChange(final Session session, SessionState state, Exception exception) {

        if (isResumed) {
            if (session != null && session.isOpened())

                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser user, Response response) {

                    mFirebaseRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Map<String, Object> wannaUser = (Map<String, Object>) dataSnapshot.getValue();

                            if (dataSnapshot.getKey().equals(user.getId()))
                                isLoged = true;

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
                        if (!isLoged){
                            String fbName = user.getName();
                            String fbAge = "22";

                            //String fbAge = user.getBirthday().toString();
                            //String fbAge = "22";
                            //we create the instance of the MainMenu
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            //we put the user's name as an extra data to the next activity

                            //We set the default image and we encode it to base64
                            //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profileimage);

                            //String im = ImageManager.encodeTobase64(bm);
                            String im = "";
                            try {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                        .permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                URL imgUrl = new URL("https://graph.facebook.com/"
                                        + user.getId() + "/picture?type=large");
                                Bitmap mIcon1 = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                                im = ImageManager.encodeTobase64(mIcon1);


                            }
                            catch (MalformedURLException e){
                                im = "lksnflksdnflsd";
                            }

                            catch (IOException e2) {

                                im = "odfnsdfisdjf";

                            }
                            //We create the tandem user with the needed data

                            // we create a new instance cause it will be useful to get the ID of the new user
                            WannajobUser newUser = new WannajobUser(fbName, fbAge, im);

                            //We store the user in the Firebase root
                            mFirebaseRef.child(user.getId()).setValue(newUser);

                            //we put it as an extra data in the next activity
                            intent.putExtra("userID", user.getId());

                        }

                        intent.putExtra("userID", user.getId());
                        intent.putExtra("name", user.getName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                });
        }
        else
            Log.e("ERROR", "ERROR");

    }


    /**
     *
     * @param requestCode from the Facebook Login
     * @param resultCode from the Facebook Login
     * @param data from the Facebook Login
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }

    }

    //we get the age from a date
    public String getAge(String age) {

        int year = Integer.parseInt(age.substring(6));
        int day = Integer.parseInt(age.substring(3, 5));
        int month = Integer.parseInt(age.substring(0, 2));

        Date now = new Date();
        int nowMonth = now.getMonth()+ 1;
        int nowYear = now.getYear()+1900;
        int result = nowYear - year;

        if (month > nowMonth) {
            result--;
        }
        else if (month == nowMonth) {
            int nowDay = now.getDate();

            if (day > nowDay) {
                result--;
            }
        }
        return result+"";
    }


    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onFacebookSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    //This function will option signing intent
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.job);
            String im = ImageManager.encodeTobase64(bm);
            WannajobUser newU = new WannajobUser(acct.getDisplayName(), "22",im);
            Firebase newTandRef = mFirebaseRef.push();
            newTandRef.setValue(newU);
            String logedUserID = newTandRef.getKey();
            intent.putExtra("userID", logedUserID);
            intent.putExtra("name", acct.getDisplayName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, acct.getDisplayName() + " " + acct.getEmail(), Toast.LENGTH_LONG).show();

            /*

            //Initializing image loader
            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                    .getImageLoader();

            imageLoader.get(acct.getPhotoUrl().toString(),
                    ImageLoader.getImageListener(profilePhoto,
                            R.mipmap.ic_launcher,
                            R.mipmap.ic_launcher));

            //Loading image
            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);
            */

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == signInButton) {
            //Calling signin
            signIn();
        }
    }
}
