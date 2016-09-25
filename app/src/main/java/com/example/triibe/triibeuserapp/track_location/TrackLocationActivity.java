//package com.example.triibe.triibeuserapp.trackLocation;
//
//import android.Manifest;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.triibe.triibeuserapp.R;
//import com.example.triibe.triibeuserapp.util.Constants;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.ActivityRecognition;
//import com.google.android.gms.location.DetectedActivity;
//import com.google.android.gms.location.Geofence;
//import com.google.android.gms.location.GeofencingRequest;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.model.LatLng;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import pub.devrel.easypermissions.EasyPermissions;
//
//public class TrackLocationActivity extends AppCompatActivity implements
//        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
//        LocationListener, EasyPermissions.PermissionCallbacks, ResultCallback<Status> {
//
//    private static final String TAG = "TrackLocationActivity";
//    private static final int FINE_LOCAITON = 123;
//    GoogleApiClient mGoogleApiClient;
//    LocationRequest mLocationRequest;
//    Location mLastLocation;
//    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
//    ActivityDetectionBroadcastReceiver mBroadcastReceiver;
//
//    @BindView(R.id.lat)
//    TextView mLat;
//    @BindView(R.id.lon)
//    TextView mLon;
//    @BindView(R.id.activity)
//    TextView mActivity;
//    @BindView(R.id.track_activity)
//    Button mTrackActivity;
//    @BindView(R.id.stop_tracking_activity)
//    Button mStopTrackingActivity;
//    @BindView(R.id.add_geofences)
//    Button mAddGeofences;
//    @BindView(R.id.remove_geofences)
//    Button mRemoveGeofences;
//    private ArrayList<Geofence> mGeofences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_track_location);
//        ButterKnife.bind(this);
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addApi(ActivityRecognition.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        // Geofence stuff
//        mGeofences = new ArrayList<>();
//        populateGeofenceList();
//
//        mAddGeofences.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addGeofences();
//            }
//        });
//
//        mRemoveGeofences.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                removeGeofences();
//            }
//        });
//
//        // Activity detection stuff
//        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
//
//        mTrackActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                requestActivityUpdates();
//            }
//        });
//
//        mStopTrackingActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                removeActivityUpdates();
//            }
//        });
//    }
//
//    private void populateGeofenceList() {
//        for (Map.Entry<String, LatLng> entry : Constants.WESTFIELD_LANDMARKS.entrySet()) {
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
//    }
//
//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(mGeofences);
//        return builder.build();
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
//            Log.d(TAG, "addGeofences: probably no permission for ACCESS_FINE_LOCATION");
//        }
//    }
//
//    private void removeGeofences() {
//        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, "Google API client not connected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            LocationServices.GeofencingApi.removeGeofences(
//                    mGoogleApiClient,
//                    getGeofencePendingIntent()
//            ).setResultCallback(this);
//        } catch (SecurityException securityException) {
//            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
//            Log.d(TAG, "addGeofences: probably no permission for ACCESS_FINE_LOCATION");
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.connect();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Log.d(TAG, "onConnected: CONNECTED");
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(1000);
//        if (EasyPermissions.hasPermissions(this, perms)) {
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
//                    mLocationRequest, this);
//
//        } else {
//            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, "Need location access to monitor location",
//                    FINE_LOCAITON, perms);
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.d(TAG, "onConnectionSuspended: SUSPENDED");
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed: FAILED");
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//        if (mLastLocation != null) {
//            Log.i(TAG, "onLocationChanged: " + mLastLocation.toString());
//            mLat.setText(String.format("%s", mLastLocation.getLatitude()));
//            mLon.setText(String.format("%s", mLastLocation.getLongitude()));
//        }
//
//    }
//
//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        Log.d(TAG, "onPermissionsGranted: GRANTED");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
//                mLocationRequest, this);
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        Log.d(TAG, "onPermissionsDenied: DENIED");
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
//                new IntentFilter(Constants.BROADCAST_ACTION));
//        requestActivityUpdates();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//        removeActivityUpdates();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
//    }
//
//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, this);
//    }
//
//    private void requestActivityUpdates() {
//        if (!mGoogleApiClient.isConnected()) {
//            Log.d(TAG, "requestActivityUpdates: googleApiClient not connected");
//            return;
//        }
//        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 1000,
//                getActivityDetectionPendingIntent()).setResultCallback(this);
//    }
//
//    private void removeActivityUpdates() {
//        if (!mGoogleApiClient.isConnected()) {
//            Log.d(TAG, "requestActivityUpdates: googleApiClient not connected");
//            return;
//        }
//        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient,
//                getActivityDetectionPendingIntent()).setResultCallback(this);
//    }
//
//    private PendingIntent getActivityDetectionPendingIntent() {
//        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    @Override
//    public void onResult(@NonNull Status status) {
//        if (status.isSuccess()) {
//            Log.d(TAG, "onResult: successfully added activity detection or geofence");
//        } else {
//            Log.d(TAG, "onResult: error adding or removing activity detection or geofence");
//        }
//    }
//
//    public class MallDetectionBroadcastReceiver extends BroadcastReceiver {
//
//        private static final String TAG = "MallDetectionReceiver";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "onReceive: called");
//        }
//    }
//
//    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
//
//        private static final String TAG = "BroadcastReceiver";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
//            String currentActivity = "none received";
//            int currentActivityPercent = 0;
//            for (DetectedActivity detectedActivity :
//                    detectedActivities) {
//                String type;
//                int intType = detectedActivity.getType();
//
//                switch (intType) {
//                    case DetectedActivity.IN_VEHICLE:
//                        type = "Driving";
//                        break;
//                    case DetectedActivity.ON_BICYCLE:
//                        type = "On bicycle";
//                        break;
//                    case DetectedActivity.ON_FOOT:
//                        type = "On foot";
//                        break;
//                    case DetectedActivity.RUNNING:
//                        type = "Running";
//                        break;
//                    case DetectedActivity.STILL:
//                        type = "Still";
//                        break;
//                    case DetectedActivity.TILTING:
//                        type = "Tilting";
//                        break;
//                    case DetectedActivity.UNKNOWN:
//                        type = "Unknown";
//                        break;
//                    case DetectedActivity.WALKING:
//                        type = "Walking";
//                        break;
//                    default:
//                        type = "none received";
//                        break;
//                }
//                if (detectedActivity.getConfidence() > currentActivityPercent) {
//                    currentActivityPercent = detectedActivity.getConfidence();
//                    currentActivity = type;
//                }
//                Log.d(TAG, "Activity: " + type + " confidence: " + detectedActivity.getConfidence());
//            }
//            mActivity.setText("Current activity: " + currentActivity + ". Confidence: " + currentActivityPercent);
//        }
//    }
//}
