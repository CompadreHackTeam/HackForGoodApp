package com.compadrehackteam.geoforgood.util;

import android.app.Application;
import android.content.Context;

import com.compadrehackteam.geoforgood.listener.GeofenceListener;
import com.compadrehackteam.geoforgood.manager.GeofenceManager;
import com.compadrehackteam.geoforgood.model.GeofenceObject;
import com.karumi.dexter.Dexter;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ricardo on 19/01/16.
 */
public class CustomApp extends Application {

    /**
     * The log id field
     */
    private static final String LOG = "GeoForGood";
    /**
     * The Application context
     */
    private static Context sContext;
    /**
     * The geofence Manager singleton
     */
    private static GeofenceManager sGeofenceManager;


    @Override
    public void onCreate() {
        super.onCreate();

        // The context
        sContext = getApplicationContext();

        //Dexter Permission library
        Dexter.initialize(this);

        // RealM database configuration
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this)
                .name("geoforgood.realm")
                .schemaVersion(0)
                .build());

    }

    public static Context getContext() {
        return sContext;
    }
}
