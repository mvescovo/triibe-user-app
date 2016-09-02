package com.example.triibe.triibeuserapp.create_survey;

/**
 * @author michael.
 */
public interface CreateSurveyContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showSurveys();
    }

    interface UserActionsListener {

        void createSurvey(String name, String description, String version, String points,
                          String timeTillExpiry);
    }
}
