package com.compadrehackteam.geoforgood.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ricardo on 22/09/15.
 * <p/>
 * SharedPreference Class Manager to save some flags and info about the app
 */
public class SharedPreferencesManager {

    private static final String APP_SETTINGS = "ceorunner";

    /**
     * Properties
     */
    private static final String LOCATION_PERMISSION = "location_permission";
    private static final String USER_AUTHENTICATED = "user_auth";
    private static final String USER_NAME = "user_name";



    private SharedPreferencesManager() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static Boolean isLocationPermissionGiven(Context context) {
        return getSharedPreferences(context).getBoolean(LOCATION_PERMISSION, false);
    }

    public static void setLocationPermission(Context context, Boolean permission) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(LOCATION_PERMISSION, permission);
        editor.apply();
    }

    public static Boolean isUserAuthenticated(Context context) {
        return getSharedPreferences(context).getBoolean(USER_AUTHENTICATED, false);
    }

    public static void setUserAuthenticated(Context context, Boolean auth) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(USER_AUTHENTICATED, auth);
        editor.apply();
    }

    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(USER_NAME, "");
    }

    public static void setUsername(Context context, String name) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_NAME, name);
        editor.apply();
    }

}
