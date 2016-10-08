package com.example.triibe.triibeuserapp.edit_trigger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.SurveyTrigger;
import com.example.triibe.triibeuserapp.edit_survey.EditSurveyActivity;
import com.example.triibe.triibeuserapp.track_location.GetCurrentLocationIntentService;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;
import com.example.triibe.triibeuserapp.util.Globals;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditTriggerActivity extends AppCompatActivity
        implements EditTriggerContract.View, TextWatcher {

    private static final String TAG = "EditTriggerActivity";
    public final static String EXTRA_SURVEY_ID = "com.example.triibe.SURVEY_ID";
    private static final int PLACE_PICKER_REQUEST = 1;
    EditTriggerContract.UserActionsListener mUserActionsListener;
    GetCurrentLocationIntentService mGetCurrentLocationIntentService;
    boolean mBound = false;
    private String mSurveyId;
    private List<String> mTriggerIds;
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

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.trigger_id)
    AppCompatAutoCompleteTextView mTriggerId;

    @BindView(R.id.lat)
    TextInputEditText mLatitude;

    @BindView(R.id.lon)
    TextInputEditText mLongitude;

    @BindView(R.id.radius)
    TextInputEditText mRadius;

    @BindView(R.id.dwell)
    TextInputEditText mDwell;

    @BindView(R.id.level)
    TextInputEditText mLevel;

    @BindView(R.id.time)
    TextInputEditText mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trigger);
        ButterKnife.bind(this);

        mUserActionsListener = new EditTriggerPresenter(
                Globals.getInstance().getTriibeRepository(),
                this
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getStringExtra(EXTRA_SURVEY_ID) != null) {
            mSurveyId = getIntent().getStringExtra(EXTRA_SURVEY_ID);
        }

        mTriggerIds = new ArrayList<>();
        mTriggerId.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserActionsListener.getTriggerIds(mSurveyId, true);
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
    public void addTriggerIdsToAutoComplete(List<String> triggerIds) {
        mTriggerIds = triggerIds;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                mTriggerIds
        );

        mTriggerId.setAdapter(adapter);
    }

    @Override
    public void showTrigger(SurveyTrigger trigger) {
        mLatitude.setText(trigger.getLatitude());
        mLongitude.setText(trigger.getLongitude());
        mRadius.setText(trigger.getRadius());
        mDwell.setText(trigger.getDwell());
        mLevel.setText(trigger.getLevel());
        mTime.setText(trigger.getTime());
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
    public void showEditSurey(@NonNull Integer resultCode) {
        setResult(resultCode);
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
            case R.id.get_location_from_map:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.done:
                if (validate()) {
                    hideSoftKeyboard(mRootView);
                    showEditSurey(Activity.RESULT_OK);
                }
                return true;
            case R.id.delete_trigger:
                hideSoftKeyboard(mRootView);
                mUserActionsListener.deleteTrigger(mTriggerId.getText().toString().trim());
                showEditSurey(EditSurveyActivity.RESULT_DELETE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                LatLng latLng = place.getLatLng();
                mLatitude.setText(String.format("%s", latLng.latitude));
                mLongitude.setText(String.format("%s", latLng.longitude));
            }
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean validate() {
        if (mTriggerId.getText().toString().trim().contentEquals("")) {
            mTriggerId.setError("Trigger ID must not be empty"); // TODO: 18/09/16 set in strings
            mTriggerId.requestFocus();
            return false;
        } else if (mLatitude.getText().toString().trim().contentEquals("")) {
            mLatitude.setError("Latitude must not be empty");
            mLatitude.requestFocus();
            return false;
        } else if (mLongitude.getText().toString().trim().contentEquals("")) {
            mLongitude.setError("Longitude must not be empty");
            mLongitude.requestFocus();
            return false;
        } else if (mRadius.getText().toString().trim().contentEquals("")) {
            mRadius.setError("Radius must not be empty");
            mRadius.requestFocus();
            return false;
        } else if (mDwell.getText().toString().trim().contentEquals("")) {
            mDwell.setError("Dwell must not be empty");
            mDwell.requestFocus();
            return false;
        }

        // If all ok save trigger.
        SurveyTrigger trigger = new SurveyTrigger(
                mSurveyId,
                // Save trigger with "t" prefix. Numerical values will create an array on firebase.
                "t" + mTriggerId.getText().toString().trim(),
                mLatitude.getText().toString().trim(),
                mLongitude.getText().toString().trim(),
                mRadius.getText().toString().trim(),
                mDwell.getText().toString().trim()
        );

        if (!mLevel.getText().toString().trim().contentEquals("")) {
            trigger.setLevel(mLevel.getText().toString().trim());
        }
        if (!mTime.getText().toString().trim().contentEquals("")) {
            trigger.setLevel(mTime.getText().toString().trim());
        }
        mUserActionsListener.saveTrigger(trigger);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean matched = false;
        Log.d(TAG, "onTextChanged: CHANGED");
        for (int i = 0; i < mTriggerIds.size(); i++) {
            // Get the substring at index 1 because the real triggerId is preceded with "t".
            if (s.toString().contentEquals(mTriggerIds.get(i).substring(1))) {
                mUserActionsListener.getTrigger(mTriggerIds.get(i));
                matched = true;
            }
        }
        if (!matched) {
            clearOtherFields();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void clearOtherFields() {
        mLatitude.setText("");
        mLongitude.setText("");
        mLevel.setText("");
        mTime.setText("");
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
