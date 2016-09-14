package com.example.triibe.triibeuserapp.trackLocation;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.example.triibe.triibeuserapp.util.RunAppWhenAtMallService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

/**
 * @author michael.
 */
public class MallGeofenceTransitionIntentService extends IntentService {

    private static final String TAG = "MallGeofenceTransition";

    public MallGeofenceTransitionIntentService() {
        super("MallGeofenceTransitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Start or stop the app service
        Intent startAppServiceIntent = new Intent(getApplicationContext(), RunAppWhenAtMallService.class);
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            startService(startAppServiceIntent);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            stopService(startAppServiceIntent);
        }
    }

    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pendingIntents";
            default:
                return "Unknown geofence error";
        }
    }
}
