package com.example.triibe.triibeuserapp.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * @author michael.
 */
public final class Constants {

    public static final int NUM_QUALIFYING_QUESTIONS = 2;
    public static final int FOREGROUND_SERVICE = 5;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String ACTIVITY_EXTRA = "activityResult";
    public static final String BROADCAST_ACTION = "activityRecognition";
    public static final float GEOFENCE_RADIUS_IN_METERS = 1000;
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final String MALL_GEOFENCES = "mallGeofences";
    public static final String MALL_GEOFENCES_ADDED = "mallGeofencesAdded";
    public static final int APP_SERVICE_RUNNING_ID = 13;

    private Constants() {
    }

    public static final HashMap<String, LatLng> WESTFIELD_MALLS = new HashMap<>();
    static {

    }

    public static final HashMap<String, LatLng> WESTFIELD_LANDMARKS = new HashMap<>();
    static {

    }

    public static final HashMap<String, LatLng> TEST_MALLS = new HashMap<>();
    static {
        TEST_MALLS.put("home", new LatLng(-37.958503, 145.052802));
    }

    public static final HashMap<String, LatLng> TEST_LANDMARKS = new HashMap<>();
    static {
        TEST_LANDMARKS.put("home", new LatLng(-37.958503, 145.052802));
    }
}
