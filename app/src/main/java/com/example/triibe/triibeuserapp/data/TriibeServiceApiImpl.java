package com.example.triibe.triibeuserapp.data;

import android.support.annotation.NonNull;
import android.util.Log;

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
public class TriibeServiceApiImpl implements TriibeServiceApi {

    private static final String TAG = "TriibeServiceApiImpl";
    private DatabaseReference mDatabase;

    public TriibeServiceApiImpl() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    
    /*
    * Surveys
    * */
    @Override
    public void getSurveyIds(@NonNull String path, @NonNull final GetSurveyIdsCallback callback) {
        final ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Boolean>> t =
                        new GenericTypeIndicator<Map<String, Boolean>>() {};
                Map<String, Boolean> allSurveyIds = dataSnapshot.getValue(t);
                callback.onSurveyIdsLoaded(allSurveyIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting user data failed, log a message
                Log.w(TAG, "loadUserData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child(path).addListenerForSingleValueEvent(userDataListener);
    }

    @Override
    public void saveSurveyIds(@NonNull String path, @NonNull Map<String, Boolean> surveyIds) {
        mDatabase.child(path).setValue(surveyIds);
    }

    @Override
    public void getSurvey(@NonNull String surveyId, @NonNull final GetSurveyCallback callback) {
        ValueEventListener surveyDetailsDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SurveyDetails surveyDetails = dataSnapshot.getValue(SurveyDetails.class);
                callback.onSurveyLoaded(surveyDetails);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting survey details data failed, log a message
                Log.w(TAG, "loadSurveyDetailsData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys")
                .child(surveyId)
                .child("surveyDetails")
                .addListenerForSingleValueEvent(surveyDetailsDataListener);
    }

    @Override
    public void saveSurvey(@NonNull String surveyId, @NonNull SurveyDetails surveyDetails) {
        Map<String, Object> surveyDetailsValues = surveyDetails.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + surveyId + "/surveyDetails", surveyDetailsValues);
        childUpdates.put("/surveyIds/" + surveyId, true);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void deleteSurvey(@NonNull String surveyId) {
        if (!surveyId.contentEquals("")) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/surveys/" + surveyId, null);
            childUpdates.put("/surveyIds/" + surveyId, null);
            mDatabase.updateChildren(childUpdates);
        }
    }

    
    /*
    * Questions
    * */
    @Override
    public void getQuestionIds(@NonNull String path, @NonNull final GetQuestionIdsCallback callback) {
        final ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Boolean>> t =
                        new GenericTypeIndicator<Map<String, Boolean>>() {};
                Map<String, Boolean> allQuestionIds = dataSnapshot.getValue(t);
                callback.onQuestionIdsLoaded(allQuestionIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting user data failed, log a message
                Log.w(TAG, "loadUserData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child(path).addListenerForSingleValueEvent(userDataListener);
    }

    @Override
    public void saveQuestionIds(@NonNull String path, @NonNull Map<String, Boolean> questionIds) {
        // TODO: 25/09/16  
    }

    @Override
    public void getQuestions(@NonNull String surveyId, @NonNull final GetQuestionsCallback callback) {
        ValueEventListener questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Question>> t =
                        new GenericTypeIndicator<Map<String, Question>>() {};
                Map<String, Question> questions = dataSnapshot.getValue(t);
                callback.onQuestionsLoaded(questions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting questions failed, log a message
                Log.w(TAG, "loadQuestions:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child(surveyId).child("questions")
                .addListenerForSingleValueEvent(questionListener);
    }

    @Override
    public void getQuestion(@NonNull String surveyId, @NonNull String questionId, @NonNull final GetQuestionCallback callback) {
        ValueEventListener surveyDetailsDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QuestionDetails questionDetails = dataSnapshot.getValue(QuestionDetails.class);
                callback.onQuestionLoaded(questionDetails);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting survey details data failed, log a message
                Log.w(TAG, "loadSurveyDetailsData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys/" + surveyId + "/questions/" + questionId + "/questionDetails")
                .addListenerForSingleValueEvent(surveyDetailsDataListener);
    }

    @Override
    public void saveQuestion(@NonNull String surveyId, @NonNull String questionId, @NonNull QuestionDetails questionDetails) {
        Map<String, Object> questionDetailsValues = questionDetails.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + surveyId + "/questions/" + questionId + "/questionDetails", questionDetailsValues);
        childUpdates.put("/surveys/" + surveyId + "/questionIds/" + questionId, true);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void deleteQuestion(@NonNull String surveyId, @NonNull String questionId) {
        // TODO: 25/09/16  
    }

    
    /*
    * Options
    * */
    @Override
    public void getOptionIds(@NonNull String path, @NonNull GetOptionIdsCallback callback) {
        // TODO: 25/09/16  
    }

    @Override
    public void saveOptionIds(@NonNull String path, @NonNull Map<String, Boolean> optionIds) {
        // TODO: 25/09/16  
    }

    @Override
    public void getOptions(@NonNull String surveyId, @NonNull String questionId, @NonNull GetOptionsCallback callback) {
        // TODO: 25/09/16  
    }

    @Override
    public void getOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId, @NonNull GetOptionCallback callback) {
        // TODO: 25/09/16  
    }

    @Override
    public void saveOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId, @NonNull Option option) {
        // TODO: 25/09/16  
    }

    @Override
    public void deleteOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId) {
        // TODO: 25/09/16  
    }

    
    /*
    * Triggers
    * */
    
    
    /*
    * Answers
    * */
    @Override
    public void getAnswers(@NonNull String surveyId, @NonNull String userId, @NonNull final GetAnswersCallback callback) {
        ValueEventListener answerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Answer>> t =
                        new GenericTypeIndicator<Map<String, Answer>>() {};
                Map<String, Answer> answers = dataSnapshot.getValue(t);
                callback.onAnswersLoaded(answers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting answers failed, log a message
                Log.w(TAG, "loadAnswers:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child(surveyId).child("answers").child(userId)
                .addListenerForSingleValueEvent(answerListener);
    }

    @Override
    public void getAnswer(@NonNull String surveyId, @NonNull String questionId, @NonNull GetAnswerCallback callback) {
        // TODO: 25/09/16  
    }

    @Override
    public void saveAnswer(@NonNull String surveyId, @NonNull String userId,
                           @NonNull String questionId, @NonNull Answer answer) {
        mDatabase.child("surveys").child(surveyId).child("answers").child(userId).child(questionId)
                .setValue(answer);
    }

    // TODO: 25/09/16 maybe put a method to detach listeners when the activity is destroyed? Not sure if it's necessary.
}
