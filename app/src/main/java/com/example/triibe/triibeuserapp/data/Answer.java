package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Answer entity.
 * 
 * @author michael
 */
@IgnoreExtraProperties
public class Answer {

    private AnswerDetails mAnswerDetails;
    private Map<String, Option> mSelectedOptions;

    // Empty constructor required for firebase
    public Answer() {}

    public Answer(AnswerDetails answerDetails, Map<String, Option> selectedOptions) {
        mAnswerDetails = answerDetails;
        mSelectedOptions = selectedOptions;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public AnswerDetails getAnswerDetails() {
        return mAnswerDetails;
    }

    public void setAnswerDetails(AnswerDetails answerDetails) {
        mAnswerDetails = answerDetails;
    }

    public Map<String, Option> getSelectedOptions() {
        return mSelectedOptions;
    }

    public void setSelectedOptions(Map<String, Option> selectedOptions) {
        mSelectedOptions = selectedOptions;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("answerDetails", mAnswerDetails);
        result.put("selectedOptions", mSelectedOptions);
        return result;
    }
}
