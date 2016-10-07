package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
@IgnoreExtraProperties
public class SurveyDetails implements Serializable {

    private String mId;
    private String mDescription;
    private String mPoints;
    private String mNumProtectedQuestions;
    private boolean mActive;

    // Empty constructor required for firebase
    public SurveyDetails() {}

    public SurveyDetails(String id, String description, String points,
                         String numProtectedQuestions, boolean active) {
        mId = id;
        mDescription = description;
        mPoints = points;
        mNumProtectedQuestions = numProtectedQuestions;
        mActive = active;
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

    public String getPoints() {
        return mPoints;
    }

    public void setPoints(String  points) {
        mPoints = points;
    }

    public String getNumProtectedQuestions() {
        return mNumProtectedQuestions;
    }

    public void setNumProtectedQuestions(String numProtectedQuestions) {
        mNumProtectedQuestions = numProtectedQuestions;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", mId);
        result.put("description", mDescription);
        result.put("points", mPoints);
        result.put("numProtectedQuestions", mNumProtectedQuestions);
        result.put("active", mActive);
        return result;
    }
}
