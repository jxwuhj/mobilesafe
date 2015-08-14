package com.it.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.it.mobilesafe.bean.BlackInfo;

/*操作数据库的类  增,删,改,查     -- _id是自增类型, number text类型,属于值唯一的类型;type是integer类型
 * 增加   number,type
 * 删除   根据电话号码 来删除  一个item的信息
 * 修改   根据number来改type
 * 查询  -- 查询表中所有的信息 --  直接显示在ListView 中,分页  -- 显示
 * */
public class BlackDao {

	private BlackListDbHelper helper;

	public BlackDao(Context context) {
		helper = new BlackListDbHelper(context);
	}

	/**
	 * 添加数据到blacklist表中
	 * 
	 * @param number
	 * @param type
	 * @return
	 */

	public boolean add(String number, int type) {

		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(BlacklistDB.Table_db.COLUMN_NUMBER, number);
		values.put(BlacklistDB.Table_db.COLUMN_TYPE, type);
		long insert = db.insert(BlacklistDB.Table_db.TABLE_NAME, null, values);

		db.close();

		return insert != -1;
	}

	/**
	 * 根据号码来删除 item信息
	 * 
	 * @param number
	 * @return 当返回0 说明delete()方法这个操作没有对表中数据做成任何影响
	 */
	public boolean delete(String number) {

		SQLiteDatabase db = helper.getWritableDatabase();

		String whereClause = BlacklistDB.Table_db.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };

		int delete = db.delete(BlacklistDB.Table_db.TABLE_NAME, whereClause,
				whereArgs);

		db.close();

		return delete != 0;
	}

	/**
	 * 根据号码来修改类型type
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	public boolean update(String number, int type) {

		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		// 更新的列
		values.put(BlacklistDB.Table_db.COLUMN_TYPE, type);

		// 判断条件
		String whereClause = BlacklistDB.Table_db.COLUMN_NUMBER + "=?";
		// 判断条件所携带的参数
		String[] whereArgs = new String[] { number };

		int update = db.update(BlacklistDB.Table_db.TABLE_NAME, values,
				whereClause, whereArgs);
		db.close();

		return update != 0;
	}

	/**
	 * 根据number寻找 type
	 * 
	 * @param number
	 * @return
	 */
	public int findType(String number) {

		SQLiteDatabase db = helper.getWritableDatabase();

		String sql = "select " + BlacklistDB.Table_db.COLUMN_TYPE + " from "
				+ BlacklistDB.Table_db.TABLE_NAME + " where "
				+ BlacklistDB.Table_db.COLUMN_NUMBER + "=?";

		String[] selectionArgs = new String[] { number };
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		int type = -1;
		if (cursor != null) {

			while (cursor.moveToNext()) {
				type = cursor.getInt(0);
				return type;
			}
		}
		db.close();
		return type;
	}

	/**
	 * 
	 * @return
	 */
	public List<BlackInfo> getAllInfo() {
		
		SQLiteDatabase db = helper.getWritableDatabase();

		List<BlackInfo> list = new ArrayList<BlackInfo>();

		String sql = "select " + BlacklistDB.Table_db.COLUMN_TYPE + ","
				+ BlacklistDB.Table_db.COLUMN_NUMBER + " from "
				+ BlacklistDB.Table_db.TABLE_NAME;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {

				int type = cursor.getInt(0);

				String number = cursor.getString(1);

				BlackInfo info = new BlackInfo();

				info.number = number;
				info.type = type;

				list.add(info);
			}

			return list;
		}
		db.close();

		return null;
	}
	
	/**
	 * 查询部分显示的数据   
	 * @param querySize
	 * 			每次查询的item
	 * @param indexOffset
	 * @return
	 */
	public List<BlackInfo> getPartinfo(int querySize,int indexOffset) {
		
		SQLiteDatabase db = helper.getWritableDatabase();

		List<BlackInfo> list = new ArrayList<BlackInfo>();
		
		//limit : 每次查询的条数  offset : 从数据库取数据的时候,从什么位置开始取 offset 2 :  0,1,2
		
		//sql  = select * from tableName limit querySize offset nindexOffsetum;
		//注意  : limit 是放在sql末尾,语法原因
		
		String sql = "select " + BlacklistDB.Table_db.COLUMN_TYPE + ","
				+ BlacklistDB.Table_db.COLUMN_NUMBER + " from "
				+ BlacklistDB.Table_db.TABLE_NAME
				+ " limit "+querySize+" offset "+indexOffset;
		
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {

				int type = cursor.getInt(0);

				String number = cursor.getString(1);

				BlackInfo info = new BlackInfo();

				info.number = number;
				info.type = type;

				list.add(info);
			}

			return list;
		}
		db.close();
		
		
		return null;
	}

}
