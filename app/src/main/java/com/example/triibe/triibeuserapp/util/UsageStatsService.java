package com.example.triibe.triibeuserapp.util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Matthew on 2/10/2016.
 */

public class UsageStatsService extends Service{
    //change the CHECK_INTERVAL to manage how often you check for Usage Stats.
    public static final long CHECK_INTERVAL = 10000;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    /*********FIREBASE*********/
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    /**************************/
    DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    String currentDate;


    @Override
    public void onCreate() {
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new UsageStatsService.TimeDisplayTimerTask(), 0, CHECK_INTERVAL);
        currentDate = date.format(new Date());
        /*********FIREBASE*********/
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        /**************************/

    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    //Method Calls Here

                }
            });
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
