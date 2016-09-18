package com.example.triibe.triibeuserapp.util;

import android.app.IntentService;
import android.content.Intent;

/**
 * @author michael.
 */
public class StopTrackingIntentService extends IntentService {

    private static final String TAG = "StopTrackingIntentService";
    public StopTrackingIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent AppServiceIntent = new Intent(this, RunAppWhenAtMallService.class);
        stopService(AppServiceIntent);
    }
}
