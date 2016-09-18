package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Survey entity. Used for Firebase realtime database so an entire survey can be
 * retrieved in one call.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Survey {

    private SurveyDetails mSurveyDetails;
    private Map<String, Question> mQuestions;
    private Map<String, Answer> mAnswers;

    // Empty constructor required for firebase
    public Survey() {}

    public Survey(SurveyDetails surveyDetails) {
        mSurveyDetails = surveyDetails;
    }

    /*
    * All setters are getters required by firebase even if not used in the program.
    *
    * Note: getters must be of the form "get<parameter name>".
    * Boolean values cannot use "hasExtraValue" for example.
    * */

    public SurveyDetails getSurveyDetails() {
        return mSurveyDetails;
    }

    public void setSurveyDetails(SurveyDetails surveyDetails) {
        mSurveyDetails = surveyDetails;
    }

    public Map<String, Question> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(Map<String, Question> questions) {
        mQuestions = questions;
    }

    public Map<String, Answer> getAnswers() {
        return mAnswers;
    }

    public void setAnswers(Map<String, Answer> answers) {
        mAnswers = answers;
    }
}
