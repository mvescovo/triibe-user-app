package com.example.triibe.triibeuserapp.takeSurvey;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Answer;
import com.example.triibe.triibeuserapp.data.Question;
import com.example.triibe.triibeuserapp.data.Survey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TakeSurveyActivity extends AppCompatActivity {

    private static final String TAG = "TakeSurveyActivity";

    private DatabaseReference mDatabase;
    private Map<String, Question> mQuestions;
    private int mCurrentQuestionNum;

    @BindView(R.id.question_logo)
    ImageView mImage;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.intro)
    TextView mIntro;

    @BindView(R.id.question)
    TextView mQuestion;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;

    @BindView(R.id.text_input_layout)
    TextInputLayout mTextInputLayout;

    @BindView(R.id.text_input_edit_text)
    TextInputEditText mTextInputEditText;

    @BindView(R.id.checkbox_group)
    LinearLayout mCheckboxGroup;

    @BindView(R.id.edit_text_group)
    LinearLayout mEditTextGroup;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_survey);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getQuestions();
    }

    private void getQuestions() {
        ValueEventListener surveyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Survey object and use the values to update the UI
                Survey survey = dataSnapshot.getValue(Survey.class);
                if (survey != null) {
                    mQuestions = survey.getQuestions();
                    displayCurrentQuestion();
                } else {
                    Log.i(TAG, "onDataChange: No questions in survey");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Survey failed, log a message
                Log.w(TAG, "loadSurvey:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child("survey1").addValueEventListener(surveyListener);
    }

    private void displayCurrentQuestion() {
        mRadioGroup.setVisibility(View.GONE);
        mCheckboxGroup.setVisibility(View.GONE);
        mTextInputLayout.setVisibility(View.GONE);
        mEditTextGroup.setVisibility(View.GONE);

        if (mCurrentQuestionNum == 0 && mQuestions.size() > 0) {
            mCurrentQuestionNum++;
        }

        float progress = (float) mCurrentQuestionNum / mQuestions.size() * 100;
        mProgressBar.setProgress((int) progress);

        Question question = mQuestions.get("question" + mCurrentQuestionNum);

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
        } else {
            mIntro.setVisibility(View.GONE);
        }
        if (!question.getQuestion().contentEquals("")) {
            mQuestion.setVisibility(View.VISIBLE);
            mQuestion.setText(question.getQuestion());
        } else {
            mQuestion.setVisibility(View.GONE);
        }

        switch (question.getAnswer().getFormatType()) {
            case "radio":
                mRadioGroup.removeAllViews();
                mRadioGroup.setVisibility(View.VISIBLE);
                for (Map.Entry<String, Object> entry : question.getAnswer().getAnswer().entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof String) {
                        RadioButton radioButton = new RadioButton(this);
                        radioButton.setText(key);
                        radioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onRadioButtonClicked(view);
                            }
                        });
                        mRadioGroup.addView(radioButton);
                        if (((String) value).contentEquals("true")) {
                            radioButton.toggle();
                        }
                    } else if (value instanceof Map) {
                        Map<String, Object> convertedValue = (Map<String, Object>) value;
                        for (Map.Entry<String, Object> insideEntry : convertedValue.entrySet()) {
                            String insideKey = insideEntry.getKey();
                            Object insideValue = insideEntry.getValue();

                            if (((String) insideValue).contentEquals("false") || ((String) insideValue).contentEquals("true")) {
                                RadioButton radioButton = new RadioButton(this);
                                radioButton.setText(insideKey);
                                radioButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        onRadioButtonClicked(view);
                                    }
                                });
                                mRadioGroup.addView(radioButton);
                                if (((String) insideValue).contentEquals("true")) {
                                    radioButton.toggle();
                                }
                            } else {
                                mTextInputLayout.setVisibility(View.VISIBLE);
                                mTextInputEditText.setHint(insideKey);
                                mTextInputEditText.setText((String) insideValue);
                            }
                        }
                    }
                }
                break;
            case "checkbox":
                mCheckboxGroup.removeAllViews();
                mCheckboxGroup.setVisibility(View.VISIBLE);
                for (Map.Entry<String, Object> entry : question.getAnswer().getAnswer().entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof String) {
                        CheckBox checkbox = new CheckBox(this);
                        checkbox.setText(key);
                        checkbox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onCheckboxClicked(view);
                            }
                        });
                        mCheckboxGroup.addView(checkbox);
                        if (((String) value).contentEquals("true")) {
                            checkbox.setChecked(true);
                        }
                    } else if (value instanceof Map) {
                        Map<String, Object> convertedValue = (Map<String, Object>) value;
                        for (Map.Entry<String, Object> insideEntry : convertedValue.entrySet()) {
                            String insideKey = insideEntry.getKey();
                            Object insideValue = insideEntry.getValue();

                            if (((String) insideValue).contentEquals("false") || ((String) insideValue).contentEquals("true")) {
                                CheckBox checkbox = new CheckBox(this);
                                checkbox.setText(key);
                                checkbox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        onCheckboxClicked(view);
                                    }
                                });
                                mCheckboxGroup.addView(checkbox);
                                if (((String) insideValue).contentEquals("true")) {
                                    checkbox.setChecked(true);
                                }
                            } else {
                                mTextInputLayout.setVisibility(View.VISIBLE);
                                mTextInputEditText.setHint(insideKey);
                                mTextInputEditText.setText((String) insideValue);
                            }
                        }
                    }
                }
                break;
            case "text":
                mEditTextGroup.removeAllViews();
                mEditTextGroup.setVisibility(View.VISIBLE);
                for (Map.Entry<String, Object> entry : question.getAnswer().getAnswer().entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    final TextInputEditText textInputEditText = new TextInputEditText(this);
                    textInputEditText.setText(value.toString());
                    textInputEditText.setHint(key);
                    textInputEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            onTextInputEditTextClicked(textInputEditText);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    mEditTextGroup.addView(textInputEditText);
                }
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        Map<String, Object> answer = new HashMap<>();

        // Check which radio button was clicked
        Question question = mQuestions.get("question" + mCurrentQuestionNum);
        for (Map.Entry<String, Object> entry : question.getAnswer().getAnswer().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                if (((RadioButton) view).getText().equals(key)) {
                    if (checked) {
                        answer.put(key, "true");
                    } else {
                        answer.put(key, "false");
                    }
                } else {
                    answer.put(key, "false");
                }
            } else if (value instanceof Map) {
                Map<String, Object> radioText = new HashMap<>();

                if (key.contentEquals("radiotext")) {
                    Map<String, Object> convertedValue = (Map<String, Object>) value;
                    for (Map.Entry<String, Object> innerEntry : convertedValue.entrySet()) {
                        String innerKey = innerEntry.getKey();
                        Object innerValue = innerEntry.getValue();

                        if ((innerValue.toString().contentEquals("false")) || (innerValue.toString().contentEquals("true"))) {
                            // Got the radio button question (in this inner hashmap)
                            if (((RadioButton) view).getText().equals(innerKey)) {
                                if (checked) {
                                    radioText.put(innerKey, "true");
                                } else {
                                    radioText.put(innerKey, "false");
                                }
                            } else {
                                radioText.put(innerKey, "false");
                            }
                        } else {
                            // Got the EditText question (in this inner hashmap)
                            radioText.put(innerKey, mTextInputEditText.getText().toString());
                        }
                    }
                    answer.put("radiotext", radioText);
                }
            }
        }
        mQuestions.get("question" + mCurrentQuestionNum).getAnswer().setAnswer(answer);
        updateQuestions(mQuestions);
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        Map<String, Object> answer = new HashMap<>();

        // Check which checkbox was clicked
        Question question = mQuestions.get("question" + mCurrentQuestionNum);
        for (Map.Entry<String, Object> entry : question.getAnswer().getAnswer().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                if (((CheckBox) view).getText().equals(key)) {
                    if (checked) {
                        answer.put(key, "true");
                    } else {
                        answer.put(key, "false");
                    }
                } else {
                    answer.put(key, "false");
                }
            } else if (value instanceof Map) {
                Map<String, Object> checkboxText = new HashMap<>();

                if (key.contentEquals("checkboxtext")) {
                    Map<String, Object> convertedValue = (Map<String, Object>) value;
                    for (Map.Entry<String, Object> innerEntry : convertedValue.entrySet()) {
                        String innerKey = innerEntry.getKey();
                        Object innerValue = innerEntry.getValue();

                        if ((innerValue.toString().contentEquals("false")) || (innerValue.toString().contentEquals("true"))) {
                            // Got the checkbox button question (in this inner hashmap)
                            if (((CheckBox) view).getText().equals(innerKey)) {
                                if (checked) {
                                    checkboxText.put(innerKey, "true");
                                } else {
                                    checkboxText.put(innerKey, "false");
                                }
                            } else {
                                checkboxText.put(innerKey, "false");
                            }
                        } else {
                            // Got the EditText question (in this inner hashmap)
                            checkboxText.put(innerKey, mTextInputEditText.getText().toString());
                        }
                    }
                    answer.put("checkboxtext", checkboxText);
                }
            }
        }
        mQuestions.get("question" + mCurrentQuestionNum).getAnswer().setAnswer(answer);
        updateQuestions(mQuestions);
    }

    public void onTextInputEditTextClicked(View view) {
        Map<String, Object> answer = new HashMap<>();

        // Check which textInputEditText was clicked
        Question question = mQuestions.get("question" + mCurrentQuestionNum);
        for (Map.Entry<String, Object> entry : question.getAnswer().getAnswer().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (((TextInputEditText) view).getHint().toString().equals(key)) {
                answer.put(key, ((TextInputEditText) view).getText().toString());
            } else {
                answer.put(key, value);
            }
        }

        mQuestions.get("question" + mCurrentQuestionNum).getAnswer().setAnswer(answer);
        updateQuestions(mQuestions);
    }

    public void nextQuestion(View view) {
        if (mCurrentQuestionNum < mQuestions.size()) {
            mCurrentQuestionNum++;
            displayCurrentQuestion();
        }
    }

    public void previousQuestion(View view) {
        if (mCurrentQuestionNum > 1) {
            mCurrentQuestionNum--;
            displayCurrentQuestion();
        }
    }

    private void updateQuestions(Map<String, Question> questions) {
        Survey newSurvey = new Survey("TRIIBE user survey v1.0", questions);
        mDatabase.child("surveys").child("survey1").setValue(newSurvey);
    }

    public void addNewQuestion(View view) {
        Map<String, Object> answer = new HashMap<>();
        answer.put("Name", "");
        answer.put("Email", "");
        answer.put("Phone", "");

        // Comment out or fill out these next 4 lines for radio groups with a text area at the end
//        Map<String, Object> checkboxtext = new HashMap<>();
//        checkboxtext.put("Other, please specify:", "false");
//        checkboxtext.put("textquestion", "");
//        answer.put("checkboxtext", checkboxtext);

        // Enter the answer type. E.g. "checkbox" or "radio" or "text"
        Answer newAnswer = new Answer("text", answer);

        // Enter the question details
        Question question = new Question("https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/The_Westfield_Group_logo.svg/500px-The_Westfield_Group_logo.svg.png", "Prize", "", "Please provide us the following information to perform a draw and award the prize:", newAnswer);

        // Enter the question number below before adding a new question (increment or set the number)
        mQuestions.put("question31", question);

        updateQuestions(mQuestions);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
