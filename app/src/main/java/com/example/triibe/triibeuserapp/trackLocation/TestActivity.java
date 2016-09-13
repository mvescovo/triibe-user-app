//package com.example.triibe.triibeuserapp.trackLocation;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//
//import com.example.triibe.triibeuserapp.R;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class TestActivity extends AppCompatActivity {
//
//    Intent addGeofenceIntent;
//
//    @BindView(R.id.start_service)
//    Button mStartService;
//
//    @BindView(R.id.stop_service)
//    Button mStopService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//        ButterKnife.bind(this);
//
//        addGeofenceIntent = new Intent(this, AddGeofencesIntentService.class);
//
//        mStartService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startService(addGeofenceIntent);
//            }
//        });
//
//        mStopService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopService(addGeofenceIntent);
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//}
