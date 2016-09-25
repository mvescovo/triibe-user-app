package com.example.triibe.triibeuserapp.edit_question;

import android.support.annotation.NonNull;

import com.example.triibe.triibeuserapp.data.QuestionDetails;

import java.util.List;

/**
 * @author michael.
 */
public interface EditQuestionContract {

    interface View {

        void setProgressIndicator(boolean active);

        void addQuestionIdsToAutoComplete(List<String> questionIds);

        void showQuestionDetails(QuestionDetails questionDetails);

        void showEditOption();

        void showEditSurvey(@NonNull Integer resultCode);
    }

    interface UserActionsListener {

        void getQuestionIds(@NonNull String surveyId, @NonNull Boolean forceUpdate);

        void getQuestion(@NonNull String questionId);

        void saveQuestion(QuestionDetails questionDetails);

        void deleteQuestion(@NonNull String questionId);

        void editOption();
    }
}
