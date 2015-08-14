package com.it.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.it.mobilesafe.db.AddressDao;
import com.it.mobilesafe.view.AddressToast;

public class NumberAddressService extends Service {

	private static final String TAG = "NumberAddressService";
	private TelephonyManager mTm;

	// private WindowManager mWM;

	// private final WindowManager.LayoutParams mParams = new
	// WindowManager.LayoutParams();
	
	private BroadcastReceiver receiver =  new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			
			String address = AddressDao.findAddress(
					getApplicationContext(), number);
			
			toast.show(address);
			
			
		}
	};
	

	private PhoneStateListener listener = new PhoneStateListener() {
		// private TextView mView;

		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {

			case TelephonyManager.CALL_STATE_IDLE:
				
				toast.hide();
				
				// 闲置的状态 -- 空闲的时候隐藏
				/*
				 * if (mView != null) { // note: checking parent() just to make
				 * sure the view has // been added... i have seen cases where we
				 * get here when // the view isn't yet added, so let's try not
				 * to crash. if (mView.getParent() != null) {
				 * mWM.removeView(mView); }
				 * 
				 * mView = null; }
				 */

				break;
			case TelephonyManager.CALL_STATE_RINGING:
				// 在电话响起的时候显示归属地

				String address = AddressDao.findAddress(
						getApplicationContext(), incomingNumber);
				Log.d(TAG, "address : " + address);
				
				toast.show(address);
				// Toast.makeText(getApplicationContext(), "归属地 :"+address,
				// Toast.LENGTH_LONG).show();

				/*
				 * mView = new TextView(getApplicationContext());
				 * mView.setText(address);
				 * 
				 * WindowManager.LayoutParams params = mParams; params.height =
				 * WindowManager.LayoutParams.WRAP_CONTENT; params.width =
				 * WindowManager.LayoutParams.WRAP_CONTENT; params.flags =
				 * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
				 * WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
				 * WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; params.format
				 * = PixelFormat.TRANSLUCENT; // params.windowAnimations =
				 * com.android.internal.R.style.Animation_Toast; params.type =
				 * WindowManager.LayoutParams.TYPE_TOAST; //
				 * params.setTitle("Toast");
				 * 
				 * mWM.addView(mView, mParams);
				 */
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			default:
				break;
			}

		};
	};
	private AddressToast toast;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "归属地服务开启");

		toast = new AddressToast(this);

		mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 拨入手机的归属地
		// 监听到拨入电话的状态
		mTm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 拨出号码的归属地
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "归属地服务关闭");

		unregisterReceiver(receiver);
		// 在结束服务的时候关闭
		mTm.listen(listener, PhoneStateListener.LISTEN_NONE);

	}

}
