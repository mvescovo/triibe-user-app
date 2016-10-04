package com.example.triibe.triibeuserapp.edit_survey;

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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.edit_question.EditQuestionActivity;
import com.example.triibe.triibeuserapp.edit_trigger.EditTriggerActivity;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;
import com.example.triibe.triibeuserapp.util.Globals;
import com.example.triibe.triibeuserapp.view_surveys.ViewSurveysActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditSurveyActivity extends AppCompatActivity
        implements EditSurveyContract.View, TextWatcher {

    private static final String TAG = "EditSurveyActivity";
    public final static String EXTRA_SURVEY_ID = "com.example.triibe.SURVEY_ID";
    private static String STATE_SURVEY_IDS = "com.example.triibe.SURVEY_IDS";
    public static final int REQUEST_EDIT_QUESTION = 1;
    public static final int REQUEST_EDIT_TRIGGER = 2;
    public static final int RESULT_DELETE = -2;
    EditSurveyContract.UserActionsListener mUserActionsListener;
    BottomSheetBehavior mBottomSheetBehavior;
    private List<String> mSurveyIds;

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.bottom_sheet)
    View mBottomSheet;

    @BindView(R.id.edit_question_button_layout)
    LinearLayout mEditQuestionButtonLayout;

    @BindView(R.id.edit_trigger_button_layout)
    LinearLayout mEditTriggerButtonLayout;

    @BindView(R.id.survey_id)
    AppCompatAutoCompleteTextView mSurveyId;

    @BindView(R.id.survey_description)
    TextInputEditText mDescription;

    @BindView(R.id.survey_version)
    TextInputEditText mVersion;

    @BindView(R.id.survey_points)
    TextInputEditText mPoints;

    @BindView(R.id.survey_time_till_expiry)
    TextInputEditText mTimeTillExpiry;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_survey);
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mUserActionsListener = new EditSurveyPresenter(
                Globals.getInstance().getTriibeRepository(),
                this
        );

        mEditQuestionButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForQuestion()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    mUserActionsListener.editQuestion();
                }
            }
        });

        mEditTriggerButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForQuestion()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    mUserActionsListener.editTrigger();
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

        mSurveyIds = new ArrayList<>();
        mSurveyId.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserActionsListener.loadSurveyIds(true);
    }

    @Override
    public void addSurveyIdsToAutoComplete(List<String> surveyIds) {
        mSurveyIds = surveyIds;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                mSurveyIds
        );

        mSurveyId.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(STATE_SURVEY_IDS, new ArrayList<>(mSurveyIds));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSurveyIds = savedInstanceState.getStringArrayList(STATE_SURVEY_IDS);
    }

    @Override
    public void showSurveyDetails(SurveyDetails surveyDetails) {
        mDescription.setText(surveyDetails.getDescription());
        mVersion.setText(surveyDetails.getVersion());
        mPoints.setText(surveyDetails.getPoints());
        mTimeTillExpiry.setText(surveyDetails.getDurationTillExpiry());
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
    public void showSurveys(@NonNull Integer resultCode) {
        setResult(resultCode);
        finish();
    }

    @Override
    public void showEditQuestion() {
        Intent intent = new Intent(this, EditQuestionActivity.class);
        intent.putExtra(EXTRA_SURVEY_ID, mSurveyId.getText().toString().trim());
        startActivityForResult(intent, REQUEST_EDIT_QUESTION);
    }

    @Override
    public void showEditTrigger() {
        Intent intent = new Intent(this, EditTriggerActivity.class);
        intent.putExtra(EXTRA_SURVEY_ID, mSurveyId.getText().toString().trim());
        startActivityForResult(intent, REQUEST_EDIT_TRIGGER);
    }

    private boolean validateForQuestion() {
        if (mSurveyId.getText().toString().trim().contentEquals("")) {
            mSurveyId.setError("Name must not be empty"); // TODO: 18/09/16 set in strings
            mSurveyId.requestFocus();
            return false;
        } else if (mDescription.getText().toString().trim().contentEquals("")) {
            mDescription.setError("Description must not be empty");
            mDescription.requestFocus();
            return false;
        } else if (mVersion.getText().toString().trim().contentEquals("")) {
            mVersion.setError("Version must not be empty");
            mVersion.requestFocus();
            return false;
        } else if (mPoints.getText().toString().trim().contentEquals("")) {
            mPoints.setError("Points must not be empty");
            mPoints.requestFocus();
            return false;
        } else if (mTimeTillExpiry.getText().toString().trim().contentEquals("")) {
            mTimeTillExpiry.setError("Time till expiry must not be empty");
            mTimeTillExpiry.requestFocus();
            return false;
        }

        // If all ok save survey.
        mUserActionsListener.saveSurvey(mSurveyId.getText().toString().trim(),
                mDescription.getText().toString().trim(),
                mVersion.getText().toString().trim(),
                mPoints.getText().toString().trim(),
                mTimeTillExpiry.getText().toString().trim());

        return true;
    }

//    private boolean validateForTrigger() {
//        if (mSurveyId.getText().toString().trim().contentEquals("")) {
//            mSurveyId.setError("Name must not be empty"); // TODO: 18/09/16 set in strings
//            mSurveyId.requestFocus();
//            return false;
//        }
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_QUESTION && resultCode == Activity.RESULT_OK) {
            Snackbar.make(mRootView, getString(R.string.successfully_saved_question),
                    Snackbar.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_EDIT_TRIGGER && resultCode == Activity.RESULT_OK) {
            Snackbar.make(mRootView, getString(R.string.successfully_saved_trigger),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_survey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.more_options:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            case R.id.done:
                if (validateForQuestion()) {
                    showSurveys(Activity.RESULT_OK);
                }
                return true;
            case R.id.delete_survey:
                mUserActionsListener.deleteSurvey(mSurveyId.getText().toString().trim());
                showSurveys(ViewSurveysActivity.RESULT_DELETE);
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
        for (int i = 0; i < mSurveyIds.size(); i++) {
            if (s.toString().contentEquals(mSurveyIds.get(i))) {
                mUserActionsListener.getSurvey(mSurveyIds.get(i));
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
}
