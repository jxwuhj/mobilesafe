package com.it.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlackListDbHelper extends SQLiteOpenHelper {

	private static final String TAG = "BlackListDB";

	public BlackListDbHelper(Context context) {
		super(context, BlacklistDB.DB_NAME, null, BlacklistDB.DB_VERSION);
		
	}

	//创建数据库的的时候使用
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql=BlacklistDB.Table_db.SQL;
		Log.d(TAG, "sql :"+ sql);
		db.execSQL(sql);
	}

	//数据库升级的时候使用
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
