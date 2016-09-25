package com.example.triibe.triibeuserapp.track_location;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

/**
 * @author michael.
 */
public class GetCurrentLocationIntentService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationResult> {

    private static final String TAG = "GetCurrentLocation";
    GoogleApiClient mGoogleApiClient;
    private final IBinder mBinder = new GetCurrentLocationBinder();
    private double mLat = 10;
    private double mLon = 10;

    public GetCurrentLocationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Awareness.API)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onConnected: NO FINE LOCATION PERMISSION");
            return;
        }
        Awareness.SnapshotApi.getLocation(mGoogleApiClient).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationResult locationResult) {
        if (!locationResult.getStatus().isSuccess()) {
            Log.e(TAG, "Could not get location.");
            return;
        }
        Location location = locationResult.getLocation();
        mLat = location.getLatitude();
        mLon = location.getLongitude();
        Log.i(TAG, "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class GetCurrentLocationBinder extends Binder {
        public GetCurrentLocationIntentService getService() {
            return GetCurrentLocationIntentService.this;
        }
    }

    public double getLat() {
        return mLat;
    }

    public double getLon() {
        return mLon;
    }
}
