package com.example.triibe.triibeuserapp.trackData;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.triibe.triibeuserapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TrackDataActivity extends AppCompatActivity {
    private static final String TAG = "TrackDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_data);

        displayProfileInfo();
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

            Log.i(TAG, "displayProfileInfo: name: " + name);
            Log.i(TAG, "displayProfileInfo: email: " + email);
            Log.i(TAG, "displayProfileInfo: photoUrl: " + photoUrl);
            Log.i(TAG, "displayProfileInfo: uid: " + uid);
        }
    }
}
