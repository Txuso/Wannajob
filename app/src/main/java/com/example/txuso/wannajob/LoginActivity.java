package com.example.txuso.wannajob;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.firebase.client.Firebase;
import java.util.Date;
import wannajob.classes.ImageManager;
import wannajob.classes.WannajobUser;


public class LoginActivity extends Activity {

    boolean isResumed = false;

    Firebase mFirebaseRef;

    //UiLifecycleHelper helps to create automatically open, save and restore the Active Session
    private UiLifecycleHelper uiHelper;


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
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");
        //The Facebook login button which a component within the GUI
        LoginButton mFacebookLoginButton  = (LoginButton) findViewById(R.id.login_with_facebook);
        //Data that will be gathered from the user's Facebook account is set here
        mFacebookLoginButton.setReadPermissions("public_profile", "user_birthday");

    }
    /* Handle any changes to the Facebook session */
    private void onFacebookSessionStateChange(final Session session, SessionState state, Exception exception) {

        if (isResumed) {
            if (session != null && session.isOpened())
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        //we get data from the Facebook account
                        String fbName = user.getName();
                        String fbAge = user.getBirthday();
                        //we create the instance of the MainMenu
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //we put the user's name as an extra data to the next activity
                        intent.putExtra("name", fbName);

                        //We set the default image and we encode it to base64
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profileimage);
                        String im = ImageManager.encodeTobase64(bm);
                        //We create the tandem user with the needed data


                        mFirebaseRef.removeValue();
                        // we create a new instance cause it will be useful to get the ID of the new user

                        WannajobUser newUser = new WannajobUser(fbName, fbAge, im);

                        Firebase newTandRef = mFirebaseRef.push();
                        //We store the user in the Firebase root
                        newTandRef.setValue(newUser);
                        //we get the created user's ID
                        String logedUserID = newTandRef.getKey();
                        //we put it as an extra data in the next activity
                        intent.putExtra("id", logedUserID);


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


}
