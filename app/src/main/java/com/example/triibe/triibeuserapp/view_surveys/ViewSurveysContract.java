package com.example.triibe.triibeuserapp.view_surveys;

import android.support.annotation.NonNull;

import com.example.triibe.triibeuserapp.data.SurveyDetails;

import java.util.ArrayList;

/**
 * @author michael.
 */
public interface ViewSurveysContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showSurveys(@NonNull ArrayList<SurveyDetails> surveys);

        void showSurveyDetails(String surveyId);
    }

    interface UserActionsListener {

        void loadUser();

        void loadSurveys();

        void openSurveyDetails(@NonNull String surveyId);
    }
}
