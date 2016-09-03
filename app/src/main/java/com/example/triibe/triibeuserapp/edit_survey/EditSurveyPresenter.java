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
    private HashMap<String, Object> mSurveyIds;

    public EditSurveyPresenter(EditSurveyContract.View view) {
        mView = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get surveyIds to allow the user to select them in the UI
        mDatabase.child("surveyIds")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            GenericTypeIndicator<HashMap<String, Object>> t
                                    = new GenericTypeIndicator<HashMap<String, Object>>() {
                            };
                            mSurveyIds = dataSnapshot.getValue(t);
                            for (Map.Entry<String, Object> id : mSurveyIds.entrySet()) {
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
    public void editSurvey(String id, String description, String version, String points,
                           String timeTillExpiry, boolean editQuestion) {
        mView.setProgressIndicator(true);

        SurveyDetails surveyDetails = new SurveyDetails(id, version, description, timeTillExpiry,
                points);
        Map<String, Object> surveyDetailsValues = surveyDetails.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + id + "/surveyDetails", surveyDetailsValues);
        childUpdates.put("/surveyIds/" + id, true);
        mDatabase.updateChildren(childUpdates);

        mView.setProgressIndicator(false);

        if (editQuestion) {
            mView.showEditQuestion();
        } else {
            mView.showSurveys();
        }
    }
}
