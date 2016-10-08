package com.example.triibe.triibeuserapp.view_points;

/**
 * @author michael.
 */

public interface ViewPointsContract {

    interface View {

        void setIndeterminateProgressIndicator(boolean active);

        void showPoints(String points, String totalPoints);
    }

    interface UserActionsListener {

        void loadCurrentPoints(String user, String surveyPoints);
    }
}
