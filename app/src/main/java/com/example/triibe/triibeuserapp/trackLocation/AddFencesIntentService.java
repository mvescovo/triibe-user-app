package com.example.triibe.triibeuserapp.trackLocation;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.triibe.triibeuserapp.util.Constants;
import com.example.triibe.triibeuserapp.util.RunAppWhenAtMallService;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

/**
 * @author michael.
 */
public class AddFencesIntentService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private static final String TAG = "AddFences";

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private FenceUpdateRequest mMallFenceUpdateRequest;
    private FenceUpdateRequest mLandmarkFenceUpdateRequest;

    public AddFencesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, MallFenceReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Awareness.API)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String type = "";
        if (intent.getStringExtra("type") != null) {
            type = intent.getStringExtra("type");
        }

        if (type.contentEquals("mall")) {
            createMallFences();
        } else if (type.contentEquals("landmark")) {
            createLandmarkFences();
        }
    }

    public void createMallFences() {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        for (Map.Entry<String, LatLng> entry : Constants.WESTFIELD_MALLS.entrySet()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onHandleIntent: NO PERMISSION");
                return;
            }
            AwarenessFence location = LocationFence.in(
                    entry.getValue().latitude,
                    entry.getValue().longitude,
                    Constants.GEOFENCE_MALL_RADIUS_IN_METERS,
                    Constants.GEOFENCE_MALL_DWELL_IN_MILLISECONDS);
            builder.addFence(entry.getKey(), location, mPendingIntent).build();
        }
        mMallFenceUpdateRequest = builder.build();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            SharedPreferences preferences = getSharedPreferences(Constants.MALL_GEOFENCES, 0);
            boolean mallGeofencesAdded = preferences.getBoolean(Constants.MALL_GEOFENCES_ADDED, false);
            if (!mallGeofencesAdded && mMallFenceUpdateRequest != null) {
                addFences("mall");
            }
        }
    }

    public void createLandmarkFences() {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        for (Map.Entry<String, LatLng> entry : Constants.WESTFIELD_LANDMARKS.entrySet()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onHandleIntent: NO PERMISSION");
                return;
            }
            AwarenessFence location = LocationFence.in(
                    entry.getValue().latitude,
                    entry.getValue().longitude,
                    Constants.GEOFENCE_LANDMARK_RADIUS_IN_METERS,
                    Constants.GEOFENCE_LANDMARK_DWELL_IN_MILLISECONDS);
            builder.addFence(entry.getKey(), location, mPendingIntent).build();
        }
        mLandmarkFenceUpdateRequest = builder.build();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            addFences("landmark");
        }
    }

    private void addFences(String type) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google API client not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (type.contentEquals("mall")) {
            try {
                Awareness.FenceApi.updateFences(
                        mGoogleApiClient,
                        mMallFenceUpdateRequest
                ).setResultCallback(this);
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            }

            // Mark mall fences as added.
            SharedPreferences preferences = getSharedPreferences(Constants.MALL_GEOFENCES, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.MALL_GEOFENCES_ADDED, true);
            editor.apply();
        } else if (type.contentEquals("landmark")) {
            try {
                Awareness.FenceApi.updateFences(
                        mGoogleApiClient,
                        mLandmarkFenceUpdateRequest
                ).setResultCallback(this);
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SharedPreferences preferences = getSharedPreferences(Constants.MALL_GEOFENCES, 0);
        boolean mallGeofencesAdded = preferences.getBoolean(Constants.MALL_GEOFENCES_ADDED, false);
        if (!mallGeofencesAdded && mMallFenceUpdateRequest != null) {
            addFences("mall");
        }

        if (mLandmarkFenceUpdateRequest != null) {
            addFences("landmark");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: FAILED");
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.i(TAG, "Fence was successfully registered.");
        } else {
            Log.e(TAG, "Fence could not be registered: " + status);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Toast.makeText(this, "add fences service done", Toast.LENGTH_SHORT).show();
    }

    public static class MallFenceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            // Start or stop the app service
            Intent startAppServiceIntent = new Intent(context, RunAppWhenAtMallService.class);

            if (TextUtils.equals(fenceState.getFenceKey(), "southland")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In southland");
                        context.startService(startAppServiceIntent);
                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in southland");
                        context.stopService(startAppServiceIntent);
                        break;
                    case FenceState.UNKNOWN:
                        Log.d(TAG, "UNKONWN if in southland");
                        break;
                }
            }

            if (TextUtils.equals(fenceState.getFenceKey(), "eastSide")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In eastSide");
                        context.startService(startAppServiceIntent);
                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in eastSide");
                        context.stopService(startAppServiceIntent);
                        break;
                    case FenceState.UNKNOWN:
                        Log.d(TAG, "UNKONWN if in eastSide");
                        break;
                }
            }

            if (TextUtils.equals(fenceState.getFenceKey(), "westSide")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In westSide");
                        context.startService(startAppServiceIntent);
                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in westSide");
                        context.stopService(startAppServiceIntent);
                        break;
                    case FenceState.UNKNOWN:
                        Log.d(TAG, "UNKONWN if in westSide");
                        break;
                }
            }

            if (TextUtils.equals(fenceState.getFenceKey(), "furtherSouth")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In furtherSouth");
                        context.startService(startAppServiceIntent);
                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in furtherSouth");
                        context.stopService(startAppServiceIntent);
                        break;
                    case FenceState.UNKNOWN:
                        Log.d(TAG, "UNKONWN if in furtherSouth");
                        break;
                }
            }
        }
    }

    public static class BootDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Intent addMallFencesIntent = new Intent(context, AddFencesIntentService.class);
                addMallFencesIntent.putExtra("type", "mall");
                context.startService(addMallFencesIntent);
            }
        }
    }
}
