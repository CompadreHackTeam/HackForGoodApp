package com.compadrehackteam.geoforgood.manager;

import com.compadrehackteam.geoforgood.util.CustomApp;

/**
 * Created by ricardo on 26/02/16.
 * TODO : NO TIME; NO COMMENTS, IT'S A HACKATON !
 */
public class UserManager {

    public static boolean isUserAuthenticated(){
        return SharedPreferencesManager.isUserAuthenticated(CustomApp.getContext());
    }

    public static void authenticateUser(){
        SharedPreferencesManager.setUserAuthenticated(CustomApp.getContext(),true);
    }

    public static void setUsernameAuthenticated(String username){
        SharedPreferencesManager.setUsername(CustomApp.getContext(),username);
    }

    public static String getUserName(){

        return SharedPreferencesManager.getUserName(CustomApp.getContext());
    }
}
