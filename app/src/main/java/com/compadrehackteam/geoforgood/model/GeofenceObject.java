package com.compadrehackteam.geoforgood.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ricardo on 26/02/16.
 */
public class GeofenceObject {

    /**
     * The NimbeesGeofence id
     */
    @SerializedName("_id")
    private String id;

    /**
     * The Geofence name
     */
    private String name;
    /**
     * Latitude
     */
    private double latitude;

    /**
     * Longitude
     */
    private double longitude;

    /**
     * Radius
     */
    private float radius;

    public GeofenceObject() {
    }

    public GeofenceObject(String id, String name, double latitude, double longitude, float radius) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
