package com.it.mobilesafe.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.receiver.SafeAdminReceiver;

public class GuideSetup4Activity extends BaseSetupActivity {

	protected static final int REQUEST_CODE_ENABLE_ADMIN = 888;
	private RelativeLayout mRlActivate;
	private ImageView mIv;
	private DevicePolicyManager dpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_guide_setup4);

		mRlActivate = (RelativeLayout) findViewById(R.id.setup4_rl_activate);
		mIv = (ImageView) findViewById(R.id.setup4_iv);
		
		dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		final ComponentName who = new ComponentName(getApplicationContext(),
				SafeAdminReceiver.class);

		// 初始化UI
//		 (dpm.isAdminActive(who))?(mIv.setImageResource(R.drawable.admin_inactivated)):(mIv.setImageResource(R.drawable.admin_activated));
		
		if(dpm.isAdminActive(who)) {
			
			mIv.setImageResource(R.drawable.admin_activated);
		} else {
			mIv.setImageResource(R.drawable.admin_inactivated);
		}
		
		

		mRlActivate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				if (dpm.isAdminActive(who)) {
					// 如果是激活,就取消激活
					dpm.removeActiveAdmin(who);
					mIv.setImageResource(R.drawable.admin_inactivated);
				} else {
					// 如果没有激活,那就去激活
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							who);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"让您更安全的装b,so,请装完b就跑");
					startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);

				}

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			switch (resultCode) {
			case Activity.RESULT_OK:

				// 返回true,那么改变UI
				mIv.setImageResource(R.drawable.admin_activated);

				break;

			default:
				break;
			}
		}
	}

	/*
	 * public void performNext(View v) { Intent intent = new Intent(this,
	 * GuideSetup5Activity.class); startActivity(intent);
	 * overridePendingTransition(R.anim.next_enter, R.anim.next_exit); finish();
	 * }
	 * 
	 * public void performPre(View v) { Intent intent = new Intent(this,
	 * GuideSetup3Activity.class); startActivity(intent);
	 * overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit); finish(); }
	 */
	@Override
	protected boolean clickNext() {
		ComponentName who = new ComponentName(getApplicationContext(),
				SafeAdminReceiver.class);
		if(!dpm.isAdminActive(who)) {
			Toast.makeText(getApplicationContext(), "请点击激活,才能下一步,谢谢!", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		Intent intent = new Intent(this, GuideSetup5Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean clickPre() {
		Intent intent = new Intent(this, GuideSetup3Activity.class);
		startActivity(intent);
		return false;

	}
}
