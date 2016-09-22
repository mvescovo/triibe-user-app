package com.example.triibe.triibeuserapp.view_question;

import android.support.annotation.Nullable;

/**
 * @author michael.
 */
public interface ViewQuestionContract {

    interface View {

        void setIndeterminateProgressIndicator(boolean active);

        void setProgressIndicator(int progress);

        void showImage(String imageUrl);

        void hideImage();

        void showTitle(String title);

        void hideTitle();

        void showIntro(String intro, @Nullable String linkKey, @Nullable String linkUrl);

        void hideIntro();

        void showPhrase(String phrase);

        void hidePhrase();

        void showRadioButtonGroup();

        void showRadioButtonItem(String phrase, @Nullable String extraInputHint,
                                 @Nullable String extraInputType);

        void selectRadioButtonItem(String phrase, boolean hasExtraInput,
                                   @Nullable String extraInput,
                                   @Nullable String extraInputHint,
                                   @Nullable String extraInputType,
                                   int size);

        void showExtraInputTextboxItem(String hint, String type, @Nullable String text);

        void hideExtraInputTextboxItem();

        void showCheckboxGroup();

        void showCheckboxItem(String phrase, @Nullable String extraInputHint,
                              @Nullable String extraInputType,
                              int size);

        void selectCheckboxItem(String phrase, boolean checked, int size);

        void showTextboxGroup();

        void showTextboxItem(String hint, String type);

        void showSnackbar(String text, int duration);

        void showSubmitButton();

        void hideSubmitButton();

        void showViewSurveys();
    }

    interface UserActionsListener {

        void loadCurrentQuestion();

        void saveAnswer(String phrase, String type, boolean checked);

        void goToNextQuestion();

        void goToPreviousQuestion();

        void submitSurvey();
    }
}
