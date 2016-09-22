package com.example.triibe.triibeuserapp.view_surveys;

import android.support.annotation.NonNull;

import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.data.TriibeRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class ViewSurveysPresenter implements ViewSurveysContract.UserActionsListener {

    private static final String TAG = "ViewSurveysPresenter";
    private TriibeRepository mTriibeRepository;
    private ViewSurveysContract.View mView;

    public ViewSurveysPresenter(TriibeRepository triibeRepository, ViewSurveysContract.View view) {
        mTriibeRepository = triibeRepository;
        mView = view;
    }

    @Override
    public void loadSurveys() {
        mView.setProgressIndicator(true);

        final Map<String, SurveyDetails> surveys = new HashMap<>();

        mTriibeRepository.getUserSurveyIds(new TriibeRepository.GetUserSurveyIdsCallback() {
                    @Override
                    public void onUserSurveyIdsLoaded(final Map<String, Boolean> userSurveyIds) {
                        Object[] surveyIds = userSurveyIds.keySet().toArray();
                        surveys.clear();
                        for (int i = 0; i < userSurveyIds.size(); i++) {
                            final int position = i;
                            mTriibeRepository.getSurvey(surveyIds[i].toString(),
                                    new TriibeRepository.GetSurveyCallback() {
                                @Override
                                public void onSurveyLoaded(SurveyDetails survey) {
                                    surveys.put("" + position, survey);
                                    if (surveys.size() == 0) {
                                        mView.showNoSurveysMessage();
                                    } else {
                                        // Only show survey and hide the progress bar when all
                                        // values are received.
                                        if (position == userSurveyIds.size() - 1) {
                                            mView.showSurveys(surveys);
                                            mView.setProgressIndicator(false);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void openSurveyQuestions(@NonNull String surveyId) {
        // Show the first question in the survey // TODO: 20/09/16 make it so it shows the question the user is up to
        mView.showQuestionUi(surveyId, "q1");
    }
}
