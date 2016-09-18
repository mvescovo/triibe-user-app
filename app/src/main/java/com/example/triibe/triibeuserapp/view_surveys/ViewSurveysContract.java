package com.example.triibe.triibeuserapp.view_surveys;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author michael.
 */
public interface ViewSurveysContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showSurveys(@NonNull List surveyDetails);

        void showNoSurveysMessage();

        void showSurveyDetails(String surveyId);
    }

    interface UserActionsListener {

        void loadUser();

        void loadSurveys();

        void openSurveyDetails(@NonNull String surveyId);
    }
}
