package com.karvalakki.ippe.karvalakkitracker;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;


public class PolylineList {

    private static final Integer SIZE = 50;
    private ArrayList<GeoPoint> mList;

    public PolylineList(){
        mList = new ArrayList<GeoPoint>(5);
    }

    public void clear(){
        mList.clear();
    }

    public void addItem(GeoPoint point){
        mList.add(point);
        trimList();
    }

    private void trimList(){
        if(mList.size() > 0 && mList.size() > SIZE){
            getPath().remove(0);
        }
    }

    public ArrayList<GeoPoint> getPath() {
        return mList;
    }

    public void setPath(ArrayList<GeoPoint> mList) {
        this.mList = mList;
    }
}
