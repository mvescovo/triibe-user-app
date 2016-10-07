package com.example.triibe.triibeuserapp.view_points;

import android.support.annotation.Nullable;

import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.data.User;

/**
 * @author michael.
 */

public class ViewPointsPresenter implements ViewPointsContract.UserActionsListener {

    TriibeRepository mTriibeRepository;
    ViewPointsContract.View mView;

    public ViewPointsPresenter(TriibeRepository triibeRepository, ViewPointsContract.View view) {
        mTriibeRepository = triibeRepository;
        mView = view;
    }

    @Override
    public void loadCurrentPoints(String user, final String surveyPoints) {
        mView.setIndeterminateProgressIndicator(true);
        mTriibeRepository.getUser(user, new TriibeRepository.GetUserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                if (user != null) {
                    mView.showNewPoints(surveyPoints, user.getPoints());
                }
            }
        });
        mView.setIndeterminateProgressIndicator(false);
    }
}
