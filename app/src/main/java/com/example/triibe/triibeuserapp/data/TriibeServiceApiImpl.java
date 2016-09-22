package com.example.triibe.triibeuserapp.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.triibe.triibeuserapp.util.Globals;
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

    @Override
    public void getUserSurveyIds(@NonNull final GetUserSurveyIdsCallback callback) {
        final ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Boolean>> t =
                        new GenericTypeIndicator<Map<String, Boolean>>() {
                        };
                Map<String, Boolean> userSurveyIds = dataSnapshot.getValue(t);
                if (userSurveyIds != null) {
                    callback.onUserSurveyIdsLoaded(userSurveyIds);
                } else {
                    // Add new user survey id's.
                    Map<String, Boolean> newUserSurveyIds = new HashMap<>();
                    newUserSurveyIds.put("enrollmentSurvey", true);

                    // Set new ID's in firebase
                    mDatabase.child("users").child(Globals.getInstance().getUser().getId())
                            .child("surveyIds")
                            .setValue(newUserSurveyIds);

                    callback.onUserSurveyIdsLoaded(newUserSurveyIds);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting user data failed, log a message
                Log.w(TAG, "loadUserData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("users").child(Globals.getInstance().getUser().getId()).child("surveyIds")
                .addValueEventListener(userDataListener);
    }

    @Override
    public void getSurvey(@NonNull String surveyId, @NonNull final GetSurveyCallback callback) {
        ValueEventListener surveyDetailsDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SurveyDetails surveyDetails = dataSnapshot.getValue(SurveyDetails.class);
                if (surveyDetails != null) {
                    callback.onSurveyLoaded(dataSnapshot.getValue(SurveyDetails.class));
                } else {
                    callback.onSurveyLoaded(new SurveyDetails());
                }
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
                .addValueEventListener(surveyDetailsDataListener);
    }

    @Override
    public void getQuestions(@NonNull String surveyId, @NonNull final GetQuestionsCallback callback) {
        ValueEventListener questionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Question>> t =
                        new GenericTypeIndicator<Map<String, Question>>() {};
                Map<String, Question> questions = dataSnapshot.getValue(t);
                if (questions != null) {
                    callback.onQuestionsLoaded(dataSnapshot.getValue(t));
                } else {
                    callback.onQuestionsLoaded(new HashMap<String, Question>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting questions failed, log a message
                Log.w(TAG, "loadQuestions:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child(surveyId).child("questions")
                .addValueEventListener(questionListener);
    }

    @Override
    public void getQuestion(@NonNull String questionId, @NonNull GetQuestionCallback callback) {

    }

    @Override
    public void getAnswers(@NonNull String surveyId, @NonNull String userId, @NonNull final GetAnswersCallback callback) {
        ValueEventListener answerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Answer>> t =
                        new GenericTypeIndicator<Map<String, Answer>>() {};
                Map<String, Answer> answers = dataSnapshot.getValue(t);
                if (answers != null) {
                    callback.onAnswersLoaded(answers);
                } else {
                    callback.onAnswersLoaded(new HashMap<String, Answer>());
                }


//                mLoadSurveyProgressBar.setVisibility(View.GONE);
//                if (mAnswers != null && mAnswers.size() >= mCurrentQuestionNum
//                        && !mDownloadedAnswers) {
//                    displayCurrentAnswer();
//                } else {
//                    Log.d(TAG, "onDataChange: No answers in survey");
//                }
//                mDownloadedAnswers = true;

                // Prevent users from changing their responses to qualifying questions
//                if (mAnswers != null && mAnswers.size() > Constants.NUM_QUALIFYING_QUESTIONS &&
//                        mCurrentQuestionNum <= Constants.NUM_QUALIFYING_QUESTIONS) {
//                    mCurrentQuestionNum = Constants.NUM_QUALIFYING_QUESTIONS + 1;
//                    displayCurrentQuestion();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting answers failed, log a message
                Log.w(TAG, "loadAnswers:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child(surveyId).child("answers").child(userId)
                .addValueEventListener(answerListener);
    }

    @Override
    public void getAnswer(@NonNull String questionId, @NonNull GetAnswerCallback callback) {

    }

    @Override
    public void saveAnswer(@NonNull String surveyId, @NonNull String userId,
                           @NonNull String questionId, @NonNull Answer answer) {
        mDatabase.child("surveys").child(surveyId).child("answers").child(userId).child(questionId)
                .setValue(answer);
    }
}
