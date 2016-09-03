package com.example.triibe.triibeuserapp.edit_option;

import com.example.triibe.triibeuserapp.data.Option;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author michael.
 */
public class EditOptionPresenter implements EditOptionContract.UserActionsListener {

    EditOptionContract.View mView;
    private DatabaseReference mDatabase; // TODO: 2/09/16 detach listeners

    public EditOptionPresenter(EditOptionContract.View view) {
        mView = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void editOption(String surveyId, String questionId, String id, String phrase, String extraInput, String extraInputType, String extraInputHint) {
        mView.setProgressIndicator(true);

        Option option = new Option(phrase, false);
        Map<String, Object> optionValues = option.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/surveys/" + surveyId + "/questions/" + questionId + "/options/" + id, optionValues);
        mDatabase.updateChildren(childUpdates);

        mView.setProgressIndicator(false);

        mView.showEditQuestion();
    }
}
