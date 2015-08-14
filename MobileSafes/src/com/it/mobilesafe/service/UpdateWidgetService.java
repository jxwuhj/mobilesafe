package com.it.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.it.mobilesafe.R;
import com.it.mobilesafe.engine.ProcessProvider;
import com.it.mobilesafe.receiver.ProcessWidget;

public class UpdateWidgetService extends Service {

	private static final String TAG = "UpdateWidgetService";
	private AppWidgetManager widgetManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(TAG, "widgit更新服务开启了");

		widgetManager = AppWidgetManager.getInstance(this);
		// 开启更新widget
		openUpdateWidget();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "widgit更新服务关闭了");
	}

	private void openUpdateWidget() {

		new Thread() {
			public void run() {

				while (true) {

					SystemClock.sleep(5000);

					ComponentName provider = new ComponentName(
							getApplicationContext(), ProcessWidget.class);

					// 远程的view
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);

					// 进程数
					views.setTextViewText(
							R.id.process_count,
							"可用的进程数: "
									+ ProcessProvider
											.getRunningProcessCount(getApplicationContext())
									+ "个");

					// freeMemory
					views.setTextViewText(
							R.id.process_memory,
							"可用内存有:"
									+ Formatter
											.formatFileSize(
													UpdateWidgetService.this,
													ProcessProvider
															.getFreeMemory(UpdateWidgetService.this)));

					// 清理内存

					// 不是开启服务,而是让服务进行监听
					// Intent intent = new
					// Intent(UpdateWidgetService.this,MyReceiver.class);
					// 设置广播事件
					Intent intent = new Intent();
					intent.setAction("xxx.receiver");

					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							getApplicationContext(), 100, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

					widgetManager.updateAppWidget(provider, views);
				}
			};

		}.start();

	}

}
