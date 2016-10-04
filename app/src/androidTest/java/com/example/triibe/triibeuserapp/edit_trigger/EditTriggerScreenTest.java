package com.example.triibe.triibeuserapp.edit_trigger;

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

public class EditTriggerScreenTest {

    private static String TEST_SURVEY_ID = "Test";
    private static String TEST_TRIGGER_ID = "t1";
    private static String TEST_TRIGGER_LAT = "10.0";
    private static String TEST_TRIGGER_LON = "10.0";

    @Rule
    public IntentsTestRule<EditTriggerActivity> mEditTriggerIntentsTestRule =
            new IntentsTestRule<>(EditTriggerActivity.class, true, false);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mEditTriggerIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Before
    public void setupActivity() {
        Intent intent = new Intent();
        intent.putExtra(EditTriggerActivity.EXTRA_SURVEY_ID, TEST_SURVEY_ID);
        mEditTriggerIntentsTestRule.launchActivity(intent);
    }

    @Test
    public void enteringAnExistingTriggerIdLoadsSavedTriggerDetails() {
        onView(withId(R.id.trigger_id)).perform(typeText(TEST_TRIGGER_ID));
        onView(withId(R.id.lat)).check(matches(withText(TEST_TRIGGER_LAT)));
        onView(withId(R.id.lon)).check(matches(withText(TEST_TRIGGER_LON)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
