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
    public void getSurvey(@NonNull final String surveyId, @NonNull final GetSurveyCallback callback) {
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
            // TODO: 4/10/16 delete surveyIds for each user that might be watching it
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
        mDatabase.child(path).setValue(questionIds);
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
        ValueEventListener questionDetailsDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                QuestionDetails questionDetails = dataSnapshot.getValue(QuestionDetails.class);
                callback.onQuestionLoaded(questionDetails);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting question details data failed, log a message
                Log.w(TAG, "loadQuestionDetailsData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys/" + surveyId + "/questions/" + questionId + "/questionDetails")
                .addListenerForSingleValueEvent(questionDetailsDataListener);
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
        if (!surveyId.contentEquals("") && !questionId.contentEquals("")) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/surveys/" + surveyId + "/questions/" + questionId, null);
            childUpdates.put("/surveys/" + surveyId + "/questionIds/" + questionId, null);

            mDatabase.updateChildren(childUpdates);
        }
    }

    
    /*
    * Options
    * */
    @Override
    public void getOptionIds(@NonNull String path, @NonNull final GetOptionIdsCallback callback) {
        final ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Boolean>> t =
                        new GenericTypeIndicator<Map<String, Boolean>>() {};
                Map<String, Boolean> allOptionIds = dataSnapshot.getValue(t);
                callback.onOptionIdsLoaded(allOptionIds);
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
    public void saveOptionIds(@NonNull String path, @NonNull Map<String, Boolean> optionIds) {
        mDatabase.child(path).setValue(optionIds);
    }

    @Override
    public void getOptions(@NonNull String surveyId, @NonNull String questionId, @NonNull final GetOptionsCallback callback) {
        ValueEventListener optionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Option>> t =
                        new GenericTypeIndicator<Map<String, Option>>() {};
                Map<String, Option> options = dataSnapshot.getValue(t);
                callback.onOptionsLoaded(options);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting options failed, log a message
                Log.w(TAG, "loadOptions:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys").child(surveyId).child("questions").child(questionId).child("options")
                .addListenerForSingleValueEvent(optionListener);
    }

    @Override
    public void getOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId, @NonNull final GetOptionCallback callback) {
        ValueEventListener optionDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Option option = dataSnapshot.getValue(Option.class);
                callback.onOptionLoaded(option);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting option data failed, log a message
                Log.w(TAG, "loadOptionData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys/" + surveyId + "/questions/" + questionId + "/options/" + optionId)
                .addListenerForSingleValueEvent(optionDataListener);
    }

    @Override
    public void saveOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId, @NonNull Option option) {
        Map<String, Object> optionValues = option.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + surveyId + "/questions/" + questionId + "/options/" + optionId, optionValues);
        childUpdates.put("/surveys/" + surveyId + "/questions/" + questionId + "/optionIds/" + optionId, true);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void deleteOption(@NonNull String surveyId, @NonNull String questionId, @NonNull String optionId) {
        if (!surveyId.contentEquals("") && !questionId.contentEquals("") && !optionId.contentEquals("")) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/surveys/" + surveyId + "/questions/" + questionId + "/options/" + optionId, null);
            childUpdates.put("/surveys/" + surveyId + "/questions/" + questionId + "/optionIds/" + optionId, null);
            mDatabase.updateChildren(childUpdates);
        }
    }


    /*
    * Triggers
    * */
    @Override
    public void getTriggerIds(@NonNull String path, @NonNull final GetTriggerIdsCallback callback) {
        final ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Boolean>> t =
                        new GenericTypeIndicator<Map<String, Boolean>>() {};
                Map<String, Boolean> allTriggerIds = dataSnapshot.getValue(t);
                callback.onTriggerIdsLoaded(allTriggerIds);
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
    public void saveTriggerIds(@NonNull String path, @NonNull Map<String, Boolean> triggerIds) {
        mDatabase.child(path).setValue(triggerIds);
    }

    @Override
    public void getTriggers(@NonNull String surveyId, @NonNull final GetTriggersCallback callback) {
        ValueEventListener triggerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, SurveyTrigger>> t =
                        new GenericTypeIndicator<Map<String, SurveyTrigger>>() {};
                Map<String, SurveyTrigger> triggers = dataSnapshot.getValue(t);
                callback.onTriggersLoaded(triggers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting triggers failed, log a message
                Log.w(TAG, "loadTriggers:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("/surveys/" + surveyId + "/triggers/")
                .addListenerForSingleValueEvent(triggerListener);
    }

    @Override
    public void getTrigger(@NonNull String surveyId, @NonNull String triggerId, @NonNull final GetTriggerCallback callback) {
        ValueEventListener triggerDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SurveyTrigger trigger = dataSnapshot.getValue(SurveyTrigger.class);
                callback.onTriggerLoaded(trigger);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting trigger data failed, log a message
                Log.w(TAG, "loadTriggerData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("surveys/" + surveyId + "/triggers/" + triggerId)
                .addListenerForSingleValueEvent(triggerDataListener);
    }

    @Override
    public void saveTrigger(@NonNull String surveyId, @NonNull String triggerId, @NonNull SurveyTrigger trigger) {
        Map<String, Object> triggerValues = trigger.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + surveyId + "/triggers/" + triggerId, triggerValues);
        childUpdates.put("/surveys/" + surveyId + "/triggerIds/" + triggerId, true);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void deleteTrigger(@NonNull String surveyId, @NonNull String triggerId) {
        if (!surveyId.contentEquals("") && !triggerId.contentEquals("")) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/surveys/" + surveyId + "/triggers/" + triggerId, null);
            childUpdates.put("/surveys/" + surveyId + "/triggerIds/" + triggerId, null);

            mDatabase.updateChildren(childUpdates);
        }
    }
    
    
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


    /*
    * Users
    * */
    @Override
    public void getUser(@NonNull String userId, @NonNull final GetUserCallback callback) {
        ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onUserLoaded(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting user data failed, log a message
                Log.w(TAG, "loadUserData:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("/users/" + userId)
                .addListenerForSingleValueEvent(userDataListener);
    }

    @Override
    public void saveUser(@NonNull User user) {
        Map<String, Object> userValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + user.getId() + "/", userValues);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void addUserSurvey(@NonNull String userId, @NonNull String surveyId) {
        mDatabase.child("users/").child(userId).child("activeSurveyIds").child(surveyId)
                .setValue(true);
    }

    @Override
    public void markUserSurveyDone(@NonNull String userId, @NonNull String surveyId) {
        mDatabase.child("users/").child(userId).child("activeSurveyIds").child(surveyId)
                .setValue(null);
        mDatabase.child("users/").child(userId).child("completedSurveyIds").child(surveyId)
                .setValue(true);
    }

    @Override
    public void addUserPoints(@NonNull String userId, @NonNull String points) {
        mDatabase.child("users/").child(userId).child("points").setValue(points);
    }

    @Override
    public void removeUserSurvey(@NonNull String userId, @NonNull String surveyId) {
        mDatabase.child("users/").child(userId).child("activeSurveyIds").child(surveyId)
                .setValue(null);
    }

    // TODO: 25/09/16 maybe put a method to detach listeners when the activity is destroyed? Not sure if it's necessary.
}
