package com.it.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.ProcessInfo;

public class ProcessProvider {

	private static final String TAG = "ProcessProvider";

	// 正在运行的进程数,总的 可用的进程数
	/**
	 * 正在运行的进程数
	 * 
	 * @param context
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {

		// 获取任务管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();

		if (processes != null) {
			return processes.size();
		}
		return 0;
	}

	/**
	 * 获得可用的所有的进程数
	 * 
	 * @param context
	 * @return
	 */
	public static int getTotalProcessCount(Context context) {

		PackageManager pm = context.getPackageManager();

		// PackageInfo 清单文件的对象
		List<PackageInfo> packages = pm.getInstalledPackages(0);

		int count = 0;

		for (PackageInfo pack : packages) {
			HashSet<String> set = new HashSet<String>();

			ActivityInfo[] activities = pack.activities;

			set.add(pack.applicationInfo.processName);

			// activity
			if (activities != null) {
				for (ActivityInfo activity : activities) {
					String processName = activity.processName;

					set.add(processName);

				}
			}

			// services
			ServiceInfo[] services = pack.services;
			if (services != null) {
				for (ServiceInfo service : services) {
					String processName = service.processName;
					set.add(processName);
				}
			}

			// receiver
			ActivityInfo[] receivers = pack.receivers;
			if (receivers != null) {
				for (ActivityInfo receiver : receivers) {
					String processName = receiver.processName;
					set.add(processName);
				}
			}

			// provider
			ProviderInfo[] providers = pack.providers;

			if (providers != null) {
				for (ProviderInfo provider : providers) {

					String processName = provider.processName;
					set.add(processName);
				}
			}
			count += set.size();
		}
		return count;
	}

	/**
	 * 获得可用的内存信息
	 * 
	 * @param context
	 * @return
	 */
	public static long getFreeMemory(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);

		return outInfo.availMem;

	}

	/**
	 * 获得所有的内存
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static long getTotalMemory(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);

		// 代码适配
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			Log.d(TAG, "高版本内存");
			return outInfo.totalMem;
		} else {
			Log.d(TAG, "低版本内存");
			return getLowTotalMemory();
		}

	}

	private static long getLowTotalMemory() {
		File file = new File("/proc/memifo");
		BufferedReader br = null;

		try {
			// MemTotal: 513492 kB
			br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			String size = line.replace("emTotal:", "").replace("kB", "").trim();

			return Long.valueOf(size);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 用于获取用户来源数据,进程名,图标以及占用的内存信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<ProcessInfo> getAllRunningProcess(Context context) {

		// 获取任务管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 包管理器
		PackageManager pm = context.getPackageManager();
		// 获取正在运行的进程信息
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();

		List<ProcessInfo> list = new ArrayList<ProcessInfo>();

		if (processes != null) {
			for (int i = 0; i < processes.size(); i++) {
				RunningAppProcessInfo process = processes.get(i);
				// process.importance 表示进程的优先级
				// process.lastTrimLevel 最后回收的进程
				// process.lru 算法,用户内存不够,回收内存
				// process.pid 进程

				// process.processName 进程id
				String packageName = process.processName;

				Drawable icon = null;
				String name = null;
				long memory = 0;
				boolean isSystem = false;

				try {
					ApplicationInfo applicationInfo = pm.getApplicationInfo(
							packageName, 0);
					
					icon = applicationInfo.loadIcon(pm);
					name = applicationInfo.loadLabel(pm).toString();
					

					int flags = applicationInfo.flags;
					
					if ((flags & ApplicationInfo.FLAG_SYSTEM) 
							== ApplicationInfo.FLAG_SYSTEM) {
						isSystem = true;
					} else {
						isSystem = false;
					}
					
				} catch (Exception e) {
					e.printStackTrace();

					icon = context.getResources().getDrawable(
							R.drawable.ic_launcher);
					name = packageName;
					
					//c语言写的native 
					isSystem = true;
				}
				
				// 内存信息
				android.os.Debug.MemoryInfo memoryInfo = am
						.getProcessMemoryInfo(new int[] { process.pid})[0];
				memory = memoryInfo.getTotalPss() * 1024;

				
				
				ProcessInfo info = new ProcessInfo();
				info.icon = icon;
				info.Memory = memory; // 占用的内存信息
				info.name = name;
				info.isSystem = isSystem;
				info.packageName = packageName;
				
				info.isForeground = process.importance == 100 ||
						process.importance == 130 ||
						process.importance == 50 ||(info.isSystem && process.importance == 400);
				
				list.add(info);
			}
		}
		return list;
	}
	
	
	/**
	 * 清理后台进程
	 * 
	 * @param context
	 * @param packageName
	 */
	public static void cleanProcess(Context context,String packageName) {
		
		ActivityManager am = (ActivityManager) 
				context.getSystemService(Context.ACTIVITY_SERVICE);
		
		am.killBackgroundProcesses(packageName);
	}

}
