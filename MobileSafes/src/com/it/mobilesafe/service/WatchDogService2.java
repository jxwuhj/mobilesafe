package com.it.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.it.mobilesafe.activity.LockScreenActivity;
import com.it.mobilesafe.db.AppLockDao;

public class WatchDogService2 extends AccessibilityService {
	
	
	private static final String TAG = "WatchDogService2";
	
	private List<String> mFreeList = new ArrayList<String>();
	private List<String> mLockList;
	
	private AppLockDao mDao;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			
			if(action.equals(Intent.ACTION_SCREEN_OFF)) {
				//清空,让再次开屏输入密码
				mFreeList.clear();
				
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
	protected void onServiceConnected() {
		super.onServiceConnected();
		//服务器连接开启
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		
		info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
		
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		setServiceInfo(info);
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mDao = new AppLockDao(getApplicationContext());
		 mLockList = mDao.findAll();

		// 注册广播接收
		IntentFilter filter = new IntentFilter();
		filter.addAction("free.the.app");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);

		//添加数据库监听
		ContentResolver cr = getContentResolver();
		cr.registerContentObserver(
				Uri.parse("content://com.it.mobile.db.applock"),
				true, mOberver);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		// 销毁广播
		unregisterReceiver(receiver);
		
		//注销内容观察者
		getContentResolver().unregisterContentObserver(mOberver);
		
	}
	

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		
		int eventType = event.getEventType();
		
		if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			//window改变的事件
			//获取改变后的包名
			String packageName = event.getPackageName().toString();
			
			Log.d(TAG, "packageName"+packageName);
			
			if (mFreeList.contains(packageName)) {
				return;
			}
			
			if(mLockList.contains(packageName)) {
				
				//注意,我这里最开始是getapplicationContenxt()
				Intent intent = new Intent(WatchDogService2.this,
						LockScreenActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(LockScreenActivity.EXTRA_PACKAGE_NAME,
						packageName);
				startActivity(intent);
			}
			
			
		}
		
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

}
