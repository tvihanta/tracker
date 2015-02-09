package com.karvalakki.ippe.karvalakkitracker;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;


public class PolylineList {

    private static final Integer SIZE = 50;
    private ArrayList<GeoPoint> mList;

    public PolylineList(){
        mList = new ArrayList<GeoPoint>(5);
    }
    public PolylineList(List<TrackerLocation> path){
        mList = new ArrayList<GeoPoint>();
        for (TrackerLocation element : path) {
            mList.add(element.getGeopoint());
        }
    }

    public void clear(){
        mList.clear();
    }

    public void addItem(GeoPoint point){
        mList.add(point);
        trimList();
    }

    private void trimList(){
        if(mList.size() > SIZE){
            getPath().remove(0);
        }
    }

    public ArrayList<GeoPoint> getPath() {
        return mList;
    }

    public void setPath(ArrayList<GeoPoint> mList) {
        this.mList = mList;
    }

    public void addMultiplePoints(List<TrackerLocation> locations) {
        for (TrackerLocation element : locations) {
            mList.add(element.getGeopoint());
        }
        trimList();
    }
}
