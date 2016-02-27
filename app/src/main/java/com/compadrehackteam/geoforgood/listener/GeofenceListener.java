package com.compadrehackteam.geoforgood.listener;

import com.compadrehackteam.geoforgood.model.GeofenceObject;

/**
 * The interface for a NimbeaconFence event
 */
public interface GeofenceListener {

    /**
     * Event triggered when an action of type Message should be launched.
     */
    void onGeofenceFound(int geofenceTransition, GeofenceObject n);

}



