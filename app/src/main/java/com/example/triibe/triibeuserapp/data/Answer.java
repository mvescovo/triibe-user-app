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

    private String questionId;
    private String type;
    ArrayList<Option> selectedOptions;

    // Empty constructor required for firebase
    public Answer() {}

    public Answer(String questionId, String type, ArrayList<Option> selectedOptions) {
        this.questionId = questionId;
        this.type = type;
        this.selectedOptions = selectedOptions;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Option> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(ArrayList<Option> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}
