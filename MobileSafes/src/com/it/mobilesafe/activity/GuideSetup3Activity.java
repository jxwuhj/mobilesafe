package com.it.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;

public class GuideSetup3Activity extends BaseSetupActivity {
	
	private static final int REQUEST_CODE_CONTANTS = 888;
	private EditText mStNumber;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_guide_setup3);
		
		mStNumber = (EditText) findViewById(R.id.setup3_et_safePwd);
		String number = PreferenceUtils.getString(this, Constants.SJFD_NUMBER);
		mStNumber.setText(number);
		if(!TextUtils.isEmpty(number)) {
			mStNumber.setSelection(number.length());
		}
		//设置安全密码
		
		
	}
	
	public void clickContacts(View v) {
		Intent intent = new Intent(this,ContactsActivity.class);
		startActivityForResult(intent, REQUEST_CODE_CONTANTS);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		//requestCode   页面跳转 前 约定的码  ,data 数据    ,resultCode   结果码 与数据一起被传输回来
		
		if(requestCode ==REQUEST_CODE_CONTANTS) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				
				String number = data.getStringExtra(ContactsActivity.KEY_NUMBER);
				mStNumber.setText(number);
				System.out.println("number :" +number);
				System.out.println(data);
				if(!TextUtils.isEmpty(number)) {
					mStNumber.setSelection(number.length());
				}
				break;

			default:
				break;
			}
		}
	}

/*	public void performNext(View v) {
		Intent intent = new Intent(this, GuideSetup4Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		finish();
	}
	
	public void performPre(View v) {
		Intent intent = new Intent(this, GuideSetup2Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
	}*/

	@Override
	protected boolean clickNext() {
		//在跳转前,判断有没有输入安全号码
		String number = mStNumber.getText().toString().trim();
		
		if(TextUtils.isEmpty(number)) {
			Toast.makeText(this, "开启服务,需要号码,请输入,谢谢!", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		//记录安全号码
		PreferenceUtils.putString(this, Constants.SJFD_NUMBER, number);
		
		
		Intent intent = new Intent(this, GuideSetup4Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean clickPre() {
		Intent intent = new Intent(this, GuideSetup2Activity.class);
		startActivity(intent);
		return false;
	}
}
