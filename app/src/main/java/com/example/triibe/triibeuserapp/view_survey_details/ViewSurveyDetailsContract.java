package com.example.triibe.triibeuserapp.view_survey_details;

import com.example.triibe.triibeuserapp.data.Survey;

/**
 * @author michael.
 */
public interface ViewSurveyDetailsContract {

    interface View {

        void showSurveyDetails(Survey survey);

        void showSnackbar(int stringResource);
    }

    interface UserActionsListener {

        void loadSurveyDetails(String surveyId, boolean forceUpdate);
    }
}
