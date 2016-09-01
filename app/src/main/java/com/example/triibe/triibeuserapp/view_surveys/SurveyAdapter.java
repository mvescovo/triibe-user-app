package com.example.triibe.triibeuserapp.view_surveys;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.SurveyDetails;

import java.util.ArrayList;

/**
 * @author michael.
 */
public class SurveyAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SurveyAdapter";

    private ViewSurveysContract.UserActionsListener mUserActionsListener;
    private ArrayList<SurveyDetails> mSurveys;

    public SurveyAdapter(ViewSurveysContract.UserActionsListener userActionsListener, ArrayList<SurveyDetails> surveys) {
        mUserActionsListener = userActionsListener;
        mSurveys = surveys;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_card, parent, false);
        return new SurveyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) ((SurveyViewHolder)holder).getView().findViewById(R.id.survey_description);
        textView.setText(mSurveys.get(position).getDescription());
        textView = (TextView) ((SurveyViewHolder)holder).getView().findViewById(R.id.survey_points);
        textView.setText("Points: " + mSurveys.get(position).getPoints());
        textView = (TextView) ((SurveyViewHolder)holder).getView().findViewById(R.id.survey_expiry);
        textView.setText("Expiry time: " + mSurveys.get(position).getDurationTillExpiry() + "hour.");
    }

    @Override
    public int getItemCount() {
        return mSurveys.size();
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
                    mUserActionsListener.openSurveyDetails(mSurveys.get(getAdapterPosition()).getId());
                }
            });
        }

        private View getView() {
            return mView;
        }
    }

    public void replaceData(@NonNull ArrayList<SurveyDetails> surveys) {
        mSurveys = surveys;
        notifyDataSetChanged();
    }
}
