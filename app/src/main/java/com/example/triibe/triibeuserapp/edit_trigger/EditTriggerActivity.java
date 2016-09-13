package com.example.triibe.triibeuserapp.edit_trigger;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditTriggerActivity extends AppCompatActivity implements EditTriggerContract.View {

    EditTriggerContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.trigger_id)
    TextInputEditText mTriggerId;

    @BindView(R.id.lat)
    TextInputEditText mLatitude;

    @BindView(R.id.lon)
    TextInputEditText mLongitude;

    @BindView(R.id.level)
    TextInputEditText mLevel;

    @BindView(R.id.time)
    TextInputEditText mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trigger);
        mUserActionsListener = new EditTriggerPresenter(this);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getStringExtra("surveyId") != null) {
            mSurveyId = getIntent().getStringExtra("surveyId");
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_trigger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                mUserActionsListener.editTrigger(mSurveyId, mTriggerId.getText().toString().trim(),
                        mLatitude.getText().toString().trim(),
                        mLongitude.getText().toString().trim(),
                        mLevel.getText().toString().trim(),
                        mTime.getText().toString().trim());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
