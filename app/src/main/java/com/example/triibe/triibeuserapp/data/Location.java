package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class Location {

    private String mId;
    private String mLat;
    private String mLon;
    private String mLevel;

    // Empty constructor required for firebase
    public Location() {}

    public Location(String id, String lat, String lon) {
        mId = id;
        mLat = lat;
        mLon = lon;
    }

    public Location(String id, String lat, String lon, String level) {
        mId = id;
        mLat = lat;
        mLon = lon;
        mLevel = level;
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

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLon() {
        return mLon;
    }

    public void setLon(String lon) {
        mLon = lon;
    }

    public String getLevel() {
        return mLevel;
    }

    public void setLevel(String level) {
        mLevel = level;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", mId);
        result.put("latitude", mLat);
        result.put("longitude", mLon);
        result.put("level", mLevel);
        return result;
    }
}
