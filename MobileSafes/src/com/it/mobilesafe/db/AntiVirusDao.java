package com.it.mobilesafe.db;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {
	
	/**
	 * 根据病毒库中的特征码（md5），来配置手机中文件是否是病毒
	 * 
	 * @param context
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(Context context,String md5) {
		
		//文件存储绝对路径
		String path = new File(context.getFilesDir(),"antivirus.db").getAbsolutePath();
		//获取数据库对象
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		
		//select count(1) from datable where md5='97cc29f97dfa01be219ccfe497e5a690'
		String sql = "select count(1) from datable where md5=?";
		Cursor cursor = db.rawQuery(sql, new String[]{md5});
		
		int count = 0;
		if(cursor!=null) {
			while(cursor.moveToNext()) {
				//查询不是获取其值，而是看影响了多少行和列
				count = cursor.getInt(0);
			}
			
			cursor.close();
		}
		db.close();
		
		return count>0;
	}
	
	/**
	 * 插入一条数据
	 *
	 * @param context
	 * @param md5
	 */
	public static boolean add(Context context,String md5) {
		
		String path = new File(context.getFilesDir(),"antivirus.db").getAbsolutePath();
		//获取数据库对象
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		
		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("type", "6");
		values.put("name", "Android.Adware.AirAD.a");
		values.put("desc", "恶意后台扣费,病毒木马程序");
		long insert = db.insert("datable", null, values);
		
		
		db.close();
		return insert>0;
	}

}
