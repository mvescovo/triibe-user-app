package com.example.triibe.triibeuserapp.edit_trigger;

import android.support.annotation.NonNull;

import com.example.triibe.triibeuserapp.data.SurveyTrigger;

import java.util.List;

/**
 * @author michael.
 */
public interface EditTriggerContract {

    interface View {
        void setProgressIndicator(boolean active);

        void addTriggerIdsToAutoComplete(List<String> triggerIds);

        void showTrigger(SurveyTrigger trigger);

        void setCurrentLocation(double lat, double lon);

        void showEditSurey(@NonNull Integer resultCode);
    }

    interface UserActionsListener {

        void getTriggerIds(@NonNull String surveyId, @NonNull Boolean forceUpdate);

        void getTrigger(@NonNull String triggerId);

        void saveTrigger(SurveyTrigger trigger);

        void deleteTrigger(@NonNull String triggerId);
    }
}
