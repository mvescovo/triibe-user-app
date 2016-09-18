package com.example.triibe.triibeuserapp.edit_question;

import com.example.triibe.triibeuserapp.data.QuestionDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class EditQuestionPresenter implements EditQuestionContract.UserActionsListener {

    private static final String TAG = "EditQuestionPresenter";
    EditQuestionContract.View mView;
    private DatabaseReference mDatabase; // TODO: 2/09/16 detach listeners
//    private HashMap<String, Object> mSurveyIds;


    public EditQuestionPresenter(EditQuestionContract.View view) {
        mView = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get surveyIds to allow the user to select them in the UI
//        mDatabase.child("surveyIds")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            GenericTypeIndicator<HashMap<String, Object>> t
//                                    = new GenericTypeIndicator<HashMap<String, Object>>() {
//                            };
//                            mSurveyIds = dataSnapshot.getValue(t);
//                            for (Map.Entry<String, Object> id : mSurveyIds.entrySet()) {
//                                Log.d(TAG, "onDataChange: ID: " + id.getKey());
//                            }
//                        } else {
//                            Log.d(TAG, "onDataChange: DATA SNAPSHOT DOES NOT EXIST");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

    @Override
    public void editQuestion(QuestionDetails questionDetails, boolean editOption) {
        mView.setProgressIndicator(true);

        QuestionDetails testQuestionDetails = new QuestionDetails("Test", "test", "test"); // TODO: 18/09/16 fix with real values
        Map<String, Object> questionDetailsValues = testQuestionDetails.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + questionDetails.getSurveyId() + "/questions/"
                + questionDetails.getId() + "/questionDetails", questionDetailsValues);
        mDatabase.updateChildren(childUpdates);

        mView.setProgressIndicator(false);

        if (editOption) {
            mView.showEditOption();
        } else {
            mView.showEditSurvey();
        }
    }
}
