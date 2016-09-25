package com.example.triibe.triibeuserapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Access local TRIIBE data.
 *
 * @author michael.
 */
public interface TriibeRepository {

    // Surveys
    interface GetSurveyIdsCallback {
        void onSurveyIdsLoaded(@Nullable Map<String, Boolean> surveyIds);
    }

    interface GetSurveyCallback {
        void onSurveyLoaded(@Nullable SurveyDetails survey);
    }


    // Questions
    interface GetQuestionIdsCallback {
        void onQuestionIdsLoaded(@Nullable Map<String, Boolean> questionIds);
    }

    interface GetQuestionsCallback {
        void onQuestionsLoaded(@Nullable Map<String, Question> questions);
    }

    interface GetQuestionCallback {
        void onQuestionLoaded(@Nullable QuestionDetails question);
    }


    // Options
    interface GetOptionIdsCallback {
        void onOptionIdsLoaded(@Nullable Map<String, Boolean> optionIds);
    }

    interface GetOptionsCallback {
        void onOptionsLoaded(@Nullable Map<String, Option> options);
    }

    interface GetOptionCallback {
        void onOptionLoaded(@Nullable Option option);
    }


    // Triggers


    // Answers
    interface GetAnswersCallback {
        void onAnswersLoaded(@Nullable Map<String, Answer> answers);
    }

    interface GetAnswerCallback {
        void onAnswerLoaded(@Nullable AnswerDetails answer);
    }


    // Surveys
    void getSurveyIds(@NonNull String path, @NonNull GetSurveyIdsCallback callback);

    void refreshSurveyIds();

    void saveSurveyIds(@NonNull String path, @NonNull Map<String, Boolean> surveyIds);

    void getSurvey(@NonNull String surveyId, @NonNull GetSurveyCallback callback);

    void saveSurvey(@NonNull String surveyId, @NonNull SurveyDetails surveyDetails);

    void deleteSurvey(@NonNull String surveyId);

    // Questions
    void getQuestionIds(@NonNull String path, @NonNull GetQuestionIdsCallback callback);

    void refreshQuestionIds();

    void getQuestions(@NonNull String surveyId, @NonNull GetQuestionsCallback callback);

    void refreshQuestions();

    void saveQuestionIds(@NonNull String path, @NonNull Map<String, Boolean> questionIds);

    void getQuestion(@NonNull String surveyId, @NonNull String questionId, @NonNull GetQuestionCallback callback);

    void saveQuestion(@NonNull String surveyId, @NonNull String questionId, @NonNull QuestionDetails questionDetails);

    void deleteQuestion(@NonNull String surveyId, @NonNull String questionId);

    // Options
    void getOptionIds(@NonNull String path, @NonNull GetOptionIdsCallback callback);

    void refreshOptionIds();

    void getOptions(@NonNull String surveyId, @NonNull String questionId, @NonNull GetOptionsCallback callback);

    void refreshOptions();

    void saveOptionIds(@NonNull String path, @NonNull Map<String, Boolean> optionIds);

    void getOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId, @NonNull GetOptionCallback callback);

    void saveOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId, @NonNull Option option);

    void deleteOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId);

    // Triggers

    // Answers
    void getAnswers(@NonNull String surveyId, @NonNull String userId, @NonNull GetAnswersCallback callback);

    void refreshAnswers();

    void getAnswer(@NonNull String surveyId, @NonNull String questionId, @NonNull GetAnswerCallback callback);

    void saveAnswer(@NonNull String surveyId, @NonNull String userId, @NonNull String questionId, @NonNull Answer answer);
}
