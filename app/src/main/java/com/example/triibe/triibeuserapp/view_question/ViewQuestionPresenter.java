package com.example.triibe.triibeuserapp.view_question;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Answer;
import com.example.triibe.triibeuserapp.data.AnswerDetails;
import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.data.Question;
import com.example.triibe.triibeuserapp.data.QuestionDetails;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.data.User;
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
    private String mQuestionId;
    private int mNumProtectedQuestions;
    private int mCurrentQuestionNum;
    @VisibleForTesting
    public Map<String, Question> mQuestions;
    @VisibleForTesting
    public Map<String, Answer> mAnswers;
    private String mSurveyPoints;


    public ViewQuestionPresenter(TriibeRepository triibeRepository, ViewQuestionContract.View view,
                                 String surveyId, String userId, String questionId, int numProtectedQuestions) {
        mTriibeRepository = triibeRepository;
        mView = view;
        mSurveyId = surveyId;
        mUserId = userId;
        mQuestionId = questionId;
        mNumProtectedQuestions = numProtectedQuestions;
        mCurrentQuestionNum = 1;
        mQuestions = new HashMap<>();
        mAnswers = new HashMap<>();
    }

    @Override
    public void loadCurrentQuestion() {
        Log.d(TAG, "loadCurrentQuestion: survey: " + mSurveyId);
        mView.setIndeterminateProgressIndicator(true);

        // Remove notification for the survey.
        mView.removeNotification(mSurveyId);

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

                        // If the question ID was specified (such as from Espresso), go to the
                        // requested question. If "-1" is set (invalid question) then just go to
                        // the current question.
                        if (!mQuestionId.contentEquals("-1")) {
                            mCurrentQuestionNum = Integer.valueOf(mQuestionId.substring(1));
                        } else {
                            // Move to the question the user is up to.
                            if (mAnswers.size() < mQuestions.size()) {
                                // If they haven't completed all questions move to the next question.
                                mCurrentQuestionNum = mAnswers.size() + 1;
                            } else {
                                // If they have completed all questions, move to the last question.
                                mCurrentQuestionNum = mAnswers.size();
                            }
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
                                mView.showRadioButtonItem(optionPhrase, extraInputHint, extraInputType);
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
                                mView.showCheckboxItem(optionPhrase, extraInputHint, extraInputType, options.size());
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
        Question question = mQuestions.get("q" + mCurrentQuestionNum);
        if (question != null) {
            QuestionDetails questionDetails = question.getQuestionDetails();
            Map<String, Option> options = question.getOptions();
            String type = questionDetails.getType();

            if (mAnswers.size() >= mCurrentQuestionNum) {
                Answer answer = mAnswers.get("a" + mCurrentQuestionNum);
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
                            mView.selectRadioButtonItem(
                                    selectedOptionPhrase,
                                    selectedOptionHasExtraInput,
                                    selectedOptionExtraInputHint,
                                    selectedOptionExtraInputType,
                                    selectedOptionExtraInput,
                                    options.size()
                            );
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
                            }
                        }
                        break;
                    case "text":
                        if (selectedOptions != null) {
                            for (int i = 1; i <= options.size(); i++) {
                                Option selectedOption = selectedOptions.get("o" + i);
                                if (selectedOption != null) {
                                    String selectedOptionHint = selectedOption.getExtraInputHint();
                                    String selectedOptionAnswerPhrase = selectedOption.getExtraInput();
                                    mView.showTextboxItem(selectedOptionHint, "text", selectedOptionAnswerPhrase);
                                }
                            }
                        }
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
    public void saveAnswer(final String phrase, final String extraInput, final String type, final boolean checked) {
        Question question = mQuestions.get("q" + mCurrentQuestionNum);
        QuestionDetails questionDetails = question.getQuestionDetails();
        String questionId = questionDetails.getId();
        Map<String, Option> options = question.getOptions();
        Answer answer;
        Option option;

        switch (type) {
            case "radio":
            case "checkbox":
                for (int i = 1; i <= options.size(); i++) {
                    option = options.get("o" + i);
                    String optionPhrase = option.getPhrase();
                    boolean hasExtraInput = option.getHasExtraInput();
                    if (optionPhrase.contentEquals(phrase)) {
                        if (hasExtraInput && checked) {
                            String extraInputHint = option.getExtraInputHint();
                            String extraInputType = option.getExtraInputType();
                            mView.showExtraInputTextboxItem(extraInputHint, extraInputType, null);
                        } else {
                            mView.hideExtraInputTextboxItem();
                        }
                    }
                }

                if (mAnswers == null) {
                    mAnswers = new HashMap<>();
                }

                if (mAnswers.get("a" + mCurrentQuestionNum) != null) {
                    // Modify existing answer
                    answer = mAnswers.get("a" + mCurrentQuestionNum);
                    Map<String, Option> previousOptions = answer.getSelectedOptions();
                    for (int i = 1; i <= options.size(); i++) {
                        option = options.get("o" + i);
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
                        option = options.get("o" + i);
                        String optionPhrase = option.getPhrase();
                        if (optionPhrase.contentEquals(phrase) && checked) {
                            option.setChecked(true);
                            selectedOptions.put("o" + i, option);
                        }
                    }

                    AnswerDetails answerDetails = new AnswerDetails(questionId, "a" + mCurrentQuestionNum, type);
                    if (selectedOptions.size() == 0) {
                        // Don't save an empty answerDetails object. This will make firebase delete the
                        // current answer options and can lead to a crash when loading the answer.
                        // Using an empty Answer object resolves this as it will delete the entire answer and
                        // not just the options.
                        answer = new Answer();
                    } else {
                        answer = new Answer(answerDetails, selectedOptions);
                    }
                }
                mTriibeRepository.saveAnswer(mSurveyId, mUserId, "a" + mCurrentQuestionNum, answer);
                break;
            case "text":
                Log.d(TAG, "saveAnswer: got text answer");
                if (mAnswers == null) {
                    mAnswers = new HashMap<>();
                }

                if (mAnswers.get("a" + mCurrentQuestionNum) != null) {
                    // Modify existing answer
                    answer = mAnswers.get("a" + mCurrentQuestionNum);
                    Map<String, Option> previousOptions = answer.getSelectedOptions();
                    for (int i = 1; i <= options.size(); i++) {
                        option = options.get("o" + i);
                        String extraInputHint = option.getExtraInputHint();
                        if (extraInputHint.contentEquals(phrase)) {
                            option.setExtraInput(extraInput);
                            previousOptions.put("o" + i, option);
                        }
                    }
                    if (previousOptions.size() == 0) {
                        answer = new Answer();
                    }
                } else {
                    // Create a new answer
                    Map<String, Option> selectedOptions = new HashMap<>();
                    for (int i = 1; i <= options.size(); i++) {
                        option = options.get("o" + i);
                        String extraInputHint = option.getExtraInputHint();
                        if (extraInputHint.contentEquals(phrase)) {
                            option.setExtraInput(extraInput);
                            selectedOptions.put("o" + i, option);
                        }
                    }

                    AnswerDetails answerDetails = new AnswerDetails(questionId, "a" + mCurrentQuestionNum, type);
                    if (selectedOptions.size() == 0) {
                        // Don't save an empty answerDetails object. This will make firebase delete the
                        // current answer options and can lead to a crash when loading the answer.
                        // Using an empty Answer object resolves this as it will delete the entire answer and
                        // not just the options.
                        answer = new Answer();
                    } else {
                        answer = new Answer(answerDetails, selectedOptions);
                    }
                }
                mTriibeRepository.saveAnswer(mSurveyId, mUserId, "a" + mCurrentQuestionNum, answer);
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

        // Make sure local answer are now updated with the saved answer.
        mView.setIndeterminateProgressIndicator(true);
        mTriibeRepository.refreshAnswers();
        EspressoIdlingResource.increment();
        mTriibeRepository.getAnswers(mSurveyId, mUserId, new TriibeRepository.GetAnswersCallback() {
            @Override
            public void onAnswersLoaded(@Nullable Map<String, Answer> answers) {
                EspressoIdlingResource.decrement();
                mAnswers = answers;
                mView.setIndeterminateProgressIndicator(false);
            }
        });
    }

    @Override
    public void goToNextQuestion() {
        mView.setIndeterminateProgressIndicator(true);
        loadAnswers(new LoadAnswersCallback() {
            @Override
            public void onAnswersLoaded(Map<String, Answer> answers) {
                mAnswers = answers;
                checkAnswerToGoNext();
            }
        }, false);
    }

    @VisibleForTesting
    public void checkAnswerToGoNext() {
        if (mQuestions != null && mQuestions.size() >= mCurrentQuestionNum) {
            Question question = mQuestions.get("q" + mCurrentQuestionNum);
            QuestionDetails questionDetails = question.getQuestionDetails();
            Map<String, Option> options = question.getOptions();
            String requiredPhrase = questionDetails.getRequiredPhrase();
            String incorrectAnswerPhrase = questionDetails.getIncorrectAnswerPhrase();
            final String type = questionDetails.getType();

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
                        // We're at the last question and the survey is complete.

                        mTriibeRepository.markUserSurveyDone(mUserId, mSurveyId);

                        // Update user points
                        mTriibeRepository.getSurvey(mSurveyId, new TriibeRepository.GetSurveyCallback() {
                            @Override
                            public void onSurveyLoaded(@Nullable SurveyDetails survey) {
                                if (survey != null) {
                                    mSurveyPoints = survey.getPoints();
                                    final int surveyPoints = Integer.parseInt(mSurveyPoints);
                                    mTriibeRepository.getUser(mUserId, new TriibeRepository.GetUserCallback() {
                                        @Override
                                        public void onUserLoaded(@Nullable User user) {
                                            if (user != null) {
                                                // To ensure the enrollment survey doesn't come back and the user can get
                                                // other surveys, mark them as enrolled once they've completed it.
                                                if (mSurveyId.contentEquals("enrollmentSurvey")) {
                                                    mTriibeRepository.getUser(mUserId, new TriibeRepository.GetUserCallback() {
                                                        @Override
                                                        public void onUserLoaded(@Nullable User user) {
                                                            if (user != null) {
                                                                user.setEnrolled(true);
                                                            }
                                                        }
                                                    });
                                                }

                                                int currentPoints = Integer.parseInt(user.getPoints());
                                                int newtotalPoints = currentPoints += surveyPoints;
                                                mTriibeRepository.addUserPoints(mUserId, String.valueOf(newtotalPoints));
                                                mView.setIndeterminateProgressIndicator(false);
                                                mView.showViewSurveys(Activity.RESULT_OK, surveyPoints, newtotalPoints);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        mCurrentQuestionNum++;
                        mView.hideOptions();
                        mView.showBackButton();
//                    mTextInputEditText.removeTextChangedListener(this);
                        mView.setIndeterminateProgressIndicator(false);
                        if (mCurrentQuestionNum == mQuestions.size()) {
                            mView.showSubmitButton();
                        }
                        displayCurrentQuestion();
                    }
                } else {
                    if (incorrectAnswerPhrase != null) {
                        mView.setIndeterminateProgressIndicator(false);
                        mView.showSnackbar(incorrectAnswerPhrase, Snackbar.LENGTH_SHORT);
                    } else {
                        mView.setIndeterminateProgressIndicator(false);
                        mView.showSnackbar(((Context) mView).getString(R.string.question_incomplete), Snackbar.LENGTH_SHORT);
                    }
                }

            } else if (requiredPhrase != null) {
                mView.setIndeterminateProgressIndicator(false);
                mView.showSnackbar(incorrectAnswerPhrase, Snackbar.LENGTH_SHORT);
            } else {
                mView.setIndeterminateProgressIndicator(false);
                mView.showSnackbar(((Context) mView).getString(R.string.question_incomplete), Snackbar.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void goToPreviousQuestion() {
        mView.setIndeterminateProgressIndicator(true);
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
            mView.hideSubmitButton();
            if (mCurrentQuestionNum == 1 ||
                    mCurrentQuestionNum == mNumProtectedQuestions + 1) {
                mView.hideBackButton();
            }
            displayCurrentQuestion();
        } else {
            mView.showSnackbar("You're at the first question.", Snackbar.LENGTH_SHORT); // TODO: 22/09/16 work out where to put this string (testing will not work when calling from strings.xml because of no mock context. Or work out how to mock it).
        }

        mView.setIndeterminateProgressIndicator(false);
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
