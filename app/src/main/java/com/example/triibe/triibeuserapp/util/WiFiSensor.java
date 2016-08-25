package com.example.triibe.triibeuserapp.util;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * WiFi Module. Scans and returns surrounding WiFi AccessPoints devices
 * information and RSSI dB values.
 */
public class WiFiSensor extends BasicSensor {

	private static String TAG = "Indoor::WiFi";

	private static AlarmManager alarmManager = null;
	private static WifiManager wifiManager = null;
	private static PendingIntent wifiScan = null;
	private static Intent backgroundService = null;

	/**
	 * WiFi scanning interval in seconds (default = 60)
	 */
	private static int UPDATE_WIFI_INTERVAL = 60;

	/**
	 * Broadcasted event: new WiFi AP device detected
	 */
	public static final String ACTION_INDOOR_WIFI_NEW_DEVICE = "ACTION_INDOOR_WIFI_NEW_DEVICE";

	/**
	 * Broadcasted event: WiFi scan started
	 */
	public static final String ACTION_INDOOR_WIFI_SCAN_STARTED = "ACTION_INDOOR_WIFI_SCAN_STARTED";

	/**
	 * Broadcasted event: WiFi scan ended
	 */
	public static final String ACTION_INDOOR_WIFI_SCAN_ENDED = "ACTION_INDOOR_WIFI_SCAN_ENDED";

	/**
	 * Broadcast receiving event: request a WiFi scan
	 */
	public static final String ACTION_INDOOR_WIFI_REQUEST_SCAN = "ACTION_INDOOR_WIFI_REQUEST_SCAN";

	/**
	 * WIFI Service singleton object
	 */
	private static WiFiSensor wifiService = WiFiSensor.getService();

	/**
	 * Get an instance for the WiFi Service
	 * 
	 * @return WiFi obj
	 */
	public static WiFiSensor getService() {
		if (wifiService == null)
			wifiService = new WiFiSensor();
		return wifiService;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

		TAG = IndoorService.getSetting(getContentResolver(), "debug_tag").length() > 0 ? IndoorService.getSetting(
				getContentResolver(), "debug_tag") : TAG;

		UPDATE_WIFI_INTERVAL = Integer.parseInt(IndoorService.getSetting(getContentResolver(), "wifi_frequency"));

		if (!wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(true);

		DATABASE_TABLES = WiFiProvider.DATABASE_TABLES;
		TABLES_FIELDS = WiFiProvider.TABLES_FIELDS;
		CONTEXT_URIS = new Uri[] { WiFiProvider.WiFi_Data.CONTENT_URI, WiFiProvider.WiFi_Sensor.CONTENT_URI };

		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(wifiMonitor, filter);

		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		registerReceiver(wifiConnectMonitor, filter1);

		backgroundService = new Intent(this, BackgroundService.class);
		backgroundService.setAction(ACTION_INDOOR_WIFI_REQUEST_SCAN);

		wifiScan = PendingIntent.getService(this, 0, backgroundService, 0);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000,
				UPDATE_WIFI_INTERVAL * 1000, wifiScan);

		Log.d(TAG, "IndoorService.DEBUG: " + IndoorService.DEBUG);
		if (IndoorService.DEBUG)
			Log.d(TAG, "WiFi service created!");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		TAG = IndoorService.getSetting(getContentResolver(), "debug_tag").length() > 0 ? IndoorService.getSetting(
				getContentResolver(), "debug_tag") : TAG;

		if (Integer.parseInt(IndoorService.getSetting(getContentResolver(), "wifi_frequency")) != UPDATE_WIFI_INTERVAL) {

			UPDATE_WIFI_INTERVAL = Integer.parseInt(IndoorService.getSetting(getContentResolver(), "wifi_frequency"));

			alarmManager.cancel(wifiScan);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000,
					UPDATE_WIFI_INTERVAL * 1000, wifiScan);
		}

		if (IndoorService.DEBUG)
			Log.d(TAG, "WiFi service active... UPDATE_WIFI_INTERVAL: " + UPDATE_WIFI_INTERVAL);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(wifiMonitor);
		unregisterReceiver(wifiConnectMonitor);
		alarmManager.cancel(wifiScan);

		if (IndoorService.DEBUG)
			Log.d(TAG, "WiFi service terminated...");
	}

	private final IBinder wifiBinder = new WiFiBinder();

	/**
	 * Binder for WiFi module
	 */
	public class WiFiBinder extends Binder {
		WiFiSensor getService() {
			return WiFiSensor.getService();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return wifiBinder;
	}

	public static class WiFiMonitor extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				Intent backgroundService = new Intent(context, BackgroundService.class);
				backgroundService.setAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
				context.startService(backgroundService);
			}
		}
	}

	public static class WiFiConnectionMonitor extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
				SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

				if (SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED) {
					WifiScanner wifiScanner = new WifiScanner(context);
					ContentValues wifiConnectionInfo = wifiScanner.getConnectedInfo();

					// NB: added writing of the current connected WiFi.
					if (wifiConnectionInfo != null) {
						try {
							context.getContentResolver().insert(WiFiProvider.WiFi_Sensor.CONTENT_URI, wifiConnectionInfo);
							if (IndoorService.DEBUG)
								Log.d(TAG, "WiFi local sensor information: " + wifiConnectionInfo.toString());
						} catch (SQLiteException e) {
							if (IndoorService.DEBUG)
								Log.d(TAG, e.getMessage());
						} catch (SQLException e) {
							if (IndoorService.DEBUG)
								Log.d(TAG, e.getMessage());
						}
					}
				}
			}
		}
	}

	private static final WiFiMonitor wifiMonitor = new WiFiMonitor();
	private static final WiFiConnectionMonitor wifiConnectMonitor = new WiFiConnectionMonitor();

	/**
	 * Background service for WiFi module - ACTION_Indoor_WIFI_REQUEST_SCAN -
	 * {@link WifiManager#SCAN_RESULTS_AVAILABLE_ACTION} -
	 */
	public static class BackgroundService extends IntentService {
		public BackgroundService() {
			super(TAG + " background service");
		}

		@Override
		protected void onHandleIntent(Intent intent) {
			if (wifiManager == null)
				wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

			if (intent.getAction().equals(WiFiSensor.ACTION_INDOOR_WIFI_REQUEST_SCAN)) {
				if (!wifiManager.isWifiEnabled())
					wifiManager.setWifiEnabled(true);
				wifiManager.startScan();

				Intent scanStart = new Intent(ACTION_INDOOR_WIFI_SCAN_STARTED);
				sendBroadcast(scanStart);
			}

			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

				// NB: done adding nearby APs information.
				WifiScanner wifiScanner = new WifiScanner(getApplicationContext());
				ContentValues wifiConnectionInfo = wifiScanner.getConnectedInfo();
				String wifi_network = IndoorService.getSetting(getContentResolver(), "wifi_network");
				List<ContentValues> aps;

				wifi_network = IndoorService.getSetting( getContentResolver(), "wifi_network");
				if (wifi_network.equalsIgnoreCase("all")) {
					aps = wifiScanner.getNearbyAllAPs();
				} else if (wifiConnectionInfo != null) {
					wifi_network = (String) wifiConnectionInfo.get("ssid");
					aps = wifiScanner.getNearbyAPsBySSID(wifi_network);
				} else if (wifi_network.equals("")) {
					aps = wifiScanner.getNearbyAllAPs();
				} else {
					aps = wifiScanner.getNearbyAPsBySSID(wifi_network);
				}
				
				if (aps != null && aps.size() > 0)
					for (ContentValues ap : aps) {
						try {
							getContentResolver().insert(WiFiProvider.WiFi_Data.CONTENT_URI, ap);
						} catch (SQLiteException e) {
							if (IndoorService.DEBUG)
								Log.d(TAG, e.getMessage());
						} catch (SQLException e) {
							if (IndoorService.DEBUG)
								Log.d(TAG, e.getMessage());
						}

						if (IndoorService.DEBUG)
							Log.d(TAG, ACTION_INDOOR_WIFI_NEW_DEVICE + ">>" + ap.toString());
					}
			}
		}
	}
}
