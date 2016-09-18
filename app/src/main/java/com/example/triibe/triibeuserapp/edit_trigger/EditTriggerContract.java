package com.example.triibe.triibeuserapp.edit_trigger;

/**
 * @author michael.
 */
public interface EditTriggerContract {

    interface View {

        void setProgressIndicator(boolean active);

        void setCurrentLocation(double lat, double lon);

        void showEditSurvey();
    }

    interface UserActionsListener {

        void editTrigger(String surveyId, String triggerId, Double lat, Double lon, String level,
                         String time);
    }
}
