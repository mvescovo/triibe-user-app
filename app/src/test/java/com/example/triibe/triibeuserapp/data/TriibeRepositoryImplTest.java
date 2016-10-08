package com.example.triibe.triibeuserapp.data;

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
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author michael.
 */

public class TriibeRepositoryImplTest {

    private static String USER_ID = "testUser";

    // Surveys
    private static Map<String, Boolean> SURVEY_IDS_MAP = Maps.newHashMap();
    private static List<String> SURVEY_IDS = Lists.newArrayList();
    private static Map<String, SurveyDetails> SURVEYS = Maps.newHashMap();
    private static String PATH = "fakePath";


    // Questions
    private static Map<String, Question> QUESTIONS = Maps.newHashMap();
    private static String QUESTION1_ID = "q1";
    private static QuestionDetails QUESTION1_DETAILS = new QuestionDetails();


    // Options
    private static Map<String, Option> OPTIONS = Maps.newHashMap();
    private static String OPTION1_ID = "o1";
    private static Option OPTION = new Option();


    // Triggers


    // Answers
    private static Map<String, Answer> ANSWERS = Maps.newHashMap();
    private static Answer ANSWER1 = new Answer();


    static {
        SURVEY_IDS_MAP.put("enrollmentSurvey", true);
        SURVEY_IDS_MAP.put("Test", true);

        SURVEY_IDS.add("enrollmentSurvey");
        SURVEY_IDS.add("Test");

        SURVEYS.put("enrollmentSurvey", new SurveyDetails(
                "s1", "Enrollment Survey", "100", "2", true
        ));
        SURVEYS.put("Test", new SurveyDetails(
                "s2", "Test Survey", "10", "0", true
        ));
    }


    private TriibeRepositoryImpl mTriibeRepository;

    @Mock
    private TriibeServiceApiImpl mTriibeServiceApi;


    // Surveys
    @Mock
    private TriibeRepository.GetSurveyIdsCallback mGetSurveyIdsCallback;

    @Captor
    private ArgumentCaptor<TriibeServiceApi.GetSurveyIdsCallback> mSurveyIdsCallbackArgumentCaptor;

    @Mock
    private TriibeRepository.GetSurveyCallback mGetSurveyCallback;

    @Captor
    private ArgumentCaptor<TriibeServiceApi.GetSurveyCallback> mSurveyCallbackArgumentCaptor;


    // Questions
    @Mock
    private TriibeRepository.GetQuestionsCallback mGetQuestionsCallback;

    @Captor
    private ArgumentCaptor<TriibeServiceApi.GetQuestionsCallback> mQuestionsCallbackArgumentCaptor;

    @Mock
    private TriibeRepository.GetQuestionCallback mGetQuestionCallback;

    @Captor
    private ArgumentCaptor<TriibeServiceApi.GetQuestionCallback> mQuestionCallbackArgumentCaptor;


    // Options
    @Mock
    private TriibeRepository.GetOptionsCallback mGetOptionsCallback;

    @Captor
    private ArgumentCaptor<TriibeServiceApi.GetOptionsCallback> mOptionsCallbackArgumentCaptor;

    @Mock
    private TriibeRepository.GetOptionCallback mGetOptionCallback;

    @Captor
    private ArgumentCaptor<TriibeServiceApi.GetOptionCallback> mOptionCallbackArgumentCaptor;


    // Triggers


    // Answers
    @Mock
    private TriibeRepository.GetAnswersCallback mGetAnswersCallback;

    @Captor
    private ArgumentCaptor<TriibeServiceApi.GetAnswersCallback> mAnswersCallbackArgumentCaptor;

    @Mock
    private TriibeRepository.GetAnswerCallback mGetAnswerCallback;

    @Captor
    private ArgumentCaptor<TriibeServiceApi.GetAnswerCallback> mAnswerCallbackArgumentCaptor;

    @Before
    public void setupTriibeRepository() {
        MockitoAnnotations.initMocks(this);
        mTriibeRepository = new TriibeRepositoryImpl(mTriibeServiceApi);
    }


    // Surveys
    @Test
    public void getSurveyIdsAndCacheAfterFirstApiCall() {
        // Call the repo once which should call the service API when the cache is null.
        mTriibeRepository.getSurveyIds(PATH, mGetSurveyIdsCallback);

        // Call the service API and send the callback which is then cached in the repo.
        verify(mTriibeServiceApi).getSurveyIds(anyString(), mSurveyIdsCallbackArgumentCaptor.capture());
        mSurveyIdsCallbackArgumentCaptor.getValue().onSurveyIdsLoaded(SURVEY_IDS_MAP);

        // Call repo again and confirm that the total times the service API was called is 1.
        // This confirms the cache was used the second time.
        mTriibeRepository.getSurveyIds(PATH, mGetSurveyIdsCallback);
        verify(mTriibeServiceApi, times(1)).getSurveyIds(anyString(), any(TriibeServiceApi.GetSurveyIdsCallback.class));
    }

    @Test
    public void invalidatingSurveyIdsCacheDoesNotCallTheServiceApi() {
        // Call the repo once which should call the service API when the cache is null.
        mTriibeRepository.getSurveyIds(PATH, mGetSurveyIdsCallback);

        // Call the service API and send the callback which is then cached in the repo.
        verify(mTriibeServiceApi).getSurveyIds(anyString(), mSurveyIdsCallbackArgumentCaptor.capture());
        mSurveyIdsCallbackArgumentCaptor.getValue().onSurveyIdsLoaded(SURVEY_IDS_MAP);

        // Call the repo again which should use the cache rather than the service API.
        mTriibeRepository.getSurveyIds(PATH, mGetSurveyIdsCallback);

        // Clear cache to force the next request to be a service API call.
        mTriibeRepository.refreshSurveyIds();

        // Call repo a third time and confirm the service API was called due to emptied cache.
        // Two total calls.
        mTriibeRepository.getSurveyIds(PATH, mGetSurveyIdsCallback);
        verify(mTriibeServiceApi, times(2)).getSurveyIds(anyString(), any(TriibeServiceApi.GetSurveyIdsCallback.class));
    }

    @Test
    public void saveSurveyIdsToRepo() {
        mTriibeRepository.mCachedSurveyIds = SURVEY_IDS_MAP;
        mTriibeRepository.saveSurveyIds(anyString(), anyMapOf(String.class, Boolean.class));
        verify(mTriibeServiceApi).saveSurveyIds(anyString(), anyMapOf(String.class, Boolean.class));
    }

    @Test
    public void getSurvey() {
        mTriibeRepository.getSurvey(SURVEY_IDS.get(0), mGetSurveyCallback);
        verify(mTriibeServiceApi).getSurvey(anyString(), any(TriibeServiceApi.GetSurveyCallback.class));
    }

    @Test
    public void saveSurvey() {
        mTriibeRepository.saveSurvey(SURVEY_IDS.get(0), SURVEYS.get(SURVEY_IDS.get(0)));
        verify(mTriibeServiceApi).saveSurvey(anyString(), any(SurveyDetails.class));
    }

    @Test
    public void deleteSurvey() {
        mTriibeRepository.deleteSurvey(SURVEY_IDS.get(0));
        verify(mTriibeServiceApi).deleteSurvey(anyString());
    }


    // Questions
    @Test
    public void getQuestionsAndCacheAfterFirstApiCall() {
        // Call the repo once which should call the service API when the cache is null.
        mTriibeRepository.getQuestions("enrollmentSurvey", mGetQuestionsCallback);

        // Call the service API and send the callback which is then cached in the repo.
        verify(mTriibeServiceApi).getQuestions(anyString(), mQuestionsCallbackArgumentCaptor.capture());
        mQuestionsCallbackArgumentCaptor.getValue().onQuestionsLoaded(QUESTIONS);

        // Call repo again and confirm that the total times the service API was called is 1.
        // This confirms the cache was used the second time.
        mTriibeRepository.getQuestions("enrollmentSurvey", mGetQuestionsCallback);
        verify(mTriibeServiceApi, times(1)).getQuestions(anyString(), any(TriibeServiceApi.GetQuestionsCallback.class));
    }

    @Test
    public void invalidatingQuestionsCacheDoesNotCallTheServiceApi() {
        // Call the repo once which should call the service API when the cache is null.
        mTriibeRepository.getQuestions("enrollmentSurvey", mGetQuestionsCallback);

        // Call the service API and send the callback which is then cached in the repo.
        verify(mTriibeServiceApi).getQuestions(anyString(), mQuestionsCallbackArgumentCaptor.capture());
        mQuestionsCallbackArgumentCaptor.getValue().onQuestionsLoaded(QUESTIONS);

        // Call the repo again which should use the cache rather than the service API.
        mTriibeRepository.getQuestions("enrollmentSurvey", mGetQuestionsCallback);

        // Clear cache to force the next request to be a service API call.
        mTriibeRepository.refreshQuestions();

        // Call repo a third time and confirm the service API was called due to emptied cache.
        // Two total calls.
        mTriibeRepository.getQuestions("enrollmentSurvey", mGetQuestionsCallback);
        verify(mTriibeServiceApi, times(2)).getQuestions(anyString(), any(TriibeServiceApi.GetQuestionsCallback.class));
    }

    @Test
    public void getQuestion() {
        mTriibeRepository.getQuestion(SURVEY_IDS.get(0), QUESTION1_ID, mGetQuestionCallback);
        verify(mTriibeServiceApi).getQuestion(anyString(), anyString(), any(TriibeServiceApi.GetQuestionCallback.class));
    }

    @Test
    public void saveQuestionToRepo() {
        mTriibeRepository.saveQuestion(SURVEY_IDS.get(0), QUESTION1_ID, QUESTION1_DETAILS);
        verify(mTriibeServiceApi).saveQuestion(anyString(), anyString(), any(QuestionDetails.class));
    }

    @Test
    public void deleteQuestion() {
        mTriibeRepository.deleteQuestion(SURVEY_IDS.get(0), QUESTION1_ID);
        verify(mTriibeServiceApi).deleteQuestion(anyString(), anyString());
    }


    // Options
    @Test
    public void getOptionsAndCacheAfterFirstApiCall() {
        // Call the repo once which should call the service API when the cache is null.
        mTriibeRepository.getOptions("enrollmentSurvey", "q1", mGetOptionsCallback);

        // Call the service API and send the callback which is then cached in the repo.
        verify(mTriibeServiceApi).getOptions(anyString(), anyString(), mOptionsCallbackArgumentCaptor.capture());
        mOptionsCallbackArgumentCaptor.getValue().onOptionsLoaded(OPTIONS);

        // Call repo again and confirm that the total times the service API was called is 1.
        // This confirms the cache was used the second time.
        mTriibeRepository.getOptions("enrollmentSurvey", "q1", mGetOptionsCallback);
        verify(mTriibeServiceApi, times(1)).getOptions(anyString(), anyString(), any(TriibeServiceApi.GetOptionsCallback.class));
    }

    @Test
    public void invalidatingOptionsCacheDoesNotCallTheServiceApi() {
        // Call the repo once which should call the service API when the cache is null.
        mTriibeRepository.getOptions("enrollmentSurvey", "q1", mGetOptionsCallback);

        // Call the service API and send the callback which is then cached in the repo.
        verify(mTriibeServiceApi).getOptions(anyString(), anyString(), mOptionsCallbackArgumentCaptor.capture());
        mOptionsCallbackArgumentCaptor.getValue().onOptionsLoaded(OPTIONS);

        // Call the repo again which should use the cache rather than the service API.
        mTriibeRepository.getOptions("enrollmentSurvey", "q1", mGetOptionsCallback);

        // Clear cache to force the next request to be a service API call.
        mTriibeRepository.refreshOptions();

        // Call repo a third time and confirm the service API was called due to emptied cache.
        // Two total calls.
        mTriibeRepository.getOptions("enrollmentSurvey", "q1", mGetOptionsCallback);
        verify(mTriibeServiceApi, times(2)).getOptions(anyString(), anyString(), any(TriibeServiceApi.GetOptionsCallback.class));
    }

    @Test
    public void getOption() {
        mTriibeRepository.getOption(SURVEY_IDS.get(0), QUESTION1_ID, OPTION1_ID, mGetOptionCallback);
        verify(mTriibeServiceApi).getOption(anyString(), anyString(), anyString(), any(TriibeServiceApi.GetOptionCallback.class));
    }

    @Test
    public void saveOptionToRepo() {
        mTriibeRepository.saveOption(SURVEY_IDS.get(0), QUESTION1_ID, OPTION1_ID, OPTION);
        verify(mTriibeServiceApi).saveOption(anyString(), anyString(), anyString(), any(Option.class));
    }

    @Test
    public void deleteOption() {
        mTriibeRepository.deleteOption(SURVEY_IDS.get(0), QUESTION1_ID, OPTION1_ID);
        verify(mTriibeServiceApi).deleteOption(anyString(), anyString(), anyString());
    }

    // Triggers


    // Answers
    @Test
    public void getAnswersAndCacheAfterFirstApiCall() {
        // Call the repo once which should call the service API when the cache is null.
        mTriibeRepository.getAnswers("enrollmentSurvey", USER_ID, mGetAnswersCallback);

        // Call the service API and send the callback which is then cached in the repo.
        verify(mTriibeServiceApi).getAnswers(anyString(), anyString(), mAnswersCallbackArgumentCaptor.capture());
        mAnswersCallbackArgumentCaptor.getValue().onAnswersLoaded(ANSWERS);

        // Call repo again and confirm that the total times the service API was called is 1.
        // This confirms the cache was used the second time.
        mTriibeRepository.getAnswers("enrollmentSurvey", USER_ID, mGetAnswersCallback);
        verify(mTriibeServiceApi, times(1)).getAnswers(anyString(), anyString(), any(TriibeServiceApi.GetAnswersCallback.class));
    }

    @Test
    public void invalidatingAnswersCacheDoesNotCallTheServiceApi() {
        // Call the repo once which should call the service API when the cache is null.
        mTriibeRepository.getAnswers("enrollmentSurvey", USER_ID, mGetAnswersCallback);

        // Call the service API and send the callback which is then cached in the repo.
        verify(mTriibeServiceApi).getAnswers(anyString(), anyString(), mAnswersCallbackArgumentCaptor.capture());
        mAnswersCallbackArgumentCaptor.getValue().onAnswersLoaded(ANSWERS);

        // Call the repo again which should use the cache rather than the service API.
        mTriibeRepository.getAnswers("enrollmentSurvey", USER_ID, mGetAnswersCallback);

        // Clear cache to force the next request to be a service API call.
        mTriibeRepository.refreshAnswers();

        // Call repo a third time and confirm the service API was called due to emptied cache.
        // Two total calls.
        mTriibeRepository.getAnswers("enrollmentSurvey", USER_ID, mGetAnswersCallback);
        verify(mTriibeServiceApi, times(2)).getAnswers(anyString(), anyString(), any(TriibeServiceApi.GetAnswersCallback.class));
    }

    @Test
    public void getAnswer() {
        mTriibeRepository.getAnswer(SURVEY_IDS.get(0), QUESTION1_ID, mGetAnswerCallback);
        verify(mTriibeServiceApi).getAnswer(anyString(), anyString(), any(TriibeServiceApi.GetAnswerCallback.class));
    }

    @Test
    public void saveAnswerToRepo() {
        mTriibeRepository.saveAnswer(SURVEY_IDS.get(0), USER_ID, QUESTION1_ID, ANSWER1);
        verify(mTriibeServiceApi).saveAnswer(anyString(), anyString(), anyString(), any(Answer.class));
    }

    // Users
    @Test public void addUserSurveyToRepo() {
        mTriibeRepository.addUserSurvey(USER_ID, SURVEY_IDS.get(0));
        verify(mTriibeServiceApi).addUserSurvey(anyString(), anyString());
    }

    @Test public void removeUserSurveyToRepo() {
        mTriibeRepository.removeUserSurvey(USER_ID, SURVEY_IDS.get(0));
        verify(mTriibeServiceApi).removeUserSurvey(anyString(), anyString());
    }
}
