package com.it.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;

public class GuideSetup5Activity extends BaseSetupActivity {
	private CheckBox mCb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.acticity_guide_setup5);
		
		mCb = (CheckBox) findViewById(R.id.setup5_cb);
		
		//初始化状态
		boolean isCheck = PreferenceUtils.getBoolean(this, Constants.IS_CHECK);
		mCb.setChecked(isCheck);
		
		mCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//标记是否有开启防盗功能
				
				PreferenceUtils.putBoolean(GuideSetup5Activity.this, 
						Constants.IS_CHECK, isChecked);
			}
		});
	}
	
	//设置完成
	/*public void performNext(View v) {
		Intent intent = new Intent(this, GuideSetup4Activity.class);
		startActivity(intent);
	}
	
	public void performPre(View v) {
		Intent intent = new Intent(this, GuideSetup4Activity.class);
		startActivity(intent);
		
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
	}*/

	@Override
	protected boolean clickNext() {
		// TODO Auto-generated method stub
		//校验是否勾选
		if(!mCb.isChecked()) {
			Toast.makeText(this, "要完全开启防盗功能,请勾选服务", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		//标记已经设置过
		PreferenceUtils.putBoolean(this, Constants.IS_CHECK, true);
		
		//往结果页面跳转
		Intent intent = new Intent(this,GuildeSetupActivity.class);
		startActivity(intent);
		
		finish();
		return false;
	}

	@Override
	protected boolean clickPre() {
		Intent intent = new Intent(this, GuideSetup4Activity.class);
		startActivity(intent);
		return false;
	}

}
