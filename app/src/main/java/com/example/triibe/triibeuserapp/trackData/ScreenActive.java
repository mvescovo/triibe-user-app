package com.example.triibe.triibeuserapp.trackData;

import java.util.Date;

/**
 * Created by Matthew on 4/09/2016.
 */
public class ScreenActive {

    private Date startTime;
    private Date stopTime;


    public ScreenActive() {
    }

    public ScreenActive(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
}