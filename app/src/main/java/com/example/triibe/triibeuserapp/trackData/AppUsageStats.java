package com.example.triibe.triibeuserapp.trackData;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AppUsageStats {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-yyyy HH:mm:ss");
    public static final String TAG = AppUsageStats.class.getSimpleName();

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

    /**
     * Get statistics of apps running from interval of calender value, in this case a a day
     */
    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usageStatsManager = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return usageStatsList;
    }

    /**
     * Return the most recently opened app.
     */
    public static String getMostRecentApp(List<UsageStats> usageStatsList){
        String packageName = "";

        int appIndex = 0;
        int mostRecentIndex = 0;
        long mostRecentTime = 0;
        for (UsageStats usageStats : usageStatsList){
            if (usageStats.getLastTimeUsed() > mostRecentTime) {
                mostRecentIndex = appIndex;
                mostRecentTime = usageStats.getLastTimeUsed();
            }
            appIndex++;
        }
        packageName = usageStatsList.get(mostRecentIndex).getPackageName();

        return packageName;
    }

    /**
     * Return a list of recently opened apps.
     */
    public static ArrayList<String> getBackgroundApps(List<UsageStats> usageStatsList){
        ArrayList<String> appList = new ArrayList<>();
        for (UsageStats usageStats : usageStatsList){
            appList.add(usageStats.getPackageName());
        }
        return appList;
    }

    /**
     * Returns most recently used app at current time.
     */
    public static String getMostCurrentRecentApp(Context context){
        return getMostRecentApp(getUsageStatsList(context));
    }

    /**
     * Return a list of recently opened apps at current time.
     */
    public static ArrayList<String> getCurrentBackgroundApps(Context context){
        return getBackgroundApps(getUsageStatsList(context));
    }

}
