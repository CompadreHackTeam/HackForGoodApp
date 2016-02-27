package com.compadrehackteam.geoforgood.rest;

import com.compadrehackteam.geoforgood.model.GeofenceObject;
import com.compadrehackteam.geoforgood.model.MessageObject;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * This class implements the retrofit calls to the server.
 * The configuration is implemented in ApiService.java.
 */

public interface EndpointInterface {

    @GET("/api/download")
    void getGeofences(Callback<List<GeofenceObject>> geofenceCallback);

    @GET("/api/getMsg/{geoID}")
    void getMessagesOfGeofence(@Path("geoID") String geoID, Callback<List<MessageObject>> messageObjectCallback);

    @POST("/api/putMsgJSON")
    void sendMessage(@Body MessageObject message, Callback<MessageObject> messageObjectCallback);
}

