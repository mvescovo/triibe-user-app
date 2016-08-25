package com.example.triibe.triibeuserapp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * database helper
 */
public class DBHelper extends SQLiteOpenHelper {

	private final boolean DEBUG = true;
	private final String TAG = "DBHelper";

	private final String[] database_tables;
	private final String[] table_fields;
	private final int new_version;

	public DBHelper(Context context, String database_name, CursorFactory cursor_factory, int database_version,
					String[] database_tables, String[] table_fields) {
		super(context, database_name, cursor_factory, database_version);

		this.database_tables = database_tables;
		this.table_fields = table_fields;
		this.new_version = database_version;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (DEBUG)
			Log.w(TAG, "Database in use: " + db.getPath());

		for (int i = 0; i < database_tables.length; i++) {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + database_tables[i] + " (" + table_fields[i] + ");");
		}
		db.setVersion(new_version);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (DEBUG)
			Log.w(TAG, "Upgrading database: " + db.getPath());

		for (int i = 0; i < database_tables.length; i++) {
			db.execSQL("DROP TABLE IF EXISTS " + database_tables[i]);
		}
		onCreate(db);
	}
}