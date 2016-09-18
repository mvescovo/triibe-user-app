package com.example.triibe.triibeuserapp.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * @author michael.
 */
public final class Constants {

    public static final int NUM_QUALIFYING_QUESTIONS = 2;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final float FENCE_MALL_RADIUS_IN_METERS = 500;
    public static final long FENCE_MALL_DWELL_IN_MILLISECONDS = 1000;
    public static final long FENCE_LANDMARK_DWELL_IN_MILLISECONDS = 1000;
    public static final float FENCE_LANDMARK_RADIUS_IN_METERS = 10;
    public static final long FENCE_EXPIRATION_IN_HOURS = 12;
    public static final long FENCE_EXPIRATION_IN_MILLISECONDS = FENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final String MALL_FENCES = "mallGeofences";
    public static final String MALL_FENCES_ADDED = "mallGeofencesAdded";
    public static final int APP_SERVICE_RUNNING_ID = 13;
    public static final String ACTIVITY_EXTRA = "activityResult";
    public static final String BROADCAST_ACTION = "activityRecognition";


    public static final HashMap<String, LatLng> WESTFIELD_MALLS = new HashMap<>();
    static {
        WESTFIELD_MALLS.put("southland", new LatLng(-37.958388, 145.052658));
    }

    public static final HashMap<String, LatLng> WESTFIELD_LANDMARKS = new HashMap<>();
    static {
        WESTFIELD_LANDMARKS.put("eastSide", new LatLng(-37.958201, 145.056192));
        WESTFIELD_LANDMARKS.put("westSide", new LatLng(-37.958876, 145.049591));
    }
}
