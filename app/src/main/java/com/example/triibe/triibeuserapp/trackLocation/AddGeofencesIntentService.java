//package com.example.triibe.triibeuserapp.trackLocation;
//
//import android.app.IntentService;
//import android.app.PendingIntent;
//import android.content.Context;
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
//public class AddGeofencesIntentService extends IntentService
//        implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        ResultCallback<Status> {
//
//    private static final String TAG = "AddGeofences";
//    public static final String ACTION_ADD_MALL_GEOFENCES = "com.example.triibe.triibeuserapp.trackLocation.action.ADD_MALL_GEOFENCES";
//    public static final String ACTION_ADD_LANDMARK_GEOFENCES = "com.example.triibe.triibeuserapp.trackLocation.action.ADD_LANDMARK_GEOFENCES";
//
//    private static final String EXTRA_GEOFENCE_TYPE = "com.example.triibe.triibeuserapp.trackLocation.extra.GEOFENCE_TYPE";
//    private static final String EXTRA_PARAM2 = "com.example.triibe.triibeuserapp.trackLocation.extra.PARAM2";
//
//    GoogleApiClient mGoogleApiClient;
//    private ArrayList<Geofence> mGeofences;
//    private String mType;
//
//    public AddGeofencesIntentService() {
//        super("AddGeofencesIntentService");
//    }
//
//    public static void addMallGeofences(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, AddGeofencesIntentService.class);
//        intent.setAction(ACTION_ADD_MALL_GEOFENCES);
//        intent.putExtra(EXTRA_GEOFENCE_TYPE, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }
//
//    public static void addLandmarkGeofences(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, AddGeofencesIntentService.class);
//        intent.setAction(ACTION_ADD_LANDMARK_GEOFENCES);
//        intent.putExtra(EXTRA_GEOFENCE_TYPE, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
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
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_ADD_MALL_GEOFENCES.equals(action)) {
//                mType = "mall";
//                final String param1 = intent.getStringExtra(EXTRA_PARAM2);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleAddMallGeofences(param1, param2);
//            } else if (ACTION_ADD_LANDMARK_GEOFENCES.equals(action)) {
//                mType = "landmark";
//                final String param1 = intent.getStringExtra(EXTRA_PARAM2);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleAddLandmarkGeofences(param1, param2);
//            }
//        }
//    }
//
//    /**
//     * Handle action Foo in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleAddMallGeofences(String param1, String param2) {
////        populateGeofenceList(Constants.GEOFENCE_MALL_RADIUS_IN_METERS);
//
//        for (Map.Entry<String, LatLng> entry : Constants.TEST_MALLS.entrySet()) {
//            mGeofences.add(new Geofence.Builder()
//                    .setRequestId(entry.getKey())
//                    .setCircularRegion(
//                            entry.getValue().latitude,
//                            entry.getValue().longitude,
//                            Constants.GEOFENCE_MALL_RADIUS_IN_METERS
//                    )
//                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
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
//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleAddLandmarkGeofences(String param1, String param2) {
////        populateGeofenceList(Constants.GEOFENCE_LANDMARK_RADIUS_IN_METERS);
//
//        for (Map.Entry<String, LatLng> entry : Constants.TEST_LANDMARKS.entrySet()) {
//            mGeofences.add(new Geofence.Builder()
//                    .setRequestId(entry.getKey())
//                    .setCircularRegion(
//                            entry.getValue().latitude,
//                            entry.getValue().longitude,
//                            Constants.GEOFENCE_LANDMARK_RADIUS_IN_METERS
//                    )
//                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
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
////    private void populateGeofenceList(float radius) {}
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
//        SharedPreferences preferences = getSharedPreferences(Constants.MALL_GEOFENCES, 0);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean(Constants.MALL_GEOFENCES_ADDED, true);
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
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
//        intent.putExtra("type", mType);
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
//            Log.d(TAG, "onResult: successfully added or removed geofence");
//        } else {
//            Log.d(TAG, "onResult: error adding or removing geofence");
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
//        Toast.makeText(this, "add geofences service done", Toast.LENGTH_SHORT).show();
//    }
//}
