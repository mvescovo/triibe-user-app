package com.example.triibe.triibeuserapp.view_survey_details;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Answer;
import com.example.triibe.triibeuserapp.data.AnswerDetails;
import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.data.Question;
import com.example.triibe.triibeuserapp.data.QuestionDetails;
import com.example.triibe.triibeuserapp.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author michael.
 */
public class ViewSurveyDetailsPresenter implements ViewSurveyDetailsContract.UserActionsListener {

    private static final String TAG = "SurveyDetailsPresenter";
    ViewSurveyDetailsContract.View mView;
    private DatabaseReference mDatabase;
    private String mSurveyId;
    private Map<String, Question> mQuestions;
    private Map<String, Answer> mAnswers;
    private boolean mDownloadedAnswers;
    private String mUserId;
    private int mCurrentQuestionNum;


    public ViewSurveyDetailsPresenter(ViewSurveyDetailsContract.View view, String surveyId) {
        mView = view;
        mSurveyId = surveyId;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            mUserId = user.getUid();
        } else {
            // User is signed out
            mUserId = "invalidUser";
        }

        mCurrentQuestionNum = 1;
    }

    @Override
    public void loadQuestions(boolean forceUpdate) {
        mView.setIndeterminateProgressIndicator(true);

        ValueEventListener questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Question>> t = new GenericTypeIndicator<Map<String, Question>>() {
                };
                mQuestions = dataSnapshot.getValue(t);

                if (mQuestions != null) {
                    displayCurrentQuestion();
                } else {
                    Log.i(TAG, "onDataChange: No questions in survey");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting questions failed, log a message
                Log.w(TAG, "loadQuestions:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child(mSurveyId).child("questions").addValueEventListener(questionListener);
    }

    public void loadAnswers(boolean forceUpdate) {
        mView.setIndeterminateProgressIndicator(true);

        ValueEventListener answerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Answer>> t = new GenericTypeIndicator<Map<String, Answer>>() {};
                mAnswers = dataSnapshot.getValue(t);
//                mLoadSurveyProgressBar.setVisibility(View.GONE);

                if (mAnswers != null && mAnswers.size() >= mCurrentQuestionNum && !mDownloadedAnswers) {
                    displayCurrentAnswer();
                } else {
                    Log.d(TAG, "onDataChange: No answers in survey");
                }
                mDownloadedAnswers = true;

                // Prevent users from changing their responses to qualifying questions
//                if (mAnswers != null && mAnswers.size() > Constants.NUM_QUALIFYING_QUESTIONS &&
//                        mCurrentQuestionNum <= Constants.NUM_QUALIFYING_QUESTIONS) {
//                    mCurrentQuestionNum = Constants.NUM_QUALIFYING_QUESTIONS + 1;
//                    displayCurrentQuestion();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting answers failed, log a message
                Log.w(TAG, "loadAnswers:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child(mSurveyId).child("answers").child(mUserId).addValueEventListener(answerListener);
    }

    @Override
    public void saveQuestion(String selectedPhrase, String type, boolean checked) {
        Question question = mQuestions.get("q" + mCurrentQuestionNum);
        QuestionDetails questionDetails = question.getQuestionDetails();
        String questionId = questionDetails.getId();
        Map<String, Option> options = question.getOptions();

        switch (type) {
            case "radio":
            case "checkbox":
                for (int i = 1; i <= options.size(); i++) {
                    Map option = (Map) options.get("o" + i);
                    String optionPhrase = (String) option.get("phrase");
                    boolean hasExtraInput = (boolean) option.get("hasExtraInput");
                    if (optionPhrase.contentEquals(selectedPhrase)) {
                        if (hasExtraInput && checked) {
                            String extraInputHint = (String) option.get("extraInputHint");
                            String extraInputType = (String) option.get("extraInputType");
                            mView.showExtraInputTextboxItem(extraInputHint, extraInputType);
                        } else {
                            mView.hideExtraInputTextboxItem();
                        }
                    }
                }

                Map<String, Option> selectedOptions = new HashMap<>();
                for (int i = 1; i <= options.size(); i++) {
                    Option option = options.get("o" + i);
                    String optionPhrase = option.getPhrase();
                    if (optionPhrase.contentEquals(selectedPhrase) && checked) {
                        selectedOptions.put("o" + i, option);
                    } else if (optionPhrase.contentEquals(selectedPhrase) && !checked) {
                        selectedOptions.remove("o" + i);
                    }
                }

                AnswerDetails answerDetails = new AnswerDetails(questionId, type);
                Answer answer = new Answer(answerDetails, selectedOptions);
                if (mAnswers == null) {
                    mAnswers = new HashMap<>();
                }
                mAnswers.put("a" + mCurrentQuestionNum, answer);

                // Add the answer to firebase
                mDatabase.child("surveys").child(mSurveyId).child("answers").child(mUserId).setValue(mAnswers);
                break;
            case "text":
                break;
            case "extraText":
                Map extraTextAnswer = (Map) mAnswers.get("a" + mCurrentQuestionNum);
                Map extraTextSelectedOptions = (Map) extraTextAnswer.get("selectedOptions");
                if (extraTextSelectedOptions != null) {
                    for (int i = 1; i <= options.size(); i++) {
                        Map selectedOption = (HashMap<String, Object>) extraTextSelectedOptions.get("o" + i);
                        if (selectedOption != null) {
                            boolean hasExtraInput = (boolean) selectedOption.get("hasExtraInput");
                            if (hasExtraInput) {
                                try {
                                    selectedOption.put("extraInput", selectedPhrase);
                                } catch (ClassCastException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                // Add the answer to firebase
                mDatabase.child("surveys").child(mSurveyId).child("answers").child(mUserId).setValue(mAnswers);
        }
    }

    @Override
    public void goToNextQuestion() {
        Map question = (Map) mQuestions.get("q" + mCurrentQuestionNum);
        Map questionDetails = (Map) question.get("questionDetails");
        Map options = (Map) question.get("options");
        String requiredPhrase = (String) questionDetails.get("requiredPhrase");
        String incorrectAnswerPhrase = (String) questionDetails.get("incorrectAnswerPhrase");
        String type = (String) questionDetails.get("type");

        Map answer = (Map) mAnswers.get("a" + mCurrentQuestionNum);
        Map selectedOptions = new HashMap();
        if (answer != null) {
            selectedOptions = (Map) answer.get("selectedOptions");
        }

        if (mAnswers != null && mAnswers.size() >= mCurrentQuestionNum) {
            boolean answerOk = false;
            if (requiredPhrase != null) {
                for (int i = 1; i <= options.size(); i++) {
                    Map selectedOption = (Map) selectedOptions.get("o" + i);
                    if (selectedOption != null) {
                        String selectedOptionPhrase = (String) selectedOption.get("phrase");
                        if (selectedOptionPhrase.contentEquals(requiredPhrase)) {
                            answerOk = true;
                        }
                    }
                }
            } else {
                if (type.contentEquals("text")) {
                    answerOk = true;
                    for (int i = 1; i <= options.size(); i++) {
                        Map selectedOption = (Map) selectedOptions.get("o" + i);
                        if (selectedOption != null) {
                            String extraInput = (String) selectedOption.get("extraInput");
                            if (extraInput == null || extraInput.contentEquals("")) {
                                answerOk = false;
                            }
                        }
                    }
                } else {
                    for (int i = 1; i <= options.size(); i++) {
                        Map selectedOption = (Map) selectedOptions.get("o" + i);
                        if (selectedOption != null) {
                            String selectedOptionPhrase = (String) selectedOption.get("phrase");
                            boolean hasExtraInput = (boolean) selectedOption.get("hasExtraInput");
                            String extraInput = (String) selectedOption.get("extraInput");
                            if (!selectedOptionPhrase.contentEquals("")) {
                                answerOk = !hasExtraInput || extraInput != null && !extraInput.contentEquals("");
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "answer ok: " + answerOk);

            if (answerOk) {
                if (mCurrentQuestionNum == mQuestions.size()) {
                    // TODO: 17/09/16 remove survey from users list
                    // Also, when adding surveys, check that an answer for the user doesn't already exist.
                    mView.showViewSurveys();
                } else {
                    mCurrentQuestionNum++;
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

    @Override
    public void goToPreviousQuestion() {
        // Prevent users from changing their responses to qualifying questions
        if (mCurrentQuestionNum != Constants.NUM_QUALIFYING_QUESTIONS + 1) {
//            mNextButton.setText(R.string.next_question);
            if (mCurrentQuestionNum > 1) {
                mCurrentQuestionNum--;
//                mTextInputEditText.removeTextChangedListener(this);
                displayCurrentQuestion();
            } else {
                mView.showSnackbar(((Context) mView).getString(R.string.at_first_question), Snackbar.LENGTH_SHORT);
            }
        } else {
            mView.showSnackbar(((Context) mView).getString(R.string.at_first_question), Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void submitSurvey() {

    }

    /*
    * Unmarshal the current question and display to the user.
    * */

    private void displayCurrentQuestion() {
        float progress = (float) mCurrentQuestionNum / mQuestions.size() * 100;

        /*
        * Display question details
        * */
        mView.setProgressIndicator((int) progress);

        Map question = (Map) mQuestions.get("q" + mCurrentQuestionNum);
        Map questionDetails = (Map) question.get("questionDetails");

        String imageUrl = (String) questionDetails.get("imageUrl");
        if (imageUrl != null && !imageUrl.contentEquals("")) {
            mView.showImage(imageUrl);
        } else {
            mView.hideImage();
        }

        String title = (String) questionDetails.get("title");
        if (title != null && !title.contentEquals("")) {
            mView.showTitle(title);
        } else {
            mView.hideTitle();
        }

        String intro = (String) questionDetails.get("intro");
        String introLinkKey = (String) questionDetails.get("introLinkKey");
        String introLinkUrl = (String) questionDetails.get("introLinkUrl");
        if (intro != null && !intro.contentEquals("")) {
            mView.showIntro(intro, introLinkKey, introLinkUrl);
        } else {
            mView.hideIntro();
        }

        String phrase = (String) questionDetails.get("phrase");
        if (phrase != null && !phrase.contentEquals("")) {
            mView.showPhrase(phrase);
        } else {
            mView.hidePhrase();
        }

        /*
        * Display question options
        * */
        Map options = (Map) question.get("options");
        String type = (String) questionDetails.get("type");
        if (type == null) {
            Log.d(TAG, "displayCurrentQuestion: NO TYPE");
            return;
        }

        switch (type) {
            case "radio":
                mView.showRadioButtonGroup();
                for (int i = 1; i <= options.size(); i++) {
                    Map option = (Map) options.get("o" + i);
                    String optionPhrase = (String) option.get("phrase");
                    String extraInputHint = (String) option.get("extraInputHint");
                    String extraInputType = (String) option.get("extraInputType");
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
                    Map option = (Map) options.get("o" + i);
                    String optionPhrase = (String) option.get("phrase");
                    String extraInputHint = (String) option.get("extraInputHint");
                    String extraInputType = (String) option.get("extraInputType");
                    if (optionPhrase == null) {
                        Log.d(TAG, "displayCurrentQuestion: NO OPTIONS PHRASE");
                    } else {
                        mView.showCheckboxItem(optionPhrase, extraInputHint, extraInputType);
                    }
                }
                break;
            case "text":
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

        if (!mDownloadedAnswers) {
            loadAnswers(false);
        } else {
            displayCurrentAnswer();
        }
    }

    /*
    * Unmarshal the current answer and display to the user.
    * */
    private void displayCurrentAnswer() {
//        mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        Map question = (Map) mQuestions.get("q" + mCurrentQuestionNum);
        Map questionDetails = (Map) question.get("questionDetails");
        Map options = (Map) question.get("options");
        String type = (String) questionDetails.get("type");

        if (mAnswers.size() < mCurrentQuestionNum) {
            if (type.contentEquals("text")) {
                for (int i = 0; i < options.size(); i++) {
                    final int viewNumber = i;
                    String extraInputType = (String) options.get("extraInputType");
                    if (extraInputType != null && extraInputType.contentEquals("phone")) {
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_PHONE);
                    } else if (extraInputType != null && extraInputType.contentEquals("email")) {
//                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    } else {
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
                }
            }
        } else {
            Map answer = (Map) mAnswers.get("a" + mCurrentQuestionNum);
            Map answerDetails = (Map) answer.get("answerDetails");
            Map selectedOptions = (Map) answer.get("selectedOptions");
            String answerType = (String) answerDetails.get("type");
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
                        Map selectedOption = (Map) selectedOptions.get(onlykey);
                        String selectedOptionPhrase = (String) selectedOption.get("phrase");
                        mView.selectRadioButtonItem(selectedOptionPhrase, options.size());
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
                            Map selectedOption = (Map) selectedOptions.get("o" + i);
                            String selectedOptionPhrase = (String) selectedOption.get("phrase");
                            mView.selectCheckboxItem(selectedOptionPhrase, options.size());


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
        mView.setIndeterminateProgressIndicator(false);
    }
}
