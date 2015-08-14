package com.it.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.AppInfo;
import com.it.mobilesafe.engine.AppInfoProvider;

public class LockScreenActivity extends Activity {
	
	public static final String EXTRA_PACKAGE_NAME = "extra_package_name";
	private ImageView mIvIcon;
	private EditText mEtPwd;
	private TextView mTvName;
	private String packageName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);
		
		//初始化view
		mIvIcon = (ImageView) findViewById(R.id.ls_iv_icon);
		mEtPwd = (EditText) findViewById(R.id.ls_et_pwd);
		mTvName = (TextView) findViewById(R.id.ls_tv_name);
		
		packageName = getIntent().getStringExtra(EXTRA_PACKAGE_NAME);
		AppInfo info = AppInfoProvider.getAppInfo(getApplicationContext(), packageName);
	
		mIvIcon.setImageDrawable(info.icon);
		mTvName.setText(info.name);
	}
	
	public void clickOk(View view) {
		String pwd = mEtPwd.getText().toString().trim();
		
		if(TextUtils.isEmpty(pwd)) {
			
			Toast.makeText(this, "密码不可为空", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if("123".equals(pwd)) {
			//通知电子狗放行
			Intent intent = new Intent();
			intent.setAction("free.the.app");
			intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
			sendBroadcast(intent);
			
			finish();
		}
		
	}
	
	//back按钮
	@Override
	public void onBackPressed() {
		
		//回到桌面
		/*<intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.HOME" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.MONKEY"/>
    	</intent-filter>*/
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		
		finish();
	}
}
