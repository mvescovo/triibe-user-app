package com.example.triibe.triibeuserapp.edit_survey;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author michael.
 */
public class EditSurveyPresenter implements EditSurveyContract.UserActionsListener {

    private static final String TAG = "EditSurveyPresenter";
    private TriibeRepository mTriibeRepository;
    private EditSurveyContract.View mView;

    public EditSurveyPresenter(TriibeRepository triibeRepository, EditSurveyContract.View view) {
        mTriibeRepository = triibeRepository;
        mView = view;
    }

    @Override
    public void loadSurveyIds(@NonNull Boolean forceUpdate) {
        mView.setProgressIndicator(true);

        final String path = "surveyIds";
        if (forceUpdate) {
            mTriibeRepository.refreshSurveyIds();
        }
        EspressoIdlingResource.increment();
        mTriibeRepository.getSurveyIds(path, new TriibeRepository.GetSurveyIdsCallback() {
            @Override
            public void onSurveyIdsLoaded(@Nullable Map<String, Boolean> surveyIds) {
                EspressoIdlingResource.decrement();
                List<String> surveyIdsArray;
                if (surveyIds != null) {
                    surveyIdsArray = new ArrayList<>(surveyIds.keySet());
                } else {
                    surveyIdsArray = new ArrayList<>();
                }
                mView.addSurveyIdsToAutoComplete(surveyIdsArray);
            }
        });

        mView.setProgressIndicator(false);
    }

    @Override
    public void getSurvey(@NonNull String surveyId) {
        mView.setProgressIndicator(true);

        EspressoIdlingResource.increment();
        mTriibeRepository.getSurvey(surveyId, new TriibeRepository.GetSurveyCallback() {
            @Override
            public void onSurveyLoaded(@Nullable SurveyDetails survey) {
                EspressoIdlingResource.decrement();
                if (survey != null) {
                    mView.showSurveyDetails(survey);
                    mView.setProgressIndicator(false);
                } else {
                    Log.d(TAG, "onSurveyLoaded: SURVEY NULL");
                }
            }
        });
    }

    @Override
    public void saveSurvey(String surveyId, String description, String points,
                           String numProtectedQuestions, boolean active) {
        mView.setProgressIndicator(true);

        // Save survey with "s" prefix. Numerical values will create an array on firebase.
        SurveyDetails surveyDetails = new SurveyDetails("s" + surveyId, description, points,
                numProtectedQuestions, active);

        mTriibeRepository.saveSurvey(surveyDetails.getId(), surveyDetails);

        mView.setProgressIndicator(false);
    }

    @Override
    public void deleteSurvey(@NonNull String surveyId) {
        // Save survey with "s" prefix. Numerical values will create an array on firebase.
        mTriibeRepository.deleteSurvey("s" + surveyId);
    }

    @Override
    public void editQuestion() {
        mView.showEditQuestion();
    }

    @Override
    public void editTrigger() {
        mView.showEditTrigger();
    }
}
