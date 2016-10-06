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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Option;
import com.example.triibe.triibeuserapp.edit_question.EditQuestionActivity;
import com.example.triibe.triibeuserapp.util.EspressoIdlingResource;
import com.example.triibe.triibeuserapp.util.Globals;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditOptionActivity extends AppCompatActivity
        implements EditOptionContract.View, TextWatcher {

    private static final String TAG = "EditOptionActivity";
    public final static String EXTRA_SURVEY_ID = "com.example.triibe.SURVEY_ID";
    public final static String EXTRA_QUESTION_ID = "com.example.triibe.QUESTION_ID";
    EditOptionContract.UserActionsListener mUserActionsListener;
    private String mSurveyId;
    private String mQuestionId;
    private List<String> mOptionIds;

    @BindView(R.id.view_root)
    CoordinatorLayout mRootView;

    @BindView(R.id.option_id)
    AppCompatAutoCompleteTextView mOptionId;

    @BindView(R.id.option_phrase)
    TextInputEditText mOptionPhrase;

    @BindView(R.id.option_extra_input_type)
    TextInputEditText mExtraInputType;

    @BindView(R.id.option_extra_input_hint)
    TextInputEditText mOptionExtraInputHint;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

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
            mExtraInputType.setText(option.getExtraInputType());
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
            mOptionPhrase.setError("Phrase must not be empty"); // TODO: 18/09/16 set in strings
            mOptionPhrase.requestFocus();
            return false;
        }

        Option option;
        if (!mExtraInputType.getText().toString().trim().contentEquals("")
                && !mOptionExtraInputHint.getText().toString().trim().contentEquals("")) {
            option = new Option(
                    mSurveyId,
                    mQuestionId,
                    mOptionId.getText().toString().trim(),
                    mOptionPhrase.getText().toString().trim(),
                    true
            );
            option.setExtraInputType(mExtraInputType.getText().toString().trim());
            option.setExtraInputHint(mOptionExtraInputHint.getText().toString().trim());
        } else {
            option = new Option(
                    mSurveyId,
                    mQuestionId,
                    mOptionId.getText().toString().trim(),
                    mOptionPhrase.getText().toString().trim(),
                    false
            );
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
            if (s.toString().contentEquals(mOptionIds.get(i))) {
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
        mExtraInputType.setText("");
        mOptionExtraInputHint.setText("");
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
