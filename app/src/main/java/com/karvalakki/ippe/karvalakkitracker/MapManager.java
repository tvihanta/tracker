package com.karvalakki.ippe.karvalakkitracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.karvalakki.ippe.karvalakkitracker.R;

import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayManager;
import java.util.ArrayList;
import java.util.List;
import de.greenrobot.event.EventBus;


public class MapManager implements MapEventsReceiver {

    private static final String TAG ="mapManager";
    private Context mContext;
    private Marker mClientMarker = null;
    private EventBus mEventBus;
    OverlayManager mOverlayManager;
    MapView mMapView;

    MapTileProviderBasic tileProvider;
    final ITileSource tileSource;

    public int getmZoomLevel() {
        mZoomLevel = mMapView.getZoomLevel();
        return mZoomLevel;
    }
    public void setmZoomLevel(int mZoomLevel) {
        this.mZoomLevel = mZoomLevel;
        mMapView.getController().setZoom(mZoomLevel);
    }
    int mZoomLevel = 13;

    private Polyline mPolylineOverlay;
    public Polyline getmPolylineOverlay() {
        if(mPolylineOverlay == null)
        {
            mPolylineOverlay  = new Polyline(mContext);
            mPolylineOverlay.setColor(Color.RED);
            mOverlayManager.add(mPolylineOverlay);
        }
        return mPolylineOverlay;
    }
    PolylineList mTrackerPath = new PolylineList();



    private Polyline mClientPolylineOverlay;
    public Polyline getmClientPolylineOverlay() {
        if(mClientPolylineOverlay == null){
            mClientPolylineOverlay = new Polyline(mContext);
            mClientPolylineOverlay.setColor(Color.BLUE);
            mOverlayManager.add(mClientPolylineOverlay);
        }

        return mClientPolylineOverlay;
    }

    PolylineList mClientPath = new PolylineList();
    Drawable mClientMarkerIcon ;

    public MapManager(Context context, MapView mapview) {
        mMapView = mapview;
        mContext = context;

        mClientMarkerIcon = mContext.getResources().getDrawable(R.drawable.gpsarrow);

        tileProvider = new MapTileProviderBasic (context);
        String[] urls = {"http://tiles.kartat.kapsi.fi/peruskartta/"};
        tileSource = new XYTileSource("kapsi_tms", null, 1, 20, 256, ".jpg", urls);
        tileProvider.setTileSource(tileSource);
        mMapView.setTileSource(tileSource);

        GeoPoint start = new GeoPoint(66623298, 25872116);

        mMapView.getController().setCenter(start);
        mMapView.getController().setZoom(mZoomLevel);
        mMapView.setMaxZoomLevel(17);
        mMapView.setMinZoomLevel(7);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mOverlayManager = mMapView.getOverlayManager();

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(context, this);
        mMapView.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
    }

    public void unregister(){
        mMapView.getOverlays().remove(tileProvider);
        tileProvider.clearTileCache();
        tileProvider.detach();
        tileProvider = null;
    }

    public void clearPaths() {
        mTrackerPath.clear();
        mClientPath.clear();
        mOverlayManager.remove(mPolylineOverlay);
        mOverlayManager.remove(mClientPolylineOverlay);
        mPolylineOverlay = null;
        mClientPolylineOverlay = null;
        mMapView.invalidate();
    }


    public void onEvent(ClientLocationEvent event) {
        Log.v(TAG, "client Gps Event");
        if(event != null && event.loc != null){
            GeoPoint point = new GeoPoint(event.loc.getLatitude(), event.loc.getLongitude());
            if(mClientMarker == null){
                addClientMarker(point,
                                event.loc.getBearing()
                                );
            }else{
                mClientMarker.setPosition(point);
                mClientMarker.setRotation(event.loc.getBearing());
            }

            //TODO maybe add a setting for showing path
            addPathPoint(point, mClientPath, getmClientPolylineOverlay());

        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        Log.v(TAG, geoPoint.toString());
        addPathPoint(geoPoint, mTrackerPath, getmPolylineOverlay());
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        return false;
    }

    public void addClientMarker(GeoPoint point, float bearing){
        mClientMarker = new Marker(mMapView);
        mClientMarker.setPosition(point);
        mClientMarker.setRotation(bearing);

        mClientMarker.setIcon(mClientMarkerIcon);
        mClientMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        mMapView.getOverlays().add(mClientMarker);
    }
    public void addMarker(GeoPoint point)
    {
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
    }

    private void addPathPoint(GeoPoint point, PolylineList list, Polyline overlay){
        if(list != null){
            list.addItem(point);
            overlay.setPoints(list.getPath());
            mMapView.invalidate();
        }
    }

    public void centerOnClient() {
        if(mClientMarker != null){
            mMapView.getController().setCenter(mClientMarker.getPosition());
        }

    }
}
