package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
@IgnoreExtraProperties
public class SurveyTrigger {

    private String mSurveyId;
    private String mId;
    private String mLatitude;
    private String mLongitude;
    private String mRadius;
    private String mDwell;
    private String mLevel;
    private String mLevelDistance;
    private String mTime;

    // Empty constructor required for firebase
    public SurveyTrigger() {}

    public SurveyTrigger(String surveyId, String id) {
        mSurveyId = surveyId;
        mId = id;
    }

    public SurveyTrigger(String surveyId, String id, String latitude, String longitude,
                         String radius, String dwell) {
        mSurveyId = surveyId;
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
        mRadius = radius;
        mDwell = dwell;
    }

    public SurveyTrigger(String surveyId, String id, String latitude, String longitude,
                         String radius, String dwell, String level, String levelDistance) {
        mSurveyId = surveyId;
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
        mRadius = radius;
        mDwell = dwell;
        mLevel = level;
        mLevelDistance = levelDistance;
    }

    public SurveyTrigger(String surveyId, String id, String time) {
        mSurveyId = surveyId;
        mId = id;
        mTime = time;
    }

    public SurveyTrigger(String surveyId, String id, String latitude, String longitude,
                         String radius, String dwell, String level, String levelDistance, String time) {
        mSurveyId = surveyId;
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
        mRadius = radius;
        mDwell = dwell;
        mLevel = level;
        mLevelDistance = levelDistance;
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

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public String getRadius() {
        return mRadius;
    }

    public void setRadius(String radius) {
        mRadius = radius;
    }

    public String getDwell() {
        return mDwell;
    }

    public void setDwell(String dwell) {
        mDwell = dwell;
    }

    public String getLevel() {
        return mLevel;
    }

    public void setLevel(String level) {
        mLevel = level;
    }

    public String getLevelDistance() {
        return mLevelDistance;
    }

    public void setLevelDistance(String levelDistance) {
        mLevelDistance = levelDistance;
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
        result.put("latitude", mLatitude);
        result.put("longitude", mLongitude);
        result.put("radius", mRadius);
        result.put("dwell", mDwell);
        result.put("level", mLevel);
        result.put("levelDistance", mLevelDistance);
        result.put("time", mTime);
        return result;
    }
}
