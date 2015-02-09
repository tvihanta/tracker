package com.karvalakki.ippe.karvalakkitracker;

import java.util.List;

public interface TrackerInterface {
    public List<TrackerLocation> getLatestTrackerLocation(String since);
}
