package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Option entity.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Option {

    private String mQuestionId;
    private String mId;
    private String mPhrase;
    private boolean mHasExtraInput;
    private String mExtraInput;
    private String mExtraInputType;
    private String mExtraInputHint;

    // Empty constructor required for firebase
    public Option() {}

    public Option(String phrase, boolean hasExtraInput) {
        mPhrase = phrase;
        mHasExtraInput = hasExtraInput;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public String getQuestionId() {
        return mQuestionId;
    }

    public void setQuestionId(String questionId) {
        mQuestionId = questionId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPhrase() {
        return mPhrase;
    }

    public void setPhrase(String phrase) {
        mPhrase = phrase;
    }

    public boolean getHasExtraInput() {
        return mHasExtraInput;
    }

    public void setHasExtraInput(boolean hasExtraInput) {
        mHasExtraInput = hasExtraInput;
    }

    public String getExtraInput() {
        return mExtraInput;
    }

    public void setExtraInput(String extraInput) {
        mExtraInput = extraInput;
    }

    public String getExtraInputType() {
        return mExtraInputType;
    }

    public void setExtraInputType(String extraInputType) {
        mExtraInputType = extraInputType;
    }

    public String getExtraInputHint() {
        return mExtraInputHint;
    }

    public void setExtraInputHint(String extraInputHint) {
        mExtraInputHint = extraInputHint;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("questionId", mQuestionId);
        result.put("id", mId);
        result.put("phrase", mPhrase);
        return result;
    }
}
