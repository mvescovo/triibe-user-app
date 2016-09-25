//package com.example.triibe.triibeuserapp.trackLocation;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
///**
// * @author michael.
// */
//public class BootDetectionBroadcastReceiver extends BroadcastReceiver {
//
//    private static final String TAG = "BootDetection";
//
//    public BootDetectionBroadcastReceiver() {}
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            Intent addMallFencesIntent = new Intent(context, AddFencesIntentService.class);
//            addMallFencesIntent.putExtra("type", "mall");
//            context.startService(addMallFencesIntent);
//        }
//    }
//}
