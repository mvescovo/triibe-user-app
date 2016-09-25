package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class SurveyDetails implements Serializable {

    private String mId;
    private String mVersion;
    private String mDescription;
    private String mDurationTillExpiry;
    private String mPoints;

    // Empty constructor required for firebase
    public SurveyDetails() {}

    public SurveyDetails(String id, String version, String description, String durationTillExpiry,
                         String points) {
        mId = id;
        mVersion = version;
        mDescription = description;
        mDurationTillExpiry = durationTillExpiry;
        mPoints = points;
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

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDurationTillExpiry() {
        return mDurationTillExpiry;
    }

    public void setDurationTillExpiry(String durationTillExpiry) {
        mDurationTillExpiry = durationTillExpiry;
    }

    public String getPoints() {
        return mPoints;
    }

    public void setPoints(String  points) {
        mPoints = points;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", mId);
        result.put("version", mVersion);
        result.put("description", mDescription);
        result.put("durationTillExpiry", mDurationTillExpiry);
        result.put("points", mPoints);

        return result;
    }
}
