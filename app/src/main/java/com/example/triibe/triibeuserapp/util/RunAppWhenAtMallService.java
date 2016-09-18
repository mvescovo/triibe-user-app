package com.example.triibe.triibeuserapp.util;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.view_surveys.ViewSurveysActivity;

/**
 * @author michael.
 */
public class RunAppWhenAtMallService extends Service {
    //code to allow the service to run while the screen is switched off.
    PowerManager mgr;
    PowerManager.WakeLock wakeLock;

    private static final String TAG = "RunAppWhenAtMallService";
    private static final int STOP_SERVICE_REQUEST = 9999;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private PendingIntent mStopTrackingPendingIntent;

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        //power managment.
        mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();


        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        Intent stopAppServiceIntent = new Intent(this, StopTrackingIntentService.class);
        mStopTrackingPendingIntent = PendingIntent.getService(this, STOP_SERVICE_REQUEST, stopAppServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: app service starting");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);


        // The code directly above is just an example straight from the android docs. Just change as required.

        // TODO: Matt to add data tracking tasks here. Do any required setup on oncreate and tear down in ondestroy.
        // You shouldn't need to touch anything else hopefully - which is a good thing as it's a bit of a mess currently.
        // I'll clean it up over time but this should get you running for now.

        // So this method will run when the user is within 1000 meters of the mall. See the constants file for the locations that trigger this
        // and add any you need for testing. Let me know if you have any questions.

        // Stick your classes in the trackData package and any utilities in the util package. Add constants to the Constants file.
        // Add global variables to Globals. If you need permissions added let me know and I can stick them in at the same time it asks for location permission.
        // I'm going to change the class where they're currently set so it's better not to do it yourself otherwise there'll be merge conflicts. Just tell me which permissions you need.

        // I think maybe also put a comment where you add stuff so I can see what it's for in case I accidentally think I'd done it myself and delete it.


        // Method to start the service
        startService(new Intent(getBaseContext(), IpService.class));

        // Start the service in the foreground
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.westfieldicon_transparent)
                        .setContentTitle("At mall") // TODO: 18/09/16 set in strings
                        .setContentText("Tracking data")
                        .addAction(R.drawable.ic_stop_black_24dp, "Stop", mStopTrackingPendingIntent);
        Intent resultIntent = new Intent(this, AuthUiActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ViewSurveysActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(Constants.APP_SERVICE_RUNNING_ID, mBuilder.build());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(getBaseContext(), IpService.class));
        wakeLock.release();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy: service done");
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO: Matt to handle messages here
            Log.d(TAG, "handleMessage: test data tracking task (print to console)");
        }
    }
}
