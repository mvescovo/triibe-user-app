package com.example.triibe.triibeuserapp.edit_option;

import com.example.triibe.triibeuserapp.data.Option;
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

public class EditOptionPresenterTest {

    private static Map<String, Boolean> OPTION_IDS_MAP = Maps.newHashMap();
    private static List<String> OPTION_IDS = Lists.newArrayList();
    private static String SURVEY_ID = "Test";
    private static String QUESTION_ID = "q1";
    private static String OPTION_ID = "o1";
    private static Option OPTION = new Option();

    @Mock
    TriibeRepository mTriibeRepository;

    @Mock
    EditOptionContract.View mView;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetOptionIdsCallback> mOptionIdsCallbackCaptor;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetOptionCallback> mOptionCallbackCaptor;

    private EditOptionPresenter mEditOptionPresenter;

    @Before
    public void setUpEditOptionPresenter() {
        MockitoAnnotations.initMocks(this);
        mEditOptionPresenter = new EditOptionPresenter(mTriibeRepository, mView);
    }

    @Test
    public void loadOptionIdsFromRepo() {
        // In the activity's onResume it calls getOptionIds so it can suggest id's to the user.
        mEditOptionPresenter.getOptionIds(SURVEY_ID, QUESTION_ID, true);
        verify(mView).setProgressIndicator(true);

        // Callback captured. Pass in stubbed id's
        verify(mTriibeRepository).getOptionIds(anyString(), mOptionIdsCallbackCaptor.capture());
        mOptionIdsCallbackCaptor.getValue().onOptionIdsLoaded(OPTION_IDS_MAP);

        // Add received optionIds to the view so they can be suggested when the user types.
        verify(mView).addOptionIdsToAutoComplete(OPTION_IDS);

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void getOptionFromRepo() {
        // When the user has entered and existing optionId, get that option.
        mEditOptionPresenter.getOption("o1");
        verify(mView).setProgressIndicator(true);

        // Callback captured. Pass in stubbed option
        verify(mTriibeRepository).getOption(anyString(), anyString(), anyString(), mOptionCallbackCaptor.capture());
        mOptionCallbackCaptor.getValue().onOptionLoaded(OPTION);

        // Show the option in the view
        verify(mView).showOption(OPTION);

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void saveOptionToRepo() {
        mEditOptionPresenter.saveOption(OPTION);

        verify(mView).setProgressIndicator(true);

        verify(mTriibeRepository).saveOption(anyString(), anyString(), anyString(), any(Option.class));

        verify(mView).setProgressIndicator(false);
    }

    @Test
    public void deleteOptionFromRepo() {
        mEditOptionPresenter.deleteOption(OPTION_ID);
        verify(mTriibeRepository).deleteOption(anyString(), anyString(), anyString());
    }
}
