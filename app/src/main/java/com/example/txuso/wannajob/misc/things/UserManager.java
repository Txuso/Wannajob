package com.example.txuso.wannajob.misc.things;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by Txuso on 11/05/16.
 */
public class UserManager {

    /**
     * Class created to deal with SharedPreferences
     */

    //User data

    private static final String USER_ID = "userId";

    public static String getUserId(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_ID, "");
    }

    public static void setUserId(@NonNull Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_ID, userId).commit();
    }

    private static final String USER_NAME = "userName";

    public static String getUserName(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_NAME, "");
    }

    public static void setUserName(@NonNull Context context, String userName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_NAME, userName).commit();
    }

    private static final String USER_DESCRIPTION = "userDescription";

    public static String getUserDescription(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_DESCRIPTION, "");
    }

    public static void setUserDescription(@NonNull Context context, String userDescription) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_DESCRIPTION, userDescription).commit();
    }

    private static final String USER_LONGITUDE = "userLongitude";

    public static long getUserLongitude(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(USER_LONGITUDE, 0);
    }

    public static void setUserLongitude(@NonNull Context context, long userLongitude) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(USER_DESCRIPTION, userLongitude).commit();
    }

    private static final String USER_LATITUDE = "userLatitude";

    public static long getUserLatitude(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(USER_LATITUDE, 0);
    }

    public static void setUserLatitude(@NonNull Context context, long userLatitude) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(USER_DESCRIPTION, userLatitude).commit();
    }



}
