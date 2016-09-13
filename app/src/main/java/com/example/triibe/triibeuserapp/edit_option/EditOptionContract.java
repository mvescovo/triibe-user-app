package com.example.triibe.triibeuserapp.edit_option;

/**
 * @author michael.
 */
public interface EditOptionContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showEditQuestion();
    }

    interface UserActionsListener {

        void editOption(String surveyId, String questionId, String id, String phrase, String extraInput, String extraInputType,
                        String extraInputHint);
    }
}
