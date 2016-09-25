package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * AnswerDetails entity.
 *
 * @author michael.
 */
@IgnoreExtraProperties
public class AnswerDetails {

    private String mQuestionId;
    private String mId;
    private String mType;

    // Empty constructor required for firebase
    public AnswerDetails() {}

    public AnswerDetails(String questionId, String id, String type) {
        mQuestionId = questionId;
        mId = id;
        mType = type;
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

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("questionId", mQuestionId);
        result.put("type", mType);
        return result;
    }
}
