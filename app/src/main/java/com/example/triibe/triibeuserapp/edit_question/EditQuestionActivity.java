package com.example.triibe.triibeuserapp.edit_question;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.QuestionDetails;
import com.example.triibe.triibeuserapp.edit_option.EditOptionActivity;
import com.example.triibe.triibeuserapp.edit_survey.EditSurveyActivity;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;
import com.example.triibe.triibeuserapp.util.Globals;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditQuestionActivity extends AppCompatActivity
        implements EditQuestionContract.View, TextWatcher, AdapterView.OnItemSelectedListener {

    private static final String TAG = "EditQuestionActivity";
//    public final static String EXTRA_USER_ID = "com.example.triibe.USER_ID";
    public final static String EXTRA_SURVEY_ID = "com.example.triibe.SURVEY_ID";
    private static final int REQUEST_EDIT_OPTION = 1;
    public static final int RESULT_DELETE = -2;
    EditQuestionContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;
    BottomSheetBehavior mBottomSheetBehavior;
    private List<String> mQuestionIds;
    private String mSelectedQuestionType = "radio";

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.bottom_sheet)
    View mBottomSheet;

    @BindView(R.id.edit_option_button_layout)
    LinearLayout mEditOptionButtonLayout;

    @BindView(R.id.question_id)
    AppCompatAutoCompleteTextView mQuestionId;

    @BindView(R.id.question_type)
    AppCompatSpinner mQuestionType;

    @BindView(R.id.question_image_url)
    TextInputEditText mImageUrl;

    @BindView(R.id.question_title)
    TextInputEditText mTitle;

    @BindView(R.id.question_intro)
    TextInputEditText mIntro;

    @BindView(R.id.question_phrase)
    TextInputEditText mPhrase;

    @BindView(R.id.question_intro_link_key)
    TextInputEditText mIntroLinkKey;

    @BindView(R.id.question_intro_link_url)
    TextInputEditText mIntroLinkUrl;

    @BindView(R.id.question_required_phrase)
    TextInputEditText mRequiredPhrase;

    @BindView(R.id.question_incorrect_answer_phrase)
    TextInputEditText mIncorrectAnswerPhrase;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        ButterKnife.bind(this);

        mUserActionsListener = new EditQuestionPresenter(
                Globals.getInstance().getTriibeRepository(),
                this
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getStringExtra(EXTRA_SURVEY_ID) != null) {
            mSurveyId = getIntent().getStringExtra(EXTRA_SURVEY_ID);
        } else {
            Log.d(TAG, "onCreate: NO SURVEY ID");
        }

        mEditOptionButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (validate()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    mUserActionsListener.editOption();
                }
            }
        });

        // The View with the BottomSheetBehavior
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }

        });

        // Setup question type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.question_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQuestionType.setAdapter(adapter);
        mQuestionType.setOnItemSelectedListener(this);

        mQuestionIds = new ArrayList<>();
        mQuestionId.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserActionsListener.getQuestionIds(mSurveyId, true);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void addQuestionIdsToAutoComplete(List<String> questionIds) {
        mQuestionIds = questionIds;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                mQuestionIds
        );

        mQuestionId.setAdapter(adapter);
    }

    @Override
    public void showQuestionDetails(QuestionDetails questionDetails) {
        switch (questionDetails.getType()) {
            case "radio":
                mQuestionType.setSelection(0);
                break;
            case "checkbox":
                mQuestionType.setSelection(1);
                break;
            case "text":
                mQuestionType.setSelection(2);
                break;
        }
        mImageUrl.setText(questionDetails.getImageUrl());
        mTitle.setText(questionDetails.getTitle());
        mIntro.setText(questionDetails.getIntro());
        mPhrase.setText(questionDetails.getPhrase());
        mIntroLinkKey.setText(questionDetails.getIntroLinkKey());
        mIntroLinkUrl.setText(questionDetails.getIntroLinkUrl());
        mRequiredPhrase.setText(questionDetails.getRequiredPhrase());
        mIncorrectAnswerPhrase.setText(questionDetails.getIncorrectAnswerPhrase());
    }

    @Override
    public void showEditSurvey(@NonNull Integer resultCode) {
        setResult(resultCode);
        finish();
    }

    @Override
    public void showEditOption() {
        Intent intent = new Intent(this, EditOptionActivity.class);
        intent.putExtra(EditOptionActivity.EXTRA_SURVEY_ID, mSurveyId);
        intent.putExtra(EditOptionActivity.EXTRA_QUESTION_ID, mQuestionId.getText().toString().trim());
        startActivityForResult(intent, REQUEST_EDIT_OPTION);
    }

    private boolean validate() {

        if (mQuestionId.getText().toString().trim().contentEquals("")) {
            mQuestionId.setError("Question ID must not be empty"); // TODO: 18/09/16 set in strings
            mQuestionId.requestFocus();
            return false;
        } else if (mTitle.getText().toString().trim().contentEquals("")) {
            mTitle.setError("Title must not be empty");
            mTitle.requestFocus();
            return false;
        } else if (mPhrase.getText().toString().trim().contentEquals("")) {
            mPhrase.setError("Phrase must not be empty");
            mPhrase.requestFocus();
            return false;
        }

        QuestionDetails questionDetails = new QuestionDetails(
                mSurveyId,
                mQuestionId.getText().toString().trim(),
                mSelectedQuestionType,
                mImageUrl.getText().toString().trim(), // TODO: 18/09/16 FIX THIS WITH REAL VALUES
                mTitle.getText().toString().trim(),
                mIntro.getText().toString().trim(),
                mPhrase.getText().toString().trim(),
                mIntroLinkKey.getText().toString().trim(),
                mIntroLinkUrl.getText().toString().trim(),
                mRequiredPhrase.getText().toString().trim(),
                mIncorrectAnswerPhrase.getText().toString().trim()
        );
        mUserActionsListener.saveQuestion(questionDetails);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_OPTION && resultCode == Activity.RESULT_OK) {
            Snackbar.make(mRootView, getString(R.string.successfully_saved_option),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.more_options:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            case R.id.done:
                if (validate()) {
                    showEditSurvey(Activity.RESULT_OK);
                }
                return true;
            case R.id.delete_question:
                mUserActionsListener.deleteQuestion(mQuestionId.getText().toString().trim());
                showEditSurvey(EditSurveyActivity.RESULT_DELETE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        for (int i = 0; i < mQuestionIds.size(); i++) {
            if (s.toString().contentEquals(mQuestionIds.get(i))) {
                mUserActionsListener.getQuestion(mQuestionIds.get(i));
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedQuestionType = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
