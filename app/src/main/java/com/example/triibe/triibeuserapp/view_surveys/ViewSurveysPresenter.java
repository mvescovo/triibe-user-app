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

/**
 * @author michael.
 */
public class ViewSurveysPresenter implements ViewSurveysContract.UserActionsListener {

    private static final String TAG = "ViewSurveysPresenter";
    private ViewSurveysContract.View mView;
    private DatabaseReference mDatabase;
    private ArrayList<SurveyDetails> mSurveyDetails;

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
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    Globals.getInstance().setUser(user);
                } else {
                    // Add new user.
                    ArrayList<String> surveyIds = new ArrayList<>();
                    surveyIds.add("triibeUser");
                    Globals.getInstance().getUser().setSurveyIds(surveyIds);
                    mDatabase.child("users")
                            .child(Globals.getInstance().getUser().getId())
                            .setValue(Globals.getInstance().getUser());
                }
                if (Globals.getInstance().getUser().getSurveyIds() == null) {
                    ArrayList<String> surveyIds = new ArrayList<>();
                    Globals.getInstance().getUser().setSurveyIds(surveyIds);
                }
                loadSurveys();
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
        ArrayList<String> surveyIds = Globals.getInstance().getUser().getSurveyIds();
        mSurveyDetails.clear();

        for (int i = 0; i < surveyIds.size(); i++) {
            ValueEventListener surveyDetailsDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SurveyDetails surveyDetails = dataSnapshot.getValue(SurveyDetails.class);
                    mSurveyDetails.add(surveyDetails);
                    mView.showSurveys(mSurveyDetails);
                    mView.setProgressIndicator(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting survey details data failed, log a message
                    Log.w(TAG, "loadSurveyDetailsData:onCancelled", databaseError.toException());
                    mView.setProgressIndicator(false);
                }
            };
            mDatabase.child("surveys")
                    .child(surveyIds.get(i))
                    .child("surveyDetails")
                    .addValueEventListener(surveyDetailsDataListener);
        }
    }

    @Override
    public void openSurveyDetails(@NonNull String surveyId) {
        mView.showSurveyDetails(surveyId);
    }
}
