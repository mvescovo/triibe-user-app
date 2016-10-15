package com.example.triibe.triibeuserapp.trackData;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 4/09/2016.
 */
@IgnoreExtraProperties
public class ScreenActive {

    private String startTime;
    private String stopTime;


    public ScreenActive() {
    }

    public ScreenActive(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Screen On Time", startTime);
        result.put("Screen Off Time", stopTime);

        return result;
    }

}