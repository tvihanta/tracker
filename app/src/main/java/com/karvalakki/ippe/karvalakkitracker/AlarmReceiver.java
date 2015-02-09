package com.karvalakki.ippe.karvalakkitracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private final static String TAG= "AlarmReceiver";
    MapManager mMapManager;


    public AlarmReceiver(MapManager manager){
        mMapManager = manager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive");
        new TrackerTask(){
            @Override public void onPostExecute(List<TrackerLocation> locations)
            {
                mMapManager.setTrackerPosition(locations);
            }
        }.execute(mMapManager.getLatestTrackerUpdateTimeStamp());
    }
}
