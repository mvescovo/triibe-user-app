package com.example.triibe.triibeuserapp.edit_option;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.edit_question.EditQuestionActivity;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;
import com.example.triibe.triibeuserapp.util.Globals;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.triibe.triibeuserapp.R.id.has_extra_input_yes;

public class EditOptionActivity extends AppCompatActivity
        implements EditOptionContract.View, TextWatcher, AdapterView.OnItemSelectedListener {

    private static final String TAG = "EditOptionActivity";
    public final static String EXTRA_SURVEY_ID = "com.example.triibe.SURVEY_ID";
    public final static String EXTRA_QUESTION_ID = "com.example.triibe.QUESTION_ID";
    EditOptionContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;
    private String mQuestionId;
    private List<String> mOptionIds;
    private String mSelectedOptionExtraInputType = "text";
    private boolean mHasExtraInput = false;

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.option_id)
    AppCompatAutoCompleteTextView mOptionId;

    @BindView(R.id.option_phrase)
    TextInputEditText mOptionPhrase;

    @BindView(R.id.has_extra_input_yes)
    RadioButton mExtraInputYes;

    @BindView(R.id.has_extra_input_no)
    RadioButton mExtraInputNo;

    @BindView(R.id.extra_input_type_layout)
    LinearLayout mExtraInputTypeLayout;

    @BindView(R.id.option_extra_input_type)
    AppCompatSpinner mExtraInputType;

    @BindView(R.id.extra_input_hint_layout)
    LinearLayout mExtraInputHintLayout;

    @BindView(R.id.option_extra_input_hint)
    TextInputEditText mOptionExtraInputHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_option);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserActionsListener = new EditOptionPresenter(
                Globals.getInstance().getTriibeRepository(),
                this
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getStringExtra(EXTRA_SURVEY_ID) != null) {
            mSurveyId = getIntent().getStringExtra(EXTRA_SURVEY_ID);
        }

        if (getIntent().getStringExtra(EXTRA_QUESTION_ID) != null) {
            mQuestionId = getIntent().getStringExtra(EXTRA_QUESTION_ID);
        }

        // Setup option extra input type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.option_extra_input_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mExtraInputType.setAdapter(adapter);
        mExtraInputType.setOnItemSelectedListener(this);

        mOptionIds = new ArrayList<>();
        mOptionId.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserActionsListener.getOptionIds(mSurveyId, mQuestionId, true);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void addOptionIdsToAutoComplete(List<String> optionIds) {
        mOptionIds = optionIds;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                mOptionIds
        );

        mOptionId.setAdapter(adapter);
    }

    @Override
    public void showOption(Option option) {
        mOptionPhrase.setText(option.getPhrase());
        if (option.getHasExtraInput()) {
            mExtraInputYes.performClick();
            mOptionExtraInputHint.setText(option.getExtraInputHint());
        }
    }

    @Override
    public void showEditQuestion(@NonNull Integer resultCode) {
        setResult(resultCode);
        finish();
    }

    private boolean validate() {
        if (mOptionId.getText().toString().trim().contentEquals("")) {
            mOptionId.setError("Option ID must not be empty"); // TODO: 18/09/16 set in strings
            mOptionId.requestFocus();
            return false;
        } else  if (mOptionPhrase.getText().toString().trim().contentEquals("")) {
            mOptionPhrase.setError("Phrase must not be empty");
            mOptionPhrase.requestFocus();
            return false;
        }

        Option option;
        option = new Option(
                mSurveyId,
                mQuestionId,
                // Save option with "o" prefix. Numerical values will create an array on firebase.
                "o" + mOptionId.getText().toString().trim(),
                mOptionPhrase.getText().toString().trim(),
                false
        );
        if (mHasExtraInput) {
            if (mOptionExtraInputHint.getText().toString().trim().contentEquals("")) {
                mOptionExtraInputHint.setError("Extra input hint must not be empty");
                mOptionExtraInputHint.requestFocus();
                return false;
            }
            option.setHasExtraInput(true);
            option.setExtraInputType(mSelectedOptionExtraInputType);
            option.setExtraInputHint(mOptionExtraInputHint.getText().toString().trim());
        }
        mUserActionsListener.saveOption(option);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                if (validate()) {
                    hideSoftKeyboard(mRootView);
                    showEditQuestion(Activity.RESULT_OK);
                }
                return true;
            case R.id.delete_option:
                hideSoftKeyboard(mRootView);
                mUserActionsListener.deleteOption(mOptionId.getText().toString().trim());
                showEditQuestion(EditQuestionActivity.RESULT_DELETE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean matched = false;
        for (int i = 0; i < mOptionIds.size(); i++) {
            // Get the substring at index 1 because the real optionId is preceded with "o".
            if (s.toString().contentEquals(mOptionIds.get(i).substring(1))) {
                mUserActionsListener.getOption(mOptionIds.get(i));
                matched = true;
            }
        }
        if (!matched) {
            clearOtherFields();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void clearOtherFields() {
        mOptionPhrase.setText("");
        mSelectedOptionExtraInputType = "text";
        mExtraInputNo.performClick();
        mOptionExtraInputHint.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedOptionExtraInputType = parent.getItemAtPosition(position).toString().toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onHasExtraInputRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case has_extra_input_yes:
                if (checked) {
                    mHasExtraInput = true;
                    mExtraInputTypeLayout.setVisibility(View.VISIBLE);
                    mExtraInputHintLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.has_extra_input_no:
                if (checked) {
                    mHasExtraInput = false;
                    mExtraInputTypeLayout.setVisibility(View.GONE);
                    mExtraInputHintLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
