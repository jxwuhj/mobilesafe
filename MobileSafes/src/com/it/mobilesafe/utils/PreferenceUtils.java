package com.it.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtils {

	private static SharedPreferences sp;

	private static SharedPreferences getSharedPreferences(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp;
	}

	/**
	 * 设置boolean类型的配置数据
	 * 
	 * @param content
	 * @param key
	 * @param value
	 * */
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = getSharedPreferences(context);

		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * 获取boolean类型的配置数据,如果没有返回false
	 * 
	 * @param content
	 * @param key
	 * @return value
	 * */
	public static boolean getBoolean(Context context, String key) {
		return getBoolean(context,key,false);
	}

	/**
	 * 获取boolean类型的配置数据
	 * 
	 * @param content
	 * @param key
	 * @param defValue :没有时的默认值
	 * @return value
	 * */
	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 * 设置String类型的配置数据
	 * 
	 * @param content
	 * @param key
	 * @param value
	 * */
	public static void putString(Context context, String key, String value) {
		SharedPreferences sp = getSharedPreferences(context);

		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	/**
	 * 获取String类型的配置数据,如果没有返回null
	 * 
	 * @param content
	 * @param key
	 * @return value
	 * */
	public static String getString(Context context, String key) {
		return getString(context,key,null);
	}
	
	/**
	 * 获取String类型的配置数据
	 * 
	 * @param content
	 * @param key
	 * @param defValue :没有时的默认值
	 * @return value
	 * */
	public static String getString(Context context, String key,
			String defValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getString(key, defValue);
	}
	
	/**
	 * 设置int类型的配置数据
	 * 
	 * @param content
	 * @param key
	 * @param value
	 * */
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sp = getSharedPreferences(context);

		Editor edit = sp.edit();
		edit.putInt(key, value);
		edit.commit();
	}
	
	/**
	 * 获取int类型的配置数据,如果没有返回-1
	 * 
	 * @param content
	 * @param key
	 * @return value
	 * */
	public static int getInt(Context context, String key) {
		return getInt(context,key,0);
	}
	
	/**
	 * 获取int类型的配置数据
	 * 
	 * @param content
	 * @param key
	 * @param defValue :没有时的默认值
	 * @return value
	 * */
	public static int getInt(Context context, String key,
			int defValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getInt(key, defValue);
	}
}
