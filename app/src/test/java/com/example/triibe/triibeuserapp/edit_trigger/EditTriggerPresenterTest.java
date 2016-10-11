package com.example.triibe.triibeuserapp.edit_trigger;

import com.example.triibe.triibeuserapp.data.SurveyTrigger;
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

public class EditTriggerPresenterTest {

    private static Map<String, Boolean> TRIGGER_IDS_MAP = Maps.newHashMap();
    private static List<String> TRIGGER_IDS = Lists.newArrayList();
    private static String SURVEY_ID = "Test";
    private static String TRIGGER_ID = "t1";
    private static SurveyTrigger TRIGGER = new SurveyTrigger();

    @Mock
    TriibeRepository mTriibeRepository;

    @Mock
    EditTriggerContract.View mView;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetTriggerIdsCallback> mTriggerIdsCallbackCaptor;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetTriggerCallback> mTriggerCallbackCaptor;

    private EditTriggerPresenter mEditTriggerPresenter;

    @Before
    public void setUpEditTriggerPresenter() {
        MockitoAnnotations.initMocks(this);
        mEditTriggerPresenter = new EditTriggerPresenter(mTriibeRepository, mView);
    }

    @Test
    public void loadTriggerIdsFromRepo() {
        // In the activity's onResume it calls getTriggerIds so it can suggest id's to the user.
        mEditTriggerPresenter.getTriggerIds(SURVEY_ID, true);
        verify(mView).setProgressIndicator(true);

        // Callback captured. Pass in stubbed id's
        verify(mTriibeRepository).getTriggerIds(anyString(), mTriggerIdsCallbackCaptor.capture());
        mTriggerIdsCallbackCaptor.getValue().onTriggerIdsLoaded(TRIGGER_IDS_MAP);

        // Add received triggerIds to the view so they can be suggested when the user types.
        verify(mView).addTriggerIdsToAutoComplete(TRIGGER_IDS);

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void getTriggerFromRepo() {
        // When the user has entered and existing triggerId, get that trigger.
        mEditTriggerPresenter.getTrigger("t1");
        verify(mView).setProgressIndicator(true);

        // Callback captured. Pass in stubbed trigger
        verify(mTriibeRepository).getTrigger(anyString(), anyString(), mTriggerCallbackCaptor.capture());
        mTriggerCallbackCaptor.getValue().onTriggerLoaded(TRIGGER);

        // Show the trigger in the view
        verify(mView).showTrigger(TRIGGER);

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void saveTriggerToRepo() {
        mEditTriggerPresenter.saveTrigger(TRIGGER);

        verify(mView).setProgressIndicator(true);

        verify(mTriibeRepository).saveTrigger(anyString(), anyString(), any(SurveyTrigger.class));

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void deleteTriggerFromRepo() {
        mEditTriggerPresenter.deleteTrigger(TRIGGER_ID);
        verify(mTriibeRepository).deleteTrigger(anyString(), anyString());
    }
}
