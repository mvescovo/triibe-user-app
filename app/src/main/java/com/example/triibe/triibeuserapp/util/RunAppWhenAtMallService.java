package com.example.triibe.triibeuserapp.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.SurveyDetails;
import com.example.triibe.triibeuserapp.data.SurveyTrigger;
import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.data.User;
import com.example.triibe.triibeuserapp.get_permissions.AuthUiActivity;
import com.example.triibe.triibeuserapp.track_location.AddFencesIntentService;
import com.example.triibe.triibeuserapp.track_location.RemoveFenceIntentService;
import com.example.triibe.triibeuserapp.view_surveys.ViewSurveysActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.triibe.triibeuserapp.track_location.AddFencesIntentService.EXTRA_FENCE_KEY;
import static com.example.triibe.triibeuserapp.track_location.AddFencesIntentService.EXTRA_TRIIBE_FENCE_TYPE;
import static com.example.triibe.triibeuserapp.track_location.AddFencesIntentService.TYPE_LANDMARK;

/**
 * @author michael.
 */
public class RunAppWhenAtMallService extends Service {

    public final static String EXTRA_USER_ID = "com.example.triibe.USER_ID";
    private static final String TAG = "RunAppWhenAtMallService";
    private static final int STOP_SERVICE_REQUEST = 1;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private PendingIntent mStopTrackingPendingIntent;
    private TriibeRepository mTriibeRepository;
    private String mUserId;
    private List<SurveyDetails> mSurveys;
    private User mUser;

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        Intent stopAppServiceIntent = new Intent(this, StopTrackingIntentService.class);
        mStopTrackingPendingIntent = PendingIntent.getService(this, STOP_SERVICE_REQUEST,
                stopAppServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mTriibeRepository = Globals.getInstance().getTriibeRepository();

        mSurveys = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra(EXTRA_USER_ID) != null) {
            mUserId = intent.getStringExtra(EXTRA_USER_ID);
        } else {
            mUserId = "InvalidUserId";
        }

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // Start the service in the foreground
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.westfieldicon_transparent)
                        .setContentTitle("At mall") // TODO: 18/09/16 set in strings
                        .setContentText("Tracking data")
                        .addAction(R.drawable.ic_stop_black_24dp, "Stop",
                                mStopTrackingPendingIntent);
        Intent resultIntent = new Intent(this, AuthUiActivity.class);
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ViewSurveysActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(Constants.APP_SERVICE_RUNNING_ID, mBuilder.build());

        // Check if user is enrolled before adding triggers for other surveys.
        mTriibeRepository.getUser(mUserId, new TriibeRepository.GetUserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                if (user != null) {
                    mUser = user;
                    if (user.isEnrolled()) {
                        getSurveyIds();
                    } else {
                        Log.d(TAG, "onUserLoaded: user not enrolled.");
                    }
                } else {
                    Log.d(TAG, "onUserLoaded: user is NULL");
                }
            }
        });

        /*
        * Matt's services
        * */
//        startService(new Intent(getBaseContext(), IpService.class));
//        startService(new Intent(getBaseContext(), UsageStatsService.class));

        return START_STICKY;
    }

    private void getSurveyIds() {
        // Get all surveyIds so we can get triggers for active surveys.
        Log.d(TAG, "getSurveyIds: going to called get surveyids");
        final String path = "/surveyIds";
        mTriibeRepository.refreshSurveyIds();

        EspressoIdlingResource.increment();
        mTriibeRepository.getSurveyIds(path, new TriibeRepository.GetSurveyIdsCallback() {
            @Override
            public void onSurveyIdsLoaded(@Nullable final Map<String, Boolean> surveyIds) {
                EspressoIdlingResource.decrement();
                if (surveyIds != null) {
                    getSurveyDetails(surveyIds);
                }
            }
        });
    }

    private void getSurveyDetails(Map<String, Boolean> surveyIds) {
        Object[] surveyIdsKeys = surveyIds.keySet().toArray();
        // Get each surveyDetails.
        for (int i = 0; i < surveyIds.size(); i++) {
            // Filter out surveys user has already completed.
            if (!mUser.getCompletedSurveyIds().containsKey(surveyIdsKeys[i].toString())) {
                EspressoIdlingResource.increment();
                mTriibeRepository.getSurvey(surveyIdsKeys[i].toString(),
                        new TriibeRepository.GetSurveyCallback() {
                            @Override
                            public void onSurveyLoaded(SurveyDetails survey) {
                                EspressoIdlingResource.decrement();
                                if (survey != null) {
                                    mSurveys.add(survey);
                                    if (survey.isActive()) {
                                        // Only get triggers for active surveys.
                                        getSurveyTriggers(survey.getId(),
                                                survey.getDescription(),
                                                survey.getNumProtectedQuestions()
                                        );
                                    }
                                }
                            }
                        });
            }
        }
    }

    private void getSurveyTriggers(final String surveyId, final String surveyDescription,
                                   final String numProtectedQuestions) {
        EspressoIdlingResource.increment();
        mTriibeRepository.getTriggers(surveyId, new TriibeRepository.GetTriggersCallback() {
            @Override
            public void onTriggersLoaded(@Nullable Map<String, SurveyTrigger> triggers) {
                EspressoIdlingResource.decrement();
                if (triggers != null) {
                    for (String triggerId : triggers.keySet()) {
                        // Immediately add a fence for this trigger.
                        addFence(triggers.get(triggerId), surveyDescription, numProtectedQuestions);
                    }
                }
            }
        });
    }

    private void addFence(SurveyTrigger trigger, String surveyDescription, String numProtectedQuestions) {
        // Add a location fence
        if (trigger.getLatitude() != null && trigger.getLongitude() != null) {
            Intent addLocationFencesIntent = new Intent(this, AddFencesIntentService.class);
            addLocationFencesIntent.putExtra(
                    EXTRA_TRIIBE_FENCE_TYPE,
                    TYPE_LANDMARK
            );
            addLocationFencesIntent.putExtra(
                    EXTRA_FENCE_KEY,
                    trigger.getSurveyId()
            );
            addLocationFencesIntent.putExtra(
                    AddFencesIntentService.EXTRA_LATITUDE,
                    trigger.getLatitude()
            );
            addLocationFencesIntent.putExtra(
                    AddFencesIntentService.EXTRA_LONGITUDE,
                    trigger.getLongitude()
            );
            addLocationFencesIntent.putExtra(
                    AddFencesIntentService.EXTRA_RADIUS,
                    trigger.getRadius()
            );
            addLocationFencesIntent.putExtra(
                    AddFencesIntentService.EXTRA_DWELL,
                    trigger.getDwell()
            );
            // Add survey description so it can be shown on the notification.
            addLocationFencesIntent.putExtra(
                    AddFencesIntentService.EXTRA_SURVEY_DESCRIPTION,
                    surveyDescription
            );
            // Add numProtectedQuestions so it can be passed to the notification.
            addLocationFencesIntent.putExtra(
                    AddFencesIntentService.EXTRA_NUM_PROTECTED_QUESTIONS,
                    numProtectedQuestions
            );
            // Set requestId so each pendingIntent will be different
            int requestCode = surveyDescription.hashCode();
            addLocationFencesIntent.putExtra(
                    AddFencesIntentService.EXTRA_REQUEST_CODE,
                    requestCode
            );
            startService(addLocationFencesIntent);
        }

        // Add time fence
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Remove landmark fences
        List<String> landmarkFences = Globals.getInstance().getLandmarkFences();
        for (int i = 0; i < landmarkFences.size(); i++) {
            Intent removeFenceIntent = new Intent(this, RemoveFenceIntentService.class);
            removeFenceIntent.putExtra(EXTRA_TRIIBE_FENCE_TYPE, TYPE_LANDMARK);
            removeFenceIntent.putExtra(EXTRA_FENCE_KEY, landmarkFences.get(i));
            startService(removeFenceIntent);

            // Clear notifications.
            mNotificationManager.cancelAll();
        }

        // Remove surveys from users list
        for (int i = 0; i < mSurveys.size(); i++) {
            mTriibeRepository.removeUserSurvey(mUserId, mSurveys.get(i).getId());
        }

        Log.d(TAG, "onDestroy: service done");
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }
}
