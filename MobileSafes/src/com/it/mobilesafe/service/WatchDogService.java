package com.it.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.it.mobilesafe.activity.LockScreenActivity;
import com.it.mobilesafe.db.AppLockDao;

public class WatchDogService extends Service {

	private static final String TAG = "WatchDogService";
	private ActivityManager mAm;
	private AppLockDao mDao;
	private List<String> mLockList;
	private boolean isRunning;

	private List<String> mFreeList = new ArrayList<String>();

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			
			if(action.equals(Intent.ACTION_SCREEN_OFF)) {
				isRunning = false;
				//清空,让再次开屏输入密码
				mFreeList.clear();
				
			} else if(action.equals(Intent.ACTION_SCREEN_ON)) {
				startWatch();
			} else if(action.equals("free.the.app")) {
				
				String packageName = intent
						.getStringExtra(LockScreenActivity.EXTRA_PACKAGE_NAME);
				mFreeList.add(packageName);
			}
			
		}
	};
	
	private ContentObserver mOberver = new ContentObserver(new Handler()) {
		public void onChange(boolean selfChange) {
			 mLockList = mDao.findAll();
		};
		
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "开启电子狗1服务");
		mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		mDao = new AppLockDao(getApplicationContext());
		
		 mLockList = mDao.findAll();

		// 注册广播接收
		IntentFilter filter = new IntentFilter();
		filter.addAction("free.the.app");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);

		//添加数据库监听
		ContentResolver cr = getContentResolver();
		cr.registerContentObserver(
				Uri.parse("content://com.it.mobile.db.applock"),
				true, mOberver);
		
		startWatch();
	}

	private void startWatch() {
		
		if(isRunning) {
			return;
		}
		isRunning = true;
		new Thread() {
			public void run() {

				while (isRunning) {

					List<RunningTaskInfo> tasks = mAm.getRunningTasks(1);
					RunningTaskInfo recentTask = tasks.get(0);// 当前显示的任务栈

					// 当前应用程序的包名
					String packageName = recentTask.topActivity
							.getPackageName();

					// continue 在循环中,结束当前循环
					if (mFreeList.contains(packageName)) {
						continue;
					}
					
					if(mLockList.contains(packageName)) {
						
						Intent intent = new Intent(getApplicationContext(),
								LockScreenActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(LockScreenActivity.EXTRA_PACKAGE_NAME,
								packageName);
						startActivity(intent);
					}
					

					/*if (mDao.findLock(packageName)) {
						// 需要上锁 ,, 开启一个Activity ,服务里面开启Activity 需要setFlags

						Intent intent = new Intent(getApplicationContext(),
								LockScreenActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(LockScreenActivity.EXTRA_PACKAGE_NAME,
								packageName);
						startActivity(intent);
						
					}*/
					
					SystemClock.sleep(50);
				}

			};
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "关闭电子狗1服务..");

		// 销毁广播
		unregisterReceiver(receiver);
		
		//注销内容观察者
		getContentResolver().unregisterContentObserver(mOberver);
		
	}

}
