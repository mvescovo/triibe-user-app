package com.example.triibe.triibeuserapp.view_question;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Answer;
import com.example.triibe.triibeuserapp.data.AnswerDetails;
import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.data.Question;
import com.example.triibe.triibeuserapp.data.QuestionDetails;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author michael.
 */
public class ViewQuestionPresenter implements ViewQuestionContract.UserActionsListener {

    private static final String TAG = "ViewQuestionPresenter";
    TriibeRepository mTriibeRepository;
    ViewQuestionContract.View mView;
    private String mSurveyId;
    private String mUserId;
    private int mNumProtectedQuestions;
    private int mCurrentQuestionNum;
    @VisibleForTesting
    public Map<String, Question> mQuestions;
    @VisibleForTesting
    public Map<String, Answer> mAnswers;


    public ViewQuestionPresenter(TriibeRepository triibeRepository, ViewQuestionContract.View view,
                                 String surveyId, String userId, int numProtectedQuestions) {
        mTriibeRepository = triibeRepository;
        mView = view;
        mSurveyId = surveyId;
        mUserId = userId;
        mNumProtectedQuestions = numProtectedQuestions;
        mCurrentQuestionNum = 1;
        mQuestions = new HashMap<>();
        mAnswers = new HashMap<>();
    }

    @Override
    public void loadCurrentQuestion() {
        mView.setIndeterminateProgressIndicator(true);

        loadQuestions(new LoadQuestionsCallback() {
            @Override
            public void onQuestionsLoaded(Map<String, Question> questions) {
                mQuestions = questions;
                if (mQuestions == null) {
                    mQuestions = new HashMap<>();
                }
                loadAnswers(new LoadAnswersCallback() {
                    @Override
                    public void onAnswersLoaded(Map<String, Answer> answers) {
                        mAnswers = answers;
                        if (mAnswers == null) {
                            mAnswers = new HashMap<>();
                        }
                        if (mAnswers.size() >= mNumProtectedQuestions &&
                                mCurrentQuestionNum <= mNumProtectedQuestions) {
                            mCurrentQuestionNum = mAnswers.size() + 1;
                        }
                        displayCurrentQuestion();
                    }
                }, true);
            }
        }, true);
    }

    private void loadQuestions(@NonNull final LoadQuestionsCallback callback,  @NonNull Boolean forceUpdate) {
        if (forceUpdate) {
            mTriibeRepository.refreshQuestions();
        }
        EspressoIdlingResource.increment();
        mTriibeRepository.getQuestions(mSurveyId, new TriibeRepository.GetQuestionsCallback() {
            @Override
            public void onQuestionsLoaded(Map<String, Question> questions) {
                EspressoIdlingResource.decrement();
                callback.onQuestionsLoaded(questions);
            }
        });
    }

    public void loadAnswers(@NonNull final LoadAnswersCallback callback,  @NonNull Boolean forceUpdate) {
        if (forceUpdate) {
            mTriibeRepository.refreshAnswers();
        }
        EspressoIdlingResource.increment();
        mTriibeRepository.getAnswers(mSurveyId, mUserId, new TriibeRepository.GetAnswersCallback() {
            @Override
            public void onAnswersLoaded(Map<String, Answer> answers) {
                EspressoIdlingResource.decrement();
                callback.onAnswersLoaded(answers);
            }
        });
    }

    /*
    * Unmarshal the current question and display to the user.
    * */
    private void displayCurrentQuestion() {
        /*
        * Display question details
        * */
        Question question = mQuestions.get("q" + mCurrentQuestionNum);
        if (question != null) {
            QuestionDetails questionDetails = question.getQuestionDetails();
            String imageUrl = questionDetails.getImageUrl();
            if (imageUrl != null && !imageUrl.contentEquals("")) {
                mView.showImage(imageUrl);
            } else {
                mView.hideImage();
            }

            String title = questionDetails.getTitle();
            if (title != null && !title.contentEquals("")) {
                mView.showTitle(title);
            } else {
                mView.hideTitle();
            }

            String intro = questionDetails.getIntro();
            String introLinkKey = questionDetails.getIntroLinkKey();
            String introLinkUrl = questionDetails.getIntroLinkUrl();
            if (intro != null && !intro.contentEquals("")) {
                mView.showIntro(intro, introLinkKey, introLinkUrl);
            } else {
                mView.hideIntro();
            }

            String phrase = questionDetails.getPhrase();
            if (phrase != null && !phrase.contentEquals("")) {
                mView.showPhrase(phrase);
            } else {
                mView.hidePhrase();
            }

            /*
            * Display question options
            * */
            Map<String, Option> options = question.getOptions();
            String type = questionDetails.getType();
            if (type == null) {
                Log.d(TAG, "displayCurrentQuestion: NO TYPE");
                return;
            }

            switch (type) {
                case "radio":
                    mView.showRadioButtonGroup();
                    for (int i = 1; i <= options.size(); i++) {
                        Option option = options.get("o" + i);
                        String optionPhrase = option.getPhrase();
                        String extraInputHint = option.getExtraInputHint();
                        String extraInputType = option.getExtraInputType();
                        if (optionPhrase == null) {
                            Log.d(TAG, "displayCurrentQuestion: NO OPTION PHRASE");
                        } else {
                            mView.showRadioButtonItem(optionPhrase, extraInputHint, extraInputType);
                        }
                    }
                    break;
                case "checkbox":
                    mView.showCheckboxGroup();
                    for (int i = 1; i <= options.size(); i++) {
                        Option option = options.get("o" + i);
                        String optionPhrase = option.getPhrase();
                        String extraInputHint = option.getExtraInputHint();
                        String extraInputType = option.getExtraInputType();
                        if (optionPhrase == null) {
                            Log.d(TAG, "displayCurrentQuestion: NO OPTIONS PHRASE");
                        } else {
                            mView.showCheckboxItem(optionPhrase, extraInputHint, extraInputType, options.size());
                        }
                    }
                    break;
                case "text":
                    mView.showTextboxGroup();
                    for (int i = 1; i <= options.size(); i++) {
                        Option option = options.get("o" + i);
                        String optionPhrase = option.getPhrase();
//                    String extraInputType = option.getExtraInputType(); // TODO: 19/09/16 set this for all question options in firebase
                        String extraInputType = "text";
                        if (optionPhrase == null) {
                            Log.d(TAG, "displayCurrentQuestion: NO OPTIONS PHRASE");
                        } else {
                            mView.showTextboxItem(optionPhrase, extraInputType);
                        }
                    }
//                mEditTextGroup.removeAllViews();
//                mEditTextGroup.setVisibility(View.VISIBLE);
//
//                for (int i = 0; i < question.getOptions().size(); i++) {
//                    final TextInputEditText textInputEditText = new TextInputEditText(this);
//                    textInputEditText.setHint(((Option)question.getOptions().get(Integer.toString(i))).getPhrase());
//                    mEditTextGroup.addView(textInputEditText, i);
//                    if (((Option)question.getOptions().get(Integer.toString(i))).getExtraInputType() != null &&
//                            ((Option)question.getOptions().get(Integer.toString(i))).getExtraInputType().contentEquals("InputType.TYPE_CLASS_PHONE")) {
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_PHONE);
//                    } else if (((Option)question.getOptions().get(Integer.toString(i))).getExtraInputType() != null &&
//                            ((Option)question.getOptions().get(Integer.toString(i))).getExtraInputType().contentEquals("TYPE_TEXT_VARIATION_EMAIL_ADDRESS")) {
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                    } else {
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_TEXT);
//                    }
//                }
                    break;
            }
        }

        float progress = (float) mCurrentQuestionNum / mQuestions.size() * 100;
        mView.setProgressIndicator((int)progress);

        displayCurrentAnswer();
    }

    /*
    * Unmarshal the current answer and display to the user.
    * */
    private void displayCurrentAnswer() {
//        mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        Question question = mQuestions.get("q" + mCurrentQuestionNum);
        if (question != null) {
            QuestionDetails questionDetails = question.getQuestionDetails();
            Map<String, Option> options = question.getOptions();
            String type = questionDetails.getType();

            if (mAnswers.size() < mCurrentQuestionNum) {
                if (type.contentEquals("text")) {
                    for (int i = 0; i < options.size(); i++) {
//                    final int viewNumber = i;
//                    Option option = options.get("o" + i);
//                    String extraInputType = option.getExtraInputType();
//                    if (extraInputType != null && extraInputType.contentEquals("phone")) {
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_PHONE);
//                    } else if (extraInputType != null && extraInputType.contentEquals("email")) {
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                    } else {
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_TEXT);
                    }
//                    ((TextInputEditText) mEditTextGroup.getChildAt(i)).addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                            onTextInputEditTextChanged(viewNumber);
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable editable) {
//
//                        }
//                    });
//                }
                }
            } else {
                Answer answer = mAnswers.get("a" + mCurrentQuestionNum);
//            Log.d(TAG, "displayCurrentAnswer: size: " + mAnswers.size());
                AnswerDetails answerDetails = answer.getAnswerDetails();
                Map<String, Option> selectedOptions = answer.getSelectedOptions();
                String answerType = answerDetails.getType();
                if (answerType == null) {
                    Log.d(TAG, "displayCurrentAnswer: NO ANSWER TYPE");
                    return;
                }
                switch (type) {
                    case "radio":
                        if (selectedOptions != null) {
                            // There can only be one selected radio button so get this only key
                            Set keySet = selectedOptions.keySet();
                            String onlykey = "";
                            for (Object key : keySet) {
                                onlykey = (String) key;
                            }
                            Option selectedOption = selectedOptions.get(onlykey);
                            String selectedOptionPhrase = selectedOption.getPhrase();
                            boolean selectedOptionHasExtraInput = selectedOption.getHasExtraInput();
                            String selectedOptionExtraInput = selectedOption.getExtraInput();
                            String selectedOptionExtraInputHint = selectedOption.getExtraInputHint();
                            String selectedOptionExtraInputType = selectedOption.getExtraInputType();
                            mView.selectRadioButtonItem(selectedOptionPhrase,
                                    selectedOptionHasExtraInput,
                                    selectedOptionExtraInputHint,
                                    selectedOptionExtraInputType,
                                    selectedOptionExtraInput,
                                    options.size());
//                        for (int i = 0; i < options.size(); i++) {
//                            if (((RadioButton) mRadioGroup.getChildAt(i)).getText().equals(answer.getSelectedOptions().get(0).getPhrase())) {
//                                ((RadioButton) mRadioGroup.getChildAt(i)).toggle();
//                                if (question.getQuery().getOptions().get(i).getHasExtraInput()) {
//                                    mTextInputLayout.setVisibility(View.VISIBLE);
//                                    mTextInputEditText.setVisibility(View.VISIBLE);
//                                    mTextInputEditText.setText("");
//                                    if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
//                                            question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_NUMBER")) {
//                                        mTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                                    }
//                                    mTextInputEditText.requestFocus();
//                                    if (answer.getSelectedOptions().get(0).getExtraInput() != null) {
//                                        mTextInputEditText.append(answer.getSelectedOptions().get(0).getExtraInput());
//                                    }
//                                    mTextInputEditText.setHint(answer.getSelectedOptions().get(0).getExtraInputHint());
//                                    mTextInputEditText.addTextChangedListener(this);
//                                } else {
//                                    mTextInputEditText.setText("");
//                                    mTextInputEditText.setVisibility(View.GONE);
//                                    mTextInputEditText.removeTextChangedListener(this);
//                                }
//                            }
//                        }
                        }
                        break;
                    case "checkbox":
                        if (selectedOptions != null) {
                            for (int i = 1; i <= options.size(); i++) {
                                Option selectedOption = selectedOptions.get("o" + i);
                                if (selectedOption != null) {
                                    String selectedOptionPhrase = selectedOption.getPhrase();
                                    boolean selectedOptionChecked = selectedOption.isChecked();
                                    mView.selectCheckboxItem(selectedOptionPhrase,
                                            selectedOptionChecked,
                                            options.size());
                                }

//                            for (int j = 0; j < answer.getSelectedOptions().size(); j++) {
//                                if (((CheckBox) mCheckboxGroup.getChildAt(i)).getText().equals(answer.getSelectedOptions().get(j).getPhrase())) {
//                                    ((CheckBox) mCheckboxGroup.getChildAt(i)).toggle();
//                                    if (question.getQuery().getOptions().get(i).getHasExtraInput()) {
//                                        mTextInputLayout.setVisibility(View.VISIBLE);
//                                        mTextInputEditText.setVisibility(View.VISIBLE);
//                                        mTextInputEditText.setText("");
//                                        if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
//                                                question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_NUMBER")) {
//                                            mTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                                        }
//                                        mTextInputEditText.requestFocus();
//
//                                        for (int k = 0; k < mQuestions.get(mCurrentQuestionNum - 1).getQuery().getOptions().size(); k++) {
//                                            if (mAnswers.get(mCurrentQuestionNum - 1).getSelectedOptions().get(k).getExtraInput() != null) {
//                                                mTextInputEditText.append(answer.getSelectedOptions().get(k).getExtraInput());
//                                            }
//                                        }
//
//                                        mTextInputEditText.setHint(answer.getSelectedOptions().get(i).getExtraInputHint());
//                                        mTextInputEditText.addTextChangedListener(this);
//                                    } else {
//                                        mTextInputEditText.setText("");
//                                        mTextInputEditText.setVisibility(View.GONE);
//                                        mTextInputEditText.removeTextChangedListener(this);
//                                    }
//                                }
//                            }
                            }
                        }
                        break;
                    case "text":
//                    for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
//                        final int viewNumber = i;
//
//                        if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
//                                question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_PHONE")) {
//                            ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_PHONE);
//                        } else if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
//                                question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("TYPE_TEXT_VARIATION_EMAIL_ADDRESS")) {
//                            ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                        } else {
//                            ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_TEXT);
//                        }
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                                onTextInputEditTextChanged(viewNumber);
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable editable) {
//
//                            }
//                        });
//
//                        if (answer.getSelectedOptions() != null) {
//                            for (int j = 0; j < answer.getSelectedOptions().size(); j++) {
//                                if (((TextInputEditText) mEditTextGroup.getChildAt(i)).getHint().equals(answer.getSelectedOptions().get(j).getPhrase())) {
//                                    ((TextInputEditText) mEditTextGroup.getChildAt(i)).setText(answer.getSelectedOptions().get(j).getExtraInput());
//                                }
//                            }
//                        }
//                    }
                        break;
                }
            }
        }
        updateNextButton();
        mView.setIndeterminateProgressIndicator(false);
    }

    /*
    * Ensure the next button is of the correct type.
    * If this is the last question in the survey it should be a submit button.
    * If there was only one question in the survey the user wouldn't have pressed next yet.
    * */
    private void updateNextButton() {
        if (mCurrentQuestionNum == mQuestions.size()) {
            mView.showSubmitButton();
        }
        if (mCurrentQuestionNum > 1 &&
                mCurrentQuestionNum != mNumProtectedQuestions + 1) {
            mView.showBackButton();
        }
    }

    @Override
    public void saveAnswer(String phrase, String type, boolean checked) {
        Question question = mQuestions.get("q" + mCurrentQuestionNum);
        QuestionDetails questionDetails = question.getQuestionDetails();
        String questionId = questionDetails.getId();
        Map<String, Option> options = question.getOptions();

        switch (type) {
            case "radio":
            case "checkbox":
                for (int i = 1; i <= options.size(); i++) {
                    Option option = options.get("o" + i);
                    String optionPhrase = option.getPhrase();
                    boolean hasExtraInput = option.getHasExtraInput();
                    if (optionPhrase.contentEquals(phrase)) {
                        if (hasExtraInput && checked) {
                            String extraInputHint = option.getExtraInputHint();
                            String extraInputType = option.getExtraInputType();
                            mView.showExtraInputTextboxItem(extraInputHint, extraInputType, null);
                        }
                    }
                }


                // TODO: 19/09/16 get the answer from manswers if it's there. don't delete previous answers if there's options that still need to be there.
                if (mAnswers == null) {
                    mAnswers = new HashMap<>();
                }
                Answer answer;

                if (mAnswers.get("a" + mCurrentQuestionNum) != null) {
                    // Modify existing answer
                    answer = mAnswers.get("a" + mCurrentQuestionNum);
                    Map<String, Option> previousOptions = answer.getSelectedOptions();
                    for (int i = 1; i <= options.size(); i++) {
                        Option option = options.get("o" + i);
                        String optionPhrase = option.getPhrase();
                        if (optionPhrase.contentEquals(phrase) && checked) {
                            option.setChecked(true);
                            previousOptions.put("o" + i, option);
                        } else if (optionPhrase.contentEquals(phrase) && !checked) {
                            previousOptions.remove("o" + i);
                        } else if (type.contentEquals("radio")) {
                            if (previousOptions.containsKey("o" + i)) {
                                previousOptions.remove("o" + i);
                            }
                        }
                    }
                    if (previousOptions.size() == 0) {
                        answer = new Answer();
                    }
                } else {
                    // Create a new answer
                    Map<String, Option> selectedOptions = new HashMap<>();
                    for (int i = 1; i <= options.size(); i++) {
                        Option option = options.get("o" + i);
                        String optionPhrase = option.getPhrase();
                        if (optionPhrase.contentEquals(phrase) && checked) {
                            option.setChecked(true);
                            selectedOptions.put("o" + i, option);
                        }
                    }

                    AnswerDetails answerDetails = new AnswerDetails(questionId, "a" + mCurrentQuestionNum, type);
                    if (selectedOptions.size() == 0) {
                        // Don't save an answerDetails object. This will make firebase delete the
                        // current answer.We want to do this so we don't have just answerDetails and no
                        // answer options saved to the database. This would lead to a crash when
                        // loading the answer.
                        answer = new Answer();
                    } else {
                        answer = new Answer(answerDetails, selectedOptions);
                    }
                }

                mTriibeRepository.saveAnswer(mSurveyId, mUserId, "a" + mCurrentQuestionNum, answer);

                break;
            case "text":
                break;
            case "extraText":
                Answer extraTextAnswer = mAnswers.get("a" + mCurrentQuestionNum);
                Map<String, Option> extraTextSelectedOptions;
                if (extraTextAnswer != null) {
                    extraTextSelectedOptions = extraTextAnswer.getSelectedOptions();
                    if (extraTextSelectedOptions != null) {
                        for (int i = 1; i <= options.size(); i++) {
                            Option selectedOption = extraTextSelectedOptions.get("o" + i);
                            if (selectedOption != null) {
                                boolean hasExtraInput = selectedOption.getHasExtraInput();
                                if (hasExtraInput) {
                                    try {
                                        // There is only one phrase for extraText.
                                        selectedOption.setExtraInput(phrase);
                                    } catch (ClassCastException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    mTriibeRepository.saveAnswer(mSurveyId, mUserId, "a" + mCurrentQuestionNum, extraTextAnswer);
                }
        }
    }

    @Override
    public void goToNextQuestion() {
        loadAnswers(new LoadAnswersCallback() {
            @Override
            public void onAnswersLoaded(Map<String, Answer> answers) {
                mAnswers = answers;
                checkAnswerToGoNext();
            }
        }, false);
    }

    private void checkAnswerToGoNext() {
        if (mQuestions != null && mQuestions.size() >= mCurrentQuestionNum) {
            Question question = mQuestions.get("q" + mCurrentQuestionNum);
            QuestionDetails questionDetails = question.getQuestionDetails();
            Map<String, Option> options = question.getOptions();
            String requiredPhrase = questionDetails.getRequiredPhrase();
            String incorrectAnswerPhrase = questionDetails.getIncorrectAnswerPhrase();
            String type = questionDetails.getType();

            if (mAnswers != null && mAnswers.size() >= mCurrentQuestionNum) {
                Answer answer = mAnswers.get("a" + mCurrentQuestionNum);
                Map<String, Option> selectedOptions = new HashMap<>();
                if (answer != null) {
                    selectedOptions = answer.getSelectedOptions();
                }

                boolean answerOk = false;
                if (requiredPhrase != null) {
                    for (int i = 1; i <= options.size(); i++) {
                        Option selectedOption = selectedOptions.get("o" + i);
                        if (selectedOption != null) {
                            String selectedOptionPhrase = selectedOption.getPhrase();
                            if (selectedOptionPhrase.contentEquals(requiredPhrase)) {
                                answerOk = true;
                            }
                        }
                    }
                } else {
                    if (type.contentEquals("text")) {
                        answerOk = true;
                        for (int i = 1; i <= options.size(); i++) {
                            Option selectedOption = selectedOptions.get("o" + i);
                            if (selectedOption != null) {
                                String extraInput = selectedOption.getExtraInput();
                                if (extraInput == null || extraInput.contentEquals("")) {
                                    answerOk = false;
                                }
                            }
                        }
                    } else {
                        for (int i = 1; i <= options.size(); i++) {
                            Option selectedOption = selectedOptions.get("o" + i);
                            if (selectedOption != null) {
                                String selectedOptionPhrase = selectedOption.getPhrase();
                                boolean hasExtraInput = selectedOption.getHasExtraInput();
                                String extraInput = selectedOption.getExtraInput();
                                if (!selectedOptionPhrase.contentEquals("")) {
                                    answerOk = !hasExtraInput || extraInput != null && !extraInput.contentEquals("");
                                }
                            }
                        }
                    }
                }

                Log.d(TAG, "answerOk: " + answerOk);

                if (answerOk) {
                    if (mCurrentQuestionNum == mQuestions.size()) {
                        // TODO: 17/09/16 remove survey from users list
                        // Also, when adding surveys, check that an answer for the user doesn't already exist.

                        mTriibeRepository.removeUserSurvey(mUserId, mSurveyId);
                        mView.showViewSurveys();
                    } else {
                        mCurrentQuestionNum++;
                        mView.showBackButton();
//                    mTextInputEditText.removeTextChangedListener(this);
                        displayCurrentQuestion();
                        if (mCurrentQuestionNum == mQuestions.size()) {
                            mView.showSubmitButton();
                        }
                    }
                } else {
                    if (incorrectAnswerPhrase != null) {
                        mView.showSnackbar(incorrectAnswerPhrase, Snackbar.LENGTH_SHORT);
                    } else {
                        mView.showSnackbar(((Context) mView).getString(R.string.question_incomplete), Snackbar.LENGTH_SHORT);
                    }
                }

            } else if (requiredPhrase != null) {
                mView.showSnackbar(incorrectAnswerPhrase, Snackbar.LENGTH_SHORT);
            } else {
                mView.showSnackbar(((Context) mView).getString(R.string.question_incomplete), Snackbar.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void goToPreviousQuestion() {
        loadAnswers(new LoadAnswersCallback() {
            @Override
            public void onAnswersLoaded(Map<String, Answer> answers) {
                mAnswers = answers;
                checkAnswerToGoPrevious();
            }
        }, false);
    }

    public void checkAnswerToGoPrevious() {
        // Prevent users from changing their responses to qualifying questions
        if ((mCurrentQuestionNum > 1) &&
                ((mCurrentQuestionNum <= mNumProtectedQuestions) ||
                (mCurrentQuestionNum >= mNumProtectedQuestions + 2))) {
            mCurrentQuestionNum--;
            if (mCurrentQuestionNum == 1 ||
                    mCurrentQuestionNum == mNumProtectedQuestions + 1) {
                mView.hideBackButton();
            }
            displayCurrentQuestion();
        } else {
            mView.showSnackbar("You're at the first question.", Snackbar.LENGTH_SHORT); // TODO: 22/09/16 work out where to put this string (testing will not work when calling from strings.xml because of no mock context. Or work out how to mock it).
        }
    }

    @Override
    public void submitSurvey() {

    }

    public Map<String, Question> getQuestions() {
        return mQuestions;
    }

    public Map<String, Answer> getAnswers() {
        return mAnswers;
    }

    public int getCurrentQuestionNum() {
        return mCurrentQuestionNum;
    }
}

interface LoadQuestionsCallback {
    void onQuestionsLoaded(Map<String, Question> questions);
}

interface LoadAnswersCallback {
    void onAnswersLoaded(Map<String, Answer> answers);
}
