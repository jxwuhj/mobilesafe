package com.it.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.it.mobilesafe.bean.AppInfo;

public class AppInfoProvider {


	public static List<AppInfo> getAllApps(Context context) {

		// 获取应用管理 --包的管理器
		PackageManager pm = context.getPackageManager();

		List<PackageInfo> packages = pm.getInstalledPackages(0);

		List<AppInfo> list = new ArrayList<AppInfo>();

		for (PackageInfo pack : packages) {

			AppInfo info = new AppInfo();

			info.packageName = pack.packageName;
			// applicationInfo 的标签
			ApplicationInfo applicationInfo = pack.applicationInfo;
			// 获取应用程序的名称
			info.name = applicationInfo.loadLabel(pm).toString();
			info.icon = applicationInfo.loadIcon(pm);
			// applicationInfo.sourceDir// -->data/app/xxx.apk或者
			// system/app/xxx.apk
			// applicationInfo.dataDir;// -->data/data/包名/
			
			String sourceDir = applicationInfo.sourceDir;
			info.size = new  File(sourceDir).length();
			
			/*Log.d(TAG, "info.packageName  :" + info.packageName);
			Log.d(TAG, "info.name  :" + info.name);
			Log.d(TAG, "info.size  :" + info.size);
			Log.d(TAG, "---------------");*/
			
			int flags = applicationInfo.flags;
			//判断是否是系统应用
			/*if((flags & applicationInfo.FLAG_SYSTEM ) ==applicationInfo.FLAG_SYSTEM) {
				info.isSystem = true;
			} else {
				info.isSystem = false;
			}*/
			
			info.isSystem = ((flags & applicationInfo.FLAG_SYSTEM ) 
					==applicationInfo.FLAG_SYSTEM)?true:false;
			
			//判断是否存储在sd卡
			/*if((flags & applicationInfo.FLAG_EXTERNAL_STORAGE)== 
					applicationInfo.FLAG_EXTERNAL_STORAGE) {
				info.isInstallSD = true;
			} else {
				info.isInstallSD = false;
			}*/
			
			info.isInstallSD = ((flags & applicationInfo.FLAG_EXTERNAL_STORAGE)== 
					applicationInfo.FLAG_EXTERNAL_STORAGE)?true:false;
			

			list.add(info);
		}

		return list;
	}
	
	public static AppInfo getAppInfo(Context context,String packageName) {
		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
			AppInfo info = new AppInfo();
			
			info.packageName = packageName;
			// 获取应用程序的名称
			info.name = applicationInfo.loadLabel(pm).toString();
			info.icon = applicationInfo.loadIcon(pm);
			
			String sourceDir = applicationInfo.sourceDir;
			info.size = new  File(sourceDir).length();
			
			
			int flags = applicationInfo.flags;
			info.isSystem = ((flags & applicationInfo.FLAG_SYSTEM ) 
					==applicationInfo.FLAG_SYSTEM)?true:false;
			
			info.isInstallSD = ((flags & applicationInfo.FLAG_EXTERNAL_STORAGE)== 
					applicationInfo.FLAG_EXTERNAL_STORAGE)?true:false;
			
			
			return info;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	} 

}
