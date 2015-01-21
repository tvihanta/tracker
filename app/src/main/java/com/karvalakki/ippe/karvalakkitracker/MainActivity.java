package com.karvalakki.ippe.karvalakkitracker;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.osmdroid.views.MapView;


public class MainActivity extends ActionBarActivity  {

    static final String TAG = "mainActivity";

    ClientLocationManager gps;
    MapManager mMapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapView mMapView = (MapView)findViewById(R.id.mapview);

        mMapManager = new MapManager(this, mMapView);
        gps = new ClientLocationManager(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                gps.getLocation();
                break;
            case R.id.action_clear_paths:
                mMapManager.clearPaths();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
