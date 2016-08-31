package com.example.triibe.triibeuserapp.takeSurvey;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Answer;
import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.data.Query;
import com.example.triibe.triibeuserapp.data.Question;
import com.example.triibe.triibeuserapp.data.Survey;
import com.example.triibe.triibeuserapp.util.Globals;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TakeSurveyActivity extends AppCompatActivity implements TextWatcher {

    private static final String TAG = "TakeSurveyActivity";

    private DatabaseReference mDatabase;
    private ArrayList<Question> mQuestions;
    private ArrayList<Answer> mAnswers;
    private boolean mDownloadedAnswers;
    private int mCurrentQuestionNum;
    private String mUserId;
    private String mSurveyId;

    @BindView(R.id.load_survey_progress_bar)
    ProgressBar mLoadSurveyProgressBar;

    @BindView(R.id.question_logo)
    ImageView mImage;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.intro)
    TextView mIntro;

    @BindView(R.id.query)
    TextView mQuery;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;

    @BindView(R.id.checkbox_group)
    LinearLayout mCheckboxGroup;

    @BindView(R.id.text_input_layout)
    TextInputLayout mTextInputLayout;

    @BindView(R.id.text_input_edit_text)
    TextInputEditText mTextInputEditText;

    @BindView(R.id.edit_text_group)
    LinearLayout mEditTextGroup;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.next_button)
    Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_survey);
        ButterKnife.bind(this);


        if (getIntent().getStringExtra("surveyId") != null) {
            mSurveyId = getIntent().getStringExtra("surveyId");
        } else {
            mSurveyId = "-1";
        }

        mLoadSurveyProgressBar.setVisibility(View.VISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            mUserId = user.getUid();
        } else {
            // User is signed out
            mUserId = "invalidUser";
        }

//        mUserId = "testUser5";

        mDownloadedAnswers = false;

        if (savedInstanceState != null) {
            mCurrentQuestionNum = savedInstanceState.getInt("currentQuestionNum");
        } else {
            mCurrentQuestionNum = 1;
        }
        getQuestions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("currentQuestionNum", mCurrentQuestionNum);
    }

    /*
    * Sync the questions from firebase.
    * */
    private void getQuestions() {
        ValueEventListener questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Question>> t = new GenericTypeIndicator<List<Question>>() {};
                mQuestions = (ArrayList<Question>) dataSnapshot.getValue(t);

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

    /*
    * Sync the answers from firebase.
    * */
    private void getAnswers() {
        ValueEventListener answerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Answer>> t = new GenericTypeIndicator<List<Answer>>() {
                };
                mAnswers = (ArrayList<Answer>) dataSnapshot.getValue(t);
                mLoadSurveyProgressBar.setVisibility(View.GONE);

                if (mAnswers != null && mAnswers.size() >= mCurrentQuestionNum && !mDownloadedAnswers) {
                    displayCurrentAnswer();
                } else {
                    Log.d(TAG, "onDataChange: No answers in survey");
                }

                mDownloadedAnswers = true;

                // Prevent users from changing their responses to qualifying questions
                if (mAnswers != null && mAnswers.size() > Globals.NUM_QUALIFYING_QUESTIONS &&
                        mCurrentQuestionNum <= Globals.NUM_QUALIFYING_QUESTIONS) {
                    mCurrentQuestionNum = Globals.NUM_QUALIFYING_QUESTIONS + 1;
                    displayCurrentQuestion();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting answers failed, log a message
                Log.w(TAG, "loadAnswers:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child(mSurveyId).child("answers").child(mUserId).addValueEventListener(answerListener);
    }

    /*
    * Unmarshal the current question and display to the user.
    * */
    private void displayCurrentQuestion() {
        mRadioGroup.setVisibility(View.GONE);
        mCheckboxGroup.setVisibility(View.GONE);
        mTextInputLayout.setVisibility(View.GONE);
        mEditTextGroup.setVisibility(View.GONE);

        float progress = (float) mCurrentQuestionNum / mQuestions.size() * 100;
        mProgressBar.setProgress((int) progress);

        final Question question = mQuestions.get(mCurrentQuestionNum - 1);

        if (!question.getImageUrl().contentEquals("")) {
            mImage.setVisibility(View.VISIBLE);
            Picasso.with(this).load(question.getImageUrl()).into(mImage);
        } else {
            mImage.setVisibility(View.GONE);
        }
        if (!question.getTitle().contentEquals("")) {
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(question.getTitle());
        } else {
            mTitle.setVisibility(View.GONE);
        }
        if (!question.getIntro().contentEquals("")) {
            mIntro.setVisibility(View.VISIBLE);
            mIntro.setText(question.getIntro());

            if (question.getIntroLinkKey() != null && question.getIntroLinkUrl() != null) {
                Linkify.TransformFilter mentionFilter = new Linkify.TransformFilter() {
                    public final String transformUrl(final Matcher match, String url) {
                        return question.getIntroLinkUrl();
                    }
                };

                Pattern pattern = Pattern.compile(question.getIntroLinkKey());
                String scheme = "";
                Linkify.addLinks(mIntro, pattern, scheme, null, mentionFilter);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mIntro.setLinkTextColor(getResources().getColor(R.color.linkColor, getTheme()));
                }
            }
        } else {
            mIntro.setVisibility(View.GONE);
        }
        if (!question.getQuery().getPhrase().contentEquals("")) {
            mQuery.setVisibility(View.VISIBLE);
            mQuery.setText(question.getQuery().getPhrase());
        } else {
            mQuery.setVisibility(View.GONE);
        }

        switch (question.getQuery().getType()) {
            case "radio":
                mRadioGroup.removeAllViews();
                mRadioGroup.setVisibility(View.VISIBLE);

                for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(question.getQuery().getOptions().get(i).getPhrase());
                    radioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onRadioButtonClicked(view);
                        }
                    });
                    mRadioGroup.addView(radioButton, i);
                }
                break;
            case "checkbox":
                mCheckboxGroup.removeAllViews();
                mCheckboxGroup.setVisibility(View.VISIBLE);

                for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(question.getQuery().getOptions().get(i).getPhrase());
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onCheckboxClicked(view);
                        }
                    });
                    mCheckboxGroup.addView(checkBox, i);
                }
                break;
            case "text":
                mEditTextGroup.removeAllViews();
                mEditTextGroup.setVisibility(View.VISIBLE);

                for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
                    final TextInputEditText textInputEditText = new TextInputEditText(this);
                    textInputEditText.setHint(question.getQuery().getOptions().get(i).getPhrase());
                    mEditTextGroup.addView(textInputEditText, i);
                    if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                            question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_PHONE")) {
                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_PHONE);
                    } else if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                            question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("TYPE_TEXT_VARIATION_EMAIL_ADDRESS")) {
                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    } else {
                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
                break;
        }

        if (!mDownloadedAnswers) {
            getAnswers();
        } else {
            displayCurrentAnswer();
        }
    }

    /*
    * Unmarshal the current answer and display to the user.
    * */
    private void displayCurrentAnswer() {
        mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        Question question = mQuestions.get(mCurrentQuestionNum - 1);

        if (mAnswers.size() < mCurrentQuestionNum) {
            if (question.getQuery().getType().contentEquals("text")) {
                for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
                    final int viewNumber = i;

                    if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                            question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_PHONE")) {
                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_PHONE);
                    } else if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                            question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("TYPE_TEXT_VARIATION_EMAIL_ADDRESS")) {
                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    } else {
                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    ((TextInputEditText) mEditTextGroup.getChildAt(i)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            onTextInputEditTextChanged(viewNumber);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }
            }
        } else {
            Answer answer = mAnswers.get(mCurrentQuestionNum - 1);

            switch (answer.getType()) {
                case "radio":
                    if (answer.getSelectedOptions() != null) {
                        for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
                            if (((RadioButton) mRadioGroup.getChildAt(i)).getText().equals(answer.getSelectedOptions().get(0).getPhrase())) {
                                ((RadioButton) mRadioGroup.getChildAt(i)).toggle();
                                if (question.getQuery().getOptions().get(i).getHasExtraInput()) {
                                    mTextInputLayout.setVisibility(View.VISIBLE);
                                    mTextInputEditText.setVisibility(View.VISIBLE);
                                    mTextInputEditText.setText("");
                                    if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                                            question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_NUMBER")) {
                                        mTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    }
                                    mTextInputEditText.requestFocus();
                                    if (answer.getSelectedOptions().get(0).getExtraInput() != null) {
                                        mTextInputEditText.append(answer.getSelectedOptions().get(0).getExtraInput());
                                    }
                                    mTextInputEditText.setHint(answer.getSelectedOptions().get(0).getExtraInputHint());
                                    mTextInputEditText.addTextChangedListener(this);
                                } else {
                                    mTextInputEditText.setText("");
                                    mTextInputEditText.setVisibility(View.GONE);
                                    mTextInputEditText.removeTextChangedListener(this);
                                }
                            }
                        }
                    }
                    break;
                case "checkbox":
                    if (answer.getSelectedOptions() != null) {
                        for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
                            for (int j = 0; j < answer.getSelectedOptions().size(); j++) {
                                if (((CheckBox) mCheckboxGroup.getChildAt(i)).getText().equals(answer.getSelectedOptions().get(j).getPhrase())) {
                                    ((CheckBox) mCheckboxGroup.getChildAt(i)).toggle();
                                    if (question.getQuery().getOptions().get(i).getHasExtraInput()) {
                                        mTextInputLayout.setVisibility(View.VISIBLE);
                                        mTextInputEditText.setVisibility(View.VISIBLE);
                                        mTextInputEditText.setText("");
                                        if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                                                question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_NUMBER")) {
                                            mTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        }
                                        mTextInputEditText.requestFocus();

                                        for (int k = 0; k < mQuestions.get(mCurrentQuestionNum - 1).getQuery().getOptions().size(); k++) {
                                            if (mAnswers.get(mCurrentQuestionNum - 1).getSelectedOptions().get(k).getExtraInput() != null) {
                                                mTextInputEditText.append(answer.getSelectedOptions().get(k).getExtraInput());
                                            }
                                        }

                                        mTextInputEditText.setHint(answer.getSelectedOptions().get(i).getExtraInputHint());
                                        mTextInputEditText.addTextChangedListener(this);
                                    } else {
                                        mTextInputEditText.setText("");
                                        mTextInputEditText.setVisibility(View.GONE);
                                        mTextInputEditText.removeTextChangedListener(this);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "text":
                    for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
                        final int viewNumber = i;

                        if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                                question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_PHONE")) {
                            ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_PHONE);
                        } else if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                                question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("TYPE_TEXT_VARIATION_EMAIL_ADDRESS")) {
                            ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        } else {
                            ((TextInputEditText) mEditTextGroup.getChildAt(i)).setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        ((TextInputEditText) mEditTextGroup.getChildAt(i)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                onTextInputEditTextChanged(viewNumber);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        if (answer.getSelectedOptions() != null) {
                            for (int j = 0; j < answer.getSelectedOptions().size(); j++) {
                                if (((TextInputEditText) mEditTextGroup.getChildAt(i)).getHint().equals(answer.getSelectedOptions().get(j).getPhrase())) {
                                    ((TextInputEditText) mEditTextGroup.getChildAt(i)).setText(answer.getSelectedOptions().get(j).getExtraInput());
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    /*
    * Update the answer on firebase when the user changes the "extra text".
    * Does not include answers with all text fields, just the ones with a "other" option.
    * */
    public void onExtraTextChanged(CharSequence charSequence) {
        switch (mQuestions.get(mCurrentQuestionNum - 1).getQuery().getType()) {
            case "radio":
                mAnswers.get(mCurrentQuestionNum - 1).getSelectedOptions().get(0).setExtraInput(charSequence.toString());
                break;
            case "checkbox":
                for (int i = 0; i < mQuestions.get(mCurrentQuestionNum - 1).getQuery().getOptions().size(); i++) {
                    if (mQuestions.get(mCurrentQuestionNum - 1).getQuery().getOptions().get(i).getHasExtraInput()) {
                        mAnswers.get(mCurrentQuestionNum - 1).getSelectedOptions().get(i).setExtraInput(charSequence.toString());
                    }
                }
                break;
        }

        // Add the answer to firebase
        mDatabase.child("surveys").child(mSurveyId).child("answers").child(mUserId).setValue(mAnswers);
    }

    /*
    * Update the answer on firebase when the user selects a radio option.
    * */
    public void onRadioButtonClicked(View view) {
//        Toast.makeText(TakeSurveyActivity.this, "Radio button clicked.", Toast.LENGTH_SHORT).show();
        mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        Question question = mQuestions.get(mCurrentQuestionNum - 1);

        for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
//            Toast.makeText(TakeSurveyActivity.this, "Inside for loop", Toast.LENGTH_SHORT).show();
            if (question.getQuery().getOptions().get(i).getPhrase().contentEquals(((RadioButton) view).getText())) {
//                Toast.makeText(TakeSurveyActivity.this, "Inside first if", Toast.LENGTH_SHORT).show();
//                Toast.makeText(TakeSurveyActivity.this, "Has extra input: " + question.getQuery().getOptions().get(i).HasExtraInput() + ", extra input hint: " +
//                        question.getQuery().getOptions().get(i).getExtraInputHint(), Toast.LENGTH_SHORT).show();
                if (question.getQuery().getOptions().get(i).getHasExtraInput()) {
//                    Toast.makeText(TakeSurveyActivity.this, "Has extra input but can't show it for some reason.", Toast.LENGTH_SHORT).show();
                    mTextInputLayout.setVisibility(View.VISIBLE);
                    mTextInputEditText.setVisibility(View.VISIBLE);
                    mTextInputEditText.addTextChangedListener(this);
                    mTextInputEditText.setHint(question.getQuery().getOptions().get(i).getExtraInputHint());
                    if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                            question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_NUMBER")) {
                        mTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                    mTextInputEditText.requestFocus();

                } else {
                    mTextInputEditText.setText("");
                    mTextInputEditText.setHint("");
                    mTextInputEditText.setVisibility(View.GONE);
                    mTextInputEditText.removeTextChangedListener(this);
                }
            }
        }

        ArrayList<Option> selectedOptions = new ArrayList<>();

        for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
            Option option = question.getQuery().getOptions().get(i);
            if (option.getPhrase().contentEquals(((RadioButton) view).getText())) {
                selectedOptions.add(option);
            }
        }

        Answer answer = new Answer(Integer.toString(mCurrentQuestionNum), "radio", selectedOptions);
        if (mAnswers == null) {
            mAnswers = new ArrayList<>();
        }
        if (mAnswers.size() > mCurrentQuestionNum - 1) {
            mAnswers.remove(mCurrentQuestionNum - 1);
        }
        if (mAnswers.size() > 0) {
            mAnswers.add(mCurrentQuestionNum - 1, answer);
        } else {
            mAnswers.add(answer);
        }

        // Add the answer to firebase
        mDatabase.child("surveys").child(mSurveyId).child("answers").child(mUserId).setValue(mAnswers);
    }

    /*
    * Update the answer on firebase when the user selects a checkbox option.
    * */
    public void onCheckboxClicked(View view) {
        mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        Question question = mQuestions.get(mCurrentQuestionNum - 1);
        Option dummyOption = new Option("", false);

        for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
            if (question.getQuery().getOptions().get(i).getPhrase().contentEquals(((CheckBox) view).getText())) {
                if (question.getQuery().getOptions().get(i).getHasExtraInput() && checked) {
                    mTextInputLayout.setVisibility(View.VISIBLE);
                    mTextInputEditText.setVisibility(View.VISIBLE);
                    mTextInputEditText.addTextChangedListener(this);
                    mTextInputEditText.setHint(question.getQuery().getOptions().get(i).getExtraInputHint());
                    if (question.getQuery().getOptions().get(i).getExtraInputType() != null &&
                            question.getQuery().getOptions().get(i).getExtraInputType().contentEquals("InputType.TYPE_CLASS_NUMBER")) {
                        mTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                    mTextInputEditText.requestFocus();

                } else if (question.getQuery().getOptions().get(i).getHasExtraInput() && !checked) {
                    mTextInputEditText.setText("");
                    mTextInputEditText.setHint("");
                    mTextInputEditText.setVisibility(View.GONE);
                    mTextInputEditText.removeTextChangedListener(this);
                }
            }
        }

        ArrayList<Option> selectedOptions;
        if (mAnswers.size() < mCurrentQuestionNum) {
            selectedOptions = new ArrayList<>();
        } else {
            selectedOptions = mAnswers.get(mCurrentQuestionNum - 1).getSelectedOptions();
            if (selectedOptions == null) {
                selectedOptions = new ArrayList<>();
            }
        }

        if (selectedOptions.size() < question.getQuery().getOptions().size()) {
            int optionsSize = question.getQuery().getOptions().size() - selectedOptions.size();
            for (int i = 0; i < optionsSize; i++) {
                selectedOptions.add(i, dummyOption);
            }
        }

        for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
            Option option = question.getQuery().getOptions().get(i);
            if (option.getPhrase().contentEquals(((CheckBox) view).getText())) {
                if (checked) {
                    selectedOptions.remove(i);
                    selectedOptions.add(i, option);
                } else {
                    selectedOptions.remove(i);
                    selectedOptions.add(i, dummyOption);
                }
            }
        }

        Answer answer = new Answer(Integer.toString(mCurrentQuestionNum), "checkbox", selectedOptions);
        if (mAnswers == null) {
            mAnswers = new ArrayList<>();
        }
        if (mAnswers.size() > mCurrentQuestionNum - 1) {
            mAnswers.remove(mCurrentQuestionNum - 1);
        }
        if (mAnswers.size() > 0) {
            mAnswers.add(mCurrentQuestionNum - 1, answer);
        } else {
            mAnswers.add(answer);
        }

        // Add the answer to firebase
        mDatabase.child("surveys").child(mSurveyId).child("answers").child(mUserId).setValue(mAnswers);
    }

    /*
    * Update the answer on firebase when the user changes input text fields.
    * */
    public void onTextInputEditTextChanged(int viewNumber) {
        Question question = mQuestions.get(mCurrentQuestionNum - 1);
        Option dummyOption = new Option("", true);

        ArrayList<Option> selectedOptions;
        if (mAnswers.size() < mCurrentQuestionNum) {
            selectedOptions = new ArrayList<>();
        } else {
            selectedOptions = mAnswers.get(mCurrentQuestionNum - 1).getSelectedOptions();
            if (selectedOptions == null) {
                selectedOptions = new ArrayList<>();
            }
        }

        if (selectedOptions.size() < question.getQuery().getOptions().size()) {
            int optionsSize = question.getQuery().getOptions().size() - selectedOptions.size();
            for (int i = 0; i < optionsSize; i++) {
                selectedOptions.add(i, dummyOption);
            }
        }

        for (int i = 0; i < question.getQuery().getOptions().size(); i++) {
            Option option = question.getQuery().getOptions().get(i);
            if (option.getPhrase().contentEquals(((TextInputEditText) mEditTextGroup.getChildAt(viewNumber)).getHint())) {
                option.setExtraInput(((TextInputEditText) mEditTextGroup.getChildAt(viewNumber)).getText().toString());
                selectedOptions.remove(i);
                selectedOptions.add(i, option);
            }
        }

        Answer answer = new Answer(Integer.toString(mCurrentQuestionNum), "text", selectedOptions);
        if (mAnswers == null) {
            mAnswers = new ArrayList<>();
        }
        if (mAnswers.size() > mCurrentQuestionNum - 1) {
            mAnswers.remove(mCurrentQuestionNum - 1);
        }
        if (mAnswers.size() > 0) {
            mAnswers.add(mCurrentQuestionNum - 1, answer);
        } else {
            mAnswers.add(answer);
        }

        // Add the answer to firebase
        mDatabase.child("surveys").child(mSurveyId).child("answers").child(mUserId).setValue(mAnswers);
    }

    /*
    * Go to the next question.
    * */
    public void nextQuestion(View view) {
        Question question = mQuestions.get(mCurrentQuestionNum - 1);

        if (mAnswers != null && mAnswers.size() >= mCurrentQuestionNum) {
            boolean answerOk = false;
            if (question.getQuery().getRequiredPhrase() != null) {
                String requiredPhrase = mQuestions.get(mCurrentQuestionNum - 1).getQuery().getRequiredPhrase();
                ArrayList<Option> options = mAnswers.get(mCurrentQuestionNum - 1).getSelectedOptions();
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i).getPhrase().contentEquals(requiredPhrase)) {
                        answerOk = true;
                    }
                }
            } else {
                ArrayList<Option> options = mAnswers.get(mCurrentQuestionNum - 1).getSelectedOptions();
                if (question.getQuery().getType().contentEquals("text")) {
                    answerOk = true;
                    for (int i = 0; i < options.size(); i++) {
                        if (options.get(i).getExtraInput() == null || options.get(i).getExtraInput().contentEquals("")) {
                            answerOk = false;
                        }
                    }
                } else {
                    for (int i = 0; i < options.size(); i++) {
                        if (!options.get(i).getPhrase().contentEquals("")) {
                            answerOk = !options.get(i).getHasExtraInput() || options.get(i).getExtraInput() != null && !options.get(i).getExtraInput().contentEquals("");
                        }
                    }
                }
            }

            if (answerOk) {
                if (mNextButton.getText().toString().contentEquals(getString(R.string.finish_survey))) {
                    finish();
                } else {
                    mCurrentQuestionNum++;
                    mTextInputEditText.removeTextChangedListener(this);
                    displayCurrentQuestion();
                    if (mCurrentQuestionNum == mQuestions.size()) {
                        mNextButton.setText(R.string.finish_survey);
                    }
                }
            } else {
                if (question.getQuery().getIncorrectAnswerPhrase() != null) {
                    Snackbar snackbar = Snackbar.make(view, question.getQuery().getIncorrectAnswerPhrase(), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(view, R.string.question_incomplete, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

        } else if (question.getQuery().getRequiredPhrase() != null) {
            Snackbar snackbar = Snackbar.make(view, question.getQuery().getIncorrectAnswerPhrase(), Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(view, R.string.question_incomplete, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

    }

    /*
    * Go to the previous question.
    * */
    public void previousQuestion(View view) {
        // Prevent users from changing their responses to qualifying questions
        if (mCurrentQuestionNum != Globals.NUM_QUALIFYING_QUESTIONS + 1) {
            mNextButton.setText(R.string.next_question);
            if (mCurrentQuestionNum > 1) {
                mCurrentQuestionNum--;
                mTextInputEditText.removeTextChangedListener(this);
                displayCurrentQuestion();
            } else {
                Snackbar snackbar = Snackbar.make(view, R.string.at_first_question, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } else {
            Snackbar snackbar = Snackbar.make(view, R.string.at_first_question, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    /*
    * Helper method to add questions to firebase from the app. Only for manual use.
    * Set visible/gone the "Add question" button in "activity_take_survey.xml" to enable.
    * Fill out the appropriate fields before attempting to add a question.
    * */
    public void addNewQuestion(View view) {
        // Enter the options
        ArrayList<Option> options = new ArrayList<>();
        Option option1 = new Option("$0 - $18,200", false);
        Option option2 = new Option("$18,201 - $37,000", false);
        Option option3 = new Option("$37,001 - $80,000", false);
        Option option4 = new Option("$80,001 - $180,000", false);
        Option option5 = new Option("$180,001 and over", false);
        options.add(option1);
        options.add(option2);
        options.add(option3);
        options.add(option4);
        options.add(option5);

        // Enter the answer type. E.g. "checkbox" or "radio" or "text"
        // Enter two extra string paramaters to indicate a required answer. The second one is for the "incorectAnswerPhrase"
        Query query = new Query("radio", "What is your average annual FAMILY income (in Australian dollars, before tax)?", options);

        // Enter the question details
        Question question = new Question("5", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/The_Westfield_Group_logo.svg/500px-The_Westfield_Group_logo.svg.png", "About You", "", query);

        // Add the question
        mDatabase.child("surveys").child(mSurveyId).child("questions").child("7").setValue(question);
//        mQuestions.add(question);
//        updateSurvey(mQuestions);
    }

    /*
    * Add a whole new survey if all the questions were populated.
    * */
    private void updateSurvey(ArrayList<Question> questions) {
        Survey newSurvey = new Survey("TRIIBE user survey", "2.1", questions);
        mDatabase.child("surveys").child(mSurveyId).setValue(newSurvey);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        onExtraTextChanged(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
