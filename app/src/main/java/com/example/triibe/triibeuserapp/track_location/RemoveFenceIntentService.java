package com.example.triibe.triibeuserapp.track_location;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.triibe.triibeuserapp.util.Constants;
import com.example.triibe.triibeuserapp.util.Globals;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.triibe.triibeuserapp.track_location.AddFencesIntentService.EXTRA_FENCE_KEY;
import static com.example.triibe.triibeuserapp.track_location.AddFencesIntentService.EXTRA_TRIIBE_FENCE_TYPE;
import static com.example.triibe.triibeuserapp.track_location.AddFencesIntentService.TYPE_LANDMARK;
import static com.example.triibe.triibeuserapp.track_location.AddFencesIntentService.TYPE_MALL;

/**
 * @author michael.
 */
public class RemoveFenceIntentService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "RemoveFence";
    private GoogleApiClient mGoogleApiClient;
    private volatile List<Intent> mIntents;

    public RemoveFenceIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mIntents = new ArrayList<>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Awareness.API)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!mGoogleApiClient.isConnected()) {
            mIntents.add(intent);
            mGoogleApiClient.connect();
        } else {
            String type = "";
            if (intent.getStringExtra(EXTRA_TRIIBE_FENCE_TYPE) != null) {
                type = intent.getStringExtra(EXTRA_TRIIBE_FENCE_TYPE);
            }

            if (type.contentEquals(TYPE_MALL)) {
                SharedPreferences preferences = getSharedPreferences(Constants.MALL_FENCES, 0);
                boolean mallGeofencesAdded = preferences.getBoolean(Constants.MALL_FENCES_ADDED, false);
                if (mallGeofencesAdded) {
                    removeMallFences();
                }

            } else if (type.contentEquals(TYPE_LANDMARK)) {
                String key = "";
                if (intent.getStringExtra(EXTRA_FENCE_KEY) != null) {
                    key = intent.getStringExtra(EXTRA_FENCE_KEY);
                }
                if (!key.contentEquals("") ) {
                    removeLandmarkFence(key);
                }
            }
        }
    }

    public void removeMallFences() {
        for (Map.Entry<String, LatLng> entry : Constants.WESTFIELD_MALLS.entrySet()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onHandleIntent: NO PERMISSION");
                return;
            }
            removeFence(entry.getKey());
        }

        // Mark mall fences as not added.
        SharedPreferences preferences = getSharedPreferences(Constants.MALL_FENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.MALL_FENCES_ADDED, false);
        editor.apply();
    }

    public void removeLandmarkFence(String fenceKey) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onHandleIntent: NO PERMISSION");
            return;
        }
        removeFence(fenceKey);
        Globals.getInstance().removeLandmarkFence(fenceKey);
    }

    public void removeFence(final String fenceKey) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        for (int i = 0; i < mIntents.size(); i++) {
            onHandleIntent(mIntents.get(i));
        }
        mIntents.clear();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: FAILED");
    }
}
