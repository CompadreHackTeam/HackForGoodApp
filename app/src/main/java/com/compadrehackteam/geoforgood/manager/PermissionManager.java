package com.compadrehackteam.geoforgood.manager;

import android.Manifest;

import com.compadrehackteam.geoforgood.util.CustomApp;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

/**
 * Created by ricardo on 20/01/16.
 */
public class PermissionManager {


    private static PermissionListener mLocationPermissionListener;

    /**
     * Interface for registration result
     */
    public interface PermissionManagerListener {

        // On Success register
        void onPermissionGranted();

        // On Error register
        void onPermissionDeny();
    }
    public static void initLocationListener(final PermissionManagerListener listener) {

        mLocationPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                SharedPreferencesManager.setLocationPermission(CustomApp.getContext(), true);
                listener.onPermissionGranted();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                SharedPreferencesManager.setLocationPermission(CustomApp.getContext(), false);
                listener.onPermissionDeny();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                // TODO : Something here
            }
        };
    }

    /**
     * This method checks the permissions for android M and greater
     */
    public static void checkPermissions() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermission(mLocationPermissionListener, Manifest.permission.ACCESS_FINE_LOCATION);
    }

}
