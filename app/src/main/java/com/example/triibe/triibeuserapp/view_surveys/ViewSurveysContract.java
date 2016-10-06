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

        void showQuestionUi(String surveyId, String questionId, int numProtectedQuestions);
    }

    interface UserActionsListener {

        void loadSurveys(@NonNull String userId, @NonNull Boolean forceUpdate);

        void openSurveyQuestions(@NonNull String surveyId, @NonNull Integer numProtectedQuestions);
    }
}
