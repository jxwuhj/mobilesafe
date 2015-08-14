package com.it.mobilesafe.receiver;

import java.util.List;

import com.it.mobilesafe.bean.ProcessInfo;
import com.it.mobilesafe.engine.ProcessProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

	private static final String TAG = "MyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

//		Log.d(TAG, "执行广播  .. 3s 一次... ");
		
		//开启清理内存
		List<ProcessInfo> list = ProcessProvider.getAllRunningProcess(context);
		
		int count = 0;
		long memory = 0;
		for (ProcessInfo info : list) {
			
			if(!info.isForeground) {
				count++;
				memory += info.Memory;
				ProcessProvider.cleanProcess(context, info.packageName);
			}
		}
		
		if(count>0) {
			
			Toast.makeText(context, "杀死了"+count+"个进程,节省了"+
					Formatter.formatFileSize(context, memory), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "没有可清理的进程", Toast.LENGTH_SHORT).show();
		}
	}

}
