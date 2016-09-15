//package com.example.triibe.triibeuserapp.trackLocation;
//
//import android.app.IntentService;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.example.triibe.triibeuserapp.util.Constants;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.Geofence;
//import com.google.android.gms.location.GeofencingRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.model.LatLng;
//
//import java.util.ArrayList;
//import java.util.Map;
//
///**
// * @author michael.
// */
//public class AddLandmarkGeofencesIntentService extends IntentService
//        implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        ResultCallback<Status> {
//
//    private static final String TAG = "AddLandmarkGeofences";
//
//    GoogleApiClient mGoogleApiClient;
//    private ArrayList<Geofence> mGeofences;
//
//    public AddLandmarkGeofencesIntentService() {
//        super("AddLandmarkGeofencesIntentService");
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGeofences = new ArrayList<>();
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//        for (Map.Entry<String, LatLng> entry : Constants.TEST_LANDMARKS.entrySet()) {
//            mGeofences.add(new Geofence.Builder()
//                    .setRequestId(entry.getKey())
//                    .setCircularRegion(
//                            entry.getValue().latitude,
//                            entry.getValue().longitude,
//                            Constants.FENCE_LANDMARK_RADIUS_IN_METERS
//                    )
//                    .setExpirationDuration(Constants.FENCE_EXPIRATION_IN_MILLISECONDS)
//                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                            Geofence.GEOFENCE_TRANSITION_EXIT)
//                    .build());
//        }
//
//        if (!mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.connect();
//        } else {
//            addGeofences();
//        }
//    }
//
//    private void addGeofences() {
//        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, "Google API client not connected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            LocationServices.GeofencingApi.addGeofences(
//                    mGoogleApiClient,
//                    getGeofencingRequest(),
//                    getGeofencePendingIntent()
//            ).setResultCallback(this);
//        } catch (SecurityException securityException) {
//            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
//        }
//
//        // Mark geofences as added.
//        SharedPreferences preferences = getSharedPreferences(Constants.MALL_FENCES, 0);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean(Constants.MALL_FENCES_ADDED, true);
//        editor.apply();
//    }
//
//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(mGeofences);
//        return builder.build();
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        Intent intent = new Intent(this, LandmarkGeofenceTransitionIntentService.class);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        addGeofences();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.d(TAG, "onConnectionSuspended: googleApiClient");
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed: could not connect");
//    }
//
//    @Override
//    public void onResult(@NonNull Status status) {
//        if (status.isSuccess()) {
//            Log.d(TAG, "onResult: successfully added or removed landmark geofence");
//        } else {
//            Log.d(TAG, "onResult: error adding or removing landmark geofence");
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//        Toast.makeText(this, "add landmark geofences service done", Toast.LENGTH_SHORT).show();
//    }
//}
