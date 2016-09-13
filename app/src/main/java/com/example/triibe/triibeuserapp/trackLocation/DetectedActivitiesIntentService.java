//package com.example.triibe.triibeuserapp.trackLocation;
//
//import android.app.IntentService;
//import android.content.Intent;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
//import com.example.triibe.triibeuserapp.util.Constants;
//import com.google.android.gms.location.ActivityRecognitionResult;
//
//import java.util.ArrayList;
//
///**
// * @author michael.
// */
//public class DetectedActivitiesIntentService extends IntentService {
//
//    private static final String TAG = "DetectedActivities";
//
//    public DetectedActivitiesIntentService() {
//        super(TAG);
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
//        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
//
//        ArrayList detectedActivities = (ArrayList) result.getProbableActivities();
//
//        Log.d(TAG, "onHandleIntent: ACTIVITIES DETECTED");
//
//        localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
//
//        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
//    }
//}
