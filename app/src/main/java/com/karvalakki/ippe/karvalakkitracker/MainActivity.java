package com.karvalakki.ippe.karvalakkitracker;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements MapEventsReceiver {

    static final String TAG = "mainActivity";
    ResourceProxy mResourceProxy;
    MapView mMapView;
    Polyline mPolylineOverlay = null;
    List<GeoPoint> mTrackerPath = new ArrayList<>();


    OverlayManager mOverlayManager;

    GeoPoint mClickedGeoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView)findViewById(R.id.mapview);

        final MapTileProviderBasic tileProvider = new MapTileProviderBasic (getApplicationContext());

        // create the WMS tile source
        String[] urls = {"http://tiles.kartat.kapsi.fi/peruskartta/"};
        final ITileSource tileSource = new XYTileSource("kapsi_tms", null, 1, 20, 256, ".jpg", urls);
        tileProvider.setTileSource(tileSource);
        mMapView.setTileSource(tileSource);


       /* final TilesOverlay tilesOverlay = new TilesOverlay(tileProvider, this.getBaseContext());
        tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
        mMapView.getOverlays().add(tilesOverlay);
*/
        GeoPoint point2 = new GeoPoint(66623298, 25872116);

        mMapView.getController().setCenter(point2);
        mMapView.getController().setZoom(13);
        mMapView.setMaxZoomLevel(17);
        mMapView.setMinZoomLevel(7);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mOverlayManager = mMapView.getOverlayManager();

        setPolyline();

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mMapView.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays
    }

    public void setPolyline(){
        if(mPolylineOverlay == null){
            mPolylineOverlay  = new Polyline(this);
            mPolylineOverlay.setColor(Color.RED);
            mOverlayManager.add(mPolylineOverlay);
        }
    }

    public void addMarker(GeoPoint point)
    {
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
    }

    public void addPathPoint(GeoPoint point){
        setPolyline();
        mTrackerPath.add(point);
        mPolylineOverlay.setPoints(mTrackerPath);
        mMapView.invalidate();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (id){
            case R.id.action_settings:
                break;
            case R.id.action_clear_paths:
                clearPaths();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearPaths() {
        mTrackerPath.clear();
        mOverlayManager.remove(mPolylineOverlay);
        mPolylineOverlay = null;
        mMapView.invalidate();
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        mClickedGeoPoint = geoPoint;
        Log.v(TAG, geoPoint.toString());
        addPathPoint(geoPoint);
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        return false;
    }

   /* @Override
    public boolean longPressHelper(GeoPoint p) {

    }
*/
}
