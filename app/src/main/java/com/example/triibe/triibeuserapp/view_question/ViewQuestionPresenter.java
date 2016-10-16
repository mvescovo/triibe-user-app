package com.example.triibe.triibeuserapp.view_question;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.example.triibe.triibeuserapp.data.Answer;
import com.example.triibe.triibeuserapp.data.AnswerDetails;
import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.data.Question;
import com.example.triibe.triibeuserapp.data.QuestionDetails;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.data.User;
import com.example.triibe.triibeuserapp.util.Constants;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
class ViewQuestionPresenter implements ViewQuestionContract.UserActionsListener {

    private static final String TAG = "ViewQuestionPresenter";
    private Map<String, Question> mQuestions;
    private Map<String, Answer> mAnswers;
    private TriibeRepository mTriibeRepository;
    private ViewQuestionContract.View mView;
    private String mSurveyId;
    private String mUserId;
    private String mQuestionId;
    private int mNumProtectedQuestions;
    private int mCurrentQuestionNum;
    private String mSurveyPoints;
    private boolean mAnswerComplete;
    private boolean mSurveyResumed;

    ViewQuestionPresenter(TriibeRepository triibeRepository, ViewQuestionContract.View view,
                          String surveyId, String userId, String questionId,
                          int numProtectedQuestions) {
        mTriibeRepository = triibeRepository;
        mView = view;
        mSurveyId = surveyId;
        mUserId = userId;
        mQuestionId = questionId;
        mNumProtectedQuestions = numProtectedQuestions;
        mQuestions = new HashMap<>();
        mAnswers = new HashMap<>();
        mCurrentQuestionNum = 1;
        mAnswerComplete = false;
        mSurveyResumed = false;
    }

    @Override
    public void loadCurrentQuestion() {
        mView.setIndeterminateProgressIndicator(true);

        // Remove notification for the survey.
        mView.removeNotification(mSurveyId);

        loadQuestions(true);
    }

    private void loadQuestions(@NonNull Boolean forceUpdate) {
        if (forceUpdate) {
            mTriibeRepository.refreshQuestions();
        }
        EspressoIdlingResource.increment();
        mTriibeRepository.getQuestions(mSurveyId, new TriibeRepository.GetQuestionsCallback() {
            @Override
            public void onQuestionsLoaded(@Nullable Map<String, Question> questions) {
                EspressoIdlingResource.decrement();
                if (questions != null) {
                    mQuestions = questions;
                } else {
                    mQuestions = new HashMap<>();
                }
                loadAnswers(true);
            }
        });
    }

    private void loadAnswers(@NonNull Boolean forceUpdate) {
        if (forceUpdate) {
            mTriibeRepository.refreshAnswers();
        }
        EspressoIdlingResource.increment();
        mTriibeRepository.getAnswers(mSurveyId, mUserId, new TriibeRepository.GetAnswersCallback() {
            @Override
            public void onAnswersLoaded(@Nullable Map<String, Answer> answers) {
                EspressoIdlingResource.decrement();
                if (answers != null) {
                    mAnswers = answers;
                } else {
                    mAnswers = new HashMap<>();
                }
                moveToAppropriateQuestion();
            }
        });
    }

    private void moveToAppropriateQuestion() {
        // Resume question after rotation
        if (!mSurveyResumed) {
            if (!mQuestionId.contentEquals("-1")) {
                // Move to questionId passed in (savedState)
                mCurrentQuestionNum = Integer.valueOf(mQuestionId.substring(1));

                // Make sure to move past protected questions if current answer ok
                if (mAnswers.size() > mNumProtectedQuestions && mCurrentQuestionNum <= mNumProtectedQuestions) {
                    if (answerOk(mAnswers.get("a" + mCurrentQuestionNum))) {
                        mCurrentQuestionNum = mNumProtectedQuestions + 1;
                    }
                }
            } else {
                // Move to the question the user is up to.
                if (mAnswers.size() < mQuestions.size()) {
                    // They haven't completed all questions so move to the next one.
                    mCurrentQuestionNum = mAnswers.size() + 1;
                } else {
                    // They have completed all questions, move to the last one.
                    mCurrentQuestionNum = mAnswers.size();
                }
            }
            mSurveyResumed = true;
        }

        displayCurrentQuestion();
    }

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
            if (options != null) {
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
                                mView.showRadioButtonItem(optionPhrase, extraInputHint,
                                        extraInputType);
                            }
                        }
                        break;
                    case "checkbox":
                        mView.showCheckboxGroup();
                        for (int i = 1; i <= options.size(); i++) {
                            Option option = options.get("o" + i);
                            String optionPhrase = option.getPhrase();
                            String extraInputType = option.getExtraInputType();
                            String extraInputHint = option.getExtraInputHint();
                            if (optionPhrase == null) {
                                Log.d(TAG, "displayCurrentQuestion: NO OPTION PHRASE");
                            } else {
                                mView.showCheckboxItem(optionPhrase, extraInputHint, extraInputType,
                                        options.size());
                            }
                        }
                        break;
                    case "text":
                        mView.showTextboxGroup();
                        for (int i = 1; i <= options.size(); i++) {
                            Option option = options.get("o" + i);
                            String extraInputHint = option.getExtraInputHint();
                            String extraInputType = option.getExtraInputType();
                            if (extraInputHint == null) {
                                Log.d(TAG, "displayCurrentQuestion: NO OPTION HINT");
                            } else {
                                mView.showTextboxItem(extraInputHint, extraInputType, null);
                            }
                        }
                        break;
                }
            } else {
                Log.d(TAG, "displayCurrentQuestion: no question options");
            }
            float progress = (float) mCurrentQuestionNum / mQuestions.size() * 100;
            mView.setProgressIndicator((int) progress);
            displayCurrentAnswer();
        } else {
            mView.setIndeterminateProgressIndicator(false);
        }
    }

    private void displayCurrentAnswer() {
        mView.hideExtraInputTextboxItem();
        Question question = mQuestions.get("q" + mCurrentQuestionNum);
        if (question != null) {
            QuestionDetails questionDetails = question.getQuestionDetails();
            Map<String, Option> options = question.getOptions();
            String type = questionDetails.getType();

            if (mAnswers.size() >= mCurrentQuestionNum) {
                Answer answer = mAnswers.get("a" + mCurrentQuestionNum);
                Map<String, Option> answerOptions = answer.getSelectedOptions();
                Option selectedOption;
                if (answerOptions != null) {
                    for (int i = 1; i <= options.size(); i++) {
                        selectedOption = answerOptions.get("o" + i);
                        if (selectedOption != null) {
                            String selectedOptionPhrase = selectedOption.getPhrase();
                            boolean selectedOptionHasExtraInput = selectedOption.getHasExtraInput();
                            String selectedOptionExtraInput = selectedOption.getExtraInput();
                            String selectedOptionExtraInputHint = selectedOption.getExtraInputHint();
                            String selectedOptionExtraInputType = selectedOption.getExtraInputType();
                            switch (type) {
                                case "radio":
                                    mView.selectRadioButtonItem(
                                            selectedOptionPhrase,
                                            selectedOptionHasExtraInput,
                                            selectedOptionExtraInputHint,
                                            selectedOptionExtraInputType,
                                            selectedOptionExtraInput,
                                            options.size()
                                    );
                                    break;
                                case "checkbox":
                                    boolean selectedOptionChecked = selectedOption.isChecked();
                                    mView.selectCheckboxItem(
                                            selectedOptionPhrase,
                                            selectedOptionChecked,
                                            selectedOptionHasExtraInput,
                                            selectedOptionExtraInputHint,
                                            selectedOptionExtraInputType,
                                            selectedOptionExtraInput,
                                            options.size()
                                    );
                                    break;
                                case "text":
                                    mView.showTextboxItem(selectedOptionExtraInputHint, "text",
                                            selectedOptionExtraInput);
                                    break;
                            }
                        }
                    }
                }
            }
            updateBackwardNav();
        } else {
            mView.setIndeterminateProgressIndicator(false);
        }
    }

    private void updateBackwardNav() {
        boolean atFirstQuestion = (mCurrentQuestionNum == 1);
        boolean previousQuestionProtected;

        previousQuestionProtected = mCurrentQuestionNum - 1 == mNumProtectedQuestions;

        if (!atFirstQuestion && !previousQuestionProtected) {
            mView.setBackButtonEnabled(true);
        } else {
            mView.setBackButtonEnabled(false);
        }

        updateForwardNav();
    }

    private void updateForwardNav() {
        boolean atLastQuestion = (mCurrentQuestionNum == mQuestions.size());
        mAnswerComplete = answerOk(mAnswers.get("a" + mCurrentQuestionNum));


        Log.d(TAG, "updateForwardNav: answers size: " + mAnswers.size());
        Answer answer = mAnswers.get("a" + mCurrentQuestionNum);
        if (answer == null) {
            Log.d(TAG, "updateForwardNav: answer null");
        } else {
            Log.d(TAG, "updateForwardNav: answer not null");
            Log.d(TAG, "updateForwardNav: answer ok: " + answerOk(answer));
        }




        if (mAnswerComplete && !atLastQuestion) {
            mView.setNextButtonEnabled(true);
        } else {
            mView.setNextButtonEnabled(false);
        }

        if (mAnswerComplete && atLastQuestion) {
            mView.setSubmitButtonEnabled(true);
        }
        mView.setIndeterminateProgressIndicator(false);
    }


    @Override
    public void goToPreviousQuestion() {
        mView.setIndeterminateProgressIndicator(true);
        mCurrentQuestionNum--;
        displayCurrentQuestion();
    }

    @Override
    public void goToNextQuestion() {
        mView.setIndeterminateProgressIndicator(true);
        if (mCurrentQuestionNum == mQuestions.size()) {
            // We're at the last question and the survey might be complete.
            if (allAnswersOk()) {
                mTriibeRepository.markUserSurveyDone(mUserId, mSurveyId);
                getSurveyPoints();
            } else {
                mView.showSnackbar("Oops! You missed this question.", Snackbar.LENGTH_SHORT);
            }
        } else {
            mCurrentQuestionNum++;
            displayCurrentQuestion();
        }
    }

    @Override
    public void saveAnswer(final String answerPhrase, final String extraInput, final String type,
                           final boolean checked) {
        mView.setIndeterminateProgressIndicator(true);

        Question question = mQuestions.get("q" + mCurrentQuestionNum);
        QuestionDetails questionDetails = question.getQuestionDetails();
        String questionId = questionDetails.getId();
        Map<String, Option> questionOptions = question.getOptions();
        Option option;
        Answer answer;
        AnswerDetails answerDetails = new AnswerDetails(questionId, "a" +
                mCurrentQuestionNum, type);
        Map<String, Option> answerOptions = new HashMap<>();
        boolean reload = true;

        switch (type) {
            case "radio":
                for (int i = 1; i <= questionOptions.size(); i++) {
                    option = questionOptions.get("o" + i);
                    String optionPhrase = option.getPhrase();
                    boolean hasExtraInput = option.getHasExtraInput();
                    if (optionPhrase.contentEquals(answerPhrase)) {
                        if (hasExtraInput) {
                            // User must fill out extra input. Show the extra input box.
                            String extraInputHint = option.getExtraInputHint();
                            String extraInputType = option.getExtraInputType();
                            mView.showExtraInputTextboxItem(extraInputHint, extraInputType, null);
                        } else {
                            mView.hideExtraInputTextboxItem();
                        }
                        // Save a new answer
                        answerOptions.put("o" + i, option);
                        answer = new Answer(answerDetails, answerOptions);
                        mTriibeRepository.saveAnswer(
                                mSurveyId,
                                mUserId,
                                "a" + mCurrentQuestionNum,
                                answer
                        );
                    }
                }
                break;
            case "checkbox":
                for (int i = 1; i <= questionOptions.size(); i++) {
                    option = questionOptions.get("o" + i);
                    String optionPhrase = option.getPhrase();
                    boolean hasExtraInput = option.getHasExtraInput();
                    if (optionPhrase.contentEquals(answerPhrase)) {
                        if (hasExtraInput) {
                            // User must fill out extra input. Show the extra input box.
                            String extraInputHint = option.getExtraInputHint();
                            String extraInputType = option.getExtraInputType();
                            mView.showExtraInputTextboxItem(extraInputHint, extraInputType, null);
                        } else {
                            mView.hideExtraInputTextboxItem();
                        }

                        // Try to get existing answer.
                        answer = mAnswers.get("a" + mCurrentQuestionNum);
                        Map<String, Option> previousOptions = null;
                        if (answer != null) {
                            previousOptions = answer.getSelectedOptions();
                        }
                        if (previousOptions != null) {
                            // Modify existing answer
                            for (int j = 1; j <= questionOptions.size(); j++) {
                                option = questionOptions.get("o" + j);
                                optionPhrase = option.getPhrase();
                                if (optionPhrase.contentEquals(answerPhrase) && checked) {
                                    option.setChecked(true);
                                    previousOptions.put("o" + j, option);
                                } else if (optionPhrase.contentEquals(answerPhrase) && !checked) {
                                    previousOptions.remove("o" + j);
                                }
                                mTriibeRepository.saveAnswer(
                                        mSurveyId,
                                        mUserId,
                                        "a" + mCurrentQuestionNum,
                                        answer
                                );
                            }
                        } else {
                            // Create a new answer
                            if (checked) {
                                answerOptions.put("o" + i, option);
                                answer = new Answer(answerDetails, answerOptions);
                                mTriibeRepository.saveAnswer(
                                        mSurveyId,
                                        mUserId,
                                        "a" + mCurrentQuestionNum,
                                        answer
                                );
                            }
                        }
                    }
                }
                break;
            case "text":
                if (mAnswers.get("a" + mCurrentQuestionNum) != null) {
                    // Modify existing answer
                    answer = mAnswers.get("a" + mCurrentQuestionNum);
                    Map<String, Option> previousOptions = answer.getSelectedOptions();
                    for (int i = 1; i <= questionOptions.size(); i++) {
                        option = questionOptions.get("o" + i);
                        String extraInputHint = option.getExtraInputHint();
                        if (extraInputHint.contentEquals(answerPhrase)) {
                            option.setExtraInput(extraInput);
                            previousOptions.put("o" + i, option);
                            answer.setSelectedOptions(previousOptions);

                            mTriibeRepository.saveAnswer(
                                    mSurveyId,
                                    mUserId,
                                    "a" + mCurrentQuestionNum,
                                    answer
                            );
                            // Don't reload back into a textchanged listener box.
                            reload = false;
                        }
                    }
                } else {
                    // Create a new answer
                    Map<String, Option> selectedOptions = new HashMap<>();
                    for (int i = 1; i <= questionOptions.size(); i++) {
                        option = questionOptions.get("o" + i);
                        String extraInputHint = option.getExtraInputHint();
                        if (extraInputHint.contentEquals(answerPhrase)) {
                            option.setExtraInput(extraInput);
                            selectedOptions.put("o" + i, option);
                            answer = new Answer(answerDetails, selectedOptions);
                            mTriibeRepository.saveAnswer(
                                    mSurveyId,
                                    mUserId,
                                    "a" + mCurrentQuestionNum,
                                    answer
                            );
                            // Don't reload back into a textchanged listener box.
                            reload = false;
                        }
                    }
                }
                break;
            case "extraText":
                Answer extraTextAnswer = mAnswers.get("a" + mCurrentQuestionNum);
                Map<String, Option> extraTextSelectedOptions;
                if (extraTextAnswer != null) {
                    extraTextSelectedOptions = extraTextAnswer.getSelectedOptions();
                    if (extraTextSelectedOptions != null) {
                        for (int i = 1; i <= questionOptions.size(); i++) {
                            Option selectedOption = extraTextSelectedOptions.get("o" + i);
                            if (selectedOption != null) {
                                boolean hasExtraInput = selectedOption.getHasExtraInput();
                                if (hasExtraInput) {
                                    selectedOption.setExtraInput(answerPhrase);
                                    mTriibeRepository.saveAnswer(
                                            mSurveyId,
                                            mUserId,
                                            "a" + mCurrentQuestionNum,
                                            extraTextAnswer
                                    );
                                    // Don't reload back into a textchanged listener box.
                                    reload = false;
                                }
                            }
                        }
                    }
                }
                break;
        }

        if (reload) {
            // Make sure local answers are now updated with the saved answer.
            mView.setIndeterminateProgressIndicator(true);
            loadAnswers(true);
        } else {
            updateBackwardNav();
            mView.setIndeterminateProgressIndicator(false);
        }
    }

    private void getSurveyPoints() {
        EspressoIdlingResource.increment();
        mTriibeRepository.getSurvey(mSurveyId, new TriibeRepository.GetSurveyCallback() {
            @Override
            public void onSurveyLoaded(@Nullable SurveyDetails survey) {
                EspressoIdlingResource.decrement();
                if (survey != null) {
                    mSurveyPoints = survey.getPoints();
                    int surveyPoints = Integer.parseInt(mSurveyPoints);
                    updateUserPoints(surveyPoints);

                }
            }
        });
    }

    private void updateUserPoints(final int surveyPoints) {
        mTriibeRepository.getUser(mUserId, new TriibeRepository.GetUserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                if (user != null) {
                    // To ensure the enrollment survey doesn't come back and the user can get
                    // other surveys, mark them as enrolled once they've completed it.
                    if (mSurveyId.contentEquals(Constants.ENROLLMENT_SURVEY_ID)) {
                        user.setEnrolled(true);
                        mTriibeRepository.saveUser(user);
                    }

                    int currentPoints = Integer.parseInt(user.getPoints());
                    int newtotalPoints = currentPoints + surveyPoints;
                    mTriibeRepository.addUserPoints(mUserId, String.valueOf(newtotalPoints));
                    mView.setIndeterminateProgressIndicator(false);
                    mView.showPointsAccumulatorScreen(Integer.toString(surveyPoints));
                }
            }
        });
    }

    private boolean allAnswersOk() {
        boolean answersOk = true;

        for (int i = 1; i <= mAnswers.size(); i++) {
            Answer answer = mAnswers.get("a" + i);
            if (!answerOk(answer)) {
                answersOk = false;
                mCurrentQuestionNum = i;
            }
        }

        return answersOk;
    }

    private boolean answerOk(Answer answer) {
        boolean answerOk = true;

        if (missingAnswer(answer)) {
            Log.d(TAG, "answerOk: missingAnswer");
            answerOk = false;
        } else if (missingOption(answer)) {
            Log.d(TAG, "answerOk: missingOption");
            answerOk = false;
        } else if (missingExtraInput(answer)) {
            Log.d(TAG, "answerOk: missingExtraInput");
            answerOk = false;
        } else if (requiredAnswerIncorrect(answer)) {
            Log.d(TAG, "answerOk: requiredAnswerIncorrect");
            answerOk = false;
        }

        return answerOk;
    }

    private boolean missingAnswer(Answer answer) {
        boolean missingAnswer = false;

        if (answer == null) {
            missingAnswer = true;
        }

        return missingAnswer;
    }

    private boolean missingOption(Answer answer) {
        boolean missingOption = false;

        Map<String, Option> options = answer.getSelectedOptions();
        if (options == null || options.size() == 0) {
            missingOption = true;
        }

        return missingOption;
    }

    private boolean missingExtraInput(Answer answer) {
        boolean missingExtraInput = false;

        Map<String, Option> options = answer.getSelectedOptions();
        for (Option option :
                options.values()) {
            boolean hasExtraInput = option.getHasExtraInput();
            if (hasExtraInput) {
                String extraInput = option.getExtraInput();
                if (extraInput == null || extraInput.contentEquals("")) {
                    missingExtraInput = true;
                    break;
                }
            }
        }

        return missingExtraInput;
    }

    private boolean requiredAnswerIncorrect(Answer answer) {
        boolean requiredAnswerIncorrect = false;

        Question question = mQuestions.get(answer.getAnswerDetails().getQuestionId());

        QuestionDetails questionDetails = question.getQuestionDetails();
        String requiredPhrase = questionDetails.getRequiredPhrase();

        if (requiredPhrase != null && !requiredPhrase.contentEquals("")) {
            requiredAnswerIncorrect = true;

            Map<String, Option> options = answer.getSelectedOptions();
            for (Option option :
                    options.values()) {
                String phrase = option.getPhrase();
                if (phrase.contentEquals(requiredPhrase)) {
                    requiredAnswerIncorrect = false;
                    break;
                }
            }
        }

        return requiredAnswerIncorrect;
    }

    @VisibleForTesting
    public Map<String, Question> getQuestions() {
        return mQuestions;
    }

    @VisibleForTesting
    public Map<String, Answer> getAnswers() {
        return mAnswers;
    }

    @VisibleForTesting
    public int getCurrentQuestionNum() {
        return mCurrentQuestionNum;
    }
}