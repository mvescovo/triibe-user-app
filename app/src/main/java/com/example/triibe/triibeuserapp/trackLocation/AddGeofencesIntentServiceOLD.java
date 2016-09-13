//package com.example.triibe.triibeuserapp.trackLocation;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;
//import android.support.v4.app.TaskStackBuilder;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.example.triibe.triibeuserapp.R;
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
//public class AddGeofencesIntentServiceOLD extends Service implements
//        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
//        ResultCallback<Status> {
//
//    private static final String TAG = "AddGeofences";
//
//    private Looper mServiceLooper;
//    private ServiceHandler mServiceHandler;
//    private Notification mNotification;
//
////    private static final Class<?>[] mSetForegroundSignature = new Class[]{
////            boolean.class};
////    private static final Class<?>[] mStartForegroundSignature = new Class[]{
////            int.class, Notification.class};
////    private static final Class<?>[] mStopForegroundSignature = new Class[]{
////            boolean.class};
////
////    private NotificationManager mNM;
////    private Method mSetForeground;
////    private Method mStartForeground;
////    private Method mStopForeground;
////    private Object[] mSetForegroundArgs = new Object[1];
////    private Object[] mStartForegroundArgs = new Object[2];
////    private Object[] mStopForegroundArgs = new Object[1];
//
//    GoogleApiClient mGoogleApiClient;
//    private ArrayList<Geofence> mGeofences;
//
//    private final class ServiceHandler extends Handler {
//        public ServiceHandler(Looper looper) {
//            super(looper);
//        }
//        @Override
//        public void handleMessage(Message msg) {
////            Log.d(TAG, "handleMessage: handling message");
////            while (!mGoogleApiClient.isConnected()) {
////                Log.d(TAG, "handleMessage: while says not connected");
////                try {
////                    Log.d(TAG, "handleMessage: Sleeping for 5 seconds");
////                    Thread.sleep(5000);
////                } catch (InterruptedException e) {
////                    Thread.currentThread().interrupt();
////                }
////            }
////            Log.d(TAG, "handleMessage: out of while");
//
//        }
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        HandlerThread thread = new HandlerThread("ServiceStartArguments", Thread.MIN_PRIORITY);
//        thread.start();
//
//        mServiceLooper = thread.getLooper();
//        mServiceHandler = new ServiceHandler(mServiceLooper);
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        mGeofences = new ArrayList<>();
//
//        populateGeofenceList();
//
//        Log.d(TAG, "onCreate: service started");
//
//        //        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////        try {
////            mStartForeground = getClass().getMethod("startForeground",
////                    mStartForegroundSignature);
////            mStopForeground = getClass().getMethod("stopForeground",
////                    mStopForegroundSignature);
////            return;
////        } catch (NoSuchMethodException e) {
////            // Running on an older platform.
////            mStartForeground = mStopForeground = null;
////        }
////        try {
////            mSetForeground = getClass().getMethod("setForeground",
////                    mSetForegroundSignature);
////        } catch (NoSuchMethodException e) {
////            throw new IllegalStateException(
////                    "OS doesn't have Service.startForeground OR Service.setForeground!");
////        }
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.connect();
//        }
//
//        Message msg = mServiceHandler.obtainMessage();
//        msg.arg1 = startId;
//        mServiceHandler.sendMessage(msg);
//
//        sendNotification("TRIIBE App running");
//        startForeground(Constants.FOREGROUND_SERVICE, mNotification);
//
//        return START_STICKY;
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//
//        // Make sure our notification is gone.
////        stopForegroundCompat(R.string.foreground_service_started);
//
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Log.d(TAG, "onConnected: googleApiClient connected");
//        addGeofences();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed: could not connected googleApiClient");
//    }
//
//    @Override
//    public void onResult(@NonNull Status status) {
//        if (status.isSuccess()) {
//            Log.d(TAG, "onResult: successfully added activity geofence");
//        } else {
//            Log.d(TAG, "onResult: error adding or removing geofence");
//        }
//    }
//
//    private void populateGeofenceList() {
//        for (Map.Entry<String, LatLng> entry : Constants.TEST_LANDMARKS.entrySet()) {
//            mGeofences.add(new Geofence.Builder()
//                    .setRequestId(entry.getKey())
//                    .setCircularRegion(
//                            entry.getValue().latitude,
//                            entry.getValue().longitude,
//                            Constants.GEOFENCE_RADIUS_IN_METERS
//                    )
//                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                            Geofence.GEOFENCE_TRANSITION_EXIT)
//                    .build());
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
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
////    void invokeMethod(Method method, Object[] args) {
////        try {
////            method.invoke(this, args);
////        } catch (InvocationTargetException e) {
////            // Should not happen.
////            Log.w("ApiDemos", "Unable to invoke method", e);
////        } catch (IllegalAccessException e) {
////            // Should not happen.
////            Log.w("ApiDemos", "Unable to invoke method", e);
////        }
////    }
//
////    void startForegroundCompat(int id, Notification notification) {
////        // If we have the new startForeground API, then use it.
////        if (mStartForeground != null) {
////            mStartForegroundArgs[0] = Integer.valueOf(id);
////            mStartForegroundArgs[1] = notification;
////            invokeMethod(mStartForeground, mStartForegroundArgs);
////            return;
////        }
////
////        // Fall back on the old API.
////        mSetForegroundArgs[0] = Boolean.TRUE;
////        invokeMethod(mSetForeground, mSetForegroundArgs);
////        mNM.notify(id, notification);
////    }
////
////    void stopForegroundCompat(int id) {
////        // If we have the new stopForeground API, then use it.
////        if (mStopForeground != null) {
////            mStopForegroundArgs[0] = Boolean.TRUE;
////            invokeMethod(mStopForeground, mStopForegroundArgs);
////            return;
////        }
////
////        // Fall back on the old API.  Note to cancel BEFORE changing the
////        // foreground state, since we could be killed at that point.
////        mNM.cancel(id);
////        mSetForegroundArgs[0] = Boolean.FALSE;
////        invokeMethod(mSetForeground, mSetForegroundArgs);
////    }
//
//
//    private void sendNotification(String notificationDetails) {
//        // Create an explicit content Intent that starts the test Activity.
//        Intent notificationIntent = new Intent(getApplicationContext(), TestActivity.class);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Add the test Activity to the task stack as the parent.
//        stackBuilder.addParentStack(TestActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Get a notification builder that's compatible with platform versions >= 4
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//        // Define the notification settings.
//        builder.setSmallIcon(R.mipmap.westfield_icon)
//                // In a real app, you may want to use a library like Volley
//                // to decode the Bitmap.
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
//                        R.drawable.the_westfield_group_logo))
//                .setColor(Color.RED)
//                .setContentTitle(notificationDetails)
//                .setContentText("Foreground service")
//                .setContentIntent(notificationPendingIntent);
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Get an instance of the Notification manager
//        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
//
//        // Issue the notification
////        mNotificationManager.notify(0, builder.build());
//
//        mNotification = builder.build();
//    }
//}
