package com.example.triibe.triibeuserapp.edit_question;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Query;
import com.example.triibe.triibeuserapp.edit_option.EditOptionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditQuestionActivity extends AppCompatActivity implements EditQuestionContract.View {

    private static final String TAG = "EditQuestionActivity";

    private static final int REQUEST_EDIT_OPTION = 1;
    EditQuestionContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;
    BottomSheetBehavior mBottomSheetBehavior;

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.bottom_sheet)
    View mBottomSheet;

    @BindView(R.id.edit_option_button_layout)
    LinearLayout mEditOptionButtonLayout;

    @BindView(R.id.question_id)
    TextInputEditText mQuestionId;

    @BindView(R.id.question_title)
    TextInputEditText mTitle;

    @BindView(R.id.question_intro)
    TextInputEditText mIntro;

    @BindView(R.id.question_image_url)
    TextInputEditText mImageUrl;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        ButterKnife.bind(this);

        mUserActionsListener = new EditQuestionPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getStringExtra("surveyId") != null) {
            mSurveyId = getIntent().getStringExtra("surveyId");
        }

        mEditOptionButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (validate()) {
                    mUserActionsListener.editQuestion(mSurveyId,
                            mQuestionId.getText().toString().trim(),
                            "http://i.imgur.com/DvpvklR.png", mTitle.getText().toString(),
                            mIntro.getText().toString(), new Query(), true);
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
    public void showEditSurvey() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void showEditOption() {
        Intent intent = new Intent(this, EditOptionActivity.class);
        intent.putExtra("surveyId", mSurveyId);
        intent.putExtra("questionId", mQuestionId.getText().toString().trim());
        startActivityForResult(intent, REQUEST_EDIT_OPTION);
    }

    private boolean validate() {

        if (mTitle.getText().toString().trim().contentEquals("")) {
            mTitle.setError("Title must not be empty");
            mTitle.requestFocus();
            return false;
        } else if (mTitle.getText().toString().trim().contentEquals("")) {
            mTitle.setError("Title must not be empty");
            mTitle.requestFocus();
            return false;
        } else if (mIntro.getText().toString().trim().contentEquals("")) {
            mIntro.setError("Intro must not be empty");
            mIntro.requestFocus();
            return false;
        } else if (mImageUrl.getText().toString().trim().contentEquals("")) {
            mImageUrl.setError("Image url must not be empty");
            mImageUrl.requestFocus();
            return false;
        }

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
                // Hide keyboard (currently not working as desired so leaving commented)
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
                // Show bottomsheet
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            case R.id.done:
                if (validate()) {
                    mUserActionsListener.editQuestion(mSurveyId,
                            mQuestionId.getText().toString().trim(),
                            "http://i.imgur.com/DvpvklR.png", mTitle.getText().toString(),
                            mIntro.getText().toString(), new Query(), false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
