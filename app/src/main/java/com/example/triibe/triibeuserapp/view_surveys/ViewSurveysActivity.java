package com.example.triibe.triibeuserapp.view_surveys;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.edit_survey.EditSurveyActivity;
import com.example.triibe.triibeuserapp.takeSurvey.TakeSurveyActivity;
import com.example.triibe.triibeuserapp.trackLocation.AddGeofencesIntentService;
import com.example.triibe.triibeuserapp.util.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class ViewSurveysActivity extends AppCompatActivity implements ViewSurveysContract.View,
        EasyPermissions.PermissionCallbacks {

    private static final String TAG = "ViewSurveysActivity";

    private static final int REQUEST_EDIT_SURVEY = 1;
    private static final int REQUEST_EDIT_TRIGGER = 3;
    private static final int REQUEST_LINK_TRIGGER = 4;
    private Intent mServiceIntent;
    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int FINE_LOCAITON = 123;
    private ViewSurveysContract.UserActionsListener mUserActionsListener;
    private SurveyAdapter mSurveyAdapter;
//    private ArrayList<String> mSurveys;
//    private User mUser;

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

    @BindView(R.id.track_location_fab)
    FloatingActionButton mTrackLocationFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_surveys);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mModifySurveyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateSurvey();
            }
        });

//        mTrackLocationFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), TrackLocationActivity.class);
//                startActivity(intent);
//            }
//        });

        mUserActionsListener = new ViewSurveysPresenter(this);

        /*
        * Create test user and add a couple of survey ids.
        * */
//        mUser = new User();
//        ArrayList<String> surveyIds = new ArrayList<>();
//        surveyIds.add("1");
//        surveyIds.add("2");
//        mUser.setSurveyIds(surveyIds);

//        mSurveys = mUser.getSurveyIds();


        /*
        * RecyclerView
        * */
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSurveyAdapter = new SurveyAdapter(mUserActionsListener, new ArrayList<SurveyDetails>(0));
        mRecyclerView.setAdapter(mSurveyAdapter);

//        if (adapter.getItemCount() == 0) {
//            mSurveysTextView.setVisibility(View.VISIBLE);
//        }

        // Add test user data
//        addSurveyIdToUser();

        // Start up location tracking service
//        mServiceIntent = new Intent(this, TrackLocationService.class);
//        startService(mServiceIntent);

        // Add mall geofences if not already added (will also be added automatically on boot)
        SharedPreferences preferences = getSharedPreferences(Constants.MALL_GEOFENCES, 0);
        boolean mallGeofencesAdded = preferences.getBoolean(Constants.MALL_GEOFENCES_ADDED, false);
        if (!mallGeofencesAdded) {
            if (EasyPermissions.hasPermissions(this, perms)) {
                // Have permission
                startAddGeofencesService();
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(this, "Need location access to monitor location",
                        FINE_LOCAITON, perms);
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mUserActionsListener.loadUser();
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
    public void showSurveys(@NonNull ArrayList<SurveyDetails> surveys) {
        mSurveyAdapter.replaceData(surveys);

        if (surveys.size() == 0) {
            mSurveysTextView.setVisibility(View.VISIBLE);
        } else {
            mSurveysTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSurveyDetails(String surveyId) {
        stopService(mServiceIntent);

        Intent intent = new Intent(this, TakeSurveyActivity.class);
        intent.putExtra("surveyId", surveyId);
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
    }

    /*
    * Location permission stuff
    * */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted: GRANTED");
        startAddGeofencesService();
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

    private void startAddGeofencesService() {
        Intent addGeofencesIntent = new Intent(this, AddGeofencesIntentService.class);
        startService(addGeofencesIntent);
    }
}
