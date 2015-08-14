package com.it.mobilesafe.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.it.mobilesafe.R;
import com.it.mobilesafe.activity.SplashActivity;

//保护进程
public class ProtectedService extends Service {

	private static final String TAG = "ProtectedService";
	private static final int ID = 188;
//	private ScheduledFuture<?> future;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate 开启");
	
		//将服务做成前台进程
		Notification notification = new Notification();
		notification.tickerText ="装B卫士,竭诚为您装B安全服务";
		notification.icon = R.drawable.ic_launcher;
		//contentView 指的是 手机状态栏中,拉开后的view
		notification.contentView = new RemoteViews(getPackageName(),
				R.layout.nofitication_protected);
		Intent intent = new Intent(this,SplashActivity.class);
		
		//在服务中,意图跳转时需要设置setFlags()
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.contentIntent = PendingIntent.getActivity(this,
				100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		startForeground(ID, notification);
		
	/*	ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
		
		future = pool.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				Log.d(TAG, "Executors 执行者执行任务");
			}
		}, 0, 3000, TimeUnit.MICROSECONDS);*/
		
	
		/*AlarmManager am = (AlarmManager) 
				getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent();
		intent.setAction("xxx.receiver");
		PendingIntent operation =PendingIntent.getBroadcast
				(this, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC, 0, 3000, operation);*/
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onCreate 关闭");
//		future.cancel(true);
	}

}
