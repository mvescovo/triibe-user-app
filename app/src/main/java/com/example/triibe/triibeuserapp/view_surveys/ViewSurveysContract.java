package com.example.triibe.triibeuserapp.view_surveys;

import android.support.annotation.NonNull;

import com.example.triibe.triibeuserapp.data.SurveyDetails;

import java.util.Map;

/**
 * @author michael.
 */
public interface ViewSurveysContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showSurveys(@NonNull Map<String, SurveyDetails> surveyDetails);

        void showNoSurveysMessage();

        void showQuestionUi(String surveyId, String questionId);
    }

    interface UserActionsListener {

        void loadSurveys();

        void openSurveyQuestions(@NonNull String surveyId);
    }
}
