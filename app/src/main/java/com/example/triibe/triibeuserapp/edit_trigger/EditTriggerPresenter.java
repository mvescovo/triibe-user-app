package com.example.triibe.triibeuserapp.edit_trigger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.triibe.triibeuserapp.data.SurveyTrigger;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author michael.
 */
public class EditTriggerPresenter implements EditTriggerContract.UserActionsListener {

    private static final String TAG = "EditTriggerPresenter";
    private TriibeRepository mTriibeRepository;
    EditTriggerContract.View mView;
    private String mSurveyId;

    public EditTriggerPresenter(TriibeRepository triibeRepository, EditTriggerContract.View view) {
        mTriibeRepository = triibeRepository;
        mView = view;
    }

    @Override
    public void getTriggerIds(@NonNull String surveyId, @NonNull Boolean forceUpdate) {
        mSurveyId = surveyId;

        mView.setProgressIndicator(true);

        final String path = "triggerIds";
        if (forceUpdate) {
            mTriibeRepository.refreshTriggerIds();
        }
        EspressoIdlingResource.increment();
        mTriibeRepository.getTriggerIds(path, new TriibeRepository.GetTriggerIdsCallback() {
            @Override
            public void onTriggerIdsLoaded(@Nullable Map<String, Boolean> triggerIds) {
                EspressoIdlingResource.decrement();
                List<String> triggerIdsArray;
                if (triggerIds != null) {
                    triggerIdsArray = new ArrayList<>(triggerIds.keySet());
                } else {
                    triggerIdsArray = new ArrayList<>();
                }
                mView.addTriggerIdsToAutoComplete(triggerIdsArray);
            }
        });

        mView.setProgressIndicator(false);
    }

    @Override
    public void getTrigger(@NonNull String triggerId) {
        mView.setProgressIndicator(true);

        EspressoIdlingResource.increment();
        mTriibeRepository.getTrigger(mSurveyId, triggerId, new TriibeRepository.GetTriggerCallback() {
            @Override
            public void onTriggerLoaded(@Nullable SurveyTrigger trigger) {
                EspressoIdlingResource.decrement();
                if (trigger != null) {
                    mView.showTrigger(trigger);
                } else {
                    Log.d(TAG, "onTriggerLoaded: TRIGGER NULL");
                }
                mView.setProgressIndicator(false);
            }
        });
    }

    @Override
    public void saveTrigger(SurveyTrigger trigger) {
        mView.setProgressIndicator(true);

        mTriibeRepository.saveTrigger(trigger.getSurveyId(), trigger.getId(), trigger);

        mView.setProgressIndicator(false);
    }

    @Override
    public void deleteTrigger(@NonNull String triggerId) {
        mTriibeRepository.deleteTrigger(mSurveyId, triggerId);
    }
}
