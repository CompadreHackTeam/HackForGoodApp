package com.compadrehackteam.geoforgood.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by ricardo on 26/02/16.
 */
public class MessagePersist extends RealmObject {

    /**
     * Geofence ID
     */
    private String geofenceID;
    /**
     * User name
     */
    private String username;
    /**
     * Content
     */
    private String content;
    /**
     * Date
     */
    private Date date;

    public MessagePersist() {
    }

    public MessagePersist(String geofenceID, String username, String content, Date date) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
