package com.example.triibe.triibeuserapp.edit_trigger;

import android.location.Location;

import com.example.triibe.triibeuserapp.data.SurveyTrigger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class EditTriggerPresenter implements EditTriggerContract.UserActionsListener {

    EditTriggerContract.View mView;
    private DatabaseReference mDatabase;

    public EditTriggerPresenter(EditTriggerContract.View view) {
        mView = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void editTrigger(String surveyId, String id, Double lat, Double lon, String level,
                            String time) {
        mView.setProgressIndicator(true);

        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        SurveyTrigger surveyTrigger = new SurveyTrigger(surveyId, id, location, time);
        Map<String, Object> surveyTriggerValues = surveyTrigger.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + surveyId + "/surveyTriggers/" + id, surveyTriggerValues);
        childUpdates.put("/triggerIds/" + id, true);
        mDatabase.updateChildren(childUpdates);


//        Question question = new Question(surveyId, id, imageUrl, title, intro, query);
//        Map<String, Object> questionValues = question.toMap();
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/surveys/" + surveyId + "/questions/" + id + "/questionDetails", questionValues);
//        mDatabase.updateChildren(childUpdates);

        mView.setProgressIndicator(false);

        mView.showEditSurvey();
    }
}
