package com.it.mobilesafe.db;

public interface BlacklistDB {
	
	String DB_NAME = "blacklist";
	int DB_VERSION = 1;
	
	public interface Table_db {
		
		String TABLE_NAME = "blackInfo";
		String COLUMN_NUMBER = "number";
		String COLUMN_TYPE = "_type";
		String COLUMN_ID = "_id";
		
		String SQL = "create table "+TABLE_NAME+"("+
				COLUMN_ID +" integer primary key autoincrement, "+
				COLUMN_NUMBER+" text unique, "+
				COLUMN_TYPE+" integer "+
		")";
		
	}

}
