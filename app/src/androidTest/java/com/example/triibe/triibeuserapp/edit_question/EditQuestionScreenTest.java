package com.example.triibe.triibeuserapp.edit_question;

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

public class EditQuestionScreenTest {

    // Passed into intent (use actual id's rather than the user entered version without prefix).
    private static String TEST_SURVEY_ID = "s2";

    // Passed in by use (don't include prefix for id's).
    private static String TEST_QUESTION_ID = "1";
    private static String TEST_QUESTION_TYPE = "radio";
    private static String TEST_QUESTION_IMAGE_URL = "Test";
    private static String TEST_QUESTION_TITLE = "Question 1";
    private static String TEST_QUESTION_INTRO = "Test";
    private static String TEST_QUESTION_PHRASE = "Test";
    private static String TEST_QUESTION_INTRO_LINK_KEY = "Test";
    private static String TEST_QUESTION_INTRO_LINK_URL = "Test";
    private static String TEST_QUESTION_REQUIRED_PHRASE = "Test";
    private static String TEST_QUESTION_INCORRECT_ANSWER_PHRASE = "Test";

    @Rule
    public IntentsTestRule<EditQuestionActivity> mEditQuestionIntentsTestRule =
            new IntentsTestRule<>(EditQuestionActivity.class, true, false);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mEditQuestionIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Before
    public void setupActivity() {
        Intent intent = new Intent();
        intent.putExtra(EditQuestionActivity.EXTRA_SURVEY_ID, TEST_SURVEY_ID);
        mEditQuestionIntentsTestRule.launchActivity(intent);
    }

    @Test
    public void enteringAnExistingQuestionIdLoadsSavedQuestionDetails() {
        onView(withId(R.id.question_id)).perform(typeText(TEST_QUESTION_ID));
        onView(withId(R.id.question_title)).check(matches(withText(TEST_QUESTION_TITLE)));
        onView(withId(R.id.question_intro)).check(matches(withText(TEST_QUESTION_INTRO)));
        onView(withId(R.id.question_image_url)).check(matches(withText(TEST_QUESTION_IMAGE_URL)));
    }

    // Can't get this test to work as it has trouble clicking on the button within the bottomsheet.
    // If I click a button in the toolbar instead then it's fine but that's not where the button is.
    // Low priority.

//    @Test
//    public void validateIntentSentToEditOptionPackage() {
//        // Click to edit option.
//        onView(withId(R.id.more_options)).perform(click());
//        onView(withId(R.id.edit_option_button)).perform(click());
//
//        // Using a canned RecordedIntentMatcher to validate that an intent resolving
//        // to the "EditOption" activity has been sent.
//        intended(toPackage("com.example.triibe.triibeuserapp"));
//    }

    // For the same reason this test won't work until I find a solution. Also low priority.

//    @Test
//    public void activityResultFromEditOptionIsHandledProperly() {
    // In here we could check the correct snakbar message is shown depending on the result from the options activity.
    // e.g.       onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("successfully_saved_question")))
//                .check(matches(isDisplayed()));
//    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
