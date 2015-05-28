package com.karvalakki.ippe.karvalakkitracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrackerTask extends AsyncTask<String, String, List<TrackerLocation>> {
    private Context mContext;
    public TrackerTask (Context context){
        mContext = context;
    }
    private final String TAG = "TrackerTask";

    @Override
    protected List<TrackerLocation> doInBackground(String... params) {

        //TODO: select interface to use according to param etc...
        String timestamp = params[0];
        TrackerInterface tk = new TK102(mContext);
        return tk.getLatestTrackerLocation(timestamp);
    }

}
