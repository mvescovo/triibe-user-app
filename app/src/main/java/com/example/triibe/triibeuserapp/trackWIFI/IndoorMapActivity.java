//package com.example.triibe.triibeuserapp.trackWIFI;
//
//import android.app.Activity;
//import android.os.Bundle;
//
//import com.example.triibe.triibeuserapp.R;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.LatLng;
//
///**
// * @author michael.
// */
//public class IndoorMapActivity extends Activity implements OnMapReadyCallback {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//
//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap map) {
//        // Some buildings have indoor maps. Center the camera over
//        // the building, and a floor picker will automatically appear.
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(-37.958561, 145.053818), 18));
//    }
//}
