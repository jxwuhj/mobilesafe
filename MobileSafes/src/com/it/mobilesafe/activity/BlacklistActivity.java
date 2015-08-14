package com.it.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.BlackInfo;
import com.it.mobilesafe.db.BlackDao;

public class BlacklistActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	
	public final static String NUMBER = "number";
	public final static String TYPE = "type";
	public final static String POSITION = "position";
	
	public static final String ACTION_ADD = "action_add";
	public static final String ACTION_UPDATE = "action_update";
	
	
	private TextView mTvTitle;
	private Button mbtnOk;
	private Button mbtnCancel;
	private RadioGroup mRg;
	private EditText mTvNum;
	private int checkedId = -1;
	private BlackDao dao;
	private int mPositon = -1;
	
	private boolean  isUpdate = false;
	private Intent intent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_blicklist);
		
		mbtnOk = (Button) findViewById(R.id.bl_btn_save);
		mbtnCancel = (Button) findViewById(R.id.bl_btn_cancel);
		mRg = (RadioGroup) findViewById(R.id.bl_rg);
		mTvNum = (EditText) findViewById(R.id.bl_et_num);
		
		mTvTitle = (TextView) findViewById(R.id.bl_tv_title);
		
		//初始化
		dao = new BlackDao(this);
		
		mbtnOk.setOnClickListener(this);
		mbtnCancel.setOnClickListener(this);
		
		mRg.setOnCheckedChangeListener(this);
		
		intent = getIntent();
		String action = intent.getAction();
		
		
		if(ACTION_UPDATE.equals(action)) {
			
			isUpdate = true;
			
			mPositon = intent.getIntExtra(POSITION, -1);
			
			//做更新的操作
			//更改  的地方  1.title   ;   2.格式要变   ;  3.输入框  et  -- 禁用   ;  4. 数据  -- 需要了解更新的号码  单选框  选择的类型
			mTvTitle.setText("更新黑马单");
			mbtnOk.setText("更新");
			
			mTvNum.setEnabled(false);
			
			//需要知道当前的号码
			String number = intent.getStringExtra(NUMBER);
			mTvNum.setText(number);
			//需要知道当前单选框 -- 类型
			int type = intent.getIntExtra(TYPE, -1);
			switch (type) {
			case BlackInfo.TYPE_CALL:
				checkedId = R.id.bl_call;  // RadioGroup 的id
				break;
			case BlackInfo.TYPE_SMS:
				checkedId = R.id.bl_sms;
				break;
			case BlackInfo.TYPE_ALL:
				checkedId = R.id.bl_all;
				break;

			default:
				break;
			} 
			mRg.check(checkedId);
		} else {
			//添加
		}
			
		
	}

	@Override
	public void onClick(View v) {
		
		if(v == mbtnOk) {
			performSave();
			
		} else if(v == mbtnCancel) {
			performCancel();
			
		}
	}

	private void performCancel() {
		finish();
	}

	private void performSave() {
		
		//校验
		String number = mTvNum.getText().toString().trim();
		if(TextUtils.isEmpty(number)) {
			Toast.makeText(this, "号码不可为空", Toast.LENGTH_SHORT).show();
			mTvNum.requestFocus();
			return;
		}
		
		if(checkedId == -1) {
			Toast.makeText(this, "类型不可为空", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 单选框
		int type = -1;
		switch (checkedId) {
		
		case R.id.bl_call://电话  0
			type = BlackInfo.TYPE_CALL;
			break;
		case R.id.bl_sms://短信    1
			
			type = BlackInfo.TYPE_SMS;
			
			break;
		case R.id.bl_all://电话 + 短信   2
			
			type = BlackInfo.TYPE_ALL;
			
			break;
		default:
			break;
		}
		if(dao != null) {
			if(isUpdate) {
				dao.update(number, type);
			
				//Log.d(TAG, "update  ....  mPosition"+ mPositon);
				intent.putExtra(TYPE, type);
				setResult(Activity.RESULT_OK, intent);
				
			} else {
				//数据库操作,添加数据
				dao.add(number, type);
				
				//把数据返回
				
				intent.putExtra(NUMBER, number);
				intent.putExtra(TYPE, type);
				setResult(Activity.RESULT_OK, intent);
			}
			
		}
		
	
		
		finish();
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		this.checkedId = checkedId;
	}
	
}
