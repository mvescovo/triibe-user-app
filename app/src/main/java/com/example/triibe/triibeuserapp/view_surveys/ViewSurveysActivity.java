package com.example.triibe.triibeuserapp.view_surveys;

import android.Manifest;
import android.app.Activity;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.edit_survey.EditSurveyActivity;
import com.example.triibe.triibeuserapp.track_location.AddFencesIntentService;
import com.example.triibe.triibeuserapp.util.Constants;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;
import com.example.triibe.triibeuserapp.util.Globals;
import com.example.triibe.triibeuserapp.util.RunAppWhenAtMallService;
import com.example.triibe.triibeuserapp.util.SimpleDividerItemDecoration;
import com.example.triibe.triibeuserapp.view_question.ViewQuestionActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class ViewSurveysActivity extends AppCompatActivity
        implements ViewSurveysContract.View, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "ViewSurveysActivity";
    public final static String EXTRA_USER_ID = "com.example.triibe.USER_ID";
    private static final int REQUEST_EDIT_SURVEY = 1;
    public static final int RESULT_DELETE = -2;
    private static final int FINE_LOCAITON = 123;
    private String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
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
            mUserId = "TestUserId";
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

        // Add mall fences if not already added (will also be added automatically on boot)
        SharedPreferences preferences = getSharedPreferences(Constants.MALL_FENCES, 0);
        boolean mallfencesAdded = preferences.getBoolean(Constants.MALL_FENCES_ADDED, false);
        if (!mallfencesAdded) {
            if (EasyPermissions.hasPermissions(this, perms)) {
                // Have permission
                startAddfencesService();
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(this, "Need location access to monitor location",
                        FINE_LOCAITON, perms);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserActionsListener.loadSurveys(mUserId, true);
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
        if (mSurveyAdapter.getItemCount() == 0) {
            mSurveysTextView.setVisibility(View.VISIBLE);
        } else {
            mSurveysTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showQuestionUi(String surveyId, String questionId, int numProtectedQuestions) {
        Intent intent = new Intent(this, ViewQuestionActivity.class);
        intent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, surveyId);
        intent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, mUserId);
        intent.putExtra(ViewQuestionActivity.EXTRA_NUM_PROTECTED_QUESTIONS, numProtectedQuestions);
        startActivity(intent);
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
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted: GRANTED");
        startAddfencesService();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied: DENIED");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void startAddfencesService() {
        Log.d(TAG, "startAddfencesService: START SERVICE");
        Intent addMallFencesIntent = new Intent(this, AddFencesIntentService.class);
        addMallFencesIntent.putExtra(
                AddFencesIntentService.EXTRA_TRIIBE_FENCE_TYPE,
                AddFencesIntentService.TYPE_MALL
        );
        startService(addMallFencesIntent);
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
                Intent intent = new Intent(this, RunAppWhenAtMallService.class);
                startService(intent);
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
