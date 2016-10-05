package com.example.triibe.triibeuserapp.track_location;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.util.Constants;
import com.example.triibe.triibeuserapp.util.Globals;
import com.example.triibe.triibeuserapp.util.RunAppWhenAtMallService;
import com.example.triibe.triibeuserapp.view_question.ViewQuestionActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author michael.
 */
public class AddFencesIntentService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private static final String TAG = "AddFences";
    public final static String EXTRA_TRIIBE_FENCE_TYPE = "com.example.triibe.TRIIBE_FENCE_TYPE";
    public final static String TYPE_MALL = "com.example.triibe.TYPE_MALL";
    public final static String TYPE_LANDMARK = "com.example.triibe.TYPE_LANDMARK";
    public final static String EXTRA_FENCE_KEY = "com.example.triibe.TRIIBE_FENCE_KEY";
    public final static String EXTRA_LATITUDE = "com.example.triibe.TRIIBE_LATITUDE";
    public final static String EXTRA_LONGITUDE = "com.example.triibe.TRIIBE_LONGITUDE";
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private volatile List<Intent> mIntents;

    public AddFencesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, MallFenceReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
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
                if (!mallGeofencesAdded) {
                    createMallFences();
                }

            } else if (type.contentEquals(TYPE_LANDMARK)) {
                String key = "";
                String lat = "";
                String lon = "";
                if (intent.getStringExtra(EXTRA_FENCE_KEY) != null) {
                    key = intent.getStringExtra(EXTRA_FENCE_KEY);
                }
                if (intent.getStringExtra(EXTRA_LATITUDE) != null) {
                    lat = intent.getStringExtra(EXTRA_LATITUDE);
                }
                if (intent.getStringExtra(EXTRA_LONGITUDE )!= null) {
                    lon = intent.getStringExtra(EXTRA_LONGITUDE);
                }
                if (!key.contentEquals("") && !lat.contentEquals("") && !lon.contentEquals("")) {
                    createLandmarkFence(key, lat, lon);
                }
            }
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
                    Constants.FENCE_MALL_RADIUS_IN_METERS,
                    Constants.FENCE_MALL_DWELL_IN_MILLISECONDS);
            builder.addFence(entry.getKey(), location, mPendingIntent).build();
        }

        FenceUpdateRequest mallFenceUpdateRequest = builder.build();

        if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "createLandmarkFence: googleApiClient not connected");
            mGoogleApiClient.connect();
        } else {
            SharedPreferences preferences = getSharedPreferences(Constants.MALL_FENCES, 0);
            boolean mallGeofencesAdded = preferences.getBoolean(Constants.MALL_FENCES_ADDED, false);
            if (!mallGeofencesAdded && mallFenceUpdateRequest != null) {
                addFences(mallFenceUpdateRequest, TYPE_MALL);
            }
        }
    }

    public void createLandmarkFence(String fenceKey, String lat, String lon) {
        Log.d(TAG, "createLandmarkFence: fenceKey, lat, lon: " + fenceKey + lat + lon);

        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onHandleIntent: NO PERMISSION");
            return;
        }
        AwarenessFence location = LocationFence.in(
                Double.valueOf(lat),
                Double.valueOf(lon),
                Constants.FENCE_LANDMARK_RADIUS_IN_METERS,
                Constants.FENCE_LANDMARK_DWELL_IN_MILLISECONDS
        );
        builder.addFence(fenceKey, location, mPendingIntent).build();

        FenceUpdateRequest landmarkFenceUpdateRequest = builder.build();

        if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "createLandmarkFence: googleApiClient not connected");
            mGoogleApiClient.connect();
        } else {
            addFences(landmarkFenceUpdateRequest, TYPE_LANDMARK);
            Globals.getInstance().addLandmarkFence(fenceKey);
        }
    }

    private void addFences(FenceUpdateRequest fenceUpdateRequest, String type) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google API client not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (type.contentEquals(TYPE_MALL)) {
            try {
                Awareness.FenceApi.updateFences(
                        mGoogleApiClient,
                        fenceUpdateRequest
                ).setResultCallback(this);
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            }

            // Mark mall fences as added.
            SharedPreferences preferences = getSharedPreferences(Constants.MALL_FENCES, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.MALL_FENCES_ADDED, true);
            editor.apply();
        } else if (type.contentEquals(TYPE_LANDMARK)) {
            try {
                Awareness.FenceApi.updateFences(
                        mGoogleApiClient,
                        fenceUpdateRequest
                ).setResultCallback(this);
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            }
        }
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

        Log.d(TAG, "onDestroy: add fences service done");
    }

    public static class MallFenceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            // Start or stop the app service
            Intent AppServiceIntent = new Intent(context, RunAppWhenAtMallService.class);

            Log.d(TAG, "onReceive: fenceKey: " + fenceState.getFenceKey());

            if (TextUtils.equals(fenceState.getFenceKey(), "southland")) { // TODO: 18/09/16 add southland and others to constants file
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In southland");

                        SharedPreferences sharedPref = context.getSharedPreferences(
                                context.getString(R.string.user_id),
                                Context.MODE_PRIVATE
                        );
                        String userId = sharedPref.getString(context.getString(R.string.user_id), "testUser");
                        AppServiceIntent.putExtra(context.getString(R.string.user_id), userId);

                        context.startService(AppServiceIntent);
                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in southland");
                        // Remove landmark fences
                        List<String> landmarkFences = Globals.getInstance().getLandmarkFences();
                        for (int i = 0; i < landmarkFences.size(); i++) {
                            Intent removeFenceIntent = new Intent(context, RemoveFenceIntentService.class);
                            removeFenceIntent.putExtra(EXTRA_TRIIBE_FENCE_TYPE, TYPE_LANDMARK);
                            removeFenceIntent.putExtra(EXTRA_FENCE_KEY, landmarkFences.get(i));
                            context.startService(removeFenceIntent);
                        }

                        // Stop app
                        context.stopService(AppServiceIntent);
                        break;
                    case FenceState.UNKNOWN:
                        Log.d(TAG, "UNKONWN if in southland");
                        break;
                }
            } else {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In non mall fence");

                        SharedPreferences sharedPref = context.getSharedPreferences(
                                context.getString(R.string.user_id),
                                Context.MODE_PRIVATE
                        );
                        String userId = sharedPref.getString(context.getString(R.string.user_id), "testUser");

                        // Show survey notification
                        // TODO: 5/10/16 also, remove survey when user clicks on it
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.westfieldicon_transparent)
                                        .setContentTitle(fenceState.getFenceKey())
                                        .setContentText("New Survey Available");
                        Intent resultIntent = new Intent(context, ViewQuestionActivity.class);
                        resultIntent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, "enrollmentSurvey"); // TODO: 5/10/16 NEED TO GET SURVEYID!
                        resultIntent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, userId);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(ViewQuestionActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        int mId = 5;
                        mNotificationManager.notify(mId, mBuilder.build());

                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in non mall fence");
                        break;
                    case FenceState.UNKNOWN:
                        Log.d(TAG, "UNKONWN if in non mall fence");
                        break;
                }
            }

            if (TextUtils.equals(fenceState.getFenceKey(), "enrollmentSurveyt1")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In enrollmentSurveyt1");

                        // Show notification for survey.

//                        SharedPreferences sharedPref = context.getSharedPreferences(
//                                context.getString(R.string.user_id),
//                                Context.MODE_PRIVATE
//                        );
//                        String userId = sharedPref.getString(context.getString(R.string.user_id), "testUser");
//                        AppServiceIntent.putExtra(context.getString(R.string.user_id), userId);
//
//                        context.startService(AppServiceIntent);



                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in enrollmentSurveyt1");
                        // Remove survey notification.
//                        context.stopService(AppServiceIntent);
                        break;
                    case FenceState.UNKNOWN:
                        Log.d(TAG, "UNKONWN if in enrollmentSurveyt1");
                        break;
                }
            }

            if (TextUtils.equals(fenceState.getFenceKey(), "Testt1")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In Testt1");

                        // Show notification for survey.

//                        SharedPreferences sharedPref = context.getSharedPreferences(
//                                context.getString(R.string.user_id),
//                                Context.MODE_PRIVATE
//                        );
//                        String userId = sharedPref.getString(context.getString(R.string.user_id), "testUser");
//                        AppServiceIntent.putExtra(context.getString(R.string.user_id), userId);
//
//                        context.startService(AppServiceIntent);



                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in Testt1");
                        // Remove survey notification.
//                        context.stopService(AppServiceIntent);
                        break;
                    case FenceState.UNKNOWN:
                        Log.d(TAG, "UNKONWN if in Testt1");
                        break;
                }
            }

            if (TextUtils.equals(fenceState.getFenceKey(), "eastSide")) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.d(TAG, "In eastSide");
                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in eastSide");
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
                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in westSide");
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
                        break;
                    case FenceState.FALSE:
                        Log.d(TAG, "Not in furtherSouth");
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
                addMallFencesIntent.putExtra(EXTRA_TRIIBE_FENCE_TYPE, TYPE_MALL);
                context.startService(addMallFencesIntent);
            }
        }
    }
}
