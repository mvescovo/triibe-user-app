package com.example.triibe.triibeuserapp.view_surveys;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.Survey;

import java.util.ArrayList;

/**
 * @author michael.
 */
public class SurveyAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SurveyAdapter";

    private ViewSurveysContract.UserActionsListener mUserActionsListener;
    private ArrayList<String> mDataset;

    public SurveyAdapter(ViewSurveysContract.UserActionsListener userActionsListener, ArrayList<String> dataset) {
        mUserActionsListener = userActionsListener;
        mDataset = dataset;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_card, parent, false);
        return new SurveyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) ((SurveyViewHolder)holder).getView().findViewById(R.id.survey_description);
        textView.setText("Survey number: " + position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /*
    * Extend the abstract viewholder
    * */
    private class SurveyViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        public SurveyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Survey survey = new Survey();
                    survey.setId("triibeUser");
                    mUserActionsListener.openSurveyDetails(survey);
                }
            });
        }

        private View getView() {
            return mView;
        }
    }

    public void replaceData(@NonNull ArrayList<String> surveys) {
        if (surveys == null) {
            Log.d(TAG, "replaceData: SURVEY NULL");
        }
        mDataset = surveys;
        notifyDataSetChanged();
    }
}
