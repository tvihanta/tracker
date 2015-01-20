package com.karvalakki.ippe.karvalakkitracker;

import android.location.Location;

public class ClientLocationEvent {

    public Location loc;
    public ClientLocationEvent(Location pLoc){
        loc = pLoc;
    }

}
