package com.example.triibe.triibeuserapp.view_question;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;
import com.example.triibe.triibeuserapp.util.Globals;
import com.example.triibe.triibeuserapp.view_surveys.ViewSurveysActivity;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewQuestionActivity extends AppCompatActivity
        implements ViewQuestionContract.View, TextWatcher {

    private static final String TAG = "ViewQuestionActivity";
    public final static String EXTRA_SURVEY_ID = "com.example.triibe.SURVEY_ID";
    public final static String EXTRA_USER_ID = "com.example.triibe.USER_ID";
    public final static String EXTRA_QUESTION_ID = "com.example.triibe.QUESTION_ID";
    public final static String EXTRA_NUM_PROTECTED_QUESTIONS = "com.example.triibe.NUM_PROTECTED_QUESTIONS";
    ViewQuestionContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;
    private String mUserId;
    private String mQuestionId;
    private int mNumProtectedQuestions;

    @BindView(R.id.view_root)
    RelativeLayout mRootView;

    @BindView(R.id.load_survey_progress_bar)
    ProgressBar mLoadSurveyProgressBar;

    @BindView(R.id.question_logo)
    ImageView mImage;

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.intro)
    TextView mIntro;

    @BindView(R.id.phrase)
    TextView mPhrase;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;

    @BindView(R.id.checkbox_group)
    LinearLayout mCheckboxGroup;

    @BindView(R.id.text_input_layout)
    TextInputLayout mTextInputLayout;

    @BindView(R.id.text_input_edit_text)
    TextInputEditText mTextInputEditText;

    @BindView(R.id.edit_text_group)
    LinearLayout mEditTextGroup;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.next_button)
    Button mNextButton;

    @BindView(R.id.previous_button_image)
    ImageButton mPreviousButtonImage;

    @BindView(R.id.previous_button)
    Button mPreviousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);
        ButterKnife.bind(this);

        if (getIntent().getStringExtra(EXTRA_SURVEY_ID) != null) {
            mSurveyId = getIntent().getStringExtra(EXTRA_SURVEY_ID);
        } else {
            mSurveyId = "-1";
        }
        if (getIntent().getStringExtra(EXTRA_USER_ID) != null) {
            mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        } else {
            mUserId = "InvalidUser";
        }
        if (getIntent().getStringExtra(EXTRA_QUESTION_ID) != null) {
            mQuestionId = getIntent().getStringExtra(EXTRA_QUESTION_ID);
        } else {
            mQuestionId = "-1";
        }
        mNumProtectedQuestions = getIntent().getIntExtra(EXTRA_NUM_PROTECTED_QUESTIONS, 0);

        mUserActionsListener = new ViewQuestionPresenter(
                Globals.getInstance().getTriibeRepository(),
                this,
                mSurveyId,
                mUserId,
                mQuestionId,
                mNumProtectedQuestions
        );

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserActionsListener.goToNextQuestion();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserActionsListener.goToPreviousQuestion();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserActionsListener.loadCurrentQuestion();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(ViewSurveysActivity.EXTRA_USER_ID, mUserId);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setProgressIndicator(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void setIndeterminateProgressIndicator(boolean active) {
        if (active) {
            mLoadSurveyProgressBar.setVisibility(View.VISIBLE);
        } else {
            mLoadSurveyProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showImage(String imageUrl) {
        mImage.setVisibility(View.VISIBLE);
        Picasso.with(this).load(imageUrl).into(mImage);
    }

    @Override
    public void hideImage() {
        mImage.setVisibility(View.GONE);
    }

    @Override
    public void showTitle(String title) {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(title);
    }

    @Override
    public void hideTitle() {
        mTitle.setVisibility(View.GONE);
    }

    @Override
    public void showIntro(String intro, @Nullable String linkKey, @Nullable final String linkUrl) {
        mIntro.setVisibility(View.VISIBLE);
        mIntro.setText(intro);

        if (linkKey != null && linkUrl != null) {
            Linkify.TransformFilter mentionFilter = new Linkify.TransformFilter() {
                public final String transformUrl(final Matcher match, String url) {
                    return linkUrl;
                }
            };

            Pattern pattern = Pattern.compile(linkKey);
            String scheme = "";
            Linkify.addLinks(mIntro, pattern, scheme, null, mentionFilter);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mIntro.setLinkTextColor(getResources().getColor(R.color.linkColor, getTheme()));
            }
        }
    }

    @Override
    public void hideIntro() {
        mIntro.setVisibility(View.GONE);
    }

    @Override
    public void showPhrase(String phrase) {
        mPhrase.setVisibility(View.VISIBLE);
        mPhrase.setText(phrase);
    }

    @Override
    public void hidePhrase() {
        mPhrase.setVisibility(View.GONE);
    }

    @Override
    public void showRadioButtonGroup() {
        mCheckboxGroup.setVisibility(View.GONE);
        mEditTextGroup.setVisibility(View.GONE);
        mTextInputEditText.setVisibility(View.GONE);
        mRadioGroup.removeAllViews();
        mRadioGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRadioButtonItem(final String phrase, @Nullable String extraInputHint,
                                    @Nullable String extraInputType) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(phrase);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserActionsListener
                        .saveAnswer(((RadioButton)view).getText().toString(), "radio", true);
            }
        });
        mRadioGroup.addView(radioButton);
    }

    @Override
    public void selectRadioButtonItem(String phrase, boolean hasExtraInput,
                                      @Nullable String extraInputHint,
                                      @Nullable String extraInputType,
                                      @Nullable String extraInput,
                                      int size) {
        for (int i = 0; i < size; i++) {
            RadioButton item = (RadioButton) mRadioGroup.getChildAt(i);
            if (item.getText().toString().contentEquals(phrase)) {
                item.toggle();
                if (hasExtraInput) {
                    showExtraInputTextboxItem(extraInputHint, extraInputType, extraInput);
                } else {
                    hideExtraInputTextboxItem();
                }
                return;
            }
        }
    }

    @Override
    public void showExtraInputTextboxItem(String hint, String type, @Nullable String text) {
        mTextInputLayout.setVisibility(View.VISIBLE);
        mTextInputEditText.setVisibility(View.VISIBLE);
        mTextInputEditText.addTextChangedListener(this);
        mTextInputEditText.setHint(hint);
        if (text != null) {
            mTextInputEditText.setText(text);
        }
        switch (type) {
            case "text": // TODO: 18/09/16 set in constants or something
                mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "number":
                mTextInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "email":
                mTextInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case "phone":
                mTextInputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
        }
        mTextInputEditText.requestFocus();
    }

    @Override
    public void hideExtraInputTextboxItem() {
        mTextInputEditText.removeTextChangedListener(this);
        mTextInputEditText.setText("");
        mTextInputEditText.setHint("");
        mTextInputEditText.setVisibility(View.GONE);
    }

    @Override
    public void showCheckboxGroup() {
        mRadioGroup.setVisibility(View.GONE);
        mEditTextGroup.setVisibility(View.GONE);
        mTextInputEditText.setVisibility(View.GONE);
        mCheckboxGroup.removeAllViews();
        mCheckboxGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCheckboxItem(final String phrase, @Nullable String extraInputHint,
                                 @Nullable String extraInputType, final int size) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(phrase);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserActionsListener
                        .saveAnswer(((CheckBox)view).getText().toString(), "checkbox",
                                ((CheckBox)view).isChecked());
            }
        });
        mCheckboxGroup.addView(checkBox);
    }

    @Override
    public void selectCheckboxItem(String phrase, boolean checked, int size) {
        for (int i = 0; i < size; i++) {
            CheckBox item = (CheckBox) mCheckboxGroup.getChildAt(i);
            if (item.getText().toString().contentEquals(phrase)) {
                item.toggle();
                return;
            }
        }
    }

    @Override
    public void showTextboxGroup() {
        mRadioGroup.setVisibility(View.GONE);
        mCheckboxGroup.setVisibility(View.GONE);
        mEditTextGroup.removeAllViews();
        mEditTextGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTextboxItem(String hint, final String type) {
        final TextInputEditText textInputEditText = new TextInputEditText(this);
        textInputEditText.setHint(hint);

        switch (type) {
            case "text": // TODO: 18/09/16 set in constants or something
                textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "number":
                textInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "email":
                textInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case "phone":
                textInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
        textInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserActionsListener
                        .saveAnswer(((TextInputEditText)view).getText().toString(), type, true);
            }
        });
        mEditTextGroup.addView(textInputEditText);
    }

    @Override
    public void showSnackbar(String text, int duration) {
        Snackbar snackbar = Snackbar.make(mRootView, text, duration);
        snackbar.show();
    }

    @Override
    public void showSubmitButton() {
        mNextButton.setText("Finish"); // TODO: 18/09/16 set in strings
    }

    @Override
    public void hideSubmitButton() {
        mNextButton.setText("Next");
    }

    @Override
    public void showBackButton() {
        mPreviousButton.setVisibility(View.VISIBLE);
        mPreviousButtonImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBackButton() {
        mPreviousButton.setVisibility(View.INVISIBLE);
        mPreviousButtonImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideOptions() {
        mRadioGroup.setVisibility(View.GONE);
        mCheckboxGroup.setVisibility(View.GONE);
        mEditTextGroup.removeAllViews();
        mEditTextGroup.setVisibility(View.GONE);
    }

    @Override
    public void showViewSurveys(@NonNull Integer resultCode, @NonNull Integer surveyPoints,
                                @NonNull Integer totalPoints) {
        Intent data = new Intent();
        data.putExtra(ViewSurveysActivity.EXTRA_SURVEY_POINTS, surveyPoints);
        data.putExtra(ViewSurveysActivity.EXTRA_TOTAL_POINTS, totalPoints);
        setResult(resultCode, data);
        finish();
    }

    @Override
    public void removeNotification(@NonNull String fenceKey) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(fenceKey, 1);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mUserActionsListener.saveAnswer(charSequence.toString(), "extraText", true);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
