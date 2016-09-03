package com.example.triibe.triibeuserapp.edit_question;

import android.util.Log;

import com.example.triibe.triibeuserapp.data.Query;
import com.example.triibe.triibeuserapp.data.Question;
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
public class EditQuestionPresenter implements EditQuestionContract.UserActionsListener {

    private static final String TAG = "EditQuestionPresenter";

    EditQuestionContract.View mView;
    private DatabaseReference mDatabase; // TODO: 2/09/16 detach listeners
    private HashMap<String, Object> mSurveyIds;


    public EditQuestionPresenter(EditQuestionContract.View view) {
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
    public void editQuestion(String surveyId, String id, String imageUrl, String title,
                             String intro, Query query, boolean editOption) {
        mView.setProgressIndicator(true);

        Question question = new Question(surveyId, id, imageUrl, title, intro, query);
        Map<String, Object> questionValues = question.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + surveyId + "/questions/" + id + "/questionDetails", questionValues);
        mDatabase.updateChildren(childUpdates);

        mView.setProgressIndicator(false);

        if (editOption) {
            mView.showEditOption();
        } else {
            mView.showEditSurvey();
        }
    }
}
