package com.example.triibe.triibeuserapp.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityManager;

import com.example.triibe.triibeuserapp.R;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Preferences Allows to configure all the modules, start and stop modules.
 *
 */
public class IndoorPreferences extends PreferenceActivity {

	private static final IndoorService framework = IndoorService.getService();
	// private static Context mContext;
	// private static SensorManager mSensorMgr;
	private static SharedPreferences prefs;

	private static final int DIALOG_ERROR_ACCESSIBILITY = 1;
	private static final int DIALOG_ERROR_MISSING_PARAMETERS = 2;
	private static final int DIALOG_ERROR_MISSING_SENSOR = 3;

	/**
	 * Broadcast extra for ACTION_INDOOR_CONFIGURATION<br/>
	 * Value: setting (String)
	 */
	public static final String EXTRA_SET_SETTING = "setting";

	/**
	 * Broadcast extra for ACTION_INDOOR_CONFIGURATION<br/>
	 * Value: value (String)
	 */
	public static final String EXTRA_SET_SETTING_VALUE = "value";

	/**
	 * INDOOR preferences parameters
	 */
	public static final String DEVICE_ID = "device_id";
	public static final String DEBUG_FLAG = "debug_flag";
	public static final String DEBUG_TAG = "debug_tag";
	public static final String INDOOR_AUTO_UPDATE = "indoor_auto_update";
	public static final String WIFI_SENSOR = "wifi_sensor";
	public static final String WIFI_FREQUENCY = "wifi_frequency";
	public static final String WIFI_NETWORK = "wifi_network";

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
		case DIALOG_ERROR_ACCESSIBILITY:
			builder.setMessage("Please activate INDOOR on the Accessibility Services!");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent accessibilitySettings = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
					accessibilitySettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(accessibilitySettings);
				}
			});
			dialog = builder.create();
			break;
		case DIALOG_ERROR_MISSING_PARAMETERS:
			builder.setMessage("Some parameters are missing...");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog = builder.create();
			break;
		case DIALOG_ERROR_MISSING_SENSOR:
			builder.setMessage("This device is missing this sensor.");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// mContext = getApplicationContext();
		// mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Start the Indoor
		Intent startIndoor = new Intent(this, IndoorService.class);
		startService(startIndoor);

		loadPreferences();
	}

	private void loadPreferences() {
		addPreferencesFromResource(R.xml.indoor_preferences);
		PreferenceManager.setDefaultValues(this, R.xml.indoor_preferences, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		IndoorService.load_preferences(getContentResolver(), prefs);

		if (IndoorService.getSetting(getContentResolver(), "device_id").length() == 0) {
			UUID uuid = UUID.randomUUID();
			IndoorService.setSetting(getContentResolver(), "device_id", uuid.toString());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		setUIElements();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUIElements();
	}

	private void setUIElements() {
		developerOptions();
		servicesOptions();
	}

	/**
	 * Check if the accessibility service for INDOOR is active
	 * 
	 * @return boolean isActive
	 */
	private boolean isAccessibilityServiceActive() {
		AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
		if (accessibilityManager.isEnabled()) {
			List<ServiceInfo> accessibilityServices = accessibilityManager.getAccessibilityServiceList();
			for (ServiceInfo s : accessibilityServices) {
				if (s.name.equalsIgnoreCase("com.indoor.Applications")
						|| s.name.equalsIgnoreCase("com.indoor.ApplicationsJB")) {
					return true;
				}
			}
		}
		return false;
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

//			HttpResponse response = new Http().dataGET("http://www.indoorframework.com/index.php/indoordev/latest");
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
	 * Developer UI options - Debug flag - Debug tag - INDOOR updates - Device
	 * ID
	 */
	private void developerOptions() {
		final CheckBoxPreference debug_flag = (CheckBoxPreference) findPreference("debug_flag");
		debug_flag.setChecked(IndoorService.getSetting(getContentResolver(), "debug_flag").equals("true") ? true
				: false);
		debug_flag.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {

				IndoorService.DEBUG = debug_flag.isChecked();
				IndoorService.setSetting(getContentResolver(), "debug_flag", debug_flag.isChecked() ? "true" : "false");

				return true;
			}
		});

		final EditTextPreference debug_tag = (EditTextPreference) findPreference("debug_tag");
		debug_tag.setText(IndoorService.getSetting(getContentResolver(), "debug_tag"));
		debug_tag.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				IndoorService.TAG = (String) newValue;
				IndoorService.setSetting(getContentResolver(), "debug_tag", (String) newValue);
				return true;
			}
		});

		final CheckBoxPreference auto_update = (CheckBoxPreference) findPreference(INDOOR_AUTO_UPDATE);
		auto_update.setChecked(IndoorService.getSetting(getContentResolver(), INDOOR_AUTO_UPDATE).equals("true") ? true
				: false);

		PackageInfo indoorInfo = null;
		try {
			indoorInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		auto_update.setSummary("Current version is " + ((indoorInfo != null) ? indoorInfo.versionCode : "???"));
		auto_update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				IndoorService.setSetting(getContentResolver(), INDOOR_AUTO_UPDATE, auto_update.isChecked());
				if (auto_update.isChecked()) {
					new Update_Check().execute();
				}
				return true;
			}
		});

		final EditTextPreference device_id = (EditTextPreference) findPreference("device_id");
		device_id.setSummary("ID: " + IndoorService.getSetting(getContentResolver(), DEVICE_ID));
		device_id.setText(IndoorService.getSetting(getContentResolver(), "device_id"));
		device_id.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				IndoorService.setSetting(getContentResolver(), "device_id", (String) newValue);
				device_id.setSummary("ID: " + IndoorService.getSetting(getContentResolver(), DEVICE_ID));
				return true;
			}
		});
	}

	/**
	 * INDOOR services UI components
	 */
	private void servicesOptions() {
		wifi();
	}

	/**
	 * WiFi module settings UI
	 */
	private void wifi() {
		final CheckBoxPreference wifi = (CheckBoxPreference) findPreference("wifi_sensor");
		wifi.setChecked(IndoorService.getSetting(getContentResolver(), "wifi_sensor").equals("true") ? true : false);
		wifi.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				IndoorService.setSetting(getContentResolver(), "wifi_sensor", wifi.isChecked() ? "true" : "false");

				if (wifi.isChecked()) {
					framework.startWiFi();
				} else {
					framework.stopWiFi();
				}

				return true;
			}
		});

		final EditTextPreference wifiInterval = (EditTextPreference) findPreference("wifi_frequency");
		wifiInterval.setText(IndoorService.getSetting(getContentResolver(), "wifi_frequency"));
		wifiInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				IndoorService.setSetting(getContentResolver(), "wifi_frequency", (String) newValue);

				if (wifi.isChecked()) {
					framework.startWiFi();
				} else {
					framework.stopWiFi();
				}

				return true;
			}
		});

		final EditTextPreference wifiNetwork = (EditTextPreference) findPreference("wifi_network");
		wifiNetwork.setText(IndoorService.getSetting(getContentResolver(), "wifi_network"));
		wifiNetwork.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				IndoorService.setSetting(getContentResolver(), "wifi_network", (String) newValue);

				if (wifi.isChecked()) {
					framework.startWiFi();
				} else {
					framework.stopWiFi();
				}

				return true;
			}
		});
	}
}
