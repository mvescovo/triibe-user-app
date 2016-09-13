package com.example.triibe.triibeuserapp.edit_question;

import com.example.triibe.triibeuserapp.data.Query;

/**
 * @author michael.
 */
public interface EditQuestionContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showEditSurvey();

        void showEditOption();
    }

    interface UserActionsListener {

        void editQuestion(String surveyId, String questionid, String imageUrl, String title, String intro, Query query, boolean editOption);
    }
}
