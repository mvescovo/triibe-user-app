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

    @BindView(R.id.new_points_earned_title)
    TextView mNewPointsTitle;

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
            mSurveyPoints = "";
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
    public void showPoints(final String points, final String totalPoints) {
        if (!points.contentEquals("")) {
            // Show initial values.
            mNewPointsTitle.setVisibility(View.VISIBLE);
            mNewPoints.setVisibility(View.VISIBLE);
            mNewPoints.setText("0");
            int newPointsInt = Integer.parseInt(points);
            int totalPointsInt = Integer.parseInt(totalPoints);
            final int previousPoints = totalPointsInt - newPointsInt;
            String previousPointsString = Integer.toString(previousPoints);
            mTotalPoints.setText(previousPointsString);

            // Show animation of points accumulating.
            new Thread(new Runnable() {
                public void run() {
                    int newPoints = Integer.valueOf(points);
                    for (int i = 1; i <= newPoints; i++) {
                        final int finalI = i;
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mNewPoints.post(new Runnable() {
                            public void run() {
                                String incrementalPoints = Integer.toString(finalI);
                                mNewPoints.setText(incrementalPoints);
                            }
                        });
                    }
                    for (int i = 1; i <= newPoints; i++) {
                        final int finalI = i;
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mTotalPoints.post(new Runnable() {
                            public void run() {
                                String incrementalPoints = Integer.toString(finalI + previousPoints);
                                mTotalPoints.setText(incrementalPoints);
                            }
                        });
                    }
                }
            }).start();
        } else {
            mTotalPoints.setText(totalPoints);
        }
    }
}
