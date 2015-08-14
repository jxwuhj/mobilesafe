package com.it.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.it.mobilesafe.service.ProtectedService;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;

public class BootcompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootcompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "接收到开机");
		System.out.println("接收到开机");
		
		//检查sim是否一致,取得当前sim卡
		TelephonyManager tm = (TelephonyManager) context.getSystemService
				(Context.TELEPHONY_SERVICE);
		
		String currentSim = tm.getSimSerialNumber();
		String localSim = PreferenceUtils.getString(context, Constants.SJFD_SIM) +"gbk";
		System.out.println("SJFD_SIM"+ localSim);
		
		if(!currentSim.equals(localSim)) {
			Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
			//发送短信给安全号码
			SmsManager sm = SmsManager.getDefault();
			
			String number = PreferenceUtils.getString(context, Constants.SJFD_NUMBER);
			if(number == null) {
				return;
			}
			
			sm.sendTextMessage(number, null,  "mobile is lost", null,null);
			System.out.println("localSim");
		}
		
		//开启服务
		context.startService(new Intent(context,ProtectedService.class));
		
		
	}

}
