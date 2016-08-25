package com.example.triibe.triibeuserapp.util;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.HashMap;

/**
 * Indoor framework content provider - Device information - Framework settings
 */
public class IndoorProvider extends ContentProvider {

	public static final int DATABASE_VERSION = 1;

	/**
	 * Indoor framework content authority
	 */
	public static final String AUTHORITY = "com.example.triibe.triibeuserapp.util.indoor";

	private static final int DEVICE_INFO = 1;
	private static final int DEVICE_INFO_ID = 2;
	private static final int SETTING = 3;
	private static final int SETTING_ID = 4;

	/**
	 * Information about the device in which the framework is installed.
	 * 
	 */
	public static final class Indoor_Device implements BaseColumns {
		private Indoor_Device() {
		};

		public static final Uri CONTENT_URI = Uri.parse("content://" + IndoorProvider.AUTHORITY + "/indoor_device");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.indoor.device";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.indoor.device";

		public static final String _ID = "_id";
		public static final String TIMESTAMP = "timestamp";
		public static final String DEVICE_ID = "device_id";
		public static final String BOARD = "board";
		public static final String BRAND = "brand";
		public static final String DEVICE = "device";
		public static final String BUILD_ID = "build_id";
		public static final String HARDWARE = "hardware";
		public static final String MANUFACTURER = "manufacturer";
		public static final String MODEL = "model";
		public static final String PRODUCT = "product";
		public static final String SERIAL = "serial";
		public static final String RELEASE = "release";
		public static final String RELEASE_TYPE = "release_type";
		public static final String SDK = "sdk";
	}

	/**
	 * Indoor settings
	 */
	public static final class Indoor_Settings implements BaseColumns {
		private Indoor_Settings() {
		};

		public static final Uri CONTENT_URI = Uri.parse("content://" + IndoorProvider.AUTHORITY + "/indoor_settings");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.indoor.settings";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.indoor.settings";

		public static final String SETTING_ID = "_id";
		public static final String SETTING_KEY = "key";
		public static final String SETTING_VALUE = "value";
	}

	public static String DATABASE_NAME = "indoor.db";
	public static final String[] DATABASE_TABLES = { "indoor_device", "indoor_settings" };
	public static final String[] TABLES_FIELDS = {
			// Device information
			Indoor_Device._ID + " integer primary key autoincrement," + Indoor_Device.TIMESTAMP + " real default 0,"
					+ Indoor_Device.DEVICE_ID + " text default ''," + Indoor_Device.BOARD + " text default '',"
					+ Indoor_Device.BRAND + " text default ''," + Indoor_Device.DEVICE + " text default '',"
					+ Indoor_Device.BUILD_ID + " text default ''," + Indoor_Device.HARDWARE + " text default '',"
					+ Indoor_Device.MANUFACTURER + " text default ''," + Indoor_Device.MODEL + " text default '',"
					+ Indoor_Device.PRODUCT + " text default ''," + Indoor_Device.SERIAL + " text default '',"
					+ Indoor_Device.RELEASE + " text default ''," + Indoor_Device.RELEASE_TYPE + " text default '',"
					+ Indoor_Device.SDK + " integer default 0," + "UNIQUE (" + Indoor_Device.TIMESTAMP + ","
					+ Indoor_Device.DEVICE_ID + ")",

			// Settings
			Indoor_Settings.SETTING_ID + " integer primary key autoincrement," + Indoor_Settings.SETTING_KEY
					+ " text default ''," + Indoor_Settings.SETTING_VALUE + " text default ''" };

	private static UriMatcher sUriMatcher = null;
	private static HashMap<String, String> deviceMap = null;
	private static HashMap<String, String> settingsMap = null;

	private static DBHelper databaseHelper = null;
	private static SQLiteDatabase database = null;

	@Override
	public boolean onCreate() {
		if (databaseHelper == null)
			databaseHelper = new DBHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES,
					TABLES_FIELDS);
		database = databaseHelper.getWritableDatabase();
		return (databaseHelper != null);
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(IndoorProvider.AUTHORITY, DATABASE_TABLES[0], DEVICE_INFO);
		sUriMatcher.addURI(IndoorProvider.AUTHORITY, DATABASE_TABLES[0] + "/#", DEVICE_INFO_ID);
		sUriMatcher.addURI(IndoorProvider.AUTHORITY, DATABASE_TABLES[1], SETTING);
		sUriMatcher.addURI(IndoorProvider.AUTHORITY, DATABASE_TABLES[1] + "/#", SETTING_ID);

		deviceMap = new HashMap<String, String>();
		deviceMap.put(Indoor_Device._ID, Indoor_Device._ID);
		deviceMap.put(Indoor_Device.TIMESTAMP, Indoor_Device.TIMESTAMP);
		deviceMap.put(Indoor_Device.DEVICE_ID, Indoor_Device.DEVICE_ID);
		deviceMap.put(Indoor_Device.BOARD, Indoor_Device.BOARD);
		deviceMap.put(Indoor_Device.BRAND, Indoor_Device.BRAND);
		deviceMap.put(Indoor_Device.DEVICE, Indoor_Device.DEVICE);
		deviceMap.put(Indoor_Device.BUILD_ID, Indoor_Device.BUILD_ID);
		deviceMap.put(Indoor_Device.HARDWARE, Indoor_Device.HARDWARE);
		deviceMap.put(Indoor_Device.MANUFACTURER, Indoor_Device.MANUFACTURER);
		deviceMap.put(Indoor_Device.MODEL, Indoor_Device.MODEL);
		deviceMap.put(Indoor_Device.PRODUCT, Indoor_Device.PRODUCT);
		deviceMap.put(Indoor_Device.SERIAL, Indoor_Device.SERIAL);
		deviceMap.put(Indoor_Device.RELEASE, Indoor_Device.RELEASE);
		deviceMap.put(Indoor_Device.RELEASE_TYPE, Indoor_Device.RELEASE_TYPE);
		deviceMap.put(Indoor_Device.SDK, Indoor_Device.SDK);

		settingsMap = new HashMap<String, String>();
		settingsMap.put(Indoor_Settings.SETTING_ID, Indoor_Settings.SETTING_ID);
		settingsMap.put(Indoor_Settings.SETTING_KEY, Indoor_Settings.SETTING_KEY);
		settingsMap.put(Indoor_Settings.SETTING_VALUE, Indoor_Settings.SETTING_VALUE);

	}

	/**
	 * Delete entry from the database
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (database == null || !database.isOpen())
			database = databaseHelper.getWritableDatabase();

		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case DEVICE_INFO:
			count = database.delete(DATABASE_TABLES[0], selection, selectionArgs);
			break;
		case SETTING:
			count = database.delete(DATABASE_TABLES[1], selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case DEVICE_INFO:
			return Indoor_Device.CONTENT_TYPE;
		case DEVICE_INFO_ID:
			return Indoor_Device.CONTENT_ITEM_TYPE;
		case SETTING:
			return Indoor_Settings.CONTENT_TYPE;
		case SETTING_ID:
			return Indoor_Settings.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/**
	 * Insert entry to the database
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (database == null || !database.isOpen())
			database = databaseHelper.getWritableDatabase();

		ContentValues values = (initialValues != null) ? new ContentValues(initialValues) : new ContentValues();

		switch (sUriMatcher.match(uri)) {
		case DEVICE_INFO:
			long dev_id = database.insert(DATABASE_TABLES[0], Indoor_Device.DEVICE_ID, values);
			if (dev_id > 0) {
				Uri devUri = ContentUris.withAppendedId(Indoor_Device.CONTENT_URI, dev_id);
				getContext().getContentResolver().notifyChange(devUri, null);
				return devUri;
			}
			throw new SQLException("Failed to insert row into " + uri);
		case SETTING:
			long sett_id = database.insert(DATABASE_TABLES[1], Indoor_Settings.SETTING_KEY, values);
			if (sett_id > 0) {
				Uri settUri = ContentUris.withAppendedId(Indoor_Settings.CONTENT_URI, sett_id);
				getContext().getContentResolver().notifyChange(settUri, null);
				return settUri;
			}
			throw new SQLException("Failed to insert row into " + uri);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/**
	 * Query entries from the database
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (database == null || !database.isOpen())
			database = databaseHelper.getWritableDatabase();

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case DEVICE_INFO:
			qb.setTables(DATABASE_TABLES[0]);
			qb.setProjectionMap(deviceMap);
			break;
		case SETTING:
			qb.setTables(DATABASE_TABLES[1]);
			qb.setProjectionMap(settingsMap);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		try {
			Cursor c = qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		} catch (IllegalStateException e) {
			Log.e("Indoor_Provider", e.getMessage());
			return null;
		}
	}

	/**
	 * Update application on the database
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (database == null || !database.isOpen())
			database = databaseHelper.getWritableDatabase();
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case DEVICE_INFO:
			count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
			break;
		case SETTING:
			count = database.update(DATABASE_TABLES[1], values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
