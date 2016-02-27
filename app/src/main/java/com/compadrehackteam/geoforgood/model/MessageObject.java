package com.compadrehackteam.geoforgood.model;

/**
 * Created by ricardo on 26/02/16.
 */
public class MessageObject {

    /**
     * Geofence ID
     */
    String geofenceID;
    /**
     * User name
     */
    String username;
    /**
     * Content
     */
    String content;
    /**
     * Date
     */
    String date;

    public MessageObject() {
    }

    public MessageObject(String geofenceID, String username, String content, String date) {
        this.geofenceID = geofenceID;
        this.username = username;
        this.content = content;
        this.date = date;
    }

    public String getGeofenceID() {
        return geofenceID;
    }

    public void setGeofenceID(String geofenceID) {
        this.geofenceID = geofenceID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
