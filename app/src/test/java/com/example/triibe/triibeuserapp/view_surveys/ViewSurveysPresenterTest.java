package com.example.triibe.triibeuserapp.view_surveys;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Unit tests for the implementation of {@link ViewSurveysPresenter}
 *
 * @author michael.
 */

public class ViewSurveysPresenterTest {

    private static Map<String, Boolean> SURVEY_IDS_MAP = Maps.newHashMap();
    private static List<String> SURVEY_IDS = Lists.newArrayList();
    private static Map<String, SurveyDetails> SURVEYS = Maps.newHashMap();

    static {
        SURVEY_IDS_MAP.put("enrollmentSurvey", true);
        SURVEY_IDS_MAP.put("Test", true);

        SURVEY_IDS.add("enrollmentSurvey");
        SURVEY_IDS.add("Test");

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
    private ViewSurveysContract.View mView;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetSurveyIdsCallback> mSurveyIdsCallbackCaptor;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetSurveyCallback> mSurveyCallbackCaptor;

    private ViewSurveysPresenter mViewSurveysPresenter;

    @Before
    public void setupViewSurveyDetailsPresenter() {
        MockitoAnnotations.initMocks(this);
        mViewSurveysPresenter = new ViewSurveysPresenter(mTriibeRepository, mView);
    }

    @Test
    public void loadSurveysFromRepositoryAndLoadIntoView() {
        HashMap<String, SurveyDetails> surveys = new HashMap<>();

        mViewSurveysPresenter.loadSurveys(anyString(), true);
        verify(mView).setProgressIndicator(true);

        verify(mTriibeRepository).getSurveyIds(anyString(), mSurveyIdsCallbackCaptor.capture());
        mSurveyIdsCallbackCaptor.getValue().onSurveyIdsLoaded(SURVEY_IDS_MAP);

        // The presenter should call getSurvey for each surveyId returned in getUserSurveyIds
        verify(mTriibeRepository, times(SURVEY_IDS_MAP.size())).getSurvey(anyString(), mSurveyCallbackCaptor.capture());
        List<TriibeRepository.GetSurveyCallback> callbacks = mSurveyCallbackCaptor.getAllValues();
        for (int i = 0; i < callbacks.size(); i++) {
            callbacks.get(i).onSurveyLoaded(SURVEYS.get(SURVEY_IDS.get(i)));
            surveys.put("" + i, SURVEYS.get(SURVEY_IDS.get(i)));
        }

        verify(mView).showSurveys(surveys);
        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void clickOnSurvey_ShowsQuestionUi() {
        mViewSurveysPresenter.openSurveyQuestions("enrollmentSurvey");
        verify(mView).showQuestionUi("enrollmentSurvey", "q1");
    }
}
