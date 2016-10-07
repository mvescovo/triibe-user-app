package com.example.triibe.triibeuserapp.view_points;

/**
 * @author michael.
 */

public interface ViewPointsContract {

    interface View {

        void setIndeterminateProgressIndicator(boolean active);

        void showNewPoints(String points);

        void showTotalPoints(String points);
    }

    interface UserActionsListener {

        void loadCurrentPoints(String user, String surveyPoints);
    }
}
