package com.karvalakki.ippe.karvalakkitracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * actually just a server interface
 */
public class TK102 implements TrackerInterface {
    private final static String TAG ="TK102";

    private Context mContext;

    public TK102(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public List<TrackerLocation> getLatestTrackerLocation(String since) {
        if(isNetworkAvailable()){
            String response = "";
            DefaultHttpClient client = new DefaultHttpClient();
            //TODO: set some kind of system to set devices
            String url = "<url to Server>";
            if(since != null){
                try {
                    String query = URLEncoder.encode(since, "utf-8");
                    url = url + "?date="+query;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

                Log.v(TAG, response );
            } catch (Exception e) {
                Log.v(TAG, "error connecting to server");
                //e.printStackTrace();
            }
            return JSONtoTrackerLocation(response);
        }
        return null;
    }

    private List<TrackerLocation> JSONtoTrackerLocation(String json){
        try {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<TrackerLocation>>(){}.getType();
            List<TrackerLocation> locs = gson.fromJson(json, collectionType);
            return locs;
        } catch (Exception e) {
            Log.v(TAG, "unable to parse response from server");
            //e.printStackTrace();
            return new ArrayList<TrackerLocation>();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
