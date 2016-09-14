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

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "app service starting", Toast.LENGTH_SHORT).show();

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
                        .setSmallIcon(R.drawable.the_westfield_group_logo)
                        .setContentTitle("TRIIBE")
                        .setContentText("Tracking data");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ViewSurveysActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ViewSurveysActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        startForeground(Constants.APP_SERVICE_RUNNING_ID, mBuilder.build());

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(getBaseContext(), IpService.class));
        wakeLock.release();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

}
