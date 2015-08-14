package com.it.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.engine.SmsProvider;
import com.it.mobilesafe.engine.SmsProvider.OnSmsListener;
import com.it.mobilesafe.service.WatchDogService;
import com.it.mobilesafe.service.WatchDogService2;
import com.it.mobilesafe.utils.ServiceStateutUtils;
import com.it.mobilesafe.view.SettingItemView;

public class CommonToolActivity extends Activity {

	private SettingItemView mNumAddess;
	private SettingItemView mCommonNum;
	
	private SettingItemView mSmsBackup;
	private SettingItemView mSmsRestore;
	
	private SettingItemView mWatchDog1;
	private SettingItemView mWatchDog2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_common_tool);

		mNumAddess = (SettingItemView) findViewById(R.id.common_tv_number);
		mCommonNum = (SettingItemView) findViewById(R.id.common_tv_commonnum);
		
		mSmsBackup = (SettingItemView) findViewById(R.id.common_tv_smsbackp);
		mSmsRestore = (SettingItemView) findViewById(R.id.common_tv_smsrestore);

		mWatchDog1 = (SettingItemView) findViewById(R.id.common_tv_watchdog1);
		mWatchDog2 = (SettingItemView) findViewById(R.id.common_tv_watchdog2);
		
		
		
		mWatchDog2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
				/* <intent-filter>
	                <action android:name="android.intent.action.MAIN" />
	                <action android:name="android.settings.ACCESSIBILITY_SETTINGS" />
	                <!-- Wtf...  this action is bogus!  Can we remove it? -->
	                <action android:name="ACCESSIBILITY_FEEDBACK_SETTINGS" />
	                <category android:name="android.intent.category.DEFAULT" />
	                <category android:name="android.intent.category.VOICE_LAUNCH" />
	            </intent-filter>*/
				
				/*	//1.
				 * 
				 * Intent intent = new Intent();
				
				intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addCategory("android.intent.category.VOICE_LAUNCH");
				
				startActivity(intent);*/
				
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivity(intent);
				
				
			}
		});
		
		
		mWatchDog1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//如果服务开启,那么就关闭
				boolean running = ServiceStateutUtils.
						isRunning(getApplicationContext(),
								WatchDogService.class);
				
				if(running) {
					stopService(new Intent(getApplicationContext(),
							WatchDogService.class));
					//UI改变
					mWatchDog1.setToggleOn(false);
				} else {
					startService(new Intent(getApplicationContext(),
							WatchDogService.class));
					//UI改变
					mWatchDog1.setToggleOn(true);
				}
			}
		});
		
		mNumAddess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommonToolActivity.this,
						NumberAddressQueryActivity.class);
				startActivity(intent);
			}
		});

		mCommonNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommonToolActivity.this,
						CommonNumberActivity.class);
				startActivity(intent);
			}
		});

		// 短信备份
		mSmsBackup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 数据 -- UI
				smsBackup();
			}
		});
		
		mSmsRestore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 数据 -- UI
				smsRestore();

			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//服务状态回显
		mWatchDog1.setToggleOn(ServiceStateutUtils.
		isRunning(getApplicationContext(),
				WatchDogService.class));
		
		mWatchDog2.setToggleOn(ServiceStateutUtils.
				isRunning(getApplicationContext(),
						WatchDogService2.class));
	}

	protected void smsRestore() {


		// 设置总数量 -- > 进度 -->结果(成功,失败)
		// 1.UI 弹出进度 dialog 上下文只能是使用this 而不是getApplicationContext()
		
		  final ProgressDialog dialog = new ProgressDialog(this);
		  dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		  
		  //在复制的过程中,不让用户点击取消
		  dialog.setCanceledOnTouchOutside(false);
		  dialog.show();
		 
		  SmsProvider.smsResore(this, new OnSmsListener() {
			
			@Override
			public void onSuccess() {
				Toast.makeText(getApplicationContext(), "还原成功",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
			
			@Override
			public void onProgress(int progress) {
				dialog.setProgress(progress);
			}
			
			@Override
			public void onMax(int max) {
				dialog.setMax(max);
			}
			
			@Override
			public void onFailed() {
				Toast.makeText(getApplicationContext(), "还原失败",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		
	
	}

	protected void smsBackup() {

		/*mTvProgress = (TextView) findViewById(R.id.act_tv_progress);
		mTvTotalCount = (TextView) findViewById(R.id.act_tv_totalcount);*/

		// 设置总数量 -- > 进度 -->结果(成功,失败)
		// 1.UI 弹出进度 dialog 上下文只能是使用this 而不是getApplicationContext()
		
		  final ProgressDialog dialog = new ProgressDialog(this);
		  dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		  
		  //在复制的过程中,不让用户点击取消 dialog.setCanceledOnTouchOutside(false);
		  dialog.show();
		 

		/**
		 * <list> <sms> <adress>1314</address> <date>142565<date> <type>1</type>
		 * <body>hello</body> </sms>
		 * 
		 * </list>
		 * 
		 */
		
		SmsProvider.smsBackup(this, new OnSmsListener() {
			
			@Override
			public void onSuccess() {
//				mTvTotalCount.setText("成功");
				Toast.makeText(getApplicationContext(), "备份成功",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
			
			@Override
			public void onProgress(int progress) {
//				mTvProgress.setText("进度:"+progress);
				dialog.setProgress(progress);
			}
			
			@Override
			public void onMax(int max) {
//				mTvTotalCount.setText("最大数 :"+max);
				dialog.setMax(max);
			}
			
			@Override
			public void onFailed() {
				Toast.makeText(getApplicationContext(), "备份失败",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		
	
	}
	
	public void clickApplock(View v) {
		Intent intent = new Intent();
		intent.setClass(this, AppLockActivity.class);
		startActivity(intent);
	}
}
