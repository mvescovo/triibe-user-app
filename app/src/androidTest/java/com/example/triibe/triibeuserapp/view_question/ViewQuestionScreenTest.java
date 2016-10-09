package com.example.triibe.triibeuserapp.view_question;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.triibe.triibeuserapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author michael.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewQuestionScreenTest {

    // Passed into intent (use actual id's rather than the user entered version without prefix).
    private static String SURVEY_ID = "s2";
    private static String QUESTION1_ID = "q1";
    private static String QUESTION2_ID = "q2";
    private static String QUESTION3_ID = "q3";
    private static String QUESTION4_ID = "q4";
    private static String QUESTION5_ID = "q5";
    private static String QUESTION6_ID = "q6";

    // Passed in by use (don't include prefix for id's).
    private static String QUESTION1_TITLE = "Question 1";
    private static String QUESTION2_TITLE = "Question 2";
    private static String QUESTION3_TITLE = "Question 3";
    private static String QUESTION4_TITLE = "Question 4";
    private static String QUESTION5_TITLE = "Question 5";
    private static String QUESTION6_TITLE = "Question 6";
    private static String USER_ID = "EspressoTestUser";
    private static String NUM_PROTECTED_QUESTIONS = "0";

    @Rule
    public IntentsTestRule<ViewQuestionActivity> mViewQuestionIntentsTestRule =
            new IntentsTestRule<>(ViewQuestionActivity.class, true, false);

    private IdlingResource mIdlingResource;

    @Test
    public void loadRadioQuestion1() {
        // Setup the activity for each test so we can specify the question number in the intent.
        Intent intent = new Intent();
        intent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, SURVEY_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, USER_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_QUESTION_ID, QUESTION1_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_NUM_PROTECTED_QUESTIONS, NUM_PROTECTED_QUESTIONS);
        mViewQuestionIntentsTestRule.launchActivity(intent);
        mIdlingResource = mViewQuestionIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

        // Do tests.
        onView(withId(R.id.title)).check(matches(withText(QUESTION1_TITLE)));
    }

    @Test
    public void loadRadioQuestion2() {
        // Setup the activity for each test so we can specify the question number in the intent.
        Intent intent = new Intent();
        intent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, SURVEY_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, USER_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_QUESTION_ID, QUESTION2_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_NUM_PROTECTED_QUESTIONS, NUM_PROTECTED_QUESTIONS);
        mViewQuestionIntentsTestRule.launchActivity(intent);
        mIdlingResource = mViewQuestionIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

        // Do tests.
        onView(withId(R.id.title)).check(matches(withText(QUESTION2_TITLE)));
    }

    @Test
    public void loadCheckboxQuestion3() {
        // Setup the activity for each test so we can specify the question number in the intent.
        Intent intent = new Intent();
        intent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, SURVEY_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, USER_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_QUESTION_ID, QUESTION3_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_NUM_PROTECTED_QUESTIONS, NUM_PROTECTED_QUESTIONS);
        mViewQuestionIntentsTestRule.launchActivity(intent);
        mIdlingResource = mViewQuestionIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

        // Do tests.
        onView(withId(R.id.title)).check(matches(withText(QUESTION3_TITLE)));
    }

    @Test
    public void loadCheckboxQuestion4() {
        // Setup the activity for each test so we can specify the question number in the intent.
        Intent intent = new Intent();
        intent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, SURVEY_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, USER_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_QUESTION_ID, QUESTION4_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_NUM_PROTECTED_QUESTIONS, NUM_PROTECTED_QUESTIONS);
        mViewQuestionIntentsTestRule.launchActivity(intent);
        mIdlingResource = mViewQuestionIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

        // Do tests.
        onView(withId(R.id.title)).check(matches(withText(QUESTION4_TITLE)));
    }

    @Test
    public void loadTextQuestion5() {
        // Setup the activity for each test so we can specify the question number in the intent.
        Intent intent = new Intent();
        intent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, SURVEY_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, USER_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_QUESTION_ID, QUESTION5_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_NUM_PROTECTED_QUESTIONS, NUM_PROTECTED_QUESTIONS);
        mViewQuestionIntentsTestRule.launchActivity(intent);
        mIdlingResource = mViewQuestionIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

        // Do tests.
        onView(withId(R.id.title)).check(matches(withText(QUESTION5_TITLE)));
    }

    @Test
    public void loadTextQuestion6() {
        // Setup the activity for each test so we can specify the question number in the intent.
        Intent intent = new Intent();
        intent.putExtra(ViewQuestionActivity.EXTRA_SURVEY_ID, SURVEY_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_USER_ID, USER_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_QUESTION_ID, QUESTION6_ID);
        intent.putExtra(ViewQuestionActivity.EXTRA_NUM_PROTECTED_QUESTIONS, NUM_PROTECTED_QUESTIONS);
        mViewQuestionIntentsTestRule.launchActivity(intent);
        mIdlingResource = mViewQuestionIntentsTestRule.getActivity().getCountingIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

        // Do tests.
        onView(withId(R.id.title)).check(matches(withText(QUESTION6_TITLE)));
    }
}
