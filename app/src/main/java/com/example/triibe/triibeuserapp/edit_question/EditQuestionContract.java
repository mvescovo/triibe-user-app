package com.example.triibe.triibeuserapp.edit_question;

import com.example.triibe.triibeuserapp.data.QuestionDetails;

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

        void editQuestion(QuestionDetails questionDetails, boolean editOption);
    }
}
