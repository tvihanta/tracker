package com.karvalakki.ippe.karvalakkitracker;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

/**
 */
public class TrackerLocationEvent {

    public GeoPoint loc;
    private TrackerLocation tLoc = null;
    public TrackerLocation gettLoc() {
        return tLoc;
    }
    public void settLoc(TrackerLocation tLoc) {
        this.tLoc = tLoc;
    }

    private Date lastSince;
    public Date getLastSince() {
        return lastSince;
    }
    public void setLastSince(Date lastSince) {
        this.lastSince = lastSince;
    }

    public TrackerLocationEvent(GeoPoint pLoc){
        loc = pLoc;
    }

}
