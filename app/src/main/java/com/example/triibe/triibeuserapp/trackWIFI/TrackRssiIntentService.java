package com.example.triibe.triibeuserapp.trackWIFI;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class TrackRssiIntentService extends Service {

    private static final String TAG = "TrackRssiIntentService";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                // Level of a Scan Result
                int strongestLevel = -1000;
                String strongestAp = "unknown";
                List<ScanResult> wifiList = wifiManager.getScanResults();
                for (ScanResult scanResult : wifiList) {
                    String ssid = scanResult.SSID;
                    int rssi = scanResult.level;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        String venueName = scanResult.venueName.toString();
                        Log.d(TAG, "ssid: " + ssid + ", rssi: " + rssi + ", venue: " + venueName);
                    }
                    if (rssi > strongestLevel) {
                        strongestLevel = rssi;
                        strongestAp = ssid;
                    }
                }
                Log.i(TAG, "Strongest AP level: " + strongestAp);

                // Level of current connection
                String ssid = wifiManager.getConnectionInfo().getSSID();
                int rssi = wifiManager.getConnectionInfo().getRssi();
                Log.d(TAG, "Connected ssid: " + ssid + ", rssi: " + rssi);



                // Getting RSSI data

//                List<ContentValues> APsList;
//
//
//                StringBuffer textContent = new StringBuffer();
//                WifiScanner wifiScanner = new WifiScanner(getApplicationContext());
//                ContentValues wifiConnection = wifiScanner.getConnectedInfo();
//                String wifi_network;
//
//                wifi_network = IndoorService.getSetting(getApplicationContext().getContentResolver(), "wifi_network");
//                if (wifi_network.equalsIgnoreCase("all")) {
//                    APsList = wifiScanner.getNearbyAllAPs();
//                } else if (wifiConnection != null) {
//                    wifi_network = (String) wifiConnection.get("ssid");
//                    APsList = wifiScanner.getNearbyAPsBySSID(wifi_network);
//                } else if (wifi_network.equals("")) {
//                    APsList = wifiScanner.getNearbyAllAPs();
//                } else {
//                    APsList = wifiScanner.getNearbyAPsBySSID(wifi_network);
//                }
//
//                textContent.append("There are "+(APsList != null ? APsList.size() : 0) + " nearby APs  (Network: " + wifi_network
//                        + ")\n");
//
//                // if APsList is null, no access points around or WiFi is turn off
//                if (APsList == null)
//                    APsList = new ArrayList<ContentValues>();
//
//
//                Log.d(TAG, "handleMessage: " + textContent);


                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
//            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Thread.MIN_PRIORITY);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

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
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
