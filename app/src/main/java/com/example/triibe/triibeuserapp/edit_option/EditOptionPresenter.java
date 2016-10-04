package com.example.triibe.triibeuserapp.edit_option;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author michael.
 */
public class EditOptionPresenter implements EditOptionContract.UserActionsListener {

    private static final String TAG = "EditOptionPresenter";
    private TriibeRepository mTriibeRepository;
    EditOptionContract.View mView;
    private String mSurveyId;
    private String mQuestionId;

    public EditOptionPresenter(TriibeRepository triibeRepository, EditOptionContract.View view) {
        mTriibeRepository = triibeRepository;
        mView = view;
    }

    @Override
    public void getOptionIds(@NonNull String surveyId, @NonNull String questionId,
                             @NonNull Boolean forceUpdate) {
        mSurveyId = surveyId;
        mQuestionId = questionId;

        mView.setProgressIndicator(true);

        final String path = "surveys/" + surveyId + "/questions/" + questionId + "/optionIds";
        if (forceUpdate) {
            mTriibeRepository.refreshOptionIds();
        }
        EspressoIdlingResource.increment();
        mTriibeRepository.getOptionIds(path, new TriibeRepository.GetOptionIdsCallback() {
            @Override
            public void onOptionIdsLoaded(@Nullable Map<String, Boolean> optionIds) {
                EspressoIdlingResource.decrement();
                List<String> optionIdsArray;
                if (optionIds != null) {
                    optionIdsArray = new ArrayList<>(optionIds.keySet());
                } else {
                    optionIdsArray = new ArrayList<>();
                }
                mView.addOptionIdsToAutoComplete(optionIdsArray);
            }
        });

        mView.setProgressIndicator(false);
    }

    @Override
    public void getOption(@NonNull final String optionId) {
        mView.setProgressIndicator(true);

        EspressoIdlingResource.increment();
        mTriibeRepository.getOption(mSurveyId, mQuestionId, optionId, new TriibeRepository.GetOptionCallback() {
            @Override
            public void onOptionLoaded(@Nullable Option option) {
                EspressoIdlingResource.decrement();
                if (option != null) {
                    Log.d(TAG, "onOptionLoaded: GOT OPTION: " + option.getPhrase());
                    mView.showOption(option);
                } else {
                    Log.d(TAG, "onOptionLoaded: OPTION NULL");
                }
                mView.setProgressIndicator(false);
            }
        });
    }

    @Override
    public void saveOption(Option option) {
        mView.setProgressIndicator(true);

        mTriibeRepository.saveOption(mSurveyId, mQuestionId, option.getId(), option);

        mView.setProgressIndicator(false);
    }

    @Override
    public void deleteOption(@NonNull String optionId) {
        mTriibeRepository.deleteOption(mSurveyId, mQuestionId, optionId);
    }
}
