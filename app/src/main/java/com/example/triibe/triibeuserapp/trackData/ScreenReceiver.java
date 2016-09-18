package com.example.triibe.triibeuserapp.trackData;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.triibe.triibeuserapp.util.IpService;
import com.example.triibe.triibeuserapp.util.RunAppWhenAtMallService;

/**
 * Created by Matthew on 14/09/2016.
 */
public class ScreenReceiver extends BroadcastReceiver {

    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }
        Intent i = new Intent(context, IpService.class);
        i.putExtra("screen_state", screenOff);
        context.startService(i);
    }

}
