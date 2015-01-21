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
    Polyline mPolylineOverlay = null;

    PolylineList mTrackerPath = new PolylineList();

    Polyline mClientPolylineOverlay;
    PolylineList mClientPath = new PolylineList();

    Drawable mClientMarkerIcon ;

    public MapManager(Context context, MapView mapview) {
        mMapView = mapview;
        mContext = context;

        mClientMarkerIcon = mContext.getResources().getDrawable(R.drawable.gpsarrow);

        final MapTileProviderBasic tileProvider = new MapTileProviderBasic (context);
        String[] urls = {"http://tiles.kartat.kapsi.fi/peruskartta/"};
        final ITileSource tileSource = new XYTileSource("kapsi_tms", null, 1, 20, 256, ".jpg", urls);
        tileProvider.setTileSource(tileSource);
        mMapView.setTileSource(tileSource);

        GeoPoint start = new GeoPoint(66623298, 25872116);

        mMapView.getController().setCenter(start);
        mMapView.getController().setZoom(13);
        mMapView.setMaxZoomLevel(17);
        mMapView.setMinZoomLevel(7);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mOverlayManager = mMapView.getOverlayManager();
        setPolyline();
        createClientPolylineOverlay();

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(context, this);
        mMapView.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
    }

    public void clearPaths() {
        mTrackerPath.clear();
        mClientPath.clear();
        mOverlayManager.remove(mPolylineOverlay);
        mOverlayManager.remove(mClientPolylineOverlay);
        mPolylineOverlay = null;
        mClientPath = null;
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
            if(mClientPath == null){
                createClientPolylineOverlay();
            }
            addPathPoint(point, mClientPath, mClientPolylineOverlay);

        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        Log.v(TAG, geoPoint.toString());
        setPolyline();
        addPathPoint(geoPoint, mTrackerPath, mPolylineOverlay);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        return false;
    }

    public void setPolyline(){
        if(mPolylineOverlay == null){
            mPolylineOverlay  = new Polyline(mContext);
            mPolylineOverlay.setColor(Color.RED);
            mOverlayManager.add(mPolylineOverlay);
        }
    }

    private void createClientPolylineOverlay(){
        mClientPath = new PolylineList();
        mClientPolylineOverlay = new Polyline(mContext);
        mClientPolylineOverlay.setColor(Color.BLUE);
        mOverlayManager.add(mClientPolylineOverlay);
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
        mMapView.getController().setCenter(mClientMarker.getPosition());
    }
}
