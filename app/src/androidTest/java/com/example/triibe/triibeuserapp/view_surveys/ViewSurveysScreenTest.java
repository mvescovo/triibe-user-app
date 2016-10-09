package com.example.triibe.triibeuserapp.view_surveys;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.example.triibe.triibeuserapp.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static org.hamcrest.Matchers.allOf;

/**
 * @author michael.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewSurveysScreenTest {

    private static String TEST_USER_ID = "EspressoTestUser";
    private static String SURVEY2_DESCRIPTION = "Espresso test survey";
    private static String SURVEY2_POINTS = "100";
    private static String QUESTION_TITLE = "Test";
    private static String EDIT_SURVEY_TITLE = "Survey Details";

    @Rule
    public IntentsTestRule<ViewSurveysActivity> mViewSurveysIntentsTestRule =
            new IntentsTestRule<>(ViewSurveysActivity.class, true, false);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mViewSurveysIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Before
    public void setupActivity() {
        Intent intent = new Intent();
        intent.putExtra(ViewSurveysActivity.EXTRA_USER_ID, TEST_USER_ID);
        mViewSurveysIntentsTestRule.launchActivity(intent);
    }

    @Test
    public void loadSurveys() {
        // Check surveys are in recycler view.
        onView(withId(R.id.view_surveys_recycler_view)).perform(scrollTo(hasDescendant(withText(SURVEY2_DESCRIPTION))));
        onView(allOf(withId(R.id.survey_description), withText(SURVEY2_DESCRIPTION))).check(matches(isDisplayed()));
    }

    @Test
    public void clickingSurveyOpensQuestionUi() throws Exception {
        // Click on enrollment survey.
        onView(withId(R.id.view_surveys_recycler_view)).perform(scrollTo(hasDescendant(withText(SURVEY2_DESCRIPTION))));
        onView(withId(R.id.view_surveys_recycler_view)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(SURVEY2_DESCRIPTION)), click()));

        // Check the question logo shows up.
        onView(withId(R.id.title)).check(matches(withText(QUESTION_TITLE)));
    }

    @Test
    public void clickingEditSurveyFabOpensEditSurveyUi() throws Exception {
        onView(withId(R.id.modify_survey_fab)).perform(click());

        // Check if the edit survey screen is displayed
        onView(withId(R.id.edit_survey_title)).check(matches(withText(EDIT_SURVEY_TITLE)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA RV with text " + itemText);
            }
        };
    }
}
