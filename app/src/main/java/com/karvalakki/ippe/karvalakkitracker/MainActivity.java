package com.karvalakki.ippe.karvalakkitracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import de.greenrobot.event.EventBus;


public class MainActivity extends FragmentActivity implements showZoomDialogListener {

    static final String TAG = "mainActivity";

    ClientLocationManager gps;
    MapManager mMapManager;
    EventBus mEventBus;

    String[] zoomArray;
    TextView mDistance;
    TextView mTimeSince;
    TextView mSpeed;


    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDistance = (TextView)findViewById(R.id.distance);
        mTimeSince = (TextView)findViewById(R.id.time);
        mSpeed = (TextView)findViewById(R.id.speed);

        zoomArray = getResources().getStringArray(R.array.zoom_levels);

        MapView mMapView = (MapView)findViewById(R.id.mapview);
        if(savedInstanceState == null){
            mMapManager = new MapManager(this, mMapView);
            gps = new ClientLocationManager(this);
        }
        setAlarm();
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Date d = mMapManager.getLatestTrackerUpdateDate();
                StringBuffer sb = new StringBuffer();
                long diffInSeconds = (new Date().getTime() - d.getTime()) / 1000;
                long diff[] = new long[] { 0, 0, 0, 0 };
                /* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
                /* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
                /* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
                /* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));
                String times[] = new String[] {"d", "h", "min", "s"};
                boolean b = false;
                for(int i = 0; i < diff.length; i++) {
                    if(diff[i] > 0 || b==true){
                        b = true; // one previous time >0 found already. -> show rest zeroes
                        sb.append(diff[i]);
                        sb.append(times[i]+" ");
                    }
                }
                mTimeSince.setText(sb);
                mHandler.postDelayed(this, 1000*1);
            }
        };
        mHandler.post(runnable);

    }

    /**
     * set labels on ui
     * @param event
     */
    public void onEvent(TrackerLocationEvent event) {
        Log.v(TAG, "Tracker Gps Event");
        if(event.loc != null) {
            GeoPoint clientLocation = mMapManager.getmClientPos();
            mDistance.setText(Integer.toString(clientLocation.distanceTo(event.loc)) + " m");
            mSpeed.setText(Float.toString(event.gettLoc().getSpeed()) + "km/h");


        }
    }

    public void setAlarm()
    {
        this.registerReceiver( new AlarmReceiver(mMapManager), new IntentFilter("com.karvalakki.ippe.karvalakkitracker") );
        PendingIntent pintent = PendingIntent.getBroadcast( this, 0, new Intent("com.karvalakki.ippe.karvalakkitracker"), 0 );
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));

        // TODO: set interval to settings
        manager.setRepeating(   AlarmManager.ELAPSED_REALTIME,
                                SystemClock.elapsedRealtime(),
                                1000*30,
                                pintent );
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //gps.restartGps();
        Log.v(TAG, "resume");
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        //gps.stopUsingGPS();
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
    }

    @Override
    public void onDialogListItemSelection(int i) {
        Log.v(TAG, "dialogEvent");
        int index = Integer.parseInt(zoomArray[i]);
        mMapManager.setmZoomLevel(index);
    }

    public void centerOnTracker(View view) {
        mMapManager.centerOnLastPath();
    }
}

