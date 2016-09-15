//package com.example.triibe.triibeuserapp.trackLocation;
//
//import android.app.IntentService;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.TaskStackBuilder;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.example.triibe.triibeuserapp.R;
//import com.example.triibe.triibeuserapp.view_surveys.ViewSurveysActivity;
//import com.google.android.gms.location.Geofence;
//import com.google.android.gms.location.GeofenceStatusCodes;
//import com.google.android.gms.location.GeofencingEvent;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author michael.
// */
//public class LandmarkGeofenceTransitionIntentService extends IntentService {
//
//    private static final String TAG = "LandmarkTransition";
//
//    public LandmarkGeofenceTransitionIntentService() {
//        super("LandmarkGeofenceTransitionIntentService");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//        if (geofencingEvent.hasError()) {
//            String errorMessage = getErrorString(this,
//                    geofencingEvent.getErrorCode());
//            Log.e(TAG, errorMessage);
//            return;
//        }
//
//        // Get the transition type.
//        int geofenceTransition = geofencingEvent.getGeofenceTransition();
//
//        // Test that the reported transition was of interest.
//        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
//                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
//
//            // Get the geofences that were triggered. A single event can trigger multiple geofences.
//            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
//
//            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    this,
//                    geofenceTransition,
//                    triggeringGeofences
//            );
//
//            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails);
//            Log.i(TAG, geofenceTransitionDetails);
//        } else {
//            // Log the error.
//            Log.e(TAG, "geofence error");
//        }
//    }
//
//    private String getGeofenceTransitionDetails(
//            Context context,
//            int geofenceTransition,
//            List<Geofence> triggeringGeofences) {
//
//        String geofenceTransitionString = getTransitionString(geofenceTransition);
//
//        // Get the Ids of each geofence that was triggered.
//        ArrayList triggeringGeofencesIdsList = new ArrayList();
//        for (Geofence geofence : triggeringGeofences) {
//            triggeringGeofencesIdsList.add(geofence.getRequestId());
//        }
//        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
//
//        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
//    }
//
//    private String getTransitionString(int transitionType) {
//        switch (transitionType) {
//            case Geofence.GEOFENCE_TRANSITION_ENTER:
//                return "Entered geofence";
//            case Geofence.GEOFENCE_TRANSITION_EXIT:
//                return "Exited geofence";
//            default:
//                return "Unknown geofence transition";
//        }
//    }
//
//    private void sendNotification(String notificationDetails) {
//        // Create an explicit content Intent that starts the main Activity.
//        Intent notificationIntent = new Intent(getApplicationContext(), ViewSurveysActivity.class);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Add the tracklocation Activity to the task stack as the parent.
//        stackBuilder.addParentStack(ViewSurveysActivity.class);
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
//                        R.mipmap.westfield_icon))
//                .setColor(Color.RED)
//                .setContentTitle(notificationDetails)
//                .setContentText("Geofence transition")
//                .setContentIntent(notificationPendingIntent);
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Get an instance of the Notification manager
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Issue the notification
//        mNotificationManager.notify(0, builder.build());
//    }
//
//    public static String getErrorString(Context context, int errorCode) {
//        Resources mResources = context.getResources();
//        switch (errorCode) {
//            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
//                return "Geofence not available";
//            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
//                return "Too many geofences";
//            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
//                return "Too many pendingIntents";
//            default:
//                return "Unknown geofence error";
//        }
//    }
//}
