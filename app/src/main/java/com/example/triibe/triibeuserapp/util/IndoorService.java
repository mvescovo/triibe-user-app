package com.example.triibe.triibeuserapp.util;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.triibe.triibeuserapp.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Main service will start and manage all the services and settings.
 */
public class IndoorService extends Service {

	/**
	 * Debug flag (default = false).
	 */
	public static boolean DEBUG = false;

	/**
	 * Debug tag (default = "INDOOR").
	 */
	public static String TAG = "Indoor";

	/**
	 * Broadcasted event: indoorContext device information is available
	 */
	public static final String ACTION_INDOOR_DEVICE_INFORMATION = "ACTION_INDOOR_DEVICE_INFORMATION";

	/**
	 * Received broadcast on all modules - Sends the data to the defined
	 * webserver
	 */
	public static final String ACTION_INDOOR_WEBSERVICE = "ACTION_INDOOR_WEBSERVICE";

	/**
	 * Received broadcast on all modules<br/>
	 * - Cleans the data collected on the device
	 */
	public static final String ACTION_INDOOR_CLEAN_DATABASES = "ACTION_INDOOR_CLEAN_DATABASES";

	/**
	 * Received broadcast: change or add a new component configuration.<br/>
	 * Extras: {@link IndoorPreferences#EXTRA_SET_SETTING}<br/>
	 * {@link IndoorPreferences#EXTRA_SET_SETTING_VALUE}
	 */
	public static final String ACTION_INDOOR_CONFIGURATION = "ACTION_INDOOR_CONFIGURATION";

	/**
	 * Received broadcast: refresh the framework active sensors.<br/>
	 */
	public static final String ACTION_INDOOR_REFRESH = "ACTION_INDOOR_REFRESH";

	/**
	 * Received broadcast: plugins must implement indoorContext broadcast
	 * receiver to share their current status.
	 */
	public static final String ACTION_INDOOR_CURRENT_CONTEXT = "ACTION_INDOOR_CURRENT_CONTEXT";

	/**
	 * Used by plugin to stop all sensors
	 */
	public static final String ACTION_INDOOR_STOP_SENSORS = "ACTION_INDOOR_STOP_SENSORS";

	/**
	 * Notification ID that should be used by all core sensors in INDOOR
	 */
	public static final int NOTIFY_ID_INDOOR = 777;

	/**
	 * The framework's status check interval. By default is 5 minutes/300
	 * seconds
	 */
	private static final int STATUS_MONITOR_INTERVAL = 300;

	/**
	 * DownloadManager INDOOR update ID, used to prompt user to install the
	 * update once finished downloading.
	 */
	public static long INDOOR_FRAMEWORK_DOWNLOAD_ID = 0;

	private static AlarmManager alarmManager = null;
	private static PendingIntent repeatingIntent = null;
	private static Context indoorContext = null;

	private static Intent indoorStatusMonitor = null;
	private static Intent wifiSrv = null;

	/**
	 * Singleton instance of the framework
	 */
	private static IndoorService indoorSrv = IndoorService.getService();

	/**
	 * Get the singleton instance to the INDOOR framework
	 * 
	 * @return {@link IndoorService}
	 */
	public static IndoorService getService() {
		if (indoorSrv == null)
			indoorSrv = new IndoorService();
		return indoorSrv;
	}

	/**
	 * Activity-Service binder
	 */
	private final IBinder serviceBinder = new ServiceBinder();

	public class ServiceBinder extends Binder {
		IndoorService getService() {
			return IndoorService.getService();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return serviceBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		indoorContext = getApplicationContext();
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		PreferenceManager.setDefaultValues(this, R.xml.indoor_preferences, false);

		DEBUG = IndoorService.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEBUG_FLAG).equals(
				"true");
		TAG = IndoorService.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEBUG_TAG).length() > 0 ? IndoorService
				.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEBUG_TAG) : TAG;

		indoorStatusMonitor = new Intent(indoorContext, IndoorService.class);
		repeatingIntent = PendingIntent.getService(indoorContext, 0, indoorStatusMonitor, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000,
				STATUS_MONITOR_INTERVAL * 1000, repeatingIntent);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addDataScheme("file");
		indoorContext.registerReceiver(storageBroadCast, filter);

		filter = new IntentFilter();
		filter.addAction(IndoorService.ACTION_INDOOR_CLEAN_DATABASES);
		filter.addAction(IndoorService.ACTION_INDOOR_CONFIGURATION);
		filter.addAction(IndoorService.ACTION_INDOOR_REFRESH);
		filter.addAction(IndoorService.ACTION_INDOOR_WEBSERVICE);
		filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		indoorContext.registerReceiver(indoorBroadCast, filter);

		if (IndoorService.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEVICE_ID).length() == 0) {
			UUID uuid = UUID.randomUUID();
			IndoorService.setSetting(indoorContext.getContentResolver(), IndoorPreferences.DEVICE_ID, uuid.toString());
		}

		getDeviceInfo();
		new AsyncPing().execute();

		if (IndoorService.DEBUG)
			Log.d(TAG, "INDOOR framework is created!");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		DEBUG = IndoorService.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEBUG_FLAG).equals(
				"true");
		TAG = IndoorService.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEBUG_TAG).length() > 0 ? IndoorService
				.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEBUG_TAG) : TAG;

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if (IndoorService.DEBUG)
				Log.d(TAG, "INDOOR framework is active...");
			startAllServices();

			if (IndoorService.getSetting(getContentResolver(), IndoorPreferences.INDOOR_AUTO_UPDATE).equals("true")) {
				new Update_Check().execute();
			}
		} else {
			stopAllServices();

			if (IndoorService.DEBUG)
				Log.w(TAG, "INDOOR framework is on hold...");
		}

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		alarmManager.cancel(repeatingIntent);
		// alarmManager.cancel(webserviceUploadIntent);

		indoorContext.unregisterReceiver(indoorBroadCast);
		indoorContext.unregisterReceiver(storageBroadCast);
	}

	public static void load_preferences(ContentResolver cr, SharedPreferences prefs) {
		Map<String, ?> defaults = prefs.getAll();
		for (Map.Entry<String, ?> entry : defaults.entrySet()) {
			if (IndoorService.getSetting(cr, entry.getKey()).length() == 0) {
				IndoorService.setSetting(cr, entry.getKey(), entry.getValue());
			}
		}
	}

	private class AsyncPing extends AsyncTask<Void, Void, Void> {
		private String DEVICE_ID = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DEVICE_ID = IndoorService.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEVICE_ID);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Ping INDOOR's server with indoorContext device's information for
			// framework's statistics log
			ArrayList<NameValuePair> device_ping = new ArrayList<NameValuePair>();
			device_ping.add(new BasicNameValuePair("device_id", DEVICE_ID));
			device_ping.add(new BasicNameValuePair("ping", "" + System.currentTimeMillis()));
//			new Http()
//					.dataPOST("http://www.indoorframework.com/index.php/indoordev/alive", device_ping);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// if (IndoorService.getSetting(getContentResolver(),
			// IndoorPreferences.STATUS_WEBSERVICE).equals("true")) {
			// sendBroadcast(new
			// Intent(IndoorService.ACTION_INDOOR_WEBSERVICE));
			// }
		}
	}

	private void getDeviceInfo() {
		Cursor deviceInfo = indoorContext.getContentResolver().query(IndoorProvider.Indoor_Device.CONTENT_URI, null, null, null, null);

		if (deviceInfo == null || !deviceInfo.moveToFirst()) {
			ContentValues rowData = new ContentValues();
			rowData.put("timestamp", System.currentTimeMillis());
			rowData.put("device_id",
					IndoorService.getSetting(indoorContext.getContentResolver(), IndoorPreferences.DEVICE_ID));
			rowData.put("board", Build.BOARD);
			rowData.put("brand", Build.BRAND);
			rowData.put("device", Build.DEVICE);
			rowData.put("build_id", Build.DISPLAY);
			rowData.put("hardware", Build.HARDWARE);
			rowData.put("manufacturer", Build.MANUFACTURER);
			rowData.put("model", Build.MODEL);
			rowData.put("product", Build.PRODUCT);
			rowData.put("serial", Build.SERIAL);
			rowData.put("release", Build.VERSION.RELEASE);
			rowData.put("release_type", Build.TYPE);
			rowData.put("sdk", Build.VERSION.SDK_INT);

			try {
				indoorContext.getContentResolver().insert(IndoorProvider.Indoor_Device.CONTENT_URI, rowData);

				Intent deviceData = new Intent(ACTION_INDOOR_DEVICE_INFORMATION);
				sendBroadcast(deviceData);

				if (IndoorService.DEBUG)
					Log.d(TAG, "Device information:" + rowData.toString());

			} catch (SQLiteException e) {
				if (IndoorService.DEBUG)
					Log.d(TAG, e.getMessage());
			} catch (SQLException e) {
				if (IndoorService.DEBUG)
					Log.d(TAG, e.getMessage());
			}
		}
		if (deviceInfo != null && !deviceInfo.isClosed())
			deviceInfo.close();
	}

	private class Update_Check extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			PackageInfo indoorPkg = null;
			try {
				indoorPkg = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
			} catch (NameNotFoundException e1) {
				e1.printStackTrace();
				return null;
			}

//			HttpResponse response = new Http()
//					.dataGET("http://www.indoorframework.com/index.php/indoordev/framework_latest");
//			if (response != null && response.getStatusLine().getStatusCode() == 200) {
//				try {
//					JSONArray data = new JSONArray(EntityUtils.toString(response.getEntity()));
//					JSONObject latest_framework = data.getJSONObject(0);
//
//					if (IndoorService.DEBUG)
//						Log.d(IndoorService.TAG, "Latest:" + latest_framework.toString());
//
//					String filename = latest_framework.getString("filename");
//					int version = latest_framework.getInt("version");
//					String whats_new = latest_framework.getString("whats_new");
//
//					if (version > indoorPkg.versionCode) {
//						update_framework(filename, whats_new);
//					}
//				} catch (ParseException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			} else {
//				if (IndoorService.DEBUG)
//					Log.d(IndoorService.TAG, "Unable to fetch latest framework from INDOOR repository...");
//			}

			return null;
		}

		private void update_framework(String filename, String whats_new) {
			// Make sure we have the releases folder
			File releases = new File(Environment.getExternalStorageDirectory() + "/INDOOR/releases/");
			releases.mkdirs();

			String url = "http://www.indoorframework.com/releases/" + filename;

			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
			request.setDescription(whats_new);
			request.setTitle("INDOOR");
			request.setDestinationInExternalPublicDir("/", "INDOOR/releases/" + filename);
			DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			IndoorService.INDOOR_FRAMEWORK_DOWNLOAD_ID = manager.enqueue(request);
		}
	}

	/**
	 * BroadcastReceiver that monitors for INDOOR framework actions: -
	 * ACTION_INDOOR_WEBSERVICE = upload data to remote webservice server. -
	 * ACTION_INDOOR_CLEAN_DATABASES = clears local device's INDOOR modules
	 * databases. - ACTION_INDOOR_CONFIGURATION = change settings from the
	 * framework. - ACTION_INDOOR_REFRESH - apply changes to the configuration.
	 * - {@link DownloadManager#ACTION_DOWNLOAD_COMPLETE}
	 * 
	 */
	public static class IndoorBroadcaster extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			// String[] DATABASE_TABLES = IndoorProvider.DATABASE_TABLES;
			// String[] TABLES_FIELDS = IndoorProvider.TABLES_FIELDS;
			Uri[] CONTEXT_URIS = new Uri[] { IndoorProvider.Indoor_Device.CONTENT_URI };

			if (intent.getAction().equals(IndoorService.ACTION_INDOOR_CLEAN_DATABASES)) {
				context.getContentResolver().delete(IndoorProvider.Indoor_Device.CONTENT_URI, null, null);
				if (IndoorService.DEBUG)
					Log.d(TAG, "Cleared " + CONTEXT_URIS[0]);
			}

			if (intent.getAction().equals(IndoorService.ACTION_INDOOR_CONFIGURATION)) {
				setSetting(context.getContentResolver(), intent.getStringExtra(IndoorProvider.Indoor_Settings.SETTING_KEY),
						intent.getStringExtra(IndoorProvider.Indoor_Settings.SETTING_VALUE));
				Intent restart = new Intent(context, IndoorService.class);
				context.startService(restart);
			}

			if (intent.getAction().equals(IndoorService.ACTION_INDOOR_REFRESH)) {
				Intent refresh = new Intent(context, IndoorService.class);
				context.startService(refresh);
			}

			if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
				long downloaded_id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

				if (downloaded_id == INDOOR_FRAMEWORK_DOWNLOAD_ID) {

					if (IndoorService.DEBUG)
						Log.d(IndoorService.TAG, "INDOOR update received...");

					Query qry = new Query();
					qry.setFilterById(INDOOR_FRAMEWORK_DOWNLOAD_ID);
					Cursor data = manager.query(qry);
					if (data != null && data.moveToFirst()) {
						if (data.getInt(data.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
							String filePath = data.getString(data.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
							File mFile = new File(Uri.parse(filePath).getPath());
							Intent promptUpdate = new Intent(Intent.ACTION_VIEW);
							promptUpdate.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
							promptUpdate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(promptUpdate);
						}
					}
					if (data != null && !data.isClosed())
						data.close();
				}
			}
		}
	}

	private static final IndoorBroadcaster indoorBroadCast = new IndoorBroadcaster();

	public static class StorageBroadcaster extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
				if (IndoorService.DEBUG)
					Log.d(TAG, "Resuming INDOOR data logging...");
			}
			if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				if (IndoorService.DEBUG)
					Log.w(TAG, "Stopping INDOOR data logging until the SDCard is available again...");
			}
			Intent indoor = new Intent(context, IndoorService.class);
			context.startService(indoor);
		}
	}

	private static final StorageBroadcaster storageBroadCast = new StorageBroadcaster();

	/**
	 * Start active services
	 */
	protected void startAllServices() {
		if (IndoorService.getSetting(indoorContext.getContentResolver(), IndoorPreferences.WIFI_SENSOR).equals("true")) {
			startWiFi();
		} else
			stopWiFi();
	}

	/**
	 * Stop all services
	 */
	protected void stopAllServices() {
		stopWiFi();
	}

	/**
	 * Start the WiFi module
	 */
	protected void startWiFi() {
		if (wifiSrv == null)
			wifiSrv = new Intent(indoorContext, WiFiSensor.class);
		if (IndoorService.DEBUG)
			Log.d(TAG, "indoorContext -- >start WiFiSensor..");
		indoorContext.startService(wifiSrv);
	}

	protected void stopWiFi() {
		if (wifiSrv != null)
			indoorContext.stopService(wifiSrv);
	}

	/**
	 * Retrieve setting value by given key.
	 * 
	 * @return String value
	 */
	public static String getSetting(ContentResolver resolver, String key) {
		String value = "";

		Cursor qry = resolver.query(IndoorProvider.Indoor_Settings.CONTENT_URI, null, IndoorProvider.Indoor_Settings.SETTING_KEY + " LIKE '" + key
				+ "'", null, null);
		if (qry != null && qry.moveToFirst()) {
			value = qry.getString(qry.getColumnIndex(IndoorProvider.Indoor_Settings.SETTING_VALUE));
		}
		if (qry != null && !qry.isClosed())
			qry.close();
		return value;
	}

	/**
	 * Insert / Update settings of the framework
	 * 
	 * @return boolean if successful
	 */
	public static void setSetting(ContentResolver resolver, String key, Object value) {
		ContentValues setting = new ContentValues();
		setting.put(IndoorProvider.Indoor_Settings.SETTING_KEY, key);
		setting.put(IndoorProvider.Indoor_Settings.SETTING_VALUE, value.toString());

		Cursor qry = resolver.query(IndoorProvider.Indoor_Settings.CONTENT_URI, null, IndoorProvider.Indoor_Settings.SETTING_KEY + " LIKE '" + key
				+ "'", null, null);
		// update
		if (qry != null && qry.moveToFirst()) {
			try {
				if (!qry.getString(qry.getColumnIndex(IndoorProvider.Indoor_Settings.SETTING_VALUE)).equals(value.toString())) {
					resolver.update(
							IndoorProvider.Indoor_Settings.CONTENT_URI,
							setting,
							IndoorProvider.Indoor_Settings.SETTING_ID + "="
									+ qry.getInt(qry.getColumnIndex(IndoorProvider.Indoor_Settings.SETTING_ID)), null);
					if (IndoorService.DEBUG)
						Log.d(IndoorService.TAG, "Updated: " + key + "=" + value);
				}
			} catch (SQLiteException e) {
				if (IndoorService.DEBUG)
					Log.d(TAG, e.getMessage());
			} catch (SQLException e) {
				if (IndoorService.DEBUG)
					Log.d(TAG, e.getMessage());
			}
			// insert
		} else {
			try {
				resolver.insert(IndoorProvider.Indoor_Settings.CONTENT_URI, setting);
				if (IndoorService.DEBUG)
					Log.d(IndoorService.TAG, "Added: " + key + "=" + value);
				qry.close();
			} catch (SQLiteException e) {
				if (IndoorService.DEBUG)
					Log.d(TAG, e.getMessage());
			} catch (SQLException e) {
				if (IndoorService.DEBUG)
					Log.d(TAG, e.getMessage());
			}
		}
		if (qry != null && !qry.isClosed())
			qry.close();
	}
}
