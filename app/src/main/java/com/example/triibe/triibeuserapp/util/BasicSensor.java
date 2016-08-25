package com.example.triibe.triibeuserapp.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class BasicSensor extends Service {

	/**
	 * Debug tag for this sensor
	 */
	public static String TAG = "Sensor";

	/**
	 * Debug flag for this sensor
	 */
	public static boolean DEBUG = false;

	public ContextProducer CONTEXT_PRODUCER = null;

	/**
	 * Sensor database tables
	 */
	public String[] DATABASE_TABLES = null;

	/**
	 * Sensor table fields
	 */
	public String[] TABLES_FIELDS = null;

	/**
	 * Context Providers URIs
	 */
	public Uri[] CONTEXT_URIS = null;

	/**
	 * Sensor is inactive
	 */
	public static final int STATUS_SENSOR_OFF = 0;

	/**
	 * Sensor is active
	 */
	public static final int STATUS_SENSOR_ON = 1;

	/**
	 * Interface to share context with other applications/addons<br/>
	 * You MUST broadcast your contexts here!
	 */
	public interface ContextProducer {
		public void onContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		TAG = IndoorService.getSetting(getContentResolver(), IndoorPreferences.DEBUG_TAG).length() > 0 ? IndoorService
				.getSetting(getContentResolver(), IndoorPreferences.DEBUG_TAG) : TAG;
		DEBUG = IndoorService.getSetting(getContentResolver(), IndoorPreferences.DEBUG_FLAG).equals("true") ? true
				: DEBUG;

		if (DEBUG)
			Log.d(TAG, TAG + " sensor created!");

		// Register Context Broadcaster
		IntentFilter filter = new IntentFilter();
		filter.addAction(IndoorService.ACTION_INDOOR_CURRENT_CONTEXT);
		filter.addAction(IndoorService.ACTION_INDOOR_WEBSERVICE);
		filter.addAction(IndoorService.ACTION_INDOOR_CLEAN_DATABASES);
		filter.addAction(IndoorService.ACTION_INDOOR_STOP_SENSORS);
		registerReceiver(contextBroadcaster, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Unregister Context Broadcaster
		unregisterReceiver(contextBroadcaster);

		if (DEBUG)
			Log.d(TAG, TAG + " sensor terminated...");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		TAG = IndoorService.getSetting(getContentResolver(), IndoorPreferences.DEBUG_TAG).length() > 0 ? IndoorService
				.getSetting(getContentResolver(), IndoorPreferences.DEBUG_TAG) : TAG;
		DEBUG = IndoorService.getSetting(getContentResolver(), IndoorPreferences.DEBUG_FLAG).equals("true") ? true
				: false;
		if (DEBUG)
			Log.d(TAG, TAG + " sensor active...");
		return START_STICKY;
	}

	/**
	 * INDOOR Context Broadcaster<br/>
	 * - ACTION_INDOOR_CURRENT_CONTEXT: returns current plugin's context -
	 * ACTION_INDOOR_WEBSERVICE: push content provider data remotely -
	 * ACTION_INDOOR_CLEAN_DATABASES: clears local and remote database -
	 * ACTION_INDOOR_STOP_SENSORS: stops this sensor
	 */
	public class ContextBroadcaster extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(IndoorService.ACTION_INDOOR_CURRENT_CONTEXT)) {
				if (CONTEXT_PRODUCER != null) {
					CONTEXT_PRODUCER.onContext();
				}
			}
			if (intent.getAction().equals(IndoorService.ACTION_INDOOR_CLEAN_DATABASES)) {
				if (DATABASE_TABLES != null && CONTEXT_URIS != null) {
					for (int i = 0; i < DATABASE_TABLES.length; i++) {
						// Clear locally
						context.getContentResolver().delete(CONTEXT_URIS[i], null, null);
						if (IndoorService.DEBUG)
							Log.d(TAG, "Cleared " + CONTEXT_URIS[i].toString());
					}
				}
			}
			if (intent.getAction().equals(IndoorService.ACTION_INDOOR_STOP_SENSORS)) {
				if (IndoorService.DEBUG)
					Log.d(TAG, TAG + " stopped");
				stopSelf();
			}
		}
	}

	private ContextBroadcaster contextBroadcaster = new ContextBroadcaster();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
