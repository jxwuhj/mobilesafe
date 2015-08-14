package com.it.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.it.mobilesafe.R;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;

public class GuildeSetupActivity extends Activity {
	private TextView mTvNumber;
	private ImageView mIvProtecting;
	private RelativeLayout mRlProtecting;
	private RelativeLayout mRlAgain;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.acticity_guide_setup);
		
		//View 安全号码
		mTvNumber = (TextView) findViewById(R.id.setup_tv_number);
		
		String number = PreferenceUtils.getString(this, Constants.SJFD_NUMBER);
		mTvNumber.setText(number);
		
		mRlProtecting = (RelativeLayout) findViewById(R.id.setup_rl_protecting);
		
		mIvProtecting = (ImageView) findViewById(R.id.setup_iv_protecting);
		
		mRlProtecting.setOnClickListener(new OnClickListener() {

			//判断是否勾选
			boolean protecting = PreferenceUtils.getBoolean(GuildeSetupActivity.this, Constants.IS_CHECK);
			
			@Override
			public void onClick(View v) {
				if(protecting) {
					mIvProtecting.setImageResource(protecting?R.drawable.lock :
						R.drawable.unlock);
					PreferenceUtils.putBoolean(GuildeSetupActivity.this,
							Constants.IS_CHECK, false);
				} else {
					mIvProtecting.setImageResource(R.drawable.lock);
					PreferenceUtils.putBoolean(GuildeSetupActivity.this, 
							Constants.IS_CHECK, true);
				}
			}
		});
		
		//重新开始
				mRlAgain = (RelativeLayout) findViewById(R.id.setup_rl_again);
				mRlAgain.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(GuildeSetupActivity.this,
								GuideSetup1Activity.class);
						startActivity(intent);
						
						overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
						finish();
					}
				});
		
	}

}
