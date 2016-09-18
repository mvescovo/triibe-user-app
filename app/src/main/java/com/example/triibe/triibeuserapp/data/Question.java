package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Question entity.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Question {

    QuestionDetails mQuestionDetails;
    private Map<String, Option> mOptions;

    // Empty constructor required for firebase
    public Question() {}

    public Question(QuestionDetails questionDetails) {
        mQuestionDetails = questionDetails;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */
    public QuestionDetails getQuestionDetails() {
        return mQuestionDetails;
    }

    public void setQuestionDetails(QuestionDetails questionDetails) {
        mQuestionDetails = questionDetails;
    }

    public Map<String, Option> getOptions() {
        return mOptions;
    }

    public void setOptions(Map<String, Option> options) {
        mOptions = options;
    }

    // For firebase map
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("questionDetails", mQuestionDetails);
        result.put("options", mOptions);
        return result;
    }
}
