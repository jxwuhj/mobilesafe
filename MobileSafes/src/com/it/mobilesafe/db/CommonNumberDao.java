package com.it.mobilesafe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {
	

	/**
	 * 查询具有多少个group的数量
	 * @param context
	 * @return
	 */
	public static int findGroupCount(Context context) {
		  int count = 0;
		
		String path = new File(context.getFilesDir(),
				"commonnum.db").getAbsolutePath();
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, 
				SQLiteDatabase.OPEN_READONLY);
		
		//select count(*) from tableName
		String sql = "select count(1) from classlist";
		
		Cursor cursor = db.rawQuery(sql, new String[]{});
		
		if(cursor!=null) {
			
			while(cursor.moveToNext()) {
				
				count = cursor.getInt(0);
			}
			
			cursor.close();
		}
		db.close();
		
		return count;
		
	}
	/**
	 * 查询groupPosition的name 下文本内容   --  text(TextView)
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static String getGroupText(Context context,int groupPosition) {
		String name = "";
		String path = new File(context.getFilesDir(),
				"commonnum.db").getAbsolutePath();
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, 
				SQLiteDatabase.OPEN_READONLY);
		// sql = "select name from classlist where idx=1;"
		String sql = "select name from classlist where idx=?";
		
		//注意这里的groupPosition是从0开始,所有查询从+1开始
		Cursor cursor = db.rawQuery(sql, new String[]{groupPosition+1+""});
		
		if(cursor!=null) {
			while(cursor.moveToNext()) {
				 name = cursor.getString(0);
			}
			cursor.close();
		}
		db.close();
		
		return name;
		
	}
	
	/**
	 * 根据groupPosition以及childPosition 查询一个item目录下孩子的name以及number
	 * @param context
	 * @param groupPosition
	 * @param childPosition
	 * @return
	 */
	public static String[] getChildText(Context context,int groupPosition,int childPosition) {
		String name = "";
		String number = "";
		String path = new File(context.getFilesDir(),
				"commonnum.db").getAbsolutePath();
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, 
				SQLiteDatabase.OPEN_READONLY);
		//sql =  select name from table1 where _id=1;
		
		String sql = "select name,number from table"+(groupPosition+1)+" where _id=?";
		
		//注意这里的groupPosition是从0开始,所有查询从+1开始
		Cursor cursor = db.rawQuery(sql, new String[]{(childPosition+1)+""});
		
		if(cursor!=null) {
			while(cursor.moveToNext()) {
				 name = cursor.getString(0);
				  number = cursor.getString(1);
			}
			cursor.close();
		}
		db.close();
		
		return new String[]{name,number};
		
	}
	
	/**
	 * 一个group目录下有多少个item(孩子)
	 * @param context
	 * @param groupPosition
	 * @return
	 */
	public static int findChildCount(Context context,int groupPosition) {
		  int count = 0;
		
		String path = new File(context.getFilesDir(),
				"commonnum.db").getAbsolutePath();
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, 
				SQLiteDatabase.OPEN_READONLY);
		
		//select count(*) from tableName    : 可以理解查询数据库中一张表中存在着多少行 数据
		String sql = "select count(1) from table"+(groupPosition + 1);
		
		Cursor cursor = db.rawQuery(sql, new String[]{});
		
		if(cursor!=null) {
			
			while(cursor.moveToNext()) {
				
				count = cursor.getInt(0);
			}
			
			cursor.close();
		}
		db.close();
		
		return count;
		
	}
	
	
}


