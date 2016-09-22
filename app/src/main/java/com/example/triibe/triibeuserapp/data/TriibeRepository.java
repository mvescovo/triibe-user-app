package com.example.triibe.triibeuserapp.data;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Access local TRIIBE data.
 *
 * @author michael.
 */
public interface TriibeRepository {

    interface GetUserSurveyIdsCallback {
        void onUserSurveyIdsLoaded(Map<String, Boolean> userSurveyIds);
    }

    interface GetSurveyCallback {
        void onSurveyLoaded(SurveyDetails survey);
    }

    interface GetQuestionsCallback {
        void onQuestionsLoaded(Map<String, Question> questions);
    }

    interface GetQuestionCallback {
        void onQuestionLoaded(Question question);
    }

    interface GetAnswersCallback {
        void onAnswersLoaded(Map<String, Answer> answers);
    }

    interface GetAnswerCallback {
        void onAnswerLoaded(Answer answer);
    }

    void getUserSurveyIds(@NonNull GetUserSurveyIdsCallback callback);

    void getSurvey(@NonNull String surveyId, @NonNull GetSurveyCallback callback);

    void getQuestions(@NonNull String surveyId, @NonNull GetQuestionsCallback callback);

    void getQuestion(@NonNull String questionId, @NonNull GetQuestionCallback callback);

    void getAnswers(@NonNull String surveyId, @NonNull String userId, @NonNull GetAnswersCallback callback);

    void getAnswer(@NonNull String questionId, @NonNull GetAnswerCallback callback);

    void saveAnswer(@NonNull String surveyId, @NonNull String userId, @NonNull String questionId, @NonNull Answer answer);
}
