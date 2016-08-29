package com.example.triibe.triibeuserapp.trackLocation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.triibe.triibeuserapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Southland and move the camera
        LatLng southland = new LatLng(-37.958561, 145.053818);
        mMap.addMarker(new MarkerOptions().position(southland).title("Marker in Westfield Southland"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(southland));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(southland, 18));


        // Some buildings have indoor maps. Center the camera over
        // the building, and a floor picker will automatically appear.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-37.958561, 145.053818), 18));

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
    }
}
