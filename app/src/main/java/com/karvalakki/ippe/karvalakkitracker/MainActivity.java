package com.karvalakki.ippe.karvalakkitracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.osmdroid.views.MapView;

import java.util.Arrays;


public class MainActivity extends FragmentActivity implements showZoomDialogListener {

    static final String TAG = "mainActivity";

    ClientLocationManager gps;
    MapManager mMapManager;

    String[] zoomArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(TrackerService.class.getName()));

        zoomArray = getResources().getStringArray(R.array.zoom_levels);

        MapView mMapView = (MapView)findViewById(R.id.mapview);
        if(savedInstanceState == null){
            mMapManager = new MapManager(this, mMapView);
            gps = new ClientLocationManager(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        gps.restartGps();
        Log.v(TAG, "resume");
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        gps.stopUsingGPS();
        Log.v(TAG, "pause");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMapManager.unregister();
        gps.stopUsingGPS();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
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

    public void zoomLevel(View view) {
        String s = Integer.toString(mMapManager.getmZoomLevel());
        int index = Arrays.asList(zoomArray).indexOf(s);
        DialogFragment dfr = new showZoomDialog(index);
        dfr.show(getFragmentManager(), "zoom");

        //new HttpAsyncTask().execute("http://hmkcode.appspot.com/rest/controller/get.json");
    }

    @Override
    public void onDialogListItemSelection(int i) {
        Log.v(TAG, "dialogEvent");
        int index = Integer.parseInt(zoomArray[i]);
        mMapManager.setmZoomLevel(index);
    }
}

