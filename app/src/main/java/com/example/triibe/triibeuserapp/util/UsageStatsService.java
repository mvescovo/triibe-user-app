package com.example.triibe.triibeuserapp.util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.example.triibe.triibeuserapp.trackData.AppUsageStats;
import com.example.triibe.triibeuserapp.trackData.RunningApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Created by Matthew on 2/10/2016.
 * This service makes calls to App Usage Stats on a longer times than that of the IP Service. L
 */

public class UsageStatsService extends Service{
    //change the CHECK_INTERVAL to manage how often you check for Usage Stats.
    public static final long CHECK_INTERVAL = 5000;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    /*********FIREBASE*********/
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    /**************************/
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    String currentDate;
    String dateInput;

    private Map<String, Object> totalAppMap = new HashMap<>();
    private Map<String, RunningApp> currentAppMap = new HashMap<>();
    private Map<String, RunningApp> previousAppMap = new HashMap<>();
    Map<String, Object> appValues;


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

        // Check if UsageStatsManager for app tracking permission enabled
        if (AppUsageStats.getUsageStatsList(this).isEmpty()){
            Intent usageStatsIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usageStatsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usageStatsIntent);
        }

    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String currentApp = AppUsageStats.getMostCurrentRecentApp(getApplicationContext());
                    System.out.println("THE CURRENT APP IS:"+currentApp);
                    Date date = new Date();
                    dateInput = df.format(date);
                    RunningApp app = new RunningApp(currentApp,dateInput);
                    currentAppMap.put(app.getForgroundApp(),app);
                    compareApps();
                  //  System.out.println("usage stats service - Current App:"+ currentApp);
                }
            });
        }
    }

    public void compareApps(){
        if (previousAppMap.isEmpty()&&currentAppMap.isEmpty()){
            // if both th hashmaps are empty simply end the function
            return;
        }
        if (previousAppMap.isEmpty()){
            for (Map.Entry<String, RunningApp> entry : currentAppMap.entrySet()) {
                previousAppMap.put(entry.getValue().getForgroundApp(),currentAppMap.get(entry.getValue().getForgroundApp()));
                //  System.out.println("First Adding to previous");
                //  System.out.println(entry.getValue().getIpAddrURL());
            }
        }else {
            for (Map.Entry<String, RunningApp> entry : currentAppMap.entrySet()) {
                if (previousAppMap.containsKey(entry.getValue().getForgroundApp())){
                    // The App is already in the previous connection map so make no changes.
                }else{
                    // The app was not found in the previous connection map so we add it in.
                    previousAppMap.put(entry.getValue().getForgroundApp(),currentAppMap.get(entry.getValue().getForgroundApp()));
                }
            }
        }
        for(Iterator<Map.Entry<String, RunningApp>> it = previousAppMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RunningApp> entry = it.next();
            if (currentAppMap.containsKey(entry.getValue().getForgroundApp())){
                //If the App is in both lists.
                currentAppMap.remove(entry.getValue().getForgroundApp());
            }else{
                //System.out.println("the value: "+ entry.getValue().getIpAddrURL()+"no longer in current removing adding to total");
                Date date = new Date();
                dateInput = df.format(date);
                previousAppMap.get(entry.getValue().getForgroundApp()).setEndTime(dateInput);
                /*********FIREBASE*********/
                String dataKey = mDatabase.child("data").child("Running Apps").push().getKey();
                appValues = previousAppMap.get(entry.getValue().getForgroundApp()).toMap();
                totalAppMap.put("/data/Running Apps/"+user.getUid()+"/"+currentDate+"/"+dataKey,appValues);
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mDatabase.updateChildren(totalAppMap);
                /**************************/
                it.remove();
            }
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
