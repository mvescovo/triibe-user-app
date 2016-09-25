package com.example.triibe.triibeuserapp.edit_survey;

import android.support.annotation.NonNull;

import com.example.triibe.triibeuserapp.data.SurveyDetails;

import java.util.List;

/**
 * @author michael.
 */
public interface EditSurveyContract {

    interface View {

        void setProgressIndicator(boolean active);

        void addSurveyIdsToAutoComplete(List<String> surveyIds);

        void showSurveyDetails(SurveyDetails surveyDetails);

        void showEditQuestion();

        void showEditTrigger();

        void showSurveys(@NonNull Integer resultCode);
    }

    interface UserActionsListener {

        void loadSurveyIds(@NonNull Boolean forceUpdate);

        void getSurvey(@NonNull String surveyId);

        void saveSurvey(String surveyId, String description, String version, String points,
                        String timeTillExpiry);

        void deleteSurvey(@NonNull String surveyId);

        void editQuestion();

        void editTrigger();
    }
}
