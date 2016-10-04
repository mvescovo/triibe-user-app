package com.example.triibe.triibeuserapp.edit_question;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.triibe.triibeuserapp.data.QuestionDetails;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author michael.
 */
public class EditQuestionPresenter implements EditQuestionContract.UserActionsListener {

    private static final String TAG = "EditQuestionPresenter";
    private TriibeRepository mTriibeRepository;
    private EditQuestionContract.View mView;
    private String mSurveyId;


    public EditQuestionPresenter(TriibeRepository triibeRepository, EditQuestionContract.View view) {
        mTriibeRepository = triibeRepository;
        mView = view;
    }

    @Override
    public void getQuestionIds(@NonNull String surveyId, @NonNull Boolean forceUpdate) {
        mSurveyId = surveyId;

        mView.setProgressIndicator(true);

        final String path = "surveys/" + surveyId + "/questionIds";
        if (forceUpdate) {
            mTriibeRepository.refreshQuestionIds();
        }
        EspressoIdlingResource.increment();
        mTriibeRepository.getQuestionIds(path, new TriibeRepository.GetQuestionIdsCallback() {
            @Override
            public void onQuestionIdsLoaded(@Nullable Map<String, Boolean> questionIds) {
                EspressoIdlingResource.decrement();
                List<String> questionIdsArray;
                if (questionIds != null) {
                    questionIdsArray = new ArrayList<>(questionIds.keySet());
                } else {
                    questionIdsArray = new ArrayList<>();
                }
                mView.addQuestionIdsToAutoComplete(questionIdsArray);
            }
        });

        mView.setProgressIndicator(false);
    }

    @Override
    public void getQuestion(@NonNull String questionId) {
        mView.setProgressIndicator(true);

        EspressoIdlingResource.increment();
        mTriibeRepository.getQuestion(mSurveyId, questionId, new TriibeRepository.GetQuestionCallback() {
            @Override
            public void onQuestionLoaded(@Nullable QuestionDetails question) {
                EspressoIdlingResource.decrement();
                if (question != null) {
                    mView.showQuestionDetails(question);
                } else {
                    Log.d(TAG, "onQuestionLoaded: QUESTION NULL");
                }
                mView.setProgressIndicator(false);
            }
        });
    }

    @Override
    public void saveQuestion(QuestionDetails questionDetails) {
        mView.setProgressIndicator(true);

        mTriibeRepository.saveQuestion(questionDetails.getSurveyId(), questionDetails.getId(), questionDetails);

        mView.setProgressIndicator(false);
    }

    @Override
    public void deleteQuestion(@NonNull String questionId) {
        mTriibeRepository.deleteQuestion(mSurveyId, questionId);
    }

    @Override
    public void editOption() {
        mView.showEditOption();
    }
}
