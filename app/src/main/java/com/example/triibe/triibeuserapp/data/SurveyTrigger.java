package com.example.triibe.triibeuserapp.data;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class SurveyTrigger {

    private String mSurveyId;
    private String mId;
    private Location mLocation;
    private String mTime;

    // Empty constructor required for firebase
    public SurveyTrigger() {}

    public SurveyTrigger(String surveyId, String id) {
        mSurveyId = surveyId;
        mId = id;
    }

    public SurveyTrigger(String surveyId, String id, Location location) {
        mSurveyId = surveyId;
        mId = id;
        mLocation = location;
    }

    public SurveyTrigger(String surveyId, String id, String time) {
        mSurveyId = surveyId;
        mId = id;
        mTime = time;
    }

    public SurveyTrigger(String surveyId, String id, Location location, String time) {
        mSurveyId = surveyId;
        mId = id;
        mLocation = location;
        mTime = time;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public String getSurveyId() {
        return mSurveyId;
    }

    public void setSurveyId(String surveyId) {
        mSurveyId = surveyId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("surveyId", mSurveyId);
        result.put("id", mId);
        result.put("latitude", mLocation.getLatitude());
        result.put("longitude", mLocation.getLongitude());
        result.put("level", mLocation.getAltitude());
        result.put("time", mTime);
        return result;
    }
}
