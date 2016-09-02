package com.example.triibe.triibeuserapp.view_surveys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.create_question.CreateQuestionActivity;
import com.example.triibe.triibeuserapp.create_survey.CreateSurveyActivity;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.takeSurvey.TakeSurveyActivity;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewSurveysActivity extends AppCompatActivity implements ViewSurveysContract.View {

    private static final String TAG = "ViewSurveysActivity";

    private static final int REQUEST_ADD_SURVEY = 1;
    private static final int REQUEST_ADD_QUESTION = 2;
    private static final int REQUEST_ADD_TRIGGER = 3;
    private static final int REQUEST_LINK_TRIGGER = 4;

    @BindView(R.id.view_survey_root)
    CoordinatorLayout mSurveyRoot;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.surveys_text_view)
    TextView mSurveysTextView;

    @BindView(R.id.view_surveys_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.fab_menu)
    FloatingActionMenu mFloatingActionMenu;

    @BindView(R.id.create_question_fab)
    com.github.clans.fab.FloatingActionButton mCreateQuestionFab;

    @BindView(R.id.create_survey_fab)
    com.github.clans.fab.FloatingActionButton mCreateSurveyFab;

    private ViewSurveysContract.UserActionsListener mUserActionsListener;
    private SurveyAdapter mSurveyAdapter;
//    private ArrayList<String> mSurveys;
//    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_surveys);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCreateQuestionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingActionMenu.close(true);
                showCreateQuestion();
            }
        });

        mCreateSurveyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingActionMenu.close(true);
                showCreateSurvey();
            }
        });

        mUserActionsListener = new ViewSurveysPresenter(this);

        /*
        * Create test user and add a couple of survey ids.
        * */
//        mUser = new User();
//        ArrayList<String> surveyIds = new ArrayList<>();
//        surveyIds.add("1");
//        surveyIds.add("2");
//        mUser.setSurveyIds(surveyIds);

//        mSurveys = mUser.getSurveyIds();


        /*
        * RecyclerView
        * */
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSurveyAdapter = new SurveyAdapter(mUserActionsListener, new ArrayList<SurveyDetails>(0));
        mRecyclerView.setAdapter(mSurveyAdapter);

//        if (adapter.getItemCount() == 0) {
//            mSurveysTextView.setVisibility(View.VISIBLE);
//        }

        // Add test user data
//        addSurveyIdToUser();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mUserActionsListener.loadUser();
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSurveys(@NonNull ArrayList<SurveyDetails> surveys) {
        mSurveyAdapter.replaceData(surveys);

        if (surveys.size() == 0) {
            mSurveysTextView.setVisibility(View.VISIBLE);
        } else {
            mSurveysTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSurveyDetails(String surveyId) {
        Intent intent = new Intent(this, TakeSurveyActivity.class);
        intent.putExtra("surveyId", surveyId);
        startActivity(intent);
    }

    public void showCreateSurvey() {
        Intent intent = new Intent(this, CreateSurveyActivity.class);
        startActivityForResult(intent, REQUEST_ADD_SURVEY);
    }

    public void showCreateQuestion() {
        Intent intent = new Intent(this, CreateQuestionActivity.class);
        startActivityForResult(intent, REQUEST_ADD_QUESTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_SURVEY && resultCode == Activity.RESULT_OK) {
            Snackbar.make(mSurveyRoot, getString(R.string.successfully_saved_survey),
                    Snackbar.LENGTH_SHORT).show();
        }
    }
}
