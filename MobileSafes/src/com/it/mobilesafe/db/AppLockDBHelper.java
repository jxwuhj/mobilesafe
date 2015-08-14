package com.it.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppLockDBHelper extends SQLiteOpenHelper {

	private static final String TAG = "AppLockDBHelper";

	public AppLockDBHelper(Context context) {
		super(context, AppLockDB.DB	, null, AppLockDB.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = AppLockDB.AppLock.CREATE_SQL;
		Log.d(TAG, "sql :"+ sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
