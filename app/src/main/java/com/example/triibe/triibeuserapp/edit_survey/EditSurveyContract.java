package com.example.triibe.triibeuserapp.edit_survey;

/**
 * @author michael.
 */
public interface EditSurveyContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showSurveys();

        void showEditQuestion();
    }

    interface UserActionsListener {

        void editSurvey(String name, String description, String version, String points,
                        String timeTillExpiry, boolean editQuestion);
    }
}
