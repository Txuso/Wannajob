package com.example.txuso.wannajob.misc.things;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

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

    private static final String USER_PHOTO = "userPhoto";

    public static String getUserPhoto(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_PHOTO, "https://firebasestorage.googleapis.com/v0/b/project-6871569626797643888.appspot.com/o/images%2F-KNXaoe-WMIyO-F0_4p9.jpg?alt=media&token=684439ea-83e1-4de7-a892-ef639e9064b1");
    }

    public static void setUserPhoto(@NonNull Context context, String userPhoto) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_PHOTO, userPhoto).commit();
    }

    private static final String USER_AGE = "userAge";

    public static String getUserAge(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USER_AGE, "");
    }

    public static void setUserAge(@NonNull Context context, String userAge) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(USER_AGE, userAge).apply();
    }

    private static final String USER_FAVORITE_JOBS= "userFavoriteJobs";

    public static Set<String> getUserfavoriteJobs(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getStringSet(USER_FAVORITE_JOBS, new HashSet<String>());
    }

    public static boolean isUserfavoriteJob(@NonNull Context context, String userFavoriteJob) {
        Set<String> favorites = getUserfavoriteJobs(context);
        return favorites.contains(userFavoriteJob);
    }

    public static void addUserFavoriteJob(@NonNull Context context, String userFavoriteJobs) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> favorites = getUserfavoriteJobs(context);
        favorites.add(userFavoriteJobs);
        sharedPreferences.edit().putStringSet(USER_FAVORITE_JOBS, favorites).apply();
    }

    public static void deleteUserFavoriteJob(@NonNull Context context, String userFavoriteJobs) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> favorites = getUserfavoriteJobs(context);
        favorites.remove(userFavoriteJobs);
        sharedPreferences.edit().putStringSet(USER_FAVORITE_JOBS, favorites).apply();
    }


    private static final String IS_USER_LOGGED = "isUserLogged";


    public static boolean getIsUserLogged(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(IS_USER_LOGGED, false);
    }

    public static void setIsUserLogged(@NonNull Context context, boolean isUserLogged) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(IS_USER_LOGGED, isUserLogged).commit();
    }

    public static void removeSharedPrefData(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
