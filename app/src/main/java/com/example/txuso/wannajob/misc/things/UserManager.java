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
    public static final int NOT_CATEGORY_FILTER = -1;

    public static String getUserId(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_ID, "");
    }

    public static void setUserId(@NonNull Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_ID, userId).commit();
    }

    // User Name

    private static final String USER_NAME = "userName";

    public static String getUserName(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_NAME, "");
    }

    public static void setUserName(@NonNull Context context, String userName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_NAME, userName).commit();
    }

    // User Description

    private static final String USER_DESCRIPTION = "userDescription";

    public static String getUserDescription(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_DESCRIPTION, "");
    }

    public static void setUserDescription(@NonNull Context context, String userDescription) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_DESCRIPTION, userDescription).commit();
    }

    // User Longitude

    private static final String USER_LONGITUDE = "userLongitude";

    public static double getUserLongitude(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Double.longBitsToDouble(sharedPreferences.getLong(USER_LONGITUDE, 0));
    }

    public static void setUserLongitude(@NonNull Context context, double userLongitude) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(USER_LONGITUDE, Double.doubleToLongBits(userLongitude)).commit();
    }

    // User Latitude

    private static final String USER_LATITUDE = "userLatitude";

    public static double getUserLatitude(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Double.longBitsToDouble(sharedPreferences.getLong(USER_LATITUDE, 0));
    }

    public static void setUserLatitude(@NonNull Context context, double userLatitude) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(USER_LATITUDE, Double.doubleToLongBits(userLatitude)).commit();
    }

    // User Registered Date

    private static final String USER_REGISTERED_DATE = "userRegisteredDate";

    public static String getUserRegisteredDate(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_REGISTERED_DATE, "");
    }

    public static void setUserRegisteredDate(@NonNull Context context, String userRegisteredDate) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_REGISTERED_DATE, userRegisteredDate).commit();
    }

    // User Photo

    private static final String USER_PHOTO= "userPhoto";

    public static String getUserPhoto(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_PHOTO, "");
    }

    public static void setUserPhoto(@NonNull Context context, String userPhoto) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_PHOTO, userPhoto).commit();
    }

    private static final String USER_AGE= "userAge";

    public static String getUserAge(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_AGE, "");
    }

    public static void setUserAge(@NonNull Context context, String userAge) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_AGE, userAge).commit();
    }




}
