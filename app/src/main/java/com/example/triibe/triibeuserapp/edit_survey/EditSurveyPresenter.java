package com.example.triibe.triibeuserapp.edit_survey;

import android.util.Log;

import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class EditSurveyPresenter implements EditSurveyContract.UserActionsListener {

    private static final String TAG = "EditSurveyPresenter";
    EditSurveyContract.View mView;
    private DatabaseReference mDatabase;
    private Map<String, Boolean> mSurveyIds;

    public EditSurveyPresenter(EditSurveyContract.View view) {
        mView = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get surveyIds to allow the user to select them in the UI
        mDatabase.child("surveyIds")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            GenericTypeIndicator<Map<String, Boolean>> t
                                    = new GenericTypeIndicator<Map<String, Boolean>>() {};
                            mSurveyIds = dataSnapshot.getValue(t);
                            for (Map.Entry<String, Boolean> id : mSurveyIds.entrySet()) {
                                Log.d(TAG, "onDataChange: ID: " + id.getKey());
                            }
                        } else {
                            Log.d(TAG, "onDataChange: DATA SNAPSHOT DOES NOT EXIST");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void editSurvey(String surveyId, String description, String version, String points,
                           String timeTillExpiry, boolean editQuestion) {
        mView.setProgressIndicator(true);

        SurveyDetails surveyDetails = new SurveyDetails(surveyId, version, description,
                timeTillExpiry, points);
        Map<String, Object> surveyDetailsValues = surveyDetails.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + surveyId + "/surveyDetails", surveyDetailsValues);
        childUpdates.put("/surveyIds/" + surveyId, true);
        mDatabase.updateChildren(childUpdates);

        mView.setProgressIndicator(false);

        if (editQuestion) {
            mView.showEditQuestion();
        } else {
            mView.showSurveys();
        }
    }

    @Override
    public void editTrigger(String surveyId) {
        mView.showEditTrigger();
    }
}
