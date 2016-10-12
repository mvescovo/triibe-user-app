package com.example.triibe.triibeuserapp.view_surveys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.edit_survey.EditSurveyActivity;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;
import com.example.triibe.triibeuserapp.util.Globals;
import com.example.triibe.triibeuserapp.util.RunAppWhenAtMallService;
import com.example.triibe.triibeuserapp.util.SimpleDividerItemDecoration;
import com.example.triibe.triibeuserapp.view_points.ViewPointsActivity;
import com.example.triibe.triibeuserapp.view_question.ViewQuestionActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewSurveysActivity extends AppCompatActivity
        implements ViewSurveysContract.View {

    private static final String TAG = "ViewSurveysActivity";
    public final static String EXTRA_USER_ID = "com.example.triibe.USER_ID";
    public final static String EXTRA_SURVEY_POINTS = "com.example.triibe.SURVEY_POINTS";
    public final static String EXTRA_TOTAL_POINTS = "com.example.triibe.TOTAL_POINTS";
    private static final int REQUEST_EDIT_SURVEY = 1;
    public static final int REQUEST_TAKE_SURVEY = 2;
    public static final int RESULT_DELETE = 3;
    private ViewSurveysContract.UserActionsListener mUserActionsListener;
    private SurveyAdapter mSurveyAdapter;
    private String mUserId;

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.surveys_text_view)
    TextView mSurveysTextView;

    @BindView(R.id.view_surveys_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.modify_survey_fab)
    FloatingActionButton mModifySurveyFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_surveys);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getStringExtra(EXTRA_USER_ID) != null) {
            mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        } else {
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.user_id),
                    Context.MODE_PRIVATE
            );
            mUserId = sharedPref.getString(getString(R.string.user_id), "InvalidUser");
        }

        mModifySurveyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateSurvey();
            }
        });

        mUserActionsListener = new ViewSurveysPresenter(
                Globals.getInstance().getTriibeRepository(), this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSurveyAdapter = new SurveyAdapter(mUserActionsListener, new HashMap<String, SurveyDetails>());
        mRecyclerView.setAdapter(mSurveyAdapter);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserActionsListener.loadSurveys(mUserId, true);
        mUserActionsListener.setAdminControls(mUserId);
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
    public void showSurveys(@NonNull Map<String, SurveyDetails> surveyDetails) {
        mSurveyAdapter.replaceData(surveyDetails);
    }

    @Override
    public void showNoSurveysMessage() {
        // Refresh adapter.
        mSurveyAdapter.replaceData(new HashMap<String, SurveyDetails>());
        mSurveysTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAdminControls() {
        mModifySurveyFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void showQuestionUi(String surveyId, String questionId, int numProtectedQuestions) {
        Intent intent = new Intent(this, ViewQuestionActivity.class);
        intent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, surveyId);
        intent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, mUserId);
        intent.putExtra(ViewQuestionActivity.EXTRA_NUM_PROTECTED_QUESTIONS, numProtectedQuestions);
        startActivityForResult(intent, REQUEST_TAKE_SURVEY);
    }

    public void showCreateSurvey() {
        Intent intent = new Intent(this, EditSurveyActivity.class);
        startActivityForResult(intent, REQUEST_EDIT_SURVEY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_SURVEY && resultCode == Activity.RESULT_OK) {
            Snackbar.make(mModifySurveyFab, getString(R.string.successfully_saved_survey),
                    Snackbar.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_EDIT_SURVEY && resultCode == RESULT_DELETE) {
            Snackbar.make(mModifySurveyFab, getString(R.string.successfully_deleted_survey),
                    Snackbar.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_TAKE_SURVEY && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(this, ViewPointsActivity.class);
            intent.putExtra(ViewPointsActivity.EXTRA_USER_ID, mUserId);
            intent.putExtra(ViewPointsActivity.EXTRA_SURVEY_POINTS,
                    Integer.toString(data.getIntExtra(EXTRA_SURVEY_POINTS, -1)));
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_surveys, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_data_tracking:
                Intent runAppAtMallServiceIntent = new Intent(this, RunAppWhenAtMallService.class);
                startService(runAppAtMallServiceIntent);
                return true;
            case R.id.view_points:
                Intent viewPointsIntent = new Intent(this, ViewPointsActivity.class);
                viewPointsIntent.putExtra(ViewPointsActivity.EXTRA_USER_ID, mUserId);
                startActivity(viewPointsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
