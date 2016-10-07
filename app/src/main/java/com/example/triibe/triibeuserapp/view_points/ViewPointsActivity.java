package com.example.triibe.triibeuserapp.view_points;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.util.Globals;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author michael.
 */
public class ViewPointsActivity extends AppCompatActivity implements ViewPointsContract.View {

    public final static String EXTRA_USER_ID = "com.example.triibe.USER_ID";
    public final static String EXTRA_SURVEY_POINTS = "com.example.triibe.SURVEY_POINTS";
    ViewPointsContract.UserActionsListener mUserActionsListener;
    private String mUserId;
    private String mSurveyPoints;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.new_points_earned_points)
    TextView mNewPoints;

    @BindView(R.id.total_points_earned_points)
    TextView mTotalPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_points);
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mUserActionsListener = new ViewPointsPresenter(
                Globals.getInstance().getTriibeRepository(),
                this
        );

        if (getIntent().getStringExtra(EXTRA_USER_ID) != null) {
            mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        } else {
            mUserId = "TestUserId";
        }
        if (getIntent().getStringExtra(EXTRA_SURVEY_POINTS) != null) {
            mSurveyPoints = getIntent().getStringExtra(EXTRA_SURVEY_POINTS);
        } else {
            mSurveyPoints = "Invalid points";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserActionsListener.loadCurrentPoints(mUserId, mSurveyPoints);
    }

    @Override
    public void setIndeterminateProgressIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showNewPoints(String points) {
        mNewPoints.setText(points);
    }

    @Override
    public void showTotalPoints(String points) {
        mTotalPoints.setText(points);
    }
}
