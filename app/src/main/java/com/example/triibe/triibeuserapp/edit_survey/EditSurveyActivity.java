package com.example.triibe.triibeuserapp.edit_survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.edit_question.EditQuestionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditSurveyActivity extends AppCompatActivity implements EditSurveyContract.View {

    private static final String TAG = "EditSurveyActivity";

    private static final int REQUEST_EDIT_QUESTION = 1;
    EditSurveyContract.UserActionsListener mUserActionsListener;
    BottomSheetBehavior mBottomSheetBehavior;

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.bottom_sheet)
    View mBottomSheet;

    @BindView(R.id.edit_question_button_layout)
    LinearLayout mEditQuestionButtonLayout;

    @BindView(R.id.edit_trigger_button_layout)
    LinearLayout mEditTriggerButtonLayout;

    @BindView(R.id.survey_id)
    TextInputEditText mSurveyId;

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

        mUserActionsListener = new EditSurveyPresenter(this);

        mEditQuestionButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (validate()) {
                    mUserActionsListener.editSurvey(mSurveyId.getText().toString(),
                            mDescription.getText().toString(), mVersion.getText().toString(),
                            mPoints.getText().toString(), mTimeTillExpiry.getText().toString(),
                            true);
                }
            }
        });

        mEditTriggerButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
    public void showSurveys() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void showEditQuestion() {
        Intent intent = new Intent(this, EditQuestionActivity.class);
        intent.putExtra("surveyId", mSurveyId.getText().toString().trim());
        startActivityForResult(intent, REQUEST_EDIT_QUESTION);
    }

    private boolean validate() {

        if (mSurveyId.getText().toString().trim().contentEquals("")) {
            mSurveyId.setError("Name must not be empty");
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

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_QUESTION && resultCode == Activity.RESULT_OK) {
            Snackbar.make(mRootView, getString(R.string.successfully_saved_question),
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
                // Hide keyboard (currently not working as desired so leaving commented)
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
                // Show bottomsheet
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            case R.id.done:
                if (validate()) {
                    mUserActionsListener.editSurvey(mSurveyId.getText().toString(),
                            mDescription.getText().toString(), mVersion.getText().toString(),
                            mPoints.getText().toString(), mTimeTillExpiry.getText().toString(),
                            false);
                }
                return true;
            case R.id.delete_survey:
                Log.d(TAG, "onOptionsItemSelected: DELETE SURVEY");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
