package com.it.mobilesafe.service;

import java.util.List;

import com.it.mobilesafe.bean.ProcessInfo;
import com.it.mobilesafe.engine.ProcessProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoCleanService extends Service {

	private static final String TAG = "AutoCleanService";
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//锁屏动作,清理进程
			List<ProcessInfo> process = ProcessProvider.getAllRunningProcess
					(getApplicationContext());
			
			for (ProcessInfo info : process) {
				ProcessProvider.cleanProcess(getApplicationContext(), info.packageName);
			}
			
			
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		
		//注册广播接收者
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

}
