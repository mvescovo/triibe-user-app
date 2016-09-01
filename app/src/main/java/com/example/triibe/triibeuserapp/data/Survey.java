package com.example.triibe.triibeuserapp.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Survey entity. Used for Firebase realtime database so an entire survey can be
 * retrieved in one call.
 *
 * @author michael
 */
@IgnoreExtraProperties
public class Survey {

    private SurveyDetails mSurveyDetails;
    private ArrayList<Question> mQuestions;
    private List mAnswers;

    // Empty constructor required for firebase
    public Survey() {}

    public Survey(SurveyDetails surveyDetails, ArrayList<Question> questions) {
        mSurveyDetails = surveyDetails;
        mQuestions = questions;
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

    public ArrayList<Question> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        mQuestions = questions;
    }

    public List getAnswers() {
        return mAnswers;
    }

    public void setAnswers(List answers) {
        mAnswers = answers;
    }
}
