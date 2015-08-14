package com.it.mobilesafe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class AddressDao {
	

	public static  String findAddress(Context context,String number) {
		
		String address = null;
		
		String path = new File(context.getFilesDir(),"address.db").getAbsolutePath();
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path,
				null, SQLiteDatabase.OPEN_READONLY);
		
		//sql  : select cardtype from info where mobileprefix = "1351234";
		
		String sql = "select cardtype from info where mobileprefix=?";
		
		//手机号码的特点
		// ^1[34578]\d{9}$
		boolean isMatches = number.matches("^1[34578]\\d{9}$");
		
		if(isMatches) {
			//手机号码的查询
			String subNum = number.substring(0,7);
			String[] selectionArgs = new String[]{subNum};
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if(cursor!=null) {
				while(cursor.moveToNext()) {
					address = cursor.getString(0);
					return address;
				}
				cursor.close();
			} 
		} else {
			//非手机
			int length = number.length();
			
			switch (length) {
			case 3:
				address = "紧急电话";
				break;
				
			case 4:
				address = "模拟器";
				break;
				
			case 5:
				address = "服务号码";
				break;
			case 7:
			case 8:
				address = "本地座机";
				break;
			case 10:
			case 11:
			case 12:
				String prefix = number.substring(0, 3);
				sql = "select city from info where area=?";
				Cursor cursor = db.rawQuery(sql, new String[]{prefix});
				if(cursor!=null) {
					while(cursor.moveToNext()) {
						address = cursor.getString(0);
						return address;
					}
					cursor.close();
				}
				
				if(TextUtils.isEmpty(address)) {
					prefix = number.substring(0, 4);
					sql = "select city from info where area=?";
					 cursor = db.rawQuery(sql, new String[]{prefix});
					if(cursor!=null) {
						while(cursor.moveToNext()) {
							address = cursor.getString(0);
							return address;
						}
						cursor.close();
					}
				}
				
				if(TextUtils.isEmpty(address)) {
					address = "未知";
				}
				
				break;
			default:
				address = "未知";
				break;
			}
		}
		
		
		db.close();
		
		return address;
	}

}
