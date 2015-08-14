package com.it.mobilesafe.utils;

import android.util.Log;

public class Logger {

	private static final int LEVEL_V = 0;// 日志级别
	private static final int LEVEL_D = 1;
	private static final int LEVEL_I = 2;
	private static final int LEVEL_W = 3;
	private static final int LEVEL_E = 4;
	private static final int LOG_LEVEL = 0;
	private static boolean isEnable = true;// 日志是否可见

	public static void d(String tag, String msg) {

		if (!isEnable) {
			return;
		}
		if(LOG_LEVEL<=LEVEL_D) {
			Log.d(tag, msg);
		}
	}
	
	public static void v(String tag,String msg) {
		if (!isEnable) {
			return;
		}
		
		if(LOG_LEVEL<=LEVEL_V) {
			Log.v(tag, msg);
		}
	}
	
	public static void i(String tag,String msg) {
		if (!isEnable) {
			return;
		}
		
		if(LOG_LEVEL<=LEVEL_I) {
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag,String msg) {
		if (!isEnable) {
			return;
		}
		
		if(LOG_LEVEL<=LEVEL_W) {
			Log.w(tag, msg);
		}
	}
	public static void e(String tag,String msg) {
		if (!isEnable) {
			return;
		}
		
		if(LOG_LEVEL<=LEVEL_E) {
			Log.e(tag, msg);
		}
	}

}
