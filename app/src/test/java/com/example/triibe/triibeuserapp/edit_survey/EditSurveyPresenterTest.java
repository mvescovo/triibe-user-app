package com.example.triibe.triibeuserapp.edit_survey;

import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * @author michael.
 */

public class EditSurveyPresenterTest {

    private static Map<String, Boolean> SURVEY_IDS_MAP = Maps.newHashMap();
    private static List<String> SURVEY_IDS = Lists.newArrayList();
    private static Map<String, SurveyDetails> SURVEYS = Maps.newHashMap();
    private static SurveyDetails SURVEY1_DETAILS = new SurveyDetails();
    private static String SURVEY1_ID = "Test survey 1";
    private static String SURVEY1_DESCRIPTION = "Test survey";
    private static String SURVEY1_VERSION = "1.0";
    private static String SURVEY1_POINTS = "1";
    private static String SURVEY1_TIME_TILL_EXPIRY = "1";


    static {
        SURVEY1_DETAILS.setId(SURVEY1_ID);
        SURVEY1_DETAILS.setDescription(SURVEY1_DESCRIPTION);
        SURVEY1_DETAILS.setVersion(SURVEY1_VERSION);
        SURVEY1_DETAILS.setPoints(SURVEY1_POINTS);
        SURVEY1_DETAILS.setDurationTillExpiry(SURVEY1_TIME_TILL_EXPIRY);

        SURVEY_IDS_MAP.put("enrollmentSurvey", true);
        SURVEY_IDS_MAP.put("Test", true);

        SURVEY_IDS.add("Test");
        SURVEY_IDS.add("enrollmentSurvey");

        SURVEYS.put("enrollmentSurvey", new SurveyDetails(
                "enrollmentSurvey", "1", "first required survey", "1", "10", true
        ));
        SURVEYS.put("Test", new SurveyDetails(
                "Test", "1", "test survey", "1", "1", true
        ));
    }

    @Mock
    private TriibeRepository mTriibeRepository;

    @Mock
    private EditSurveyContract.View mView;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetSurveyIdsCallback> mSurveyIdsCallbackCaptor;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetSurveyCallback> mSurveyCallbackCaptor;

    private EditSurveyPresenter mEditSurveyPresenter;

    @Before
    public void setupEditSurveyPresenter() {
        MockitoAnnotations.initMocks(this);
        mEditSurveyPresenter = new EditSurveyPresenter(mTriibeRepository, mView);
    }

    @Test
    public void loadSurveyIdsFromRepository() {
        // In the activity's onResume it calls loadSurveyIds so it can suggest id's to the user.
        mEditSurveyPresenter.loadSurveyIds(true);
        verify(mView).setProgressIndicator(true);

        // Callback captured. Pass in stubbed id's
        verify(mTriibeRepository).getSurveyIds(anyString(), mSurveyIdsCallbackCaptor.capture());
        mSurveyIdsCallbackCaptor.getValue().onSurveyIdsLoaded(SURVEY_IDS_MAP);

        // Add received surveyIds to the view so they can be suggested when the user types.
        verify(mView).addSurveyIdsToAutoComplete(SURVEY_IDS);

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void getSurveyFromRepositoryAndLoadIntoView() {
        // When the user has entered and existing surveyId, get that survey.
        mEditSurveyPresenter.getSurvey("enrollmentSurvey");
        verify(mView).setProgressIndicator(true);

        // Callback captured. Pass in stubbed id's
        verify(mTriibeRepository).getSurvey(anyString(), mSurveyCallbackCaptor.capture());
        mSurveyCallbackCaptor.getValue().onSurveyLoaded(SURVEYS.get("enrollmentSurvey"));

        // Show the survey details in the view
        verify(mView).showSurveyDetails(SURVEYS.get("enrollmentSurvey"));

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void saveSurveyToRepository() {
        mEditSurveyPresenter.saveSurvey(SURVEY1_ID, SURVEY1_DESCRIPTION, SURVEY1_VERSION,
                SURVEY1_POINTS, SURVEY1_TIME_TILL_EXPIRY);

        verify(mView).setProgressIndicator(true);

        verify(mTriibeRepository).saveSurvey(anyString(), any(SurveyDetails.class));

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void deleteSurvey() {
        mEditSurveyPresenter.deleteSurvey(SURVEY1_ID);
        verify(mTriibeRepository).deleteSurvey(anyString());
    }

    @Test
    public void editQuestion() {
        mEditSurveyPresenter.editQuestion();
        verify(mView).showEditQuestion();
    }

    @Test
    public void editTrigger() {
        mEditSurveyPresenter.editTrigger();
        verify(mView).showEditTrigger();
    }
}
