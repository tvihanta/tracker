package com.karvalakki.ippe.karvalakkitracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.osmdroid.views.MapView;


public class MainActivity extends Activity {

    static final String TAG = "mainActivity";

    ClientLocationManager gps;
    MapManager mMapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mMapView = (MapView)findViewById(R.id.mapview);
        if(savedInstanceState == null){
            mMapManager = new MapManager(this, mMapView);
            gps = new ClientLocationManager(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.v(TAG, "omg");
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        Log.v(TAG, "omg");
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
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_clear_paths:
                mMapManager.clearPaths();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void centerClientClick(View view) {
        mMapManager.centerOnClient();
    }
}
