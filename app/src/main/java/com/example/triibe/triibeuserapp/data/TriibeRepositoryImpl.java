package com.example.triibe.triibeuserapp.data;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author michael.
 */

public class TriibeRepositoryImpl implements TriibeRepository {

    private final TriibeServiceApi mTriibeServiceApi;
    private Map<String, Boolean> mCachedUserSurveyIds;
    private Map<String, Question> mCachedQuestions;
    private Map<String, Answer> mCachedAnswers;

    public TriibeRepositoryImpl(@NonNull TriibeServiceApi triibeServiceApi) {
        mTriibeServiceApi = triibeServiceApi;
    }

    @Override
    public void getUserSurveyIds(@NonNull final GetUserSurveyIdsCallback callback) {
        if (mCachedUserSurveyIds == null) {
            mTriibeServiceApi.getUserSurveyIds(new TriibeServiceApi.GetUserSurveyIdsCallback() {
                @Override
                public void onUserSurveyIdsLoaded(Map<String, Boolean> userSurveyIds) {
                    mCachedUserSurveyIds = ImmutableMap.copyOf(userSurveyIds);
                    callback.onUserSurveyIdsLoaded(mCachedUserSurveyIds);
                }
            });
        } else {
            callback.onUserSurveyIdsLoaded(mCachedUserSurveyIds);
        }
    }

    @Override
    public void getSurvey(@NonNull String surveyId, @NonNull final GetSurveyCallback callback) {
        mTriibeServiceApi.getSurvey(surveyId, new TriibeServiceApi.GetSurveyCallback() {
            @Override
            public void onSurveyLoaded(SurveyDetails survey) {
                callback.onSurveyLoaded(survey);
            }
        });
    }

    @Override
    public void getQuestions(@NonNull String surveyId, @NonNull final GetQuestionsCallback callback) {
        if (mCachedQuestions == null) {
            mTriibeServiceApi.getQuestions(surveyId, new TriibeServiceApi.GetQuestionsCallback() {
                @Override
                public void onQuestionsLoaded(Map<String, Question> questions) {
                    mCachedQuestions = ImmutableMap.copyOf(questions);
                    callback.onQuestionsLoaded(mCachedQuestions);
                }
            });
        } else {
            callback.onQuestionsLoaded(mCachedQuestions);
        }

    }

    @Override
    public void getQuestion(@NonNull String questionId, @NonNull GetQuestionCallback callback) {

    }

    @Override
    public void getAnswers(@NonNull String surveyId, @NonNull String userId, @NonNull final GetAnswersCallback callback) {
        if (mCachedAnswers == null) {
            mTriibeServiceApi.getAnswers(surveyId, userId, new TriibeServiceApi.GetAnswersCallback() {
                @Override
                public void onAnswersLoaded(Map<String, Answer> answers) {
                    mCachedAnswers = ImmutableMap.copyOf(answers);
                    callback.onAnswersLoaded(mCachedAnswers);
                }
            });
        } else {
            callback.onAnswersLoaded(mCachedAnswers);
        }
    }

    @Override
    public void getAnswer(@NonNull String questionId, @NonNull GetAnswerCallback callback) {

    }

    @Override
    public void saveAnswer(@NonNull String surveyId, @NonNull String userId, @NonNull String questionId, @NonNull Answer answer) {
        mTriibeServiceApi.saveAnswer(surveyId, userId, questionId, answer);
    }
}
