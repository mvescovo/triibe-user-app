package com.example.triibe.triibeuserapp.trackData;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.auth.AuthUiActivity;
import com.example.triibe.triibeuserapp.takeSurvey.TakeSurveyActivity;
import com.example.triibe.triibeuserapp.trackWIFI.TrackRssiIntentService;
import com.example.triibe.triibeuserapp.util.Globals;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackDataActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private static final String TAG = "TrackDataActivity";

    @BindView(android.R.id.content)
    View mRootView;

    @BindView(R.id.start_tracking_button)
    View mStartTrackingButton;

    @BindView(R.id.stop_tracking_button)
    View mStopTrackingButton;

    Intent mTrackRssiIntent;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_data);
        ButterKnife.bind(this);

        if (!Globals.getInstance().isFirebasePersistenceSet()) {
            Globals.getInstance().setFirebasePersistenceEnabled();
        }

        displayProfileInfo();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*
    * Show that the user is logged in.
    * */
    private void displayProfileInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            Log.d(TAG, "displayProfileInfo: name: " + name);
            Log.d(TAG, "displayProfileInfo: email: " + email);
            Log.d(TAG, "displayProfileInfo: photoUrl: " + photoUrl);
            Log.d(TAG, "displayProfileInfo: uid: " + uid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.track_data_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    * Sign the user out using the Firebase UI authentication flow.
    * https://github.com/firebase/FirebaseUI-Android/tree/master/auth
    *
    * Note: of the 3 signin components (Firebase, Google signin, Google smartlock), it seems the
    * Google signin is not signing out and still auto logs in. Will need further testing.
    * */
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), AuthUiActivity.class));
                            finish();
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    public void takeSurvey(View view) {
        startActivity(new Intent(this, TakeSurveyActivity.class));
    }

    public void startDataTracking(View view) {
        Snackbar.make(mRootView, getString(R.string.started_data_tracking), Snackbar.LENGTH_LONG).show();

        // Check permissions before launching service
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i(TAG, "startDataTracking: Need permission to WIFI");

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                Log.i(TAG, "startDataTracking: No explanation needed, will request permission");

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Globals.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            Log.i(TAG, "startDataTracking: Already have permission");
            mTrackRssiIntent = new Intent(this, TrackRssiIntentService.class);
//            startService(mTrackRssiIntent);

//            Intent mapsIntent = new Intent(this, MapsActivity.class);
//            startActivity(mapsIntent);



//            // Get last known location from location API
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                Log.d(TAG, "NO PERMISSION for last location");
//                return;
//            }
////            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            LocationServices.FusedLocationApi.setMockLocation(mGoogleApiClient, mLastLocation);
//            Log.d(TAG, "latitude: " + mLastLocation.getLatitude() + ", longitude: " +
//                    mLastLocation.getLongitude() + ", altitude: " + mLastLocation.getAltitude() +
//                    ", accuracy: " + mLastLocation.getAccuracy() + " has altitude: " + mLastLocation.hasAltitude() +
//                    ", hasSpeed: " + mLastLocation.hasSpeed() + ", hasBearing: " + mLastLocation.hasBearing());
//
//            if (mLastLocation != null) {
//                Log.d(TAG, "latitude: " + mLastLocation.getLatitude() + ", longitude: " +
//                        mLastLocation.getLongitude() + ", altitude: " + mLastLocation.getAltitude() +
//                        ", accuracy: " + mLastLocation.getAccuracy() + " has altitude: " + mLastLocation.hasAltitude() +
//                        ", hasSpeed: " + mLastLocation.hasSpeed() + ", hasBearing: " + mLastLocation.hasBearing());
//            }

            if (mMap.isIndoorEnabled()) {
                if (mMap.getFocusedBuilding() != null) {
                    IndoorBuilding indoorBuilding = mMap.getFocusedBuilding();
                    if (indoorBuilding.getLevels() != null) {
                        List<IndoorLevel> levels = indoorBuilding.getLevels();
                        for (int i = 0; i < levels.size(); i++) {
                            Log.d(TAG, "Level " + levels.get(i).getName() + " exists.");
                        }
                        Log.d(TAG, "Current level: " + indoorBuilding.getActiveLevelIndex());
                    }
                }
            } else {
                Log.d(TAG, "Not indoor enabled");
            }

            LatLng location = mMap.getCameraPosition().target;
            Log.d(TAG, "Location: " + location.latitude + ", " + location.longitude);

            mStartTrackingButton.setVisibility(View.GONE);
            mStopTrackingButton.setVisibility(View.VISIBLE);
        }
    }

    public void stopDataTracking(View view) {
        Snackbar.make(mRootView, getString(R.string.stopped_data_tracking), Snackbar.LENGTH_LONG).show();
        mStopTrackingButton.setVisibility(View.GONE);
        mStartTrackingButton.setVisibility(View.VISIBLE);
        stopService(mTrackRssiIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case Globals.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult: GOT PERMISSION!");
                    mTrackRssiIntent = new Intent(this, TrackRssiIntentService.class);
                    startService(mTrackRssiIntent);

                    mStartTrackingButton.setVisibility(View.GONE);
                    mStopTrackingButton.setVisibility(View.VISIBLE);

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: PERMISSION DENIED!");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mStartTrackingButton.setEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: READY");

        mMap = googleMap;

        // Add a marker in Southland and move the camera
        LatLng southland = new LatLng(-37.958561, 145.053818);
        mMap.addMarker(new MarkerOptions().position(southland).title("Marker in Westfield Southland"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(southland));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(southland, 18));




        // Some buildings have indoor maps. Center the camera over
        // the building, and a floor picker will automatically appear.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-37.958561, 145.053818), 18));

//        if (mMap.isIndoorEnabled()) {
//            if (mMap.getFocusedBuilding() != null) {
//                IndoorBuilding indoorBuilding = mMap.getFocusedBuilding();
//                if (indoorBuilding.getLevels() != null) {
//                    List<IndoorLevel> levels = indoorBuilding.getLevels();
//                    for (int i = 0; i < levels.size(); i++) {
//                        Log.d(TAG, "Level " + levels.get(i).getName() + " exists.");
//                    }
//                    Log.d(TAG, "Current level: " + indoorBuilding.getActiveLevelIndex());
//                }
//            }
//        } else {
//            Log.d(TAG, "Not indoor enabled");
//        }
    }
}
