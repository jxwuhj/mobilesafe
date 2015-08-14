package com.it.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;

public class GuideSetup2Activity extends BaseSetupActivity {
	private TextView mBinder;
	private ImageView mIv;
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_guide_setup2);
		
		//初始化view
		mBinder = (TextView) findViewById(R.id.setup2_tv_bind);
		mIv = (ImageView) findViewById(R.id.setup2_iv);
		
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		String sim = PreferenceUtils.getString(this, Constants.ISBINDER_SIM);
		
		//初始化UI
		//根据保存的值来判断
		if(TextUtils.isEmpty(sim)) {
			mIv.setImageResource(R.drawable.unlock);
		} else {
			//不为空
			mIv.setImageResource(R.drawable.lock);
		}
		
		mBinder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String sim = PreferenceUtils.getString(GuideSetup2Activity.this, 
						Constants.ISBINDER_SIM);
				if(TextUtils.isEmpty(sim)) {
					//如果没有绑定,那么就绑定
					
					sim = tm.getSimSerialNumber();
					PreferenceUtils.putString(GuideSetup2Activity.this,
							Constants.ISBINDER_SIM, sim);
					
					//UI
					mIv.setImageResource(R.drawable.lock);
				} else {
					//如果已经绑定,那么解除绑定,数据存储 ,清除sim卡串号
					PreferenceUtils.putString(GuideSetup2Activity.this, 
							Constants.ISBINDER_SIM, null);
					
					//UI 更改UI为解锁状态
					mIv.setImageResource(R.drawable.unlock);
				}
			}
		});
		
	}

	
	/*public void performNext(View v) {
		Intent intent = new Intent(this, GuideSetup3Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		finish();
	}
	
	public void performPre(View v) {
		Intent intent = new Intent(this, GuideSetup1Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
	}*/


	@Override
	protected boolean clickNext() {
		//检测用户是否开启了锁定sim,如果没有Toast
		String sim = PreferenceUtils.getString(this, Constants.ISBINDER_SIM);
		if(TextUtils.isEmpty(sim)) {
			Toast.makeText(this, "请绑定SIM卡,才能进一步的操作,谢谢!", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		Intent intent = new Intent(this, GuideSetup3Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean clickPre() {
		Intent intent = new Intent(this, GuideSetup1Activity.class);
		startActivity(intent);
		return false;
	}

}
