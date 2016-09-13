package com.example.triibe.triibeuserapp.edit_survey;

/**
 * @author michael.
 */
public interface EditSurveyContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showSurveys();

        void showEditQuestion();

        void showEditTrigger();
    }

    interface UserActionsListener {

        void editSurvey(String surveyId, String description, String version, String points,
                        String timeTillExpiry, boolean editQuestion);

        void editTrigger(String surveyId);
    }
}
