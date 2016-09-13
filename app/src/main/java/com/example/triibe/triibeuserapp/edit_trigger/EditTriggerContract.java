package com.example.triibe.triibeuserapp.edit_trigger;

/**
 * @author michael.
 */
public interface EditTriggerContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showEditSurvey();
    }

    interface UserActionsListener {

        void editTrigger(String surveyId, String triggerId, String lat, String lon, String level,
                         String time);
    }
}
