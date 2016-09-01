package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Answer entity.
 * 
 * @author michael
 */
@IgnoreExtraProperties
public class Answer {

    private String mQuestionId;
    private String mType;
    private ArrayList<Option> mSelectedOptions;

    // Empty constructor required for firebase
    public Answer() {}

    public Answer(String questionId, String type, ArrayList<Option> selectedOptions) {
        mQuestionId = questionId;
        mType = type;
        mSelectedOptions = selectedOptions;
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

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public ArrayList<Option> getSelectedOptions() {
        return mSelectedOptions;
    }

    public void setSelectedOptions(ArrayList<Option> selectedOptions) {
        mSelectedOptions = selectedOptions;
    }
}
