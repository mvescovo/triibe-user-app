package com.example.triibe.triibeuserapp.edit_question;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Query;
import com.example.triibe.triibeuserapp.edit_option.EditOptionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditQuestionActivity extends AppCompatActivity implements EditQuestionContract.View {

    private static final int REQUEST_EDIT_OPTION = 1;
    EditQuestionContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.edit_option_fab)
    FloatingActionButton mEditOptionFab;

    @BindView(R.id.done_fab)
    FloatingActionButton mDoneFab;

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

        mEditOptionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    mUserActionsListener.editQuestion(mSurveyId,
                            mQuestionId.getText().toString().trim(),
                            "http://i.imgur.com/DvpvklR.png", mTitle.getText().toString(),
                            mIntro.getText().toString(), new Query(), true);
                }
            }
        });

        mDoneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    mUserActionsListener.editQuestion(mSurveyId,
                            mQuestionId.getText().toString().trim(),
                            "http://i.imgur.com/DvpvklR.png", mTitle.getText().toString(),
                            mIntro.getText().toString(), new Query(), false);
                }
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
            Snackbar.make(mRootView, getString(R.string.successfully_saved_question),
                    Snackbar.LENGTH_SHORT).show();
        }
    }
}
