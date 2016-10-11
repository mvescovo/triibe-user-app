package com.example.triibe.triibeuserapp.edit_option;

import android.support.annotation.NonNull;

import com.example.triibe.triibeuserapp.data.Option;

import java.util.List;

/**
 * @author michael.
 */
public interface EditOptionContract {

    interface View {

        void setProgressIndicator(boolean active);

        void addOptionIdsToAutoComplete(List<String> optionIds);

        void showOption(Option option);

        void showEditQuestion(@NonNull Integer resultCode);
    }

    interface UserActionsListener {

        void getOptionIds(@NonNull String surveyId, @NonNull String questionId, @NonNull Boolean forceUpdate);

        void getOption(@NonNull String optionId);

        void saveOption(Option option);

        void deleteOption(@NonNull String optionId);
    }
}
