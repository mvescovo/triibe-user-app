package com.example.triibe.triibeuserapp.data;

import android.location.Location;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * User entity.
 *
 * @author michael.
 */
@IgnoreExtraProperties
public class User {

    private String mId;
    private String mName;
    private String mPhone;
    private String mEmail;
    private DemographicProfile mDemographicProfile;
    private Map<String, Boolean> mCompletedSurveyIds;
    private Map<String, Boolean> mActiveSurveyIds;
    private Map<String, Location> mLocations;
    private Map<String, Answer> mResponses;
    private boolean mEnrolled;
    private boolean mAdmin;

    // Empty constructor required for firebase
    public User() {}

    public User(String id) {
        mId = id;
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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public DemographicProfile getDemographicProfile() {
        return mDemographicProfile;
    }

    public void setDemographicProfile(DemographicProfile demographicProfile) {
        mDemographicProfile = demographicProfile;
    }

    public Map<String, Boolean> getCompletedSurveyIds() {
        return mCompletedSurveyIds;
    }

    public void setCompletedSurveyIds(Map<String, Boolean> completedSurveyIds) {
        mCompletedSurveyIds = completedSurveyIds;
    }

    public Map<String, Boolean> getActiveSurveyIds() {
        return mActiveSurveyIds;
    }

    public void setActiveSurveyIds(Map<String, Boolean> activeSurveyIds) {
        mActiveSurveyIds = activeSurveyIds;
    }

    public Map<String, Location> getLocations() {
        return mLocations;
    }

    public void setLocations(Map<String, Location> locations) {
        mLocations = locations;
    }

    public Map<String, Answer> getResponses() {
        return mResponses;
    }

    public void setResponses(Map<String, Answer> responses) {
        mResponses = responses;
    }

    public boolean isEnrolled() {
        return mEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        mEnrolled = enrolled;
    }

    public boolean isAdmin() {
        return mAdmin;
    }

    public void setAdmin(boolean admin) {
        mAdmin = admin;
    }
}
