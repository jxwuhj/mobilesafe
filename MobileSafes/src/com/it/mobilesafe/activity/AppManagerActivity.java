package com.it.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.AppInfo;
import com.it.mobilesafe.engine.AppInfoProvider;
import com.it.mobilesafe.view.ProgressDesView;

public class AppManagerActivity extends Activity {
	private static final String TAG = "AppManagerActivity";
	private ProgressDesView mPdRom;
	private ProgressDesView mPdSdcard;
	private ListView mListview;
	private LinearLayout mLlLoding;
	private TextView mTvHeader;

	private List<AppInfo> mDatas;
	private List<AppInfo> mSystemDatas;
	private List<AppInfo> mUserDatas;
	
	private AppAdapter adapter;
	
	private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "接收到卸载广播");
			String dataString = intent.getDataString();
			Log.d(TAG, "卸载了"+ dataString);
			
			String packageName = dataString.replace("package:", "");
			
			//UI更新
			ListIterator<AppInfo> iterator = mUserDatas.listIterator();
			
			while(iterator.hasNext()) {
				AppInfo next = iterator.next();
				
				if(next.packageName.equals(packageName)) {
					//移除
					iterator.remove();
					break;
				}
				
			}
			
			//Adapter更新
			adapter.notifyDataSetChanged();
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		// 初始化view
		mPdRom = (ProgressDesView) findViewById(R.id.am_rom);
		mPdSdcard = (ProgressDesView) findViewById(R.id.am_sdcard);

		mListview = (ListView) findViewById(R.id.am_listview);

		mLlLoding = (LinearLayout) findViewById(R.id.pl_loading);

		mTvHeader = (TextView) findViewById(R.id.am_header);
		
		//注册package安装和卸载的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addDataScheme("package");
		registerReceiver(mPackageReceiver, filter);

		// 设置数据
		mPdRom.setDesTitle("内存:");
		File dataDirectory = Environment.getDataDirectory();
		long freeSpace = dataDirectory.getFreeSpace();
		long totalSpace = dataDirectory.getTotalSpace();
		long userSpace = totalSpace - freeSpace;
		mPdRom.setDesLeft(Formatter.formatFileSize(this, userSpace) + "M已用");
		mPdRom.setDesRight(Formatter.formatFileSize(this, freeSpace) + "M可用");
		int mProgress = (int) (userSpace * 100f / totalSpace + 0.5f);
		mPdRom.setDesProgress(mProgress);

		File SdcardDirectory = Environment.getExternalStorageDirectory();
		mPdSdcard.setDesTitle("Sd卡:");
		long sdFreeSpace = SdcardDirectory.getFreeSpace();
		long sdTotalSpace = SdcardDirectory.getTotalSpace();
		long sdUserSpace = sdTotalSpace - sdFreeSpace;
		mPdSdcard.setDesLeft(Formatter.formatFileSize(this, sdUserSpace)
				+ "M已用");
		mPdSdcard.setDesRight(Formatter.formatFileSize(this, sdFreeSpace)
				+ "M可用");
		mProgress = (int) (sdUserSpace * 100f / sdTotalSpace + 0.5f);
		mPdSdcard.setDesProgress(mProgress);

		// 数据加载
		mLlLoding.setVisibility(View.VISIBLE);
		mTvHeader.setVisibility(View.GONE);
		new Thread() {
			public void run() {

				SystemClock.sleep(1200);

				// 加载数据 -- 耗时
				mDatas = AppInfoProvider.getAllApps(AppManagerActivity.this);

				mSystemDatas = new ArrayList<AppInfo>();
				mUserDatas = new ArrayList<AppInfo>();

				for (AppInfo info : mDatas) {

					// 系统程序还是 用户程序
					if (info.isSystem) {
						mSystemDatas.add(info);
					} else {
						mUserDatas.add(info);
					}

				}

				// 给listView设置数据 -- UI改变
				runOnUiThread(new Runnable() {
					

					@Override
					public void run() {
						mTvHeader.setVisibility(View.VISIBLE);
						// 写在run()方法里面
						mLlLoding.setVisibility(View.GONE);
						
						adapter = new AppAdapter();
						mListview.setAdapter(adapter);
					}
				});

			};
		}.start();

		// 监听listView的滑动事件
		mListview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 在滑动的过程当中

				if (mUserDatas == null || mSystemDatas == null) {
					return;
				}

				// firstVisibleItem 第一个可见
				// 如果是第一个 可见,就显示为对应的头
				int userSize = mUserDatas.size();

				if (firstVisibleItem >= 0 && firstVisibleItem <= userSize) {
					// 用户程序部分 //给头布局设置数据
					mTvHeader.setText("用户程序(" + mUserDatas.size() + "个)");
				} else if (firstVisibleItem >= userSize + 1) {
					mTvHeader.setText("系统程序(" + mSystemDatas.size() + "个)");
				}

			}
		});

		// 设置ListView的item的点击事件
		mListview.setOnItemClickListener(new OnItemClickListener() {

			private PopupWindow mWindow;
			private View contentView;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 弹出层

				// 点击的是应用的头
				if (position == 0) {
					return;
				}

				// 点击的是系统的头
				int userSize = mUserDatas.size();
				if (position == userSize + 1) {
					return;
				}

				// 获取 点击的事件
				AppInfo info = null;
				if (position > 0 && position < userSize + 1) {
					info = mUserDatas.get(position - 1);
				} else {
					info = mSystemDatas.get(position - userSize - 2);
				}

				// 展示层

				/*
				 * TextView contentView = new TextView(getApplicationContext());
				 * //展示的内容的view contentView.setText("这是popupWindow展示");
				 * contentView.setTextColor(Color.RED);
				 * contentView.setPadding(8, 8, 8, 8);
				 * contentView.setBackgroundColor(Color.RED);
				 */

				// 展示view的宽高
				// 初始化

				if (mWindow == null) {

					int width = LayoutParams.WRAP_CONTENT;
					int height = LayoutParams.WRAP_CONTENT;

					contentView = View.inflate(getApplicationContext(),
							R.layout.popup_item_app, null);

					mWindow = new PopupWindow(contentView, width, height);
					// 焦点
					mWindow.setFocusable(true);
					// 设置其他位置可触摸
					mWindow.setOutsideTouchable(true);
					mWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));
					// 显示 ,在anchor控件的下方显示
					// window.showAsDropDown(view,Gravity.CENTER_HORIZONTAL,Gravity.CENTER_VERTICAL);
					// 设置动画
					mWindow.setAnimationStyle(R.style.PopupAnimation);

					final AppInfo app = info;

					contentView.findViewById(R.id.pp_ll_uninstall)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// 卸载 发意图给系统,然后卸载

									/*
									 * <intent-filter> <action
									 * android:name="android.intent.action.VIEW"
									 * /> <action
									 * android:name="android.intent.action.DELETE"
									 * /> <category android:name=
									 * "android.intent.category.DEFAULT" />
									 * <data android:scheme="package" />
									 * </intent-filter>
									 */

									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.setAction("android.intent.action.DELETE");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setData(Uri.parse("package:"
											+ app.packageName));

									startActivity(intent);
									
									//poup 隐藏
									mWindow.dismiss();
								}
							});

					contentView.findViewById(R.id.pp_ll_open)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									//打开
									PackageManager pm = getPackageManager();
									Intent intent = pm.getLaunchIntentForPackage(
											app.packageName);
									
									if(intent != null) {
										startActivity(intent);
									}
									
									//poup 隐藏
									mWindow.dismiss();
								}
							});

					contentView.findViewById(R.id.pp_ll_share)
							.setOnClickListener(new OnClickListener() {

							/*	<intent-filter>
					               <action android:name="android.intent.action.SEND" />
					               <category android:name="android.intent.category.DEFAULT" />
					               <data android:mimeType="text/plain" />
					           </intent-filter>*/
								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.SEND");
									intent.addCategory("android.intent.category.DEFAULT");
									
									intent.setDataAndType(Uri.parse("china,java"), "text/plain");
									startActivity(intent);
									
									//隐藏poup
									mWindow.dismiss();
								}
							});

					contentView.findViewById(R.id.pp_ll_info)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									//信息详情
									
									/* <intent-filter>
						                <action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
						                <category android:name="android.intent.category.DEFAULT" />
						                <data android:scheme="package" />
						            </intent-filter>*/
									Intent intent = new Intent();
									intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setData(Uri.parse("package:"+app.packageName));
									startActivity(intent);
									
									//隐藏poup
									mWindow.dismiss();
								}
							});

				}
				mWindow.showAsDropDown(view, 60, -view.getHeight());

			}
		});

	}
	
	@Override
	protected void onDestroy() {
		//注销
		unregisterReceiver(mPackageReceiver);
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 100) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				Log.d(TAG, "卸载成功");
				break;
			case Activity.RESULT_CANCELED:
				Log.d(TAG, "取消卸载");
				break;

			default:
				break;
			}
		}
	
	}
	

	private class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			/*
			 * if (mDatas != null) { return mDatas.size(); }
			 */
			int systemCount = 0;
			if (mSystemDatas != null) {
				systemCount = mSystemDatas.size();
				systemCount += 1;
			}

			int userCount = 0;
			if (mUserDatas != null) {
				userCount = mUserDatas.size();
				userCount += 1;
			}

			return systemCount + userCount;
		}

		@Override
		public Object getItem(int position) {
			/*
			 * if (mDatas != null) { return mDatas.get(position); }
			 */
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int userSize = mUserDatas.size();
			// 用户部分
			if (position == 0) {
				// 显示用户的头
				TextView tv = new TextView(getApplicationContext());
				tv.setPadding(4, 4, 4, 4);
				tv.setBackgroundColor(Color.parseColor("#ffcccccc"));
				tv.setTextColor(Color.BLACK);

				tv.setText("用户程序(" + userSize + ")");

				return tv;
			}

			int systemSize = mSystemDatas.size();
			// 系统的头部分
			if (position == userSize + 1) {
				TextView tv = new TextView(getApplicationContext());
				tv.setPadding(4, 4, 4, 4);
				tv.setBackgroundColor(Color.parseColor("#33000000"));
				tv.setTextColor(Color.BLACK);

				tv.setText("系统程序(" + systemSize + ")");

				return tv;
			}

			ViewHolder holder = null;

			if (convertView == null || convertView instanceof TextView) {

				convertView = View.inflate(getApplicationContext(),
						R.layout.item_app_info, null);

				holder = new ViewHolder();


				// 打标记
				convertView.setTag(holder);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_appinfo_iv_icon);
				holder.tvInstallPath = (TextView) convertView
						.findViewById(R.id.item_appinfo_tv_installpath);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_appinfo_tv_name);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.item_appinfo_tv_size);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 显示数据
			AppInfo info = null;

			if (position < userSize + 1) {
				// 显示的是用户程序部分
				info = mUserDatas.get(position - 1);
			} else {
				info = mSystemDatas.get(position - userSize - 2);
			}

			// 设置数据
			// AppInfo info = mDatas.get(position);

			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvInstallPath.setText(info.isInstallSD ? "SD卡安装" : "手机内存");
			holder.tvName.setText(info.name);

			holder.tvSize.setText(Formatter.formatFileSize(
					getApplicationContext(), info.size));

			return convertView;
		}

	}

	class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvInstallPath;
		TextView tvSize;

	}
}
