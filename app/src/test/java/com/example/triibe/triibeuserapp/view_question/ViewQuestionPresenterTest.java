package com.example.triibe.triibeuserapp.view_question;

import com.example.triibe.triibeuserapp.data.Answer;
import com.example.triibe.triibeuserapp.data.AnswerDetails;
import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.data.Question;
import com.example.triibe.triibeuserapp.data.QuestionDetails;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.google.common.collect.Maps;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link ViewQuestionPresenter}
 *
 * @author michael
 */
public class ViewQuestionPresenterTest {

    private static String USER_ID = "testUser";

    // Test enrollment survey
    private static String SURVEY_ID = "s1";
    private static Map<String, Question> QUESTIONS = Maps.newHashMap();
    private static Map<String, Answer> ANSWERS = Maps.newHashMap();
    private static int NUM_PROTECTED_QUESTIONS = 2;

    /*
    * Question1 will be a sample radio button question
    * */
    // Question1
    private static Question QUESTION1 = new Question();

    // Question1 details
    private static QuestionDetails QUESTION1_DETAILS = new QuestionDetails();
    private static String QUESTION1_SURVEY_ID = SURVEY_ID;
    private static String QUESTION1_ID = "q1";
    private static String QUESTION1_TYPE = "radio";
    private static String QUESTION1_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/The_Westfield_Group_logo.svg/500px-The_Westfield_Group_logo.svg.png";
    private static String QUESTION1_TITLE = "Participant Information Statement";
    private static String QUESTION1_INTRO = null;
    private static String QUESTION1_INTRO_LINK_KEY = null;
    private static String QUESTION1_INTRO_LINK_URL = null;
    private static String QUESTION1_PHRASE = "Please confirm you are 18 years old or above:";
    private static String QUESTION1_REQUIRED_PHRASE = "Yes";
    private static String QUESTION1_INCORRECT_ANSWER_PHRASE = "You must be 18 or older to participate.";

    // Question1 options
    private static Map<String, Option> QUESTION1_OPTIONS = Maps.newHashMap();
    // Question1 option1
    private static Option QUESTION1_OPTION1 = new Option();
    private static String QUESTION1_OPTION1_QUESTION_ID = QUESTION1_ID;
    private static String QUESTION1_OPTION1_ID = "o1";
    private static String QUESTION1_OPTION1_PHRASE = "Yes";
    private static boolean QUESTION1_OPTION1_CHECKED = true;
    private static boolean QUESTION1_OPTION1_HAS_EXTRA_INPUT = false;
    private static String QUESTION1_OPTION1_EXTRA_INPUT = null;
    private static String QUESTION1_OPTION1_EXTRA_INPUT_TYPE = null;
    private static String QUESTION1_OPTION1_EXTRA_INPUT_HINT = null;
    // Question1 option2
    private static Option QUESTION1_OPTION2 = new Option();
    private static String QUESTION1_OPTION2_QUESTION_ID = QUESTION1_ID;
    private static String QUESTION1_OPTION2_ID = "o2";
    private static String QUESTION1_OPTION2_PHRASE = "No";
    private static boolean QUESTION1_OPTION2_CHECKED = true;
    private static boolean QUESTION1_OPTION2_HAS_EXTRA_INPUT = false;
    private static String QUESTION1_OPTION2_EXTRA_INPUT = null;
    private static String QUESTION1_OPTION2_EXTRA_INPUT_TYPE = null;
    private static String QUESTION1_OPTION2_EXTRA_INPUT_HINT = null;

    // Question1 answer
    private static Answer ANSWER1 = new Answer();

    // Answer1 details
    private static AnswerDetails ANSWERS1_DETAILS = new AnswerDetails();
    private static String ANSWER1_QUESTION_ID = QUESTION1_ID;
    private static String ANSWER1_ID = "a1";
    private static String ANSWER1_TYPE = QUESTION1_TYPE;

    // Answer1 options
    private static Map<String, Option> ANSWER1_OPTIONS = Maps.newHashMap();
    // Answer1 option1
    private static Option ANSWER1_OPTION1 = new Option();
    private static String ANSWER1_OPTION1_ID = "o1";
    private static String ANSWER1_OPTION1_PHRASE = "Yes";
    private static boolean ANSWER1_OPTION1_CHECKED = true;
    private static boolean ANSWER1_OPTION1_HAS_EXTRA_INPUT = false;
    private static String ANSWER1_OPTION1_EXTRA_INPUT = null;
    private static String ANSWER1_OPTION1_EXTRA_INPUT_TYPE = null;
    private static String ANSWER1_OPTION1_EXTRA_INPUT_HINT = null;
    // Answer1 option2
    private static Option ANSWER1_OPTION2 = new Option();
    private static String ANSWER1_OPTION2_ID = "o2";
    private static String ANSWER1_OPTION2_PHRASE = "No";
    private static boolean ANSWER1_OPTION2_CHECKED = false;
    private static boolean ANSWER1_OPTION2_HAS_EXTRA_INPUT = false;
    private static String ANSWER1_OPTION2_EXTRA_INPUT = null;
    private static String ANSWER1_OPTION2_EXTRA_INPUT_TYPE = null;
    private static String ANSWER1_OPTION2_EXTRA_INPUT_HINT = null;

    /*
    * Question2 will be a sample checkbox question
    * */
    // Question2
    private static Question QUESTION2 = new Question();

    // Question2 details
    private static QuestionDetails QUESTION2_DETAILS = new QuestionDetails();
    private static String QUESTION2_SURVEY_ID = SURVEY_ID;
    private static String QUESTION2_ID = "q2";
    private static String QUESTION2_TYPE = "checkbox";
    private static String QUESTION2_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/The_Westfield_Group_logo.svg/500px-The_Westfield_Group_logo.svg.png";
    private static String QUESTION2_TITLE = "Participant Information Statement";
    private static String QUESTION2_INTRO = "A Study of Shopping Experience\\n\\nDear Sir/Madam:\\n\\nWe value your feedback, and to show our appreciation you will go in a draw to win a prize of $1,000 in Westfield Gift Cards if you fully complete the survey.\\n\\nPlease read the terms and conditions for this survey promotion!\\n\\nThank you very much for your cooperation!";
    private static String QUESTION2_INTRO_LINK_KEY = "terms and conditions";
    private static String QUESTION2_INTRO_LINK_URL = "https://mvescovo.github.io/triibe-user-app/";
    private static String QUESTION2_PHRASE = null;
    private static String QUESTION2_REQUIRED_PHRASE = "I confirm that I have read this Participant Information, agreed to the terms and conditions, and consented to participate in the survey.";
    private static String QUESTION2_INCORRECT_ANSWER_PHRASE = "You must agree to the terms and conditions to participate.";

    // Question2 options
    private static Map<String, Option> QUESTION2_OPTIONS = Maps.newHashMap();
    // Question2 option1
    private static Option QUESTION2_OPTION1 = new Option();
    private static String QUESTION2_OPTION1_QUESTION_ID = QUESTION2_ID;
    private static String QUESTION2_OPTION1_ID = "o1";
    private static String QUESTION2_OPTION1_PHRASE = "I confirm that I have read this Participant Information, agreed to the terms and conditions, and consented to participate in the survey.";
    private static boolean QUESTION2_OPTION1_CHECKED = true;
    private static boolean QUESTION2_OPTION1_HAS_EXTRA_INPUT = false;
    private static String QUESTION2_OPTION1_EXTRA_INPUT = null;
    private static String QUESTION2_OPTION1_EXTRA_INPUT_TYPE = null;
    private static String QUESTION2_OPTION1_EXTRA_INPUT_HINT = null;

    // Question2 answer
    private static Answer ANSWER2 = new Answer();

    // Answer2 details
    private static AnswerDetails ANSWERS2_DETAILS = new AnswerDetails();
    private static String ANSWER2_QUESTION_ID = QUESTION2_ID;
    private static String ANSWER2_ID = "a2";
    private static String ANSWER2_TYPE = QUESTION2_TYPE;

    // Answer2 options
    private static Map<String, Option> ANSWER2_OPTIONS = Maps.newHashMap();
    // Answer2 option1
    private static Option ANSWER2_OPTION1 = new Option();
    private static String ANSWER2_OPTION1_ID = "o1";
    private static String ANSWER2_OPTION1_PHRASE = "I confirm that I have read this Participant Information, agreed to the terms and conditions, and consented to participate in the survey.";
    private static boolean ANSWER2_OPTION1_CHECKED = true;
    private static boolean ANSWER2_OPTION1_HAS_EXTRA_INPUT = false;
    private static String ANSWER2_OPTION1_EXTRA_INPUT = null;
    private static String ANSWER2_OPTION1_EXTRA_INPUT_TYPE = null;
    private static String ANSWER2_OPTION1_EXTRA_INPUT_HINT = null;

    /*
    * Question3 will be another sample radio question
    * */
    // Question3
    private static Question QUESTION3 = new Question();

    // Question3 details
    private static QuestionDetails QUESTION3_DETAILS = new QuestionDetails();
    private static String QUESTION3_SURVEY_ID = SURVEY_ID;
    private static String QUESTION3_ID = "q3";
    private static String QUESTION3_TYPE = "radio";
    private static String QUESTION3_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/The_Westfield_Group_logo.svg/500px-The_Westfield_Group_logo.svg.png";
    private static String QUESTION3_TITLE = "About You";
    private static String QUESTION3_INTRO = null;
    private static String QUESTION3_INTRO_LINK_KEY = null;
    private static String QUESTION3_INTRO_LINK_URL = null;
    private static String QUESTION3_PHRASE = null;
    private static String QUESTION3_REQUIRED_PHRASE = null;
    private static String QUESTION3_INCORRECT_ANSWER_PHRASE = null;

    // Question3 options
    private static Map<String, Option> QUESTION3_OPTIONS = Maps.newHashMap();
    // Question3 option1
    private static Option QUESTION3_OPTION1 = new Option();
    private static String QUESTION3_OPTION1_QUESTION_ID = QUESTION3_ID;
    private static String QUESTION3_OPTION1_ID = "o1";
    private static String QUESTION3_OPTION1_PHRASE = "Male";
    private static boolean QUESTION3_OPTION1_CHECKED = true;
    private static boolean QUESTION3_OPTION1_HAS_EXTRA_INPUT = false;
    private static String QUESTION3_OPTION1_EXTRA_INPUT = null;
    private static String QUESTION3_OPTION1_EXTRA_INPUT_TYPE = null;
    private static String QUESTION3_OPTION1_EXTRA_INPUT_HINT = null;
    // Question3 option2
    private static Option QUESTION3_OPTION2 = new Option();
    private static String QUESTION3_OPTION2_QUESTION_ID = QUESTION3_ID;
    private static String QUESTION3_OPTION2_ID = "o2";
    private static String QUESTION3_OPTION2_PHRASE = "Female";
    private static boolean QUESTION3_OPTION2_CHECKED = true;
    private static boolean QUESTION3_OPTION2_HAS_EXTRA_INPUT = false;
    private static String QUESTION3_OPTION2_EXTRA_INPUT = null;
    private static String QUESTION3_OPTION2_EXTRA_INPUT_TYPE = null;
    private static String QUESTION3_OPTION2_EXTRA_INPUT_HINT = null;

    // Question3 answer
    private static Answer ANSWER3 = new Answer();

    // Answer3 details
    private static AnswerDetails ANSWERS3_DETAILS = new AnswerDetails();
    private static String ANSWER3_QUESTION_ID = QUESTION3_ID;
    private static String ANSWER3_ID = "a3";
    private static String ANSWER3_TYPE = QUESTION3_TYPE;

    // Answer3 options
    private static Map<String, Option> ANSWER3_OPTIONS = Maps.newHashMap();
    // Answer3 option1
    private static Option ANSWER3_OPTION1 = new Option();
    private static String ANSWER3_OPTION1_ID = "o1";
    private static String ANSWER3_OPTION1_PHRASE = "Male";
    private static boolean ANSWER3_OPTION1_CHECKED = true;
    private static boolean ANSWER3_OPTION1_HAS_EXTRA_INPUT = false;
    private static String ANSWER3_OPTION1_EXTRA_INPUT = null;
    private static String ANSWER3_OPTION1_EXTRA_INPUT_TYPE = null;
    private static String ANSWER3_OPTION1_EXTRA_INPUT_HINT = null;
    // Answer3 option2
    private static Option ANSWER3_OPTION2 = new Option();
    private static String ANSWER3_OPTION2_ID = "o2";
    private static String ANSWER3_OPTION2_PHRASE = "Female";
    private static boolean ANSWER3_OPTION2_CHECKED = false;
    private static boolean ANSWER3_OPTION2_HAS_EXTRA_INPUT = false;
    private static String ANSWER3_OPTION2_EXTRA_INPUT = null;
    private static String ANSWER3_OPTION2_EXTRA_INPUT_TYPE = null;
    private static String ANSWER3_OPTION2_EXTRA_INPUT_HINT = null;

    /*
    * Question4 will be another sample radio button question
    * */
    // Question4
    private static Question QUESTION4 = new Question();

    // Question4 details
    private static QuestionDetails QUESTION4_DETAILS = new QuestionDetails();
    private static String QUESTION4_SURVEY_ID = SURVEY_ID;
    private static String QUESTION4_ID = "q4";
    private static String QUESTION4_TYPE = "radio";
    private static String QUESTION4_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/The_Westfield_Group_logo.svg/500px-The_Westfield_Group_logo.svg.png";
    private static String QUESTION4_TITLE = "About You";
    private static String QUESTION4_INTRO = null;
    private static String QUESTION4_INTRO_LINK_KEY = null;
    private static String QUESTION4_INTRO_LINK_URL = null;
    private static String QUESTION4_PHRASE = "Which age group do you belong to?";
    private static String QUESTION4_REQUIRED_PHRASE = null;
    private static String QUESTION4_INCORRECT_ANSWER_PHRASE = null;

    // Question4 options
    private static Map<String, Option> QUESTION4_OPTIONS = Maps.newHashMap();
    // Question4 option1
    private static Option QUESTION4_OPTION1 = new Option();
    private static String QUESTION4_OPTION1_QUESTION_ID = QUESTION4_ID;
    private static String QUESTION4_OPTION1_ID = "o1";
    private static String QUESTION4_OPTION1_PHRASE = "18-24";
    private static boolean QUESTION4_OPTION1_CHECKED = true;
    private static boolean QUESTION4_OPTION1_HAS_EXTRA_INPUT = false;
    private static String QUESTION4_OPTION1_EXTRA_INPUT = null;
    private static String QUESTION4_OPTION1_EXTRA_INPUT_TYPE = null;
    private static String QUESTION4_OPTION1_EXTRA_INPUT_HINT = null;
    // Question4 option2
    private static Option QUESTION4_OPTION2 = new Option();
    private static String QUESTION4_OPTION2_QUESTION_ID = QUESTION4_ID;
    private static String QUESTION4_OPTION2_ID = "o2";
    private static String QUESTION4_OPTION2_PHRASE = "25-39";
    private static boolean QUESTION4_OPTION2_CHECKED = true;
    private static boolean QUESTION4_OPTION2_HAS_EXTRA_INPUT = false;
    private static String QUESTION4_OPTION2_EXTRA_INPUT = null;
    private static String QUESTION4_OPTION2_EXTRA_INPUT_TYPE = null;
    private static String QUESTION4_OPTION2_EXTRA_INPUT_HINT = null;
    // Question4 option3
    private static Option QUESTION4_OPTION3 = new Option();
    private static String QUESTION4_OPTION3_QUESTION_ID = QUESTION4_ID;
    private static String QUESTION4_OPTION3_ID = "o3";
    private static String QUESTION4_OPTION3_PHRASE = "40-54";
    private static boolean QUESTION4_OPTION3_CHECKED = true;
    private static boolean QUESTION4_OPTION3_HAS_EXTRA_INPUT = false;
    private static String QUESTION4_OPTION3_EXTRA_INPUT = null;
    private static String QUESTION4_OPTION3_EXTRA_INPUT_TYPE = null;
    private static String QUESTION4_OPTION3_EXTRA_INPUT_HINT = null;
    // Question4 option4
    private static Option QUESTION4_OPTION4 = new Option();
    private static String QUESTION4_OPTION4_QUESTION_ID = QUESTION4_ID;
    private static String QUESTION4_OPTION4_ID = "o4";
    private static String QUESTION4_OPTION4_PHRASE = "55+";
    private static boolean QUESTION4_OPTION4_CHECKED = true;
    private static boolean QUESTION4_OPTION4_HAS_EXTRA_INPUT = false;
    private static String QUESTION4_OPTION4_EXTRA_INPUT = null;
    private static String QUESTION4_OPTION4_EXTRA_INPUT_TYPE = null;
    private static String QUESTION4_OPTION4_EXTRA_INPUT_HINT = null;

    // Question4 answer
    private static Answer ANSWER4 = new Answer();

    // Answer4 details
    private static AnswerDetails ANSWERS4_DETAILS = new AnswerDetails();
    private static String ANSWER4_QUESTION_ID = QUESTION4_ID;
    private static String ANSWER4_ID = "a4";
    private static String ANSWER4_TYPE = QUESTION4_TYPE;

    // Answer4 options
    private static Map<String, Option> ANSWER4_OPTIONS = Maps.newHashMap();
    // Answer4 option1
    private static Option ANSWER4_OPTION1 = new Option();
    private static String ANSWER4_OPTION1_ID = "o1";
    private static String ANSWER4_OPTION1_PHRASE = "18-24";
    private static boolean ANSWER4_OPTION1_CHECKED = true;
    private static boolean ANSWER4_OPTION1_HAS_EXTRA_INPUT = false;
    private static String ANSWER4_OPTION1_EXTRA_INPUT = null;
    private static String ANSWER4_OPTION1_EXTRA_INPUT_TYPE = null;
    private static String ANSWER4_OPTION1_EXTRA_INPUT_HINT = null;
    // Answer4 option2
    private static Option ANSWER4_OPTION2 = new Option();
    private static String ANSWER4_OPTION2_ID = "o2";
    private static String ANSWER4_OPTION2_PHRASE = "25-39";
    private static boolean ANSWER4_OPTION2_CHECKED = false;
    private static boolean ANSWER4_OPTION2_HAS_EXTRA_INPUT = false;
    private static String ANSWER4_OPTION2_EXTRA_INPUT = null;
    private static String ANSWER4_OPTION2_EXTRA_INPUT_TYPE = null;
    private static String ANSWER4_OPTION2_EXTRA_INPUT_HINT = null;
    // Answer4 option3
    private static Option ANSWER4_OPTION3 = new Option();
    private static String ANSWER4_OPTION3_ID = "o3";
    private static String ANSWER4_OPTION3_PHRASE = "40-54";
    private static boolean ANSWER4_OPTION3_CHECKED = true;
    private static boolean ANSWER4_OPTION3_HAS_EXTRA_INPUT = false;
    private static String ANSWER4_OPTION3_EXTRA_INPUT = null;
    private static String ANSWER4_OPTION3_EXTRA_INPUT_TYPE = null;
    private static String ANSWER4_OPTION3_EXTRA_INPUT_HINT = null;
    // Answer4 option4
    private static Option ANSWER4_OPTION4 = new Option();
    private static String ANSWER4_OPTION4_ID = "o4";
    private static String ANSWER4_OPTION4_PHRASE = "55+";
    private static boolean ANSWER4_OPTION4_CHECKED = false;
    private static boolean ANSWER4_OPTION4_HAS_EXTRA_INPUT = false;
    private static String ANSWER4_OPTION4_EXTRA_INPUT = null;
    private static String ANSWER4_OPTION4_EXTRA_INPUT_TYPE = null;
    private static String ANSWER4_OPTION4_EXTRA_INPUT_HINT = null;


    static {
        /*
        * Question1
        * */
        // Question1 details
        QUESTION1_DETAILS.setSurveyId(QUESTION1_SURVEY_ID);
        QUESTION1_DETAILS.setId(QUESTION1_ID);
        QUESTION1_DETAILS.setType(QUESTION1_TYPE);
        QUESTION1_DETAILS.setImageUrl(QUESTION1_IMAGE_URL);
        QUESTION1_DETAILS.setTitle(QUESTION1_TITLE);
        QUESTION1_DETAILS.setIntro(QUESTION1_INTRO);
        QUESTION1_DETAILS.setIntroLinkKey(QUESTION1_INTRO_LINK_KEY);
        QUESTION1_DETAILS.setIntroLinkUrl(QUESTION1_INTRO_LINK_URL);
        QUESTION1_DETAILS.setPhrase(QUESTION1_PHRASE);
        QUESTION1_DETAILS.setRequiredPhrase(QUESTION1_REQUIRED_PHRASE);
        QUESTION1_DETAILS.setIncorrectAnswerPhrase(QUESTION1_INCORRECT_ANSWER_PHRASE);
        // Question1 option1
        QUESTION1_OPTION1.setQuestionId(QUESTION1_OPTION1_QUESTION_ID);
        QUESTION1_OPTION1.setId(QUESTION1_OPTION1_ID);
        QUESTION1_OPTION1.setPhrase(QUESTION1_OPTION1_PHRASE);
        QUESTION1_OPTION1.setChecked(QUESTION1_OPTION1_CHECKED);
        QUESTION1_OPTION1.setHasExtraInput(QUESTION1_OPTION1_HAS_EXTRA_INPUT);
        QUESTION1_OPTION1.setExtraInput(QUESTION1_OPTION1_EXTRA_INPUT);
        QUESTION1_OPTION1.setExtraInputType(QUESTION1_OPTION1_EXTRA_INPUT_TYPE);
        QUESTION1_OPTION1.setExtraInputHint(QUESTION1_OPTION1_EXTRA_INPUT_HINT);
        // Question1 option2
        QUESTION1_OPTION2.setQuestionId(QUESTION1_OPTION2_QUESTION_ID);
        QUESTION1_OPTION2.setId(QUESTION1_OPTION2_ID);
        QUESTION1_OPTION2.setPhrase(QUESTION1_OPTION2_PHRASE);
        QUESTION1_OPTION2.setChecked(QUESTION1_OPTION2_CHECKED);
        QUESTION1_OPTION2.setHasExtraInput(QUESTION1_OPTION2_HAS_EXTRA_INPUT);
        QUESTION1_OPTION2.setExtraInput(QUESTION1_OPTION2_EXTRA_INPUT);
        QUESTION1_OPTION2.setExtraInputType(QUESTION1_OPTION2_EXTRA_INPUT_TYPE);
        QUESTION1_OPTION2.setExtraInputHint(QUESTION1_OPTION2_EXTRA_INPUT_HINT);

        QUESTION1_OPTIONS.put(QUESTION1_OPTION1_ID, QUESTION1_OPTION1);
        QUESTION1_OPTIONS.put(QUESTION1_OPTION2_ID, QUESTION1_OPTION2);
        QUESTION1.setQuestionDetails(QUESTION1_DETAILS);
        QUESTION1.setOptions(QUESTION1_OPTIONS);

        // Answer1 details
        ANSWERS1_DETAILS.setQuestionId(ANSWER1_QUESTION_ID);
        ANSWERS1_DETAILS.setId(ANSWER1_ID);
        ANSWERS1_DETAILS.setType(ANSWER1_TYPE);
        // Answer1 option1
        ANSWER1_OPTION1.setQuestionId(QUESTION1_ID);
        ANSWER1_OPTION1.setId(ANSWER1_OPTION1_ID);
        ANSWER1_OPTION1.setPhrase(ANSWER1_OPTION1_PHRASE);
        ANSWER1_OPTION1.setChecked(ANSWER1_OPTION1_CHECKED);
        ANSWER1_OPTION1.setHasExtraInput(ANSWER1_OPTION1_HAS_EXTRA_INPUT);
        ANSWER1_OPTION1.setExtraInput(ANSWER1_OPTION1_EXTRA_INPUT);
        ANSWER1_OPTION1.setExtraInputType(ANSWER1_OPTION1_EXTRA_INPUT_TYPE);
        ANSWER1_OPTION1.setExtraInputHint(ANSWER1_OPTION1_EXTRA_INPUT_HINT);
        // Answer1 option2
        ANSWER1_OPTION2.setQuestionId(QUESTION1_ID);
        ANSWER1_OPTION2.setId(ANSWER1_OPTION2_ID);
        ANSWER1_OPTION2.setPhrase(ANSWER1_OPTION2_PHRASE);
        ANSWER1_OPTION2.setChecked(ANSWER1_OPTION2_CHECKED);
        ANSWER1_OPTION2.setHasExtraInput(ANSWER1_OPTION2_HAS_EXTRA_INPUT);
        ANSWER1_OPTION2.setExtraInput(ANSWER1_OPTION2_EXTRA_INPUT);
        ANSWER1_OPTION2.setExtraInputType(ANSWER1_OPTION2_EXTRA_INPUT_TYPE);
        ANSWER1_OPTION2.setExtraInputHint(ANSWER1_OPTION2_EXTRA_INPUT_HINT);

//        ANSWER1_OPTIONS.put(ANSWER1_OPTION1_ID, ANSWER1_OPTION1);
//        ANSWER1_OPTIONS.put(ANSWER1_OPTION2_ID, ANSWER1_OPTION2);
//        ANSWER1.setAnswerDetails(ANSWERS1_DETAILS);
//        ANSWER1.setSelectedOptions(ANSWER1_OPTIONS);

        /*
        * Question2
        * */
        // Question2 details
        QUESTION2_DETAILS.setSurveyId(QUESTION2_SURVEY_ID);
        QUESTION2_DETAILS.setId(QUESTION2_ID);
        QUESTION2_DETAILS.setType(QUESTION2_TYPE);
        QUESTION2_DETAILS.setImageUrl(QUESTION2_IMAGE_URL);
        QUESTION2_DETAILS.setTitle(QUESTION2_TITLE);
        QUESTION2_DETAILS.setIntro(QUESTION2_INTRO);
        QUESTION2_DETAILS.setIntroLinkKey(QUESTION2_INTRO_LINK_KEY);
        QUESTION2_DETAILS.setIntroLinkUrl(QUESTION2_INTRO_LINK_URL);
        QUESTION2_DETAILS.setPhrase(QUESTION2_PHRASE);
        QUESTION2_DETAILS.setRequiredPhrase(QUESTION2_REQUIRED_PHRASE);
        QUESTION2_DETAILS.setIncorrectAnswerPhrase(QUESTION2_INCORRECT_ANSWER_PHRASE);
        // Question2 option1
        QUESTION2_OPTION1.setQuestionId(QUESTION2_OPTION1_QUESTION_ID);
        QUESTION2_OPTION1.setId(QUESTION2_OPTION1_ID);
        QUESTION2_OPTION1.setPhrase(QUESTION2_OPTION1_PHRASE);
        QUESTION2_OPTION1.setChecked(QUESTION2_OPTION1_CHECKED);
        QUESTION2_OPTION1.setHasExtraInput(QUESTION2_OPTION1_HAS_EXTRA_INPUT);
        QUESTION2_OPTION1.setExtraInput(QUESTION2_OPTION1_EXTRA_INPUT);
        QUESTION2_OPTION1.setExtraInputType(QUESTION2_OPTION1_EXTRA_INPUT_TYPE);
        QUESTION2_OPTION1.setExtraInputHint(QUESTION2_OPTION1_EXTRA_INPUT_HINT);

        QUESTION2_OPTIONS.put(QUESTION2_OPTION1_ID, QUESTION2_OPTION1);
        QUESTION2.setQuestionDetails(QUESTION2_DETAILS);
        QUESTION2.setOptions(QUESTION2_OPTIONS);

        // Answer2 details
        ANSWERS2_DETAILS.setQuestionId(ANSWER2_QUESTION_ID);
        ANSWERS2_DETAILS.setId(ANSWER2_ID);
        ANSWERS2_DETAILS.setType(ANSWER2_TYPE);
        // Answer2 option1
        ANSWER2_OPTION1.setQuestionId(QUESTION2_ID);
        ANSWER2_OPTION1.setId(ANSWER2_OPTION1_ID);
        ANSWER2_OPTION1.setPhrase(ANSWER2_OPTION1_PHRASE);
        ANSWER2_OPTION1.setChecked(ANSWER2_OPTION1_CHECKED);
        ANSWER2_OPTION1.setHasExtraInput(ANSWER2_OPTION1_HAS_EXTRA_INPUT);
        ANSWER2_OPTION1.setExtraInput(ANSWER2_OPTION1_EXTRA_INPUT);
        ANSWER2_OPTION1.setExtraInputType(ANSWER2_OPTION1_EXTRA_INPUT_TYPE);
        ANSWER2_OPTION1.setExtraInputHint(ANSWER2_OPTION1_EXTRA_INPUT_HINT);

        ANSWER2_OPTIONS.put(ANSWER2_OPTION1_ID, ANSWER2_OPTION1);
        ANSWER2.setAnswerDetails(ANSWERS2_DETAILS);
        ANSWER2.setSelectedOptions(ANSWER2_OPTIONS);

        /*
        * Question3
        * */
        // Question3 details
        QUESTION3_DETAILS.setSurveyId(QUESTION3_SURVEY_ID);
        QUESTION3_DETAILS.setId(QUESTION3_ID);
        QUESTION3_DETAILS.setType(QUESTION3_TYPE);
        QUESTION3_DETAILS.setImageUrl(QUESTION3_IMAGE_URL);
        QUESTION3_DETAILS.setTitle(QUESTION3_TITLE);
        QUESTION3_DETAILS.setIntro(QUESTION3_INTRO);
        QUESTION3_DETAILS.setIntroLinkKey(QUESTION3_INTRO_LINK_KEY);
        QUESTION3_DETAILS.setIntroLinkUrl(QUESTION3_INTRO_LINK_URL);
        QUESTION3_DETAILS.setPhrase(QUESTION3_PHRASE);
        QUESTION3_DETAILS.setRequiredPhrase(QUESTION3_REQUIRED_PHRASE);
        QUESTION3_DETAILS.setIncorrectAnswerPhrase(QUESTION3_INCORRECT_ANSWER_PHRASE);
        // Question3 option1
        QUESTION3_OPTION1.setQuestionId(QUESTION3_OPTION1_QUESTION_ID);
        QUESTION3_OPTION1.setId(QUESTION3_OPTION1_ID);
        QUESTION3_OPTION1.setPhrase(QUESTION3_OPTION1_PHRASE);
        QUESTION3_OPTION1.setChecked(QUESTION3_OPTION1_CHECKED);
        QUESTION3_OPTION1.setHasExtraInput(QUESTION3_OPTION1_HAS_EXTRA_INPUT);
        QUESTION3_OPTION1.setExtraInput(QUESTION3_OPTION1_EXTRA_INPUT);
        QUESTION3_OPTION1.setExtraInputType(QUESTION3_OPTION1_EXTRA_INPUT_TYPE);
        QUESTION3_OPTION1.setExtraInputHint(QUESTION3_OPTION1_EXTRA_INPUT_HINT);
        // Question3 option2
        QUESTION3_OPTION2.setQuestionId(QUESTION3_OPTION2_QUESTION_ID);
        QUESTION3_OPTION2.setId(QUESTION3_OPTION2_ID);
        QUESTION3_OPTION2.setPhrase(QUESTION3_OPTION2_PHRASE);
        QUESTION3_OPTION2.setChecked(QUESTION3_OPTION2_CHECKED);
        QUESTION3_OPTION2.setHasExtraInput(QUESTION3_OPTION2_HAS_EXTRA_INPUT);
        QUESTION3_OPTION2.setExtraInput(QUESTION3_OPTION2_EXTRA_INPUT);
        QUESTION3_OPTION2.setExtraInputType(QUESTION3_OPTION2_EXTRA_INPUT_TYPE);
        QUESTION3_OPTION2.setExtraInputHint(QUESTION3_OPTION2_EXTRA_INPUT_HINT);

        QUESTION3_OPTIONS.put(QUESTION3_OPTION1_ID, QUESTION3_OPTION1);
        QUESTION3_OPTIONS.put(QUESTION3_OPTION2_ID, QUESTION3_OPTION2);
        QUESTION3.setQuestionDetails(QUESTION3_DETAILS);
        QUESTION3.setOptions(QUESTION3_OPTIONS);

        // Answer3 details
        ANSWERS3_DETAILS.setQuestionId(ANSWER3_QUESTION_ID);
        ANSWERS3_DETAILS.setId(ANSWER3_ID);
        ANSWERS3_DETAILS.setType(ANSWER3_TYPE);
        // Answer3 option1
        ANSWER3_OPTION1.setQuestionId(QUESTION3_ID);
        ANSWER3_OPTION1.setId(ANSWER3_OPTION1_ID);
        ANSWER3_OPTION1.setPhrase(ANSWER3_OPTION1_PHRASE);
        ANSWER3_OPTION1.setChecked(ANSWER3_OPTION1_CHECKED);
        ANSWER3_OPTION1.setHasExtraInput(ANSWER3_OPTION1_HAS_EXTRA_INPUT);
        ANSWER3_OPTION1.setExtraInput(ANSWER3_OPTION1_EXTRA_INPUT);
        ANSWER3_OPTION1.setExtraInputType(ANSWER3_OPTION1_EXTRA_INPUT_TYPE);
        ANSWER3_OPTION1.setExtraInputHint(ANSWER3_OPTION1_EXTRA_INPUT_HINT);
        // Answer3 option2
        ANSWER3_OPTION2.setQuestionId(QUESTION3_ID);
        ANSWER3_OPTION2.setId(ANSWER3_OPTION2_ID);
        ANSWER3_OPTION2.setPhrase(ANSWER3_OPTION2_PHRASE);
        ANSWER3_OPTION2.setChecked(ANSWER3_OPTION2_CHECKED);
        ANSWER3_OPTION2.setHasExtraInput(ANSWER3_OPTION2_HAS_EXTRA_INPUT);
        ANSWER3_OPTION2.setExtraInput(ANSWER3_OPTION2_EXTRA_INPUT);
        ANSWER3_OPTION2.setExtraInputType(ANSWER3_OPTION2_EXTRA_INPUT_TYPE);
        ANSWER3_OPTION2.setExtraInputHint(ANSWER3_OPTION2_EXTRA_INPUT_HINT);

//        ANSWER3_OPTIONS.put(ANSWER3_OPTION1_ID, ANSWER3_OPTION1);
//        ANSWER3_OPTIONS.put(ANSWER3_OPTION2_ID, ANSWER3_OPTION2);
//        ANSWER3.setAnswerDetails(ANSWERS3_DETAILS);
//        ANSWER3.setSelectedOptions(ANSWER3_OPTIONS);

        /*
        * Question4
        * */
        // Question4 details
        QUESTION4_DETAILS.setSurveyId(QUESTION4_SURVEY_ID);
        QUESTION4_DETAILS.setId(QUESTION4_ID);
        QUESTION4_DETAILS.setType(QUESTION4_TYPE);
        QUESTION4_DETAILS.setImageUrl(QUESTION4_IMAGE_URL);
        QUESTION4_DETAILS.setTitle(QUESTION4_TITLE);
        QUESTION4_DETAILS.setIntro(QUESTION4_INTRO);
        QUESTION4_DETAILS.setIntroLinkKey(QUESTION4_INTRO_LINK_KEY);
        QUESTION4_DETAILS.setIntroLinkUrl(QUESTION4_INTRO_LINK_URL);
        QUESTION4_DETAILS.setPhrase(QUESTION4_PHRASE);
        QUESTION4_DETAILS.setRequiredPhrase(QUESTION4_REQUIRED_PHRASE);
        QUESTION4_DETAILS.setIncorrectAnswerPhrase(QUESTION4_INCORRECT_ANSWER_PHRASE);
        // Question4 option1
        QUESTION4_OPTION1.setQuestionId(QUESTION4_OPTION1_QUESTION_ID);
        QUESTION4_OPTION1.setId(QUESTION4_OPTION1_ID);
        QUESTION4_OPTION1.setPhrase(QUESTION4_OPTION1_PHRASE);
        QUESTION4_OPTION1.setChecked(QUESTION4_OPTION1_CHECKED);
        QUESTION4_OPTION1.setHasExtraInput(QUESTION4_OPTION1_HAS_EXTRA_INPUT);
        QUESTION4_OPTION1.setExtraInput(QUESTION4_OPTION1_EXTRA_INPUT);
        QUESTION4_OPTION1.setExtraInputType(QUESTION4_OPTION1_EXTRA_INPUT_TYPE);
        QUESTION4_OPTION1.setExtraInputHint(QUESTION4_OPTION1_EXTRA_INPUT_HINT);
        // Question4 option2
        QUESTION4_OPTION2.setQuestionId(QUESTION4_OPTION2_QUESTION_ID);
        QUESTION4_OPTION2.setId(QUESTION4_OPTION2_ID);
        QUESTION4_OPTION2.setPhrase(QUESTION4_OPTION2_PHRASE);
        QUESTION4_OPTION2.setChecked(QUESTION4_OPTION2_CHECKED);
        QUESTION4_OPTION2.setHasExtraInput(QUESTION4_OPTION2_HAS_EXTRA_INPUT);
        QUESTION4_OPTION2.setExtraInput(QUESTION4_OPTION2_EXTRA_INPUT);
        QUESTION4_OPTION2.setExtraInputType(QUESTION4_OPTION2_EXTRA_INPUT_TYPE);
        QUESTION4_OPTION2.setExtraInputHint(QUESTION4_OPTION2_EXTRA_INPUT_HINT);
        // Question4 option3
        QUESTION4_OPTION3.setQuestionId(QUESTION4_OPTION3_QUESTION_ID);
        QUESTION4_OPTION3.setId(QUESTION4_OPTION3_ID);
        QUESTION4_OPTION3.setPhrase(QUESTION4_OPTION3_PHRASE);
        QUESTION4_OPTION3.setChecked(QUESTION4_OPTION3_CHECKED);
        QUESTION4_OPTION3.setHasExtraInput(QUESTION4_OPTION3_HAS_EXTRA_INPUT);
        QUESTION4_OPTION3.setExtraInput(QUESTION4_OPTION3_EXTRA_INPUT);
        QUESTION4_OPTION3.setExtraInputType(QUESTION4_OPTION3_EXTRA_INPUT_TYPE);
        QUESTION4_OPTION3.setExtraInputHint(QUESTION4_OPTION3_EXTRA_INPUT_HINT);
        // Question4 option4
        QUESTION4_OPTION4.setQuestionId(QUESTION4_OPTION4_QUESTION_ID);
        QUESTION4_OPTION4.setId(QUESTION4_OPTION4_ID);
        QUESTION4_OPTION4.setPhrase(QUESTION4_OPTION4_PHRASE);
        QUESTION4_OPTION4.setChecked(QUESTION4_OPTION4_CHECKED);
        QUESTION4_OPTION4.setHasExtraInput(QUESTION4_OPTION4_HAS_EXTRA_INPUT);
        QUESTION4_OPTION4.setExtraInput(QUESTION4_OPTION4_EXTRA_INPUT);
        QUESTION4_OPTION4.setExtraInputType(QUESTION4_OPTION4_EXTRA_INPUT_TYPE);
        QUESTION4_OPTION4.setExtraInputHint(QUESTION4_OPTION4_EXTRA_INPUT_HINT);

        QUESTION4_OPTIONS.put(QUESTION4_OPTION1_ID, QUESTION4_OPTION1);
        QUESTION4_OPTIONS.put(QUESTION4_OPTION2_ID, QUESTION4_OPTION2);
        QUESTION4_OPTIONS.put(QUESTION4_OPTION3_ID, QUESTION4_OPTION3);
        QUESTION4_OPTIONS.put(QUESTION4_OPTION4_ID, QUESTION4_OPTION4);
        QUESTION4.setQuestionDetails(QUESTION4_DETAILS);
        QUESTION4.setOptions(QUESTION4_OPTIONS);

        // Answer4 details
        ANSWERS4_DETAILS.setQuestionId(ANSWER4_QUESTION_ID);
        ANSWERS4_DETAILS.setId(ANSWER4_ID);
        ANSWERS4_DETAILS.setType(ANSWER4_TYPE);
        // Answer4 option1
        ANSWER4_OPTION1.setQuestionId(QUESTION4_ID);
        ANSWER4_OPTION1.setId(ANSWER4_OPTION1_ID);
        ANSWER4_OPTION1.setPhrase(ANSWER4_OPTION1_PHRASE);
        ANSWER4_OPTION1.setChecked(ANSWER4_OPTION1_CHECKED);
        ANSWER4_OPTION1.setHasExtraInput(ANSWER4_OPTION1_HAS_EXTRA_INPUT);
        ANSWER4_OPTION1.setExtraInput(ANSWER4_OPTION1_EXTRA_INPUT);
        ANSWER4_OPTION1.setExtraInputType(ANSWER3_OPTION1_EXTRA_INPUT_TYPE);
        ANSWER4_OPTION1.setExtraInputHint(ANSWER4_OPTION1_EXTRA_INPUT_HINT);
        // Answer4 option2
        ANSWER4_OPTION2.setQuestionId(QUESTION4_ID);
        ANSWER4_OPTION2.setId(ANSWER4_OPTION2_ID);
        ANSWER4_OPTION2.setPhrase(ANSWER4_OPTION2_PHRASE);
        ANSWER4_OPTION2.setChecked(ANSWER4_OPTION2_CHECKED);
        ANSWER4_OPTION2.setHasExtraInput(ANSWER4_OPTION2_HAS_EXTRA_INPUT);
        ANSWER4_OPTION2.setExtraInput(ANSWER4_OPTION2_EXTRA_INPUT);
        ANSWER4_OPTION2.setExtraInputType(ANSWER4_OPTION2_EXTRA_INPUT_TYPE);
        ANSWER4_OPTION2.setExtraInputHint(ANSWER4_OPTION2_EXTRA_INPUT_HINT);
        // Answer4 option3
        ANSWER4_OPTION3.setQuestionId(QUESTION4_ID);
        ANSWER4_OPTION3.setId(ANSWER4_OPTION3_ID);
        ANSWER4_OPTION3.setPhrase(ANSWER4_OPTION3_PHRASE);
        ANSWER4_OPTION3.setChecked(ANSWER4_OPTION3_CHECKED);
        ANSWER4_OPTION3.setHasExtraInput(ANSWER4_OPTION3_HAS_EXTRA_INPUT);
        ANSWER4_OPTION3.setExtraInput(ANSWER4_OPTION3_EXTRA_INPUT);
        ANSWER4_OPTION3.setExtraInputType(ANSWER4_OPTION3_EXTRA_INPUT_TYPE);
        ANSWER4_OPTION3.setExtraInputHint(ANSWER4_OPTION3_EXTRA_INPUT_HINT);
        // Answer4 option4
        ANSWER4_OPTION4.setQuestionId(QUESTION4_ID);
        ANSWER4_OPTION4.setId(ANSWER4_OPTION4_ID);
        ANSWER4_OPTION4.setPhrase(ANSWER4_OPTION4_PHRASE);
        ANSWER4_OPTION4.setChecked(ANSWER4_OPTION4_CHECKED);
        ANSWER4_OPTION4.setHasExtraInput(ANSWER4_OPTION4_HAS_EXTRA_INPUT);
        ANSWER4_OPTION4.setExtraInput(ANSWER4_OPTION4_EXTRA_INPUT);
        ANSWER4_OPTION4.setExtraInputType(ANSWER4_OPTION4_EXTRA_INPUT_TYPE);
        ANSWER4_OPTION4.setExtraInputHint(ANSWER4_OPTION4_EXTRA_INPUT_HINT);

//        ANSWER4_OPTIONS.put(ANSWER4_OPTION1_ID, ANSWER4_OPTION1);
//        ANSWER4_OPTIONS.put(ANSWER4_OPTION2_ID, ANSWER4_OPTION2);
//        ANSWER4_OPTIONS.put(ANSWER4_OPTION3_ID, ANSWER4_OPTION3);
//        ANSWER4_OPTIONS.put(ANSWER4_OPTION4_ID, ANSWER4_OPTION4);
//        ANSWER4.setAnswerDetails(ANSWERS4_DETAILS);
//        ANSWER4.setSelectedOptions(ANSWER4_OPTIONS);

        /*
        * Add all questions. Don't forget this.
        * */
        QUESTIONS.put(QUESTION1_ID, QUESTION1);
        QUESTIONS.put(QUESTION2_ID, QUESTION2);
        QUESTIONS.put(QUESTION3_ID, QUESTION3);
        QUESTIONS.put(QUESTION4_ID, QUESTION4);
//        ANSWERS.put(ANSWER1_ID, ANSWER1);
//        ANSWERS.put(ANSWER2_ID, ANSWER2);
//        ANSWERS.put(ANSWER3_ID, ANSWER3);
//        ANSWERS.put(ANSWER4_ID, ANSWER4);
    }

    @Mock
    private TriibeRepository mTriibeRepository;

    @Mock
    private ViewQuestionContract.View mView;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetQuestionsCallback> mQuestionsCallbackCaptor;

    @Captor
    private ArgumentCaptor<TriibeRepository.GetAnswersCallback> mAnswersCallbackCaptor;

    private ViewQuestionPresenter mViewQuestionPresenter;

    @Before
    public void setupViewSurveyDetailsPresenter() {
        MockitoAnnotations.initMocks(this);
        mViewQuestionPresenter = new ViewQuestionPresenter(mTriibeRepository, mView, SURVEY_ID,
                USER_ID, QUESTION1_ID, NUM_PROTECTED_QUESTIONS);
    }

    @Test
    public void loadCurrentQuestionFromRepositoryAndLoadIntoView() {
        mViewQuestionPresenter.loadCurrentQuestion();

        verify(mView).setIndeterminateProgressIndicator(true);

        verify(mTriibeRepository).getQuestions(anyString(), mQuestionsCallbackCaptor.capture());
        mQuestionsCallbackCaptor.getValue().onQuestionsLoaded(QUESTIONS);

        verify(mTriibeRepository).getAnswers(anyString(), anyString(), mAnswersCallbackCaptor.capture());
        mAnswersCallbackCaptor.getValue().onAnswersLoaded(ANSWERS);

        // Display question details
        if (!QUESTION1_IMAGE_URL.contentEquals("")) {
            verify(mView).showImage(QUESTION1_IMAGE_URL);
        } else {
            verify(mView).hideImage();
        }
        if (!QUESTION1_TITLE.contentEquals("")) {
            verify(mView).showTitle(QUESTION1_TITLE);
        } else {
            verify(mView).hideTitle();
        }
        if (QUESTION1_INTRO != null) {
            verify(mView).showIntro(QUESTION1_INTRO, QUESTION1_INTRO_LINK_KEY,
                    QUESTION1_INTRO_LINK_URL);
        } else {
            verify(mView).hideIntro();
        }
        if (!QUESTION1_PHRASE.contentEquals("")) {
            verify(mView).showPhrase(QUESTION1_PHRASE);
        } else {
            verify(mView).hidePhrase();
        }

        float progress = (float) mViewQuestionPresenter.getCurrentQuestionNum() /
                mViewQuestionPresenter.getQuestions().size() * 100;
        verify(mView).setProgressIndicator((int)progress);

        // Display question options
        switch (QUESTION1_TYPE) {
            case "radio":
                verify(mView).showRadioButtonGroup();
                if (QUESTION1_OPTION1_PHRASE != null) {
                    verify(mView).showRadioButtonItem(QUESTION1_OPTION1_PHRASE,
                            QUESTION1_OPTION1_EXTRA_INPUT_HINT, QUESTION1_OPTION1_EXTRA_INPUT_TYPE);
                }
                break;
            case "checkbox":
                verify(mView).showCheckboxGroup();
                if (QUESTION1_OPTION1_PHRASE != null) {
                    verify(mView).showCheckboxItem(QUESTION1_OPTION1_PHRASE,
                            QUESTION1_OPTION1_EXTRA_INPUT_HINT, QUESTION1_OPTION1_EXTRA_INPUT_TYPE,
                            QUESTION1_OPTIONS.size());
                }
                break;
            case "text":
                verify(mView).showTextboxGroup();
                if (QUESTION1_OPTION1_PHRASE != null) {
                    verify(mView).showTextboxItem(QUESTION1_OPTION1_PHRASE,
                            QUESTION1_OPTION1_EXTRA_INPUT_TYPE, null);
                }
                break;
        }

        // Display answer selected options
        if (ANSWERS.size() > 0) {
            if (ANSWER1_OPTIONS != null) {
                switch (ANSWER1_TYPE) {
                    case "radio":
                        verify(mView).selectRadioButtonItem(
                                ANSWER1_OPTION1_PHRASE,
                                ANSWER1_OPTION1_HAS_EXTRA_INPUT,
                                ANSWER1_OPTION1_EXTRA_INPUT,
                                ANSWER1_OPTION1_EXTRA_INPUT_HINT,
                                ANSWER1_OPTION1_EXTRA_INPUT_TYPE,
                                QUESTION1_OPTIONS.size()
                        );
                        break;
                    case "checkbox":
                        verify(mView).selectCheckboxItem(ANSWER1_OPTION1_PHRASE,
                                ANSWER1_OPTION1_CHECKED, ANSWER1_OPTIONS.size());
                        break;
                    case "text":
                        verify(mView).showTextboxItem(ANSWER1_OPTION1_EXTRA_INPUT_HINT, "text", ANSWER1_OPTION1_PHRASE);
                        break;
                }
            }
        }

        verify(mView).setIndeterminateProgressIndicator(false);
    }

    @Test
    public void saveAnswer() {
        mViewQuestionPresenter.mQuestions = QUESTIONS;

        mViewQuestionPresenter.saveAnswer(ANSWER1_OPTION1_PHRASE, null, ANSWER1_TYPE, ANSWER1_OPTION1_CHECKED);
        verify(mTriibeRepository).saveAnswer(anyString(), anyString(), anyString(), any(Answer.class));
        verify(mTriibeRepository).getAnswers(anyString(), anyString(), any(TriibeRepository.GetAnswersCallback.class));
    }

    @Test
    public void goToNextQuestionOnQuestionCompleteAndCorrectAnswer() {
        mViewQuestionPresenter.mQuestions = QUESTIONS;

        // Check we are on question 1
        assertEquals("Current question is not the first question", 1, mViewQuestionPresenter.getCurrentQuestionNum());

        // Simulate saving answer
        ANSWER1_OPTIONS.put(ANSWER1_OPTION1_ID, ANSWER1_OPTION1);
        ANSWER1.setAnswerDetails(ANSWERS1_DETAILS);
        ANSWER1.setSelectedOptions(ANSWER1_OPTIONS);
        ANSWERS.put(ANSWER1_ID, ANSWER1);
        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER1_ID));

        // If question has a required answer, check that the selected answer is correct.
        if (QUESTION1.getQuestionDetails().getRequiredPhrase() != null) {
            assertEquals("Incorrect answer", QUESTION1.getQuestionDetails().getRequiredPhrase(), ANSWER1.getSelectedOptions().get(ANSWER1_OPTION1_ID).getPhrase());
        }

        // Check current question has been incremented to question 2
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Current question number did not increment", 2, mViewQuestionPresenter.getCurrentQuestionNum());

        // Confirm no message is displayed (no error message). There shouldn't be any non error messages displayed otherwise this will fail.
        verify(mView, never()).showSnackbar(anyString(), anyByte());
    }

    @Test
    public void goToNextQuestionFailsOnQuestionIncomplete() {
        mViewQuestionPresenter.mQuestions = QUESTIONS;

        // Check we are on question 1.
        assertEquals("Current question is not the first question", 1, mViewQuestionPresenter.getCurrentQuestionNum());

        // Confirm no answer saved.
        assertNull("Question complete", mViewQuestionPresenter.getAnswers().get(ANSWER1_ID));

        // Check current question has not been incremented.
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Went to the next question", 1, mViewQuestionPresenter.getCurrentQuestionNum());

        // Confirm user is informed of the error.
        verify(mView).showSnackbar(anyString(), anyByte());
    }

    @Test
    public void goToNextQuestionFailsOnAnswerIncorrect() {
        mViewQuestionPresenter.mQuestions = QUESTIONS;

        // Check we are on question 1
        assertEquals("Current question is not the first question", 1, mViewQuestionPresenter.getCurrentQuestionNum());

        // Simulate saving answer
        ANSWER1_OPTIONS.put(ANSWER1_OPTION2_ID, ANSWER1_OPTION2);
        System.out.println("option: " + ANSWER1_OPTION2.getPhrase());
        ANSWER1.setAnswerDetails(ANSWERS1_DETAILS);
        ANSWER1.setSelectedOptions(ANSWER1_OPTIONS);
        ANSWERS.put(ANSWER1_ID, ANSWER1);
        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER1_ID));

        // Confirm the answer is incorrect
        assertNotEquals("The answer was correct", QUESTION1.getQuestionDetails().getRequiredPhrase(), ANSWER1.getSelectedOptions().get(ANSWER1_OPTION2_ID).getPhrase());

        // Confirm current question has not been incremented
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Went to the next question", 1, mViewQuestionPresenter.getCurrentQuestionNum());

        // Confirm user is informed of the error.
        verify(mView).showSnackbar(anyString(), anyByte());
    }

    @Test
    public void goToPreviousQuestionWhenNotPastProtectedQuestions() {
        mViewQuestionPresenter.mQuestions = QUESTIONS;

        // Go to question 2

        // Simulate saving answer
        ANSWER1_OPTIONS.put(ANSWER1_OPTION1_ID, ANSWER1_OPTION1);
        ANSWER1.setAnswerDetails(ANSWERS1_DETAILS);
        ANSWER1.setSelectedOptions(ANSWER1_OPTIONS);
        ANSWERS.put(ANSWER1_ID, ANSWER1);
        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER1_ID));
        // Check current question has been incremented to question 2
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Current question number did not increment", 2, mViewQuestionPresenter.getCurrentQuestionNum());

        // Confirm we haven't gone past the protected questions
        assertThat(mViewQuestionPresenter.getCurrentQuestionNum(), is(not(greaterThan(NUM_PROTECTED_QUESTIONS))));

        mViewQuestionPresenter.checkAnswerToGoPrevious();
        // Check we are on question 1
        assertEquals("Did not go back to question 1", 1, mViewQuestionPresenter.getCurrentQuestionNum());
    }

    @Test
    public void goToPreviousQuestionFailsWhenPastProtectedQuestionsAndTryingToGoBackToOne() {
        mViewQuestionPresenter.mQuestions = QUESTIONS;

        // Go to question 2.

        // Simulate saving answer.
        ANSWER1_OPTIONS.put(ANSWER1_OPTION1_ID, ANSWER1_OPTION1);
        ANSWER1.setAnswerDetails(ANSWERS1_DETAILS);
        ANSWER1.setSelectedOptions(ANSWER1_OPTIONS);
        ANSWERS.put(ANSWER1_ID, ANSWER1);

        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER1_ID));
        // Check current question has been incremented to question 2.
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Current question number did not increment", 2, mViewQuestionPresenter.getCurrentQuestionNum());

        // Go to question 3.

        // Simulate saving answer.
        ANSWER2_OPTIONS.put(ANSWER2_OPTION1_ID, ANSWER2_OPTION1);
        ANSWER2.setAnswerDetails(ANSWERS2_DETAILS);
        ANSWER2.setSelectedOptions(ANSWER2_OPTIONS);
        ANSWERS.put(ANSWER2_ID, ANSWER2);

        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER2_ID));
        // Check current question has been incremented to question 3.
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Current question number did not increment", 3, mViewQuestionPresenter.getCurrentQuestionNum());

        // Confirm we've gone past the protected questions.
        assertThat(mViewQuestionPresenter.getCurrentQuestionNum(), greaterThan(NUM_PROTECTED_QUESTIONS));

        mViewQuestionPresenter.checkAnswerToGoPrevious();
        // Check we didn't go back to the previous question.
        assertThat(mViewQuestionPresenter.getCurrentQuestionNum(), greaterThan(NUM_PROTECTED_QUESTIONS));

        // Confirm user is informed of the error.
        verify(mView).showSnackbar(anyString(), anyByte());
    }

    @Test
    public void goToPreviousQuestionWhenPastProtectedQuestionsButPreviousQuestionNotProtected() {
        mViewQuestionPresenter.mQuestions = QUESTIONS;

        // Go to question 2.

        // Simulate saving answer.
        ANSWER1_OPTIONS.put(ANSWER1_OPTION1_ID, ANSWER1_OPTION1);
        ANSWER1.setAnswerDetails(ANSWERS1_DETAILS);
        ANSWER1.setSelectedOptions(ANSWER1_OPTIONS);
        ANSWERS.put(ANSWER1_ID, ANSWER1);

        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER1_ID));
        // Check current question has been incremented to question 2.
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Current question number did not increment", 2, mViewQuestionPresenter.getCurrentQuestionNum());

        // Go to question 3.

        // Simulate saving answer.
        ANSWER2_OPTIONS.put(ANSWER2_OPTION1_ID, ANSWER2_OPTION1);
        ANSWER2.setAnswerDetails(ANSWERS2_DETAILS);
        ANSWER2.setSelectedOptions(ANSWER2_OPTIONS);
        ANSWERS.put(ANSWER2_ID, ANSWER2);

        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER2_ID));
        // Check current question has been incremented to question 3.
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Current question number did not increment", 3, mViewQuestionPresenter.getCurrentQuestionNum());

        // Go to question 4.

        // Simulate saving answer.
        ANSWER3_OPTIONS.put(ANSWER3_OPTION1_ID, ANSWER3_OPTION1);
        ANSWER3.setAnswerDetails(ANSWERS3_DETAILS);
        ANSWER3.setSelectedOptions(ANSWER3_OPTIONS);
        ANSWERS.put(ANSWER3_ID, ANSWER3);

        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER3_ID));
        // Check current question has been incremented to question 4.
        mViewQuestionPresenter.checkAnswerToGoNext();
        assertEquals("Current question number did not increment", 4, mViewQuestionPresenter.getCurrentQuestionNum());

        // Confirm we've gone past the protected questions.
        assertThat(mViewQuestionPresenter.getCurrentQuestionNum(), greaterThan(NUM_PROTECTED_QUESTIONS));

        mViewQuestionPresenter.checkAnswerToGoPrevious();
        // Check we were able to go back to the previous question.
        assertEquals("Current question number did not decrement", 3, mViewQuestionPresenter.getCurrentQuestionNum());
    }

    @Test
    public void startSurveyAfterQualifyingQuestionsIfPreviouslyAnswered() {
        mViewQuestionPresenter.mQuestions = QUESTIONS;

        // Set qualifying questions as answered.
        ANSWER1_OPTIONS.put(ANSWER1_OPTION1_ID, ANSWER1_OPTION1);
        ANSWER1.setAnswerDetails(ANSWERS1_DETAILS);
        ANSWER1.setSelectedOptions(ANSWER1_OPTIONS);
        ANSWERS.put(ANSWER1_ID, ANSWER1);

        ANSWER2_OPTIONS.put(ANSWER2_OPTION1_ID, ANSWER2_OPTION1);
        ANSWER2.setAnswerDetails(ANSWERS2_DETAILS);
        ANSWER2.setSelectedOptions(ANSWER2_OPTIONS);
        ANSWERS.put(ANSWER2_ID, ANSWER2);

        mViewQuestionPresenter.mAnswers = ANSWERS;
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER1_ID));
        assertNotNull("Question incomplete", mViewQuestionPresenter.getAnswers().get(ANSWER2_ID));

        // Load the current question
        mViewQuestionPresenter.loadCurrentQuestion();

        verify(mView).setIndeterminateProgressIndicator(true);

        verify(mTriibeRepository).getQuestions(anyString(), mQuestionsCallbackCaptor.capture());
        mQuestionsCallbackCaptor.getValue().onQuestionsLoaded(QUESTIONS);

        verify(mTriibeRepository).getAnswers(anyString(), anyString(), mAnswersCallbackCaptor.capture());
        mAnswersCallbackCaptor.getValue().onAnswersLoaded(ANSWERS);

        mViewQuestionPresenter.loadCurrentQuestion();

        // TODO: 9/10/16  not sure how to test this after refactoring. However the fucntionality does seem to work even though the test fails.
        // Confirm current question is after protected questions.
        assertThat("Did not move past protected questions", mViewQuestionPresenter.getCurrentQuestionNum(), greaterThan(NUM_PROTECTED_QUESTIONS));
    }

    @After
    public void tearDown() {
        ANSWERS = Maps.newHashMap();
        ANSWER1_OPTIONS.clear();
        ANSWER2_OPTIONS.clear();
        ANSWER3_OPTIONS.clear();
        ANSWER4_OPTIONS.clear();

    }
}
