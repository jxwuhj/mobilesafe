package com.it.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.HomeItem;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;

public class HomeActivity extends Activity implements OnItemClickListener {
	
	private ImageView mIvLlogo;
	private GridView mGv;
	private List<HomeItem> mDatas;
	
	private final static String[]TITTLES =new String[] {"手机防盗","骚扰拦截","软件管家",
			"进程管理","流量统计","手机杀毒","缓存清理","常用工具"};
	private final static String[] DESCS = new String[]{"进程定位手机","全面拦截骚扰",
			"管理您的软件","管理正在运行","流量一目了然","病毒无处藏身","缓存清理","常用工具大全"};
	private final static int[] ICONS = new int[]{R.drawable.sjfd,R.drawable.srlj,R.drawable.rjgj ,
		R.drawable.jcgl,R.drawable.lltj,R.drawable.sjsd,R.drawable.hcql,R.drawable.cygj};
	private static final String TAG = "HomeActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home);
		
		//view的初始化
		mIvLlogo = (ImageView) findViewById(R.id.m_iv_logo);
		
		//GridView 的初始化
		mGv = (GridView) findViewById(R.id.home_gv);
		
		mGv.setAdapter(new HomeAdapter());
		
		//设置item点击事件
		mGv.setOnItemClickListener(this);
		
		//让ImageView做动画
		ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLlogo, "rotationY",
				0,90,270,360);
		animator.setDuration(2000);
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setRepeatMode(ObjectAnimator.REVERSE);
		animator.start();
		
		//list数据的初始化
		mDatas = new ArrayList<HomeItem>();
		for (int i = 0; i < ICONS.length; i++) {
			//也需要在循环中重新new HomeItem()对象,才能达到目的
			HomeItem item = new HomeItem();
			item.tittle = TITTLES[i];
			item.desc = DESCS[i];
			item.iconId = ICONS[i];
			mDatas.add(item);
		}
		
		
	}
	
	public void clickSetting(View v) {
		Intent intent = new Intent(this,SettingActivity.class);
		startActivity(intent);
	}
	
	private class HomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(mDatas!=null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mDatas != null) {
				return mDatas.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = null;
			if(convertView == null) {
				view = View.inflate(HomeActivity.this, R.layout.item_home, null);
			} else {
				view = convertView;
			}
			//先在布局中拿到三个控件的id
			ImageView ivIcon = (ImageView) view.findViewById(R.id.item_home_iv_icon);
			TextView tvTittle = (TextView) view.findViewById(R.id.item_home_tv_tittle);
			TextView tvDesc = (TextView) view.findViewById(R.id.item_home_tv_desc);
			
			HomeItem item = mDatas.get(position);
			
			//再在布局中赋值操作
			ivIcon.setImageResource(item.iconId);
			tvTittle.setText(item.tittle);
			tvDesc.setText(item.desc);
			
			
			return view;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//position : 位置从0 开始
		switch (position) {
		case 0:	//手机防盗页面
			PerformSjfd();
			break;
		case 1:
			//骚扰拦截
			PerformSrlj();
			break;
		case 2:
			//软件管理
			PerformAppManager();
			break;
		case 3:
			//进程管理
			PerformProcessManager();
			break;
		case 4:
			//流量统计
			PerformTrafficCount();
			break;
		case 5:
			//手机杀毒
			PerformAntiVirus();
			break;
		case 6:
			//缓存清理
			PerformClearCache();
			break;
			
		case 7:
			//骚扰拦截
			PerformCommonTool();
			break;

		default:
			break;
		}
		
		
	}

	private void PerformAntiVirus() {
		Intent intent = new Intent(this,AntiVirusActivity.class);
		startActivity(intent);
	}

	private void PerformTrafficCount() {
		Intent intent = new Intent(this,TrafficActivity.class);
		startActivity(intent);
	}

	private void PerformClearCache() {
		Intent intent = new Intent(this,ClearCacheActivity.class);
		startActivity(intent);
	}

	private void PerformProcessManager() {
		Intent intent = new Intent(this,ProcessManagerActivity.class);
		startActivity(intent);
		
	}

	private void PerformAppManager() {
		Intent intent = new Intent(this,AppManagerActivity.class);
		startActivity(intent);
	}

	//常用工具
	private void PerformCommonTool() {
		
		Intent intent = new Intent(this,CommonToolActivity.class);
		startActivity(intent);
	}

	//骚扰拦截
	private void PerformSrlj() {
		// TODO
		Intent intent = new Intent(this,CallSmsActivity.class);
		startActivity(intent);
		
	}

	private void PerformSjfd() {
		//手机防盗逻辑判断,当第一次进入的时候,重复输入密码,然而进入;当不再是第一次进入的时候,仅仅是输入一个密码即可
		String sjfd = PreferenceUtils.getString(this, Constants.SJFD_PASSWORD);
		
		if(TextUtils.isEmpty(sjfd)) {
			//第一次进入
			Log.d(TAG, "弹出设置密码的对话框");
			showInitPwdDialog();
		} else {
			//不再是第一次
			Log.d(TAG, "弹出输入密码的对话框");
			showEnterPwdDialog();
		}
			
		
	}

	private void showInitPwdDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		View view = View.inflate(this, R.layout.dialog_pwd_init, null);
		
		final EditText enterPwd = (EditText) view.findViewById(R.id.init_btn_enter);
		final EditText confirmPwd = (EditText) view.findViewById(R.id.init_btn_confirm);
		
		Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
		
		//将布局填充到AlertDialog 中
		builder.setView(view);
		
		final AlertDialog dialog = builder.create();
		
		//点击事件
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//非空判断
				 String firstPwd = enterPwd.getText().toString().trim();
				if(TextUtils.isEmpty(firstPwd)  ) {
					Toast.makeText(HomeActivity.this, "密码不能设置为空", Toast.LENGTH_SHORT).show();
					//从新获得焦点
					enterPwd.requestFocus();
					return;
				}
				
				 String lastPwd = confirmPwd.getText().toString().trim();
				
				if( TextUtils.isEmpty(lastPwd) ) {
					Toast.makeText(HomeActivity.this, "确认密码能为空", Toast.LENGTH_SHORT).show();
					confirmPwd.requestFocus();
					return;
				}
				
				//判断两次输入的密码
				if(!firstPwd.equals(lastPwd)) {
					Toast.makeText(HomeActivity.this, "两次输入的密码不一致,请确认", Toast.LENGTH_SHORT).show();
					return;
				}
				//保存密码
				PreferenceUtils.putString(HomeActivity.this, Constants.SJFD_PASSWORD, firstPwd);
				
				//跳转下一个页面   --- 手机防盗
				Log.d(TAG, "进入手机防盗页面");
				enterSjfd();
				
				//关闭dialog
				dialog.dismiss();
			}
		});
		
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//关闭dialog
				dialog.dismiss();
			}
		});
		
		dialog.show();
		
	}

	

	private void showEnterPwdDialog() {
		//不再是第一次进入的逻辑
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		View view = View.inflate(this, R.layout.dialog_pwd_enter, null);
		
		final EditText enterPwd = (EditText) view.findViewById(R.id.enter_btn_pwd);
		
		Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
		
		//将布局填充到AlertDialog 中
		builder.setView(view);
		
		final AlertDialog dialog = builder.create();
		
		//点击事件
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//非空判断
				 String firstPwd = enterPwd.getText().toString().trim();
				if(TextUtils.isEmpty(firstPwd)  ) {
					Toast.makeText(HomeActivity.this, "输入密码不能为空", Toast.LENGTH_SHORT).show();
					//从新获得焦点
					enterPwd.requestFocus();
					return;
				}
				
				//取出 保存的密码
				String savePwd = PreferenceUtils.getString(HomeActivity.this, Constants.SJFD_PASSWORD);
				
				//验证输入的密码 与保存的密码是否一致
				if(!firstPwd.equals(savePwd)) {
					Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
					return;
				} else {
					//跳转下一个页面   --- 手机防盗
					Log.d(TAG, "进入手机防盗页面");
					enterSjfd();
				}
				
				
				
				//关闭dialog
				dialog.dismiss();
			}
		});
		
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "返回home,不做操作");
				//关闭dialog
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	protected void enterSjfd() {
		//手机防盗页面    
		//以是否开启 防盗保护的功能 
		boolean isLock = PreferenceUtils.getBoolean(this, Constants.SJFD_ISLOCK);
		
		if(isLock) {
			//如果开启了,进入引导页面   ---  最终页面
			Log.d(TAG, "进入最终页面");
			//TODO
		} else {
			//2.否则进入引导页面
			Log.d(TAG, "进入引导页面");
			Intent intent = new Intent();
			intent.setClass(this, GuideSetup1Activity.class);
			startActivity(intent);
		}
		
		
		
	}
}
