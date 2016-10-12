package com.example.triibe.triibeuserapp.get_permissions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.trackData.AppUsageStats;
import com.example.triibe.triibeuserapp.track_location.AddFencesIntentService;
import com.example.triibe.triibeuserapp.util.Constants;
import com.example.triibe.triibeuserapp.view_surveys.ViewSurveysActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class GetPermissionsActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "GetPermissionsActivity";
    private static final int FINE_LOCAITON = 1;
    public final static String EXTRA_USER_ID = "com.example.triibe.USER_ID";
    private String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    private String mUserId;
    boolean mHasLocationAccess;
    boolean mHasAppUsageAccess;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.grant_location_permission)
    Button mGrantLocationPermissionButton;

    @BindView(R.id.app_usage_title)
    TextView mAppUsageTitle;

    @BindView(R.id.app_usage_description)
    TextView mAppUsageDescription;

    @BindView(R.id.grant_app_usage_permission)
    Button mGrantAppUsagePermissionButton;

    @BindView(R.id.skip_app_usage_permission)
    Button mSkipAppUsagePermissionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_permissions);
        ButterKnife.bind(this);

        showAppUsageRequestDetails();

        if (getIntent().getStringExtra(EXTRA_USER_ID) != null) {
            mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        } else {
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.user_id),
                    Context.MODE_PRIVATE
            );
            mUserId = sharedPref.getString(getString(R.string.user_id), "InvalidUser");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProgressIndicator(true);
        mHasLocationAccess = getLocationPermission();
        mHasAppUsageAccess = getAppUsagePermission();
        setProgressIndicator(false);

        if (mHasLocationAccess && mHasAppUsageAccess) {
            // Skip to view surveys.
            viewSurveys();
        }
    }

    public void setProgressIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void showAppUsageRequestDetails() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAppUsageTitle.setVisibility(View.VISIBLE);
            mAppUsageDescription.setVisibility(View.VISIBLE);
            mGrantAppUsagePermissionButton.setVisibility(View.VISIBLE);
            mSkipAppUsagePermissionButton.setVisibility(View.VISIBLE);
        }
    }

    private boolean getLocationPermission() {
        boolean hasLocationPermission = false;
        if (EasyPermissions.hasPermissions(this, perms)) {
            hasLocationPermission = true;
            mGrantLocationPermissionButton.setText(R.string.remove_permission);
            mGrantLocationPermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            });
            startAddfencesService();
        } else {
            hasLocationPermission = false;
            mGrantLocationPermissionButton.setText(R.string.grant_permission);
            final Context context = this;
            mGrantLocationPermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EasyPermissions.requestPermissions(context, "Location access is required to receive surveys when you're near or inside a Westfield mall.",
                            FINE_LOCAITON, perms);
                }
            });
        }
        return hasLocationPermission;
    }

    private boolean getAppUsagePermission() {
        boolean hasAppUsagePermission = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!AppUsageStats.getUsageStatsList(this).isEmpty()) {
                hasAppUsagePermission = true;
                mGrantAppUsagePermissionButton.setText(R.string.remove_permission);
            } else {
                hasAppUsagePermission = false;
                mGrantAppUsagePermissionButton.setText(R.string.grant_permission);
            }

            mGrantAppUsagePermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    }
                    startActivity(intent);
                }
            });

            mSkipAppUsagePermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHasLocationAccess) {
                        viewSurveys();
                    } else {
                        Toast.makeText(GetPermissionsActivity.this, "Please grant location access.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return hasAppUsagePermission;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case FINE_LOCAITON:
                Log.d(TAG, "onPermissionsGranted: LOCATION PERMISSION GRANTED");
                mGrantLocationPermissionButton.setText(R.string.remove_permission);
                mGrantLocationPermissionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                startAddfencesService();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> thePerms) {
        switch (requestCode) {
            case FINE_LOCAITON:
                mGrantLocationPermissionButton.setText(getString(R.string.grant_permission));
                if (EasyPermissions.checkDeniedPermissionsNeverAskAgain(
                        this,
                        "The app loads survey triggers only when when you're near or inside a Westfield mall. Without location access, this cannot happen and the app is useless.",
                        R.string.grant_permission,
                        R.string.cancel_permission,
                        thePerms)) {
                    Log.d(TAG, "onPermissionsDenied: denied never ask again");
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void startAddfencesService() {
        // Add mall fences if not already added (will also be added automatically on boot)
        SharedPreferences preferences = getSharedPreferences(Constants.MALL_FENCES, 0);
        boolean mallfencesAdded = preferences.getBoolean(Constants.MALL_FENCES_ADDED, false);
        if (!mallfencesAdded) {
            Log.d(TAG, "startAddfencesService: START SERVICE");
            Intent addMallFencesIntent = new Intent(this, AddFencesIntentService.class);
            addMallFencesIntent.putExtra(
                    AddFencesIntentService.EXTRA_TRIIBE_FENCE_TYPE,
                    AddFencesIntentService.TYPE_MALL
            );
            startService(addMallFencesIntent);
        }
    }

    private void viewSurveys() {
        Intent intent = new Intent(this, ViewSurveysActivity.class);
        intent.putExtra(ViewSurveysActivity.EXTRA_USER_ID, mUserId);
        startActivity(intent);
        finish();
    }
}
