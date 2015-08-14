package com.it.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.it.mobilesafe.R;
import com.it.mobilesafe.service.CallSmsService;
import com.it.mobilesafe.service.NumberAddressService;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;
import com.it.mobilesafe.utils.ServiceStateutUtils;
import com.it.mobilesafe.view.AddressDialog;
import com.it.mobilesafe.view.SettingItemView;

//设置页面
public class SettingActivity extends Activity {

	public static final String TAG = "SettingActivity";
	private SettingItemView mSiv;
	private SettingItemView mCallSms;
	private SettingItemView mAdress;
	private SettingItemView mStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mSiv = (SettingItemView) findViewById(R.id.setting_siv_update);
		mCallSms = (SettingItemView) findViewById(R.id.setting_callsms_infest);
		mAdress = (SettingItemView) findViewById(R.id.setting_siv_address);
		mStyle = (SettingItemView) findViewById(R.id.setting_siv_style);
		
		
		//归属地样式
		mStyle.setOnClickListener(new OnClickListener() {
			//弹出Dialog 
			@Override
			public void onClick(View v) {
				final AddressDialog mDialog = new AddressDialog(SettingActivity.this);
				
				mDialog.setAdapter(new AddressAdapter());
				
				mDialog.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						mDialog.dismiss();
						
						PreferenceUtils.putInt(getApplicationContext(),
								Constants.ADDRESS_STYLE, icons[position]);
					}
				}) ;
				
				mDialog.show();
			}
		});
		
		
		mAdress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(ServiceStateutUtils.isRunning(getApplicationContext(),
						NumberAddressService.class)) {
					stopService(new Intent(getApplicationContext(),
							NumberAddressService.class));
					mAdress.setToggleOn(false);
				} else {
					startService(new Intent(getApplicationContext(),
							NumberAddressService.class));
					mAdress.setToggleOn(true);
				}
					
			}
		});

		// 给UI初始化,即需要设置当前更新的状态
		boolean isUpdate = PreferenceUtils.getBoolean(this,
				Constants.AUTO_UPDATE, true);
		mSiv.setToggleOn(isUpdate);

		mSiv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果是打开状态,就关闭;如果是关闭那就打开
				/*
				 * if(mSiv.isToggleOn()) { mSiv.setToggleOn(false); } else {
				 * mSiv.setToggleOn(true); }
				 */
				mSiv.toggle();

				// 存储状态信息
				boolean toggleOn = mSiv.isToggleOn();
				/*
				 * SharedPreferences sp = getSharedPreferences("config",
				 * Context.MODE_PRIVATE); Editor edit = sp.edit();
				 * edit.putBoolean("setting_update", toggleOn); edit.commit();
				 */
				PreferenceUtils.putBoolean(SettingActivity.this,
						Constants.AUTO_UPDATE, toggleOn);

			}
		});

		// 骚扰拦截 点击判断
		mCallSms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isRunning = ServiceStateutUtils.isRunning(getApplicationContext(),
						CallSmsService.class);
				System.out.println("isRunning"+ isRunning);
				// 开启或者关闭服务 --- > 怎么判断服务怎么开启
				if (isRunning) {

					// 运行时 -- >stop
					Intent intent = new Intent(getApplicationContext(),
							CallSmsService.class);
					stopService(intent);
					mCallSms.setToggleOn(false);

				} else {
					Intent intent = new Intent(getApplicationContext(),
							CallSmsService.class);
					startService(intent);
					mCallSms.setToggleOn(true);
				}

			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();

		if (ServiceStateutUtils.isRunning(getApplicationContext(),
				CallSmsService.class)) {
			
			mCallSms.setToggleOn(true);
			
		} else {
			
			mCallSms.setToggleOn(false);
			
		}
		
		//号码归属地服务判断
		if (ServiceStateutUtils.isRunning(getApplicationContext(),
				NumberAddressService.class)) {
			mAdress.setToggleOn(true);
			
		} else {
			mAdress.setToggleOn(false);
		}
	}
	private String[]titles = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	
	private int[]icons = new int[]{R.drawable.address_toast_bg,R.drawable.address_toast_bg_orange,
			R.drawable.address_toast_bg_blue,R.drawable.address_toast_bg_gray,
			R.drawable.address_toast_bg_green};
	
	private class AddressAdapter extends BaseAdapter {

		

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			/*TextView tv = new TextView(getApplicationContext());
			tv.setPadding(10, 10, 10, 10);
			tv.setText(position+"");*/
			
			ViewHolder holder = null;
			
			if(convertView == null) {
				
				//没有复用
				//初始化convertView
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_address, null);
				//初始化holder
				holder = new ViewHolder();
				
				//设置标记
				convertView.setTag(holder);
				
				//初始化holder的标记
				holder.mIvIcon = (ImageView) convertView.
						findViewById(R.id.address_iv_bg);
				holder.mTvTitle = (TextView) convertView.
						findViewById(R.id.address_tv_title);
				holder.mIvChoise = (ImageView) convertView.
						findViewById(R.id.address_iv_choise);
				
			} else {
				//有复用
				holder = (ViewHolder) convertView.getTag();
				
			}
			
			int style = PreferenceUtils.getInt(getApplicationContext(),
					Constants.ADDRESS_STYLE);
			
			System.out.println();
			
			// 给view设置数据
			Log.d(TAG,"holder :"+ holder);
			Log.d(TAG, "holder.mTvTitle :  "+holder.mTvTitle);
			
			if(holder !=null) {
				holder.mTvTitle.setText(titles[position]);
				System.out.println(titles[position]);
				holder.mIvIcon.setImageResource(icons[position]);
				System.out.println(icons[position]);
				
				if(style == icons[position]) {
					holder.mIvChoise.setVisibility(View.VISIBLE);
					System.out.println(style);
				} else {
					holder.mIvChoise.setVisibility(View.GONE);
					System.out.println(style);
				}
				
			}  
		
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		
		ImageView mIvIcon;
		TextView mTvTitle;
		ImageView mIvChoise;
	}
}
