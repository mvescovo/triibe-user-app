package com.example.triibe.triibeuserapp.view_survey_details;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Survey;

public class ViewSurveyDetailsActivity extends AppCompatActivity implements ViewSurveyDetailsContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_survey_details);
    }

    @Override
    public void showSurveyDetails(Survey survey) {

    }

    @Override
    public void showSnackbar(int stringResource) {

    }
}
