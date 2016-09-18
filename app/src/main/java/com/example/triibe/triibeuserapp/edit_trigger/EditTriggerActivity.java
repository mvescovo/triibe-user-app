package com.example.triibe.triibeuserapp.edit_trigger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.trackLocation.GetCurrentLocationIntentService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditTriggerActivity extends AppCompatActivity implements EditTriggerContract.View {

    private static final String TAG = "EditTriggerActivity";
    public final static String EXTRA_SURVEY_ID = "com.example.triibe.SURVEY_ID";
    EditTriggerContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;
    GetCurrentLocationIntentService mGetCurrentLocationIntentService;
    boolean mBound = false;

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
        ButterKnife.bind(this);

        mUserActionsListener = new EditTriggerPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getStringExtra(EXTRA_SURVEY_ID) != null) {
            mSurveyId = getIntent().getStringExtra(EXTRA_SURVEY_ID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent getLocationIntent = new Intent(this, GetCurrentLocationIntentService.class);
        bindService(getLocationIntent, mConnection, Context.BIND_AUTO_CREATE);
        startService(getLocationIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
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
    public void setCurrentLocation(double lat, double lon) {
        String latString = Double.toString(lat);
        String lonString = Double.toString(lon);
        mLatitude.setText(latString);
        mLongitude.setText(lonString);
        Log.d(TAG, "setCurrentLocation: " + lat + ", " + lon);
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
            case R.id.use_current_location:
                if (mBound) {
                    setCurrentLocation(
                            mGetCurrentLocationIntentService.getLat(),
                            mGetCurrentLocationIntentService.getLon()
                    );
                }
                return true;
            case R.id.done:
                mUserActionsListener.editTrigger(mSurveyId, mTriggerId.getText().toString().trim(),
                        Double.valueOf(mLatitude.getText().toString().trim()),
                        Double.valueOf(mLongitude.getText().toString().trim()),
                        mLevel.getText().toString().trim(),
                        mTime.getText().toString().trim());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GetCurrentLocationIntentService.GetCurrentLocationBinder binder = (GetCurrentLocationIntentService.GetCurrentLocationBinder) service;
            mGetCurrentLocationIntentService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
