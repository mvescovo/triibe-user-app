package com.example.triibe.triibeuserapp.view_surveys;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.data.User;
import com.example.triibe.triibeuserapp.util.Globals;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class ViewSurveysPresenter implements ViewSurveysContract.UserActionsListener {

    private static final String TAG = "ViewSurveysPresenter";
    private ViewSurveysContract.View mView;
    private DatabaseReference mDatabase;
    private ArrayList<SurveyDetails> mSurveyDetails;
    private boolean hasLoadedSurveys = false;

    public ViewSurveysPresenter(ViewSurveysContract.View view) {
        mView = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mSurveyDetails = new ArrayList<>();
    }

    @Override
    public void loadUser() {
        mView.setProgressIndicator(true);

        ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: USER DATA CHANGED");
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    Globals.getInstance().setUser(user);
                    if (Globals.getInstance().getUser().getSurveyIds() == null) {
                        HashMap<String, Object> surveyIds = new HashMap<>();
                        Globals.getInstance().getUser().setSurveyIds(surveyIds);
                    }
                    if (!hasLoadedSurveys) {
                        hasLoadedSurveys = true;
                        loadSurveys();
                    } else {
                        mView.setProgressIndicator(false);
                    }
                } else {
                    // Add new user.
                    HashMap<String, Object> surveyIds = new HashMap<>();
                    surveyIds.put("enrollmentSurvey", true);
                    Globals.getInstance().getUser().setSurveyIds(surveyIds);
                    mDatabase.child("users")
                            .child(Globals.getInstance().getUser().getId())
                            .setValue(Globals.getInstance().getUser());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting user data failed, log a message
                Log.w(TAG, "loadUserData:onCancelled", databaseError.toException());
                mView.setProgressIndicator(false);
            }
        };
        mDatabase.child("users")
                .child(Globals.getInstance().getUser().getId())
                .addValueEventListener(userDataListener);
    }

    @Override
    public void loadSurveys() {
        mSurveyDetails.clear();
        HashMap<String, Object> surveyIds = Globals.getInstance().getUser().getSurveyIds();
        Log.d(TAG, "loadSurveys: NUM SURVEY IDs: " + surveyIds.size());

        for (Map.Entry<String, Object> surveyId : surveyIds.entrySet()) {
            ValueEventListener surveyDetailsDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SurveyDetails surveyDetails = dataSnapshot.getValue(SurveyDetails.class);
                    mSurveyDetails.add(surveyDetails);
                    mView.showSurveys(mSurveyDetails);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting survey details data failed, log a message
                    Log.w(TAG, "loadSurveyDetailsData:onCancelled", databaseError.toException());
                }
            };
            mDatabase.child("surveys")
                    .child(surveyId.getKey())
                    .child("surveyDetails")
                    .addValueEventListener(surveyDetailsDataListener);
        }
        mView.setProgressIndicator(false);
    }

    @Override
    public void openSurveyDetails(@NonNull String surveyId) {
        mView.showSurveyDetails(surveyId);
    }
}
