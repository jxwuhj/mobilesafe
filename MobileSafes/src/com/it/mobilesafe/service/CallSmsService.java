package com.it.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.it.mobilesafe.bean.BlackInfo;
import com.it.mobilesafe.db.BlackDao;

public class CallSmsService extends Service {

	private static final String TAG = "CallSmsService";
	private TelephonyManager mtm;
	private BlackDao dao;

	private PhoneStateListener mListener = new PhoneStateListener() {
		public void onCallStateChanged(int state, final String incomingNumber) {

			// state : 电话状态
			// call_state_idle :闲置状态
			// call_state_ringing : 响铃状态
			// call_state_offhook :接听状态

			// inComingNumber 接入的电话

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:

				break;
			case TelephonyManager.CALL_STATE_RINGING:
				
				//需要拦截  挂掉电话
				int type = dao.findType(incomingNumber);
//				Log.d(TAG, "来电 type  :"+incomingNumber);
				if(type == BlackInfo.TYPE_ALL || type == BlackInfo.TYPE_CALL) {
					//需要拦截
					// ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
					try {
						
						Class<?> clazz = Class.forName("android.os.ServiceManager");
						
						Method method = clazz.getDeclaredMethod("getService", String.class);
						
						IBinder binder  = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
						
						ITelephony telephony = ITelephony.Stub.asInterface(binder);
						
						telephony.endCall();
						
						SystemClock.sleep(200);
						
						//删除通话记录
						final ContentResolver cr = getContentResolver();
						
						final Uri uri = Uri.parse("content://call_log/calls");
						
						cr.registerContentObserver(uri, true, new ContentObserver(
								new Handler()) {
							public void onChange(boolean selfChange) {
								String where = "number=?";
								
								String []selectionArgs = new String[]{incomingNumber};
								
								cr.delete(uri, where, selectionArgs);
							};
						});
						
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;

			default:
				break;
			}

		};
	};
	
	private BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			Object[] objs = (Object[]) bundle.get("pdus");
			
			for (Object object : objs) {
				
				SmsMessage msg = SmsMessage.createFromPdu((byte[])object);
				String address = msg.getOriginatingAddress();
				
				//查询 
				int type = dao.findType(address);
				
				if(type== BlackInfo.TYPE_SMS || type== BlackInfo.TYPE_ALL) {
					abortBroadcast();
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "开启拦截骚扰服务");

		mtm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		dao = new BlackDao(this);
		// 拦截电话
		mtm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		
		//拦截短信
		//注册短信接受者
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(mSmsReceiver, filter);
		
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "关闭拦截骚扰服务");
		super.onDestroy();
		
		//注销监听
		mtm.listen(mListener, PhoneStateListener.LISTEN_NONE);
		
		//注销
		unregisterReceiver(mSmsReceiver);
	}

}
