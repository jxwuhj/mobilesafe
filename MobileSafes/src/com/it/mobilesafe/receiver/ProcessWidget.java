package com.it.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.it.mobilesafe.service.UpdateWidgetService;

public class ProcessWidget extends AppWidgetProvider {
	
	private static final String TAG = "ProcessWidget";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		super.onReceive(context, intent);
		
	}
	
	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
		super.onEnabled(context);
		
	}
	
	/*@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
		
		Log.d(TAG, "onReceive");
	}*/
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
		super.onDeleted(context, appWidgetIds);
		
	}
	
	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");
		super.onDisabled(context);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, "onUpdate");
		
		/*ComponentName provider = 
				new ComponentName(context,ProcessWidget.class);
		RemoteViews views = null;
		
		appWidgetManager.updateAppWidget(provider, views);*/
		
		Intent service = new Intent(context,UpdateWidgetService.class);
		context.startService(service);
	}

}
