package com.example.triibe.triibeuserapp.edit_option;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditOptionActivity extends AppCompatActivity implements EditOptionContract.View {

    EditOptionContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;
    private String mQuestionId;

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.done_fab)
    FloatingActionButton mDoneFab;

    @BindView(R.id.option_id)
    TextInputEditText mOptionId;

    @BindView(R.id.option_phrase)
    TextInputEditText mOptionPhrase;

    @BindView(R.id.option_extra_input_type)
    TextInputEditText mExtraInputType;

    @BindView(R.id.option_extra_input_hint)
    TextInputEditText mOptionExtraInputHint;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_option);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserActionsListener = new EditOptionPresenter(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getStringExtra("surveyId") != null) {
            mSurveyId = getIntent().getStringExtra("surveyId");
        }

        if (getIntent().getStringExtra("questionId") != null) {
            mQuestionId = getIntent().getStringExtra("questionId");
        }

        mDoneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    mUserActionsListener.editOption(mSurveyId, mQuestionId,
                            mOptionId.getText().toString().trim(),
                            mOptionPhrase.getText().toString().trim(),
                            "false", mExtraInputType.getText().toString().trim(),
                            mOptionExtraInputHint.getText().toString().trim());
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
    public void showEditQuestion() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private boolean validate() {

        if (mOptionPhrase.getText().toString().trim().contentEquals("")) {
            mOptionPhrase.setError("Phrase must not be empty");
            mOptionPhrase.requestFocus();
            return false;
        }

        return true;
    }
}
