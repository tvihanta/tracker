package com.karvalakki.ippe.karvalakkitracker;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

public class TrackerLocation {

    float lat;
    float lon;
    float bearing;
    float speed;
    String created;
    String since;

    public TrackerLocation(long lat, long lon, long bearing, long speed) {
        this.lat = lat;
        this.lon = lon;
        this.bearing = bearing;
        this.speed = speed;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(long bearing) {
        this.bearing = bearing;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public GeoPoint getGeopoint(){
        return new GeoPoint(lat, lon);
    }

    public String getSince() {
        return since;
    }
    public void setSince(String since) {
        this.since = since;
    }

}
