package com.example.triibe.triibeuserapp.create_survey;

import com.example.triibe.triibeuserapp.data.Survey;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @author michael.
 */
public class CreateSurveyPresenter implements CreateSurveyContract.UserActionsListener {

    CreateSurveyContract.View mView;
    private DatabaseReference mDatabase;

    public CreateSurveyPresenter(CreateSurveyContract.View view) {
        mView = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void createSurvey(String name, String description, String version, String points,
                             String timeTillExpiry) {
        mView.setProgressIndicator(true);

        SurveyDetails surveyDetails = new SurveyDetails(name + version, version, name, description,
                timeTillExpiry, points);
        Survey survey = new Survey(surveyDetails);

        mDatabase.child("surveys").child(surveyDetails.getId()).setValue(survey);

        mView.showSurveys();
    }
}
