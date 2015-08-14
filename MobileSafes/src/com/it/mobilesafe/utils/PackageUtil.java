package com.it.mobilesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/*
 * 获取版本号
 * */

public class PackageUtil {
	public static String getVersionName(Context context) {
		//显示版本号
				PackageManager pm = context.getPackageManager();
				try {
					PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
					return info.versionName;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				return "";
	}
	
	public static int getVersionCode(Context context) {
		
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
}
