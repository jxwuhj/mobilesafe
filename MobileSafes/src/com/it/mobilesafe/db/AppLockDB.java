package com.it.mobilesafe.db;

public interface AppLockDB {
	
	String DB = "applock.db";
	int VERSION = 1;
	
	public interface AppLock {
		String TABLE_NAME = "app_lock";
		
		String COLUMN_ID = "_id";
		String COLUMN_PACKAGE_NAME = "packageName";
		
		String CREATE_SQL ="create table "+TABLE_NAME +"("
				+COLUMN_ID+" integer primary key autoincrement,"
				+COLUMN_PACKAGE_NAME + " text "
				+")";
	}
}
