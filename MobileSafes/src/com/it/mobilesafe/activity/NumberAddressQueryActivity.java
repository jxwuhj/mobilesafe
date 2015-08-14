package com.it.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.db.AddressDao;

public class NumberAddressQueryActivity extends Activity {
	
	private EditText mEtNum;
	private Button mBtnQuery;
	private TextView mTvAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_number_address_query);
		
		mEtNum = (EditText) findViewById(R.id.naq_et_number);
		mBtnQuery = (Button) findViewById(R.id.naq_btn_query);
		mTvAddress = (TextView) findViewById(R.id.naq_tv_address);
		
		
		mEtNum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当文本改变时的回调
				String address = AddressDao.findAddress(NumberAddressQueryActivity.this, s.toString());
				mTvAddress.setText("归属地:"+ address);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//在文本改变前?
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//在文本改变后
			}
		});
		
		//按钮的点击事件
		mBtnQuery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				doQuery();
			}
		});
	}

	protected void doQuery() {
		
		String number = mEtNum.getText().toString().trim();
		
		//非空校验
		if(TextUtils.isEmpty(number)) {
			Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
			Animation shake = AnimationUtils.loadAnimation(NumberAddressQueryActivity.this, R.anim.shake);
			mEtNum.startAnimation(shake);
			return ;
		}
		
		
		String address = AddressDao.findAddress(this, number);
		
		if(address == null) {
			Toast.makeText(this, "未知", Toast.LENGTH_SHORT).show();
			return ;
		} else {
			mTvAddress.setText("归属地:"+ address);
		}
		
		
		
	}
}
