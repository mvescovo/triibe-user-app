package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Survey entity. Used for Firebase realtime database so an entire survey can be
 * retrieved in one call.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Survey {

    private String mId;
    private String mVersion;
    private String mDescription;
    private ArrayList<Question> mQuestions;
    private List mAnswers;

    private int mDurationTillExpiry;
    private int mPoints;
    private Map<String, Date> mLocations; // needs to be a date range
    private List mTimes;
    private List<DemographicProfile> mDemographicProfiles;

    // Empty constructor required for firebase
    public Survey() {}

    public Survey(String description, String version, ArrayList<Question> questions) {
        mDescription = description;
        mVersion = version;
        mQuestions = questions;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public ArrayList<Question> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        mQuestions = questions;
    }
}
