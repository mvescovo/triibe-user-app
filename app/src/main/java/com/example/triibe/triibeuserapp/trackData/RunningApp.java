package com.example.triibe.triibeuserapp.trackData;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 11/10/2016.
 *
 */

public class RunningApp {
    private String forgroundApp;
    private String startTime;
    private String endTime;


    // Empty constructor required for firebase
    public RunningApp() {}

    public RunningApp(String forgroundApp, String startTime) {
        this.forgroundApp = forgroundApp;
        this.startTime = startTime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("App", forgroundApp);
        result.put("IP Address URL", startTime);
        result.put("End Time", endTime);
        return result;
    }

    public String getForgroundApp() {
        return forgroundApp;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
