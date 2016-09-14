package com.example.triibe.triibeuserapp.trackData;

import java.util.Date;

/**
 * Created by Matthew on 4/09/2016.
 */
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
}