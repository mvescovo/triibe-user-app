package com.example.triibe.triibeuserapp.edit_question;

import com.example.triibe.triibeuserapp.data.QuestionDetails;
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

public class EditQuestionPresenterTest {

    private static Map<String, Boolean> QUESTION_IDS_MAP = Maps.newHashMap();
    private static List<String> QUESTION_IDS = Lists.newArrayList();
    private static String SURVEY_ID = "Test";
    private static String QUESTION_ID = "q1";
    private static QuestionDetails QUESITON_DETAILS = new QuestionDetails();

    @Mock
    TriibeRepository mTriibeRepository;

    @Mock
    EditQuestionContract.View mView;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetQuestionIdsCallback> mQuestionIdsCallbackCaptor;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetQuestionCallback> mQuestionCallbackCaptor;

    private EditQuestionPresenter mEditQuestionPresenter;

    @Before
    public void setUpEditQuestionPresenter() {
        MockitoAnnotations.initMocks(this);
        mEditQuestionPresenter = new EditQuestionPresenter(mTriibeRepository, mView);
    }

    @Test
    public void loadQuestionIdsFromRepo() {
        // In the activity's onResume it calls getQuestionIds so it can suggest id's to the user.
        mEditQuestionPresenter.getQuestionIds(SURVEY_ID, true);
        verify(mView).setProgressIndicator(true);

        // Callback captured. Pass in stubbed id's
        verify(mTriibeRepository).getQuestionIds(anyString(), mQuestionIdsCallbackCaptor.capture());
        mQuestionIdsCallbackCaptor.getValue().onQuestionIdsLoaded(QUESTION_IDS_MAP);

        // Add received questionIds to the view so they can be suggested when the user types.
        verify(mView).addQuestionIdsToAutoComplete(QUESTION_IDS);

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void getQuestionFromRepo() {
        // When the user has entered and existing questionId, get that question.
        mEditQuestionPresenter.getQuestion("q1");
        verify(mView).setProgressIndicator(true);

        // Callback captured. Pass in stubbed id's
        verify(mTriibeRepository).getQuestion(anyString(), anyString(), mQuestionCallbackCaptor.capture());
        mQuestionCallbackCaptor.getValue().onQuestionLoaded(QUESITON_DETAILS);

        // Show the question details in the view
        verify(mView).showQuestionDetails(QUESITON_DETAILS);

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void saveQuestionToRepo() {
        mEditQuestionPresenter.saveQuestion(QUESITON_DETAILS);

        verify(mView).setProgressIndicator(true);

        verify(mTriibeRepository).saveQuestion(anyString(), anyString(), any(QuestionDetails.class));

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void deleteQuestionFromRepo() {
        mEditQuestionPresenter.deleteQuestion(QUESTION_ID);
        verify(mTriibeRepository).deleteQuestion(anyString(), anyString());
    }

    @Test
    public void editOption() {
        mEditQuestionPresenter.editOption();
        verify(mView).showEditOption();
    }
}
