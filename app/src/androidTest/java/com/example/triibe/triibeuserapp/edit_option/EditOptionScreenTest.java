package com.example.triibe.triibeuserapp.edit_option;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.example.triibe.triibeuserapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author michael.
 */

public class EditOptionScreenTest {

//    private static String TEST_USER_ID = "TestUserId";
    private static String TEST_SURVEY_ID = "Test";
    private static String TEST_QUESTION_ID = "q1";
    private static String TEST_OPTION_ID = "o1";
    private static String TEST_OPTION_PHRASE = "test";
    private static String TEST_OPTION_EXTRA_INPUT_TYPE = "test";
    private static String TEST_OPTION_EXTRA_INPUT_HINT = "test";

    @Rule
    public IntentsTestRule<EditOptionActivity> mEditOptionIntentsTestRule =
            new IntentsTestRule<>(EditOptionActivity.class, true, false);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mEditOptionIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Before
    public void setupActivity() {
        Intent intent = new Intent();
        intent.putExtra(EditOptionActivity.EXTRA_SURVEY_ID, TEST_SURVEY_ID);
        intent.putExtra(EditOptionActivity.EXTRA_QUESTION_ID, TEST_QUESTION_ID);
        mEditOptionIntentsTestRule.launchActivity(intent);
    }

    @Test
    public void enteringAnExistingOptionIdLoadsSavedOptionDetails() {
        onView(withId(R.id.option_id)).perform(typeText(TEST_OPTION_ID));
        onView(withId(R.id.option_phrase)).check(matches(withText(TEST_OPTION_PHRASE)));
    }

    @Test
    public void enteringAnExistingOptionIdLoadsSavedOptionDetailsWhenHasExtraInput() {
        onView(withId(R.id.option_id)).perform(typeText(TEST_OPTION_ID));
        onView(withId(R.id.option_phrase)).check(matches(withText(TEST_OPTION_PHRASE)));
        onView(withId(R.id.option_extra_input_type)).check(matches(withText(TEST_OPTION_EXTRA_INPUT_TYPE)));
        onView(withId(R.id.option_extra_input_hint)).check(matches(withText(TEST_OPTION_EXTRA_INPUT_HINT)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
