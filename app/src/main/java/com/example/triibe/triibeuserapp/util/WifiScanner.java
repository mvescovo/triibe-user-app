package com.example.triibe.triibeuserapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiScanner {

	private WifiManager wifiManager = null;
	private String device_id = null;
	Context context;

	public WifiScanner(Context context) {
		this.context = context;
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		device_id = IndoorService.getSetting(context.getContentResolver(), IndoorPreferences.DEVICE_ID);
	}

	public ContentValues getConnectedInfo() {
		ContentValues rowData = null;

		if (!wifiManager.isWifiEnabled())
			Toast.makeText(context, "Wifi is turn off.", Toast.LENGTH_SHORT).show();
		else {
			WifiInfo wifi = wifiManager.getConnectionInfo();
			if (wifi != null && !wifi.getSSID().startsWith("0x")) {
				rowData = new ContentValues();
				rowData.put(WiFiProvider.WiFi_Sensor.DEVICE_ID, device_id);
				rowData.put(WiFiProvider.WiFi_Sensor.TIMESTAMP, System.currentTimeMillis());
				rowData.put(WiFiProvider.WiFi_Sensor.MAC_ADDRESS, wifi.getMacAddress());
				rowData.put(WiFiProvider.WiFi_Sensor.RSSI, wifi.getRssi());
				rowData.put(WiFiProvider.WiFi_Sensor.BSSID, ((wifi.getBSSID() != null) ? wifi.getBSSID() : ""));
				rowData.put(WiFiProvider.WiFi_Sensor.SSID, ((wifi.getSSID() != null) ? filterSSID(wifi.getSSID()) : ""));
				rowData.put(WiFiProvider.WiFi_Sensor.NETWORKID, wifi.getNetworkId());
			}
		}

		return rowData;
	}

	public List<ContentValues> getNearbyAllAPs() {
		List<ContentValues> ContentValuesList = null;

		if (!wifiManager.isWifiEnabled())
			Toast.makeText(context, "Wifi is turn off.", Toast.LENGTH_SHORT).show();
		else {
			ContentValuesList = new ArrayList<ContentValues>();
			List<ScanResult> aps = wifiManager.getScanResults();
			long currentTime = System.currentTimeMillis();

			for (ScanResult ap : aps) {
				ContentValues rowData = new ContentValues();
				rowData.put(WiFiProvider.WiFi_Data.DEVICE_ID, device_id);
				rowData.put(WiFiProvider.WiFi_Data.TIMESTAMP, currentTime);
				rowData.put(WiFiProvider.WiFi_Data.BSSID, ap.BSSID);
				rowData.put(WiFiProvider.WiFi_Data.SSID, ap.SSID);
				rowData.put(WiFiProvider.WiFi_Data.SECURITY, ap.capabilities);
				rowData.put(WiFiProvider.WiFi_Data.FREQUENCY, ap.frequency);
				rowData.put(WiFiProvider.WiFi_Data.RSSI, ap.level);
				rowData.put(WiFiProvider.WiFi_Data.LABEL, ap.describeContents());
				ContentValuesList.add(rowData);
			}
		}

		return ContentValuesList;
	}

	// One ssid needed to scan
	public List<ContentValues> getNearbyAPsBySSID(String Ssid) {
		List<ContentValues> ContentValuesList = null;

		if (!wifiManager.isWifiEnabled())
			Toast.makeText(context, "Wifi is turn off.", Toast.LENGTH_SHORT).show();
		else {
			ContentValuesList = new ArrayList<ContentValues>();
			List<ScanResult> aps = wifiManager.getScanResults();
			long currentTime = System.currentTimeMillis();
			for (ScanResult ap : aps) {
				if (Ssid.equals(ap.SSID)) {
					ContentValues rowData = new ContentValues();
					rowData.put(WiFiProvider.WiFi_Data.DEVICE_ID, device_id);
					rowData.put(WiFiProvider.WiFi_Data.TIMESTAMP, currentTime);
					rowData.put(WiFiProvider.WiFi_Data.BSSID, ap.BSSID);
					rowData.put(WiFiProvider.WiFi_Data.SSID, ap.SSID);
					rowData.put(WiFiProvider.WiFi_Data.SECURITY, ap.capabilities);
					rowData.put(WiFiProvider.WiFi_Data.FREQUENCY, ap.frequency);
					rowData.put(WiFiProvider.WiFi_Data.RSSI, ap.level);
					rowData.put(WiFiProvider.WiFi_Data.LABEL, ap.describeContents());
					ContentValuesList.add(rowData);
				}
			}
		}

		return ContentValuesList;
	}

	// Multiple ssids needed to scan
	public List<ContentValues> getNearbyAPsBySSID(String[] Ssids) {
		List<ContentValues> ContentValuesList = null;

		if (!wifiManager.isWifiEnabled())
			Toast.makeText(context, "Wifi is turn off.", Toast.LENGTH_SHORT).show();
		else {
			ContentValuesList = new ArrayList<ContentValues>();
			List<ScanResult> aps = wifiManager.getScanResults();
			long currentTime = System.currentTimeMillis();
			for (ScanResult ap : aps) {
				if (containSSID(ap.SSID, Ssids)) {
					ContentValues rowData = new ContentValues();
					rowData.put(WiFiProvider.WiFi_Data.DEVICE_ID, device_id);
					rowData.put(WiFiProvider.WiFi_Data.TIMESTAMP, currentTime);
					rowData.put(WiFiProvider.WiFi_Data.BSSID, ap.BSSID);
					rowData.put(WiFiProvider.WiFi_Data.SSID, ap.SSID);
					rowData.put(WiFiProvider.WiFi_Data.SECURITY, ap.capabilities);
					rowData.put(WiFiProvider.WiFi_Data.FREQUENCY, ap.frequency);
					rowData.put(WiFiProvider.WiFi_Data.RSSI, ap.level);
					rowData.put(WiFiProvider.WiFi_Data.LABEL, ap.describeContents());
					ContentValuesList.add(rowData);
				}
			}
		}

		return ContentValuesList;
	}

	public String filterSSID(String Ssid) {
		if (Ssid.startsWith("\"") && Ssid.endsWith("\"")) {
			return Ssid.substring(1, Ssid.length() - 1);
		} else
			return Ssid;
	}

	public boolean containSSID(String Ssid, String[] Ssids) {
		for (int i = 0; i < Ssids.length; i++) {
			if (Ssid.equals(Ssids[i])) {
				return true;
			}
		}
		return false;
	}

	// find an AP's info from nearby APs
	public ContentValues findAPByAddr(String addr, List<ContentValues> list) {
		if (!list.isEmpty())
			for (ContentValues value : list) {
				if (addr.equals(value.get(WiFiProvider.WiFi_Data.BSSID).toString())) {
					return value;
				}
			}
		return null;
	}
}
