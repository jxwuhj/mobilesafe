package com.it.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.it.mobilesafe.R;

public class SmsListenerReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsListenerReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
			
		Log.d(TAG, "接收到短信");
		//短信格式 : pdus
		Object []object = (Object[]) intent.getExtras().get("pdus");
		
		for (Object obj : object) {
			//获取到一个短信对象
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			
			//短信内容
			String body = sms.getMessageBody();
			//发送信息的发送人
			String sender = sms.getOriginatingAddress();
			Log.d(TAG, "sender :"+sender);
			
			if("#*location*#".equals(body)) {
				//定位
				//开启定位服务
				Intent service = new Intent(context,SmsListenerReceiver.class);
				context.startService(service);
				
				abortBroadcast();
			} else if("#*alarm*#".equals(body)) {
				//播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);  //新直接添加一段.MP3文件   CTRL+shift+o  导包,重复
				player.setLooping(true);
				player.setVolume(05f, 05f);
				player.start();
				
				abortBroadcast();
				
			}else if("#*wipedata*#".equals(body)) {
				//远程擦除数据
				
				//获取设备管理员的权限
				DevicePolicyManager dpm = (DevicePolicyManager) 
						context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				//如果是激活了设备管理员
				ComponentName who  = new ComponentName(context,SmsListenerReceiver.class);
				if(dpm.isAdminActive(who)) {
					dpm.wipeData(0);
				}
				
				abortBroadcast();
			}else if("#*screenlock*#".equals(body)) {
				//获取设备管理员的权限
				DevicePolicyManager dpm = (DevicePolicyManager) 
						context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				//如果是激活了设备管理员
				ComponentName who  = new ComponentName(context,SmsListenerReceiver.class);
				if(dpm.isAdminActive(who)) {
					//锁屏
					dpm.lockNow();
				//设置密码
				dpm.resetPassword("123",0);
				}
				
				abortBroadcast();
			}

		}
	}

}
