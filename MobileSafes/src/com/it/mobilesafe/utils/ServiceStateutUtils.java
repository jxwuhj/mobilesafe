package com.it.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

/**
 * 判断 服务是否开启
 * @author Ethan
 *
 */
public class ServiceStateutUtils {

	/**
	 * 判断一些服务 是否开启
	 * 
	 * @param context
	 * @param clazz
	 * @return
	 */
	public static  boolean isRunning(Context context,Class<? extends Service> clazz) {
		
		ActivityManager am = (ActivityManager)
				context.getSystemService(Context.ACTIVITY_SERVICE);
		
		//ActivityManager 管理服务   类似于任务管理器
		List<RunningServiceInfo> list = am.getRunningServices(Integer.MAX_VALUE);
		
		for (RunningServiceInfo info : list) {
			
			ComponentName service = info.service;
			
			String className = service.getClassName();
			
			if(clazz.getName().equals(className))  {
				return true;
			}
		}
		
		return false;
	}
}
