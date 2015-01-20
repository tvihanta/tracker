package com.karvalakki.ippe.karvalakkitracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
    List<GeoPoint> mTrackerPath = new ArrayList<>();

    public MapManager(Context context, MapView mapview) {
        mMapView = mapview;
        mContext = context;
        final MapTileProviderBasic tileProvider = new MapTileProviderBasic (context);

        String[] urls = {"http://tiles.kartat.kapsi.fi/peruskartta/"};
        final ITileSource tileSource = new XYTileSource("kapsi_tms", null, 1, 20, 256, ".jpg", urls);
        tileProvider.setTileSource(tileSource);
        mMapView.setTileSource(tileSource);

        GeoPoint point2 = new GeoPoint(66623298, 25872116);

        mMapView.getController().setCenter(point2);
        mMapView.getController().setZoom(13);
        mMapView.setMaxZoomLevel(17);
        mMapView.setMinZoomLevel(7);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mOverlayManager = mMapView.getOverlayManager();
        setPolyline();

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(context, this);
        mMapView.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
    }

    public void clearPaths() {
        mTrackerPath.clear();
        mOverlayManager.remove(mPolylineOverlay);
        mPolylineOverlay = null;
        mMapView.invalidate();
    }


    public void onEvent(ClientLocationEvent event) {

        Log.v(TAG, "eventti");
        if(event != null && event.loc != null){
            if(mClientMarker == null){
                addClientMarker(event.loc.getLatitude(), event.loc.getLongitude());
            }else{
                mClientMarker.setPosition(new GeoPoint(event.loc.getLatitude(), event.loc.getLongitude()));
            }

        }

    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        Log.v(TAG, geoPoint.toString());
        addPathPoint(geoPoint);
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

    public void addClientMarker(double lat, double lon){
        GeoPoint point = new GeoPoint(lat,lon);
        mClientMarker = new Marker(mMapView);
        mClientMarker.setPosition(point);
        mClientMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(mClientMarker);
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
}
