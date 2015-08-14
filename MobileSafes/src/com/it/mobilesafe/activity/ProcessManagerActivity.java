package com.it.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.ProcessInfo;
import com.it.mobilesafe.engine.ProcessProvider;
import com.it.mobilesafe.service.AutoCleanService;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;
import com.it.mobilesafe.utils.ServiceStateutUtils;
import com.it.mobilesafe.view.ProgressDesView;
import com.it.mobilesafe.view.SettingItemView;

public class ProcessManagerActivity extends Activity {

	private ProgressDesView mPdvProcess;
	private ProgressDesView mPdvMemory;
	private StickyListHeadersListView mListView;
	private LinearLayout mLoading;

	private List<ProcessInfo> mDatas;
	private List<ProcessInfo> mUserDatas;
	private List<ProcessInfo> mSystemDatas;

	private ProcessAdapter mAdapter;
	private int runningProcess;
	private int totalProcessCount;
	private long freeMemory;
	private long totalMemory;

	private ImageView mIvArrow1;
	private ImageView mIvArrow2;
	private SlidingDrawer mDrawer;

	private SettingItemView mShowProcess;
	private boolean showSystem = true;
	private SettingItemView mAutoClean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_process_manager);

		// 初始化view
		mPdvProcess = (ProgressDesView) findViewById(R.id.pm_pdv_process);
		mPdvMemory = (ProgressDesView) findViewById(R.id.pm_pdv_memory);

		mListView = (StickyListHeadersListView) findViewById(R.id.pm_listview);
		mLoading = (LinearLayout) findViewById(R.id.pl_loading);

		mIvArrow1 = (ImageView) findViewById(R.id.pm_iv_arrow1);
		mIvArrow2 = (ImageView) findViewById(R.id.pm_iv_arrow2);
		mDrawer = (SlidingDrawer) findViewById(R.id.pm_draw_sd);
		mShowProcess = (SettingItemView) findViewById(R.id.pm_show_process);
		mAutoClean = (SettingItemView) findViewById(R.id.pm_autoclean);

		mLoading.setVisibility(View.VISIBLE);
		runningProcess = ProcessProvider.getRunningProcessCount(this);
		totalProcessCount = ProcessProvider.getTotalProcessCount(this);
		initProcessUI();

		freeMemory = ProcessProvider.getFreeMemory(this);
		totalMemory = ProcessProvider.getTotalMemory(this);
		initMemoryUI();

		// 开启线程
		new Thread() {
			public void run() {

				SystemClock.sleep(1200);

				// 给listView 加载数据
				mDatas = ProcessProvider
						.getAllRunningProcess(ProcessManagerActivity.this);
				mSystemDatas = new ArrayList<ProcessInfo>();
				mUserDatas = new ArrayList<ProcessInfo>();

				for (ProcessInfo info : mDatas) {
					if (info.isSystem) {
						mSystemDatas.add(info);
					} else {
						mUserDatas.add(info);
					}
				}

				mDatas.clear();
				mDatas.addAll(mUserDatas);
				mDatas.addAll(mSystemDatas);

				// 主线程中设置adapter
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 在数据展示之前隐藏 progress
						mLoading.setVisibility(View.GONE);

						mAdapter = new ProcessAdapter();
						mListView.setAdapter(mAdapter);
					}
				});

			};
		}.start();

		// 设置listView的item点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ProcessInfo info = mDatas.get(position);

				if (info.packageName.equals(getPackageName())) {
					return;
				}
				// 如果选中了,就取消
				info.checked = !info.checked;

				// UI更新
				mAdapter.notifyDataSetChanged();
			}
		});

		// 设置 slidingDrawer 的监听事件
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				showAnimationDown();

			}
		});

		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {

				mIvArrow1.setImageResource(R.drawable.drawer_arrow_up);
				mIvArrow2.setImageResource(R.drawable.drawer_arrow_up);
				showAnimationUp();
			}
		});

		showAnimationUp();

		// 显示系统开关
		showSystem = PreferenceUtils.getBoolean(getApplicationContext(),
				Constants.SHOW_PROCESS, true);
		mShowProcess.setToggleOn(showSystem);

		// 设置 系统进程的点击事件
		mShowProcess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 取得当前的UI状态
				boolean flag = PreferenceUtils.getBoolean(
						getApplicationContext(), Constants.SHOW_PROCESS, true);
				// UI更新
				mShowProcess.setToggleOn(!flag);

				showSystem = !flag;
				mAdapter.notifyDataSetChanged();

				// 数据更新
				PreferenceUtils.putBoolean(getApplicationContext(),
						Constants.SHOW_PROCESS, !flag);
			}
		});
		
	
		
		
		
		//触屏自动清除点击事件
		mAutoClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean running = ServiceStateutUtils.isRunning(
						getApplicationContext(), AutoCleanService.class);
				
				if(running) {
					stopService(new Intent(getApplicationContext(),
							AutoCleanService.class));
				} else {
					startService(new Intent(getApplicationContext(),
							AutoCleanService.class));
				}
				
				//UI操作   点击了按钮,那么按钮取反,变成不一样的状态
				mAutoClean.setToggleOn(!running);
				
				
			}
		});

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		//初始化服务状态
		boolean running = ServiceStateutUtils.isRunning(
				getApplicationContext(), AutoCleanService.class);
		mAutoClean.setToggleOn(running);
	}

	private void showAnimationUp() {

		AlphaAnimation alpha1 = new AlphaAnimation(0.2f, 1f);
		alpha1.setDuration(300);
		alpha1.setRepeatCount(AlphaAnimation.INFINITE);
		alpha1.setRepeatMode(AlphaAnimation.INFINITE);

		mIvArrow1.startAnimation(alpha1);

		AlphaAnimation alpha2 = new AlphaAnimation(1f, 0.2f);
		alpha2.setDuration(300);
		alpha2.setRepeatCount(AlphaAnimation.INFINITE);
		alpha2.setRepeatMode(AlphaAnimation.INFINITE);

		mIvArrow2.startAnimation(alpha2);

	}

	private void showAnimationDown() {

		mIvArrow1.clearAnimation();
		mIvArrow2.clearAnimation();

		mIvArrow1.setImageResource(R.drawable.drawer_arrow_down);
		mIvArrow2.setImageResource(R.drawable.drawer_arrow_down);
	}

	private void initMemoryUI() {
		freeMemory = ProcessProvider.getFreeMemory(this);
		totalMemory = ProcessProvider.getTotalMemory(this);

		long useMemory = totalMemory - freeMemory;

		mPdvMemory.setDesTitle("内存:");
		mPdvMemory.setDesLeft("占用内存"
				+ Formatter.formatFileSize(this, useMemory));
		mPdvMemory.setDesRight("可用内存"
				+ Formatter.formatFileSize(this, freeMemory));
		mPdvMemory
				.setDesProgress((int) (useMemory * 100f / totalMemory + 0.5f));

	}

	private void initProcessUI() {
		runningProcess = ProcessProvider.getRunningProcessCount(this);
		totalProcessCount = ProcessProvider.getTotalProcessCount(this);
		// 进程
		// 1.正在运行的进程数,总的应用数
		mPdvProcess.setDesTitle("进程数:");
		mPdvProcess.setDesLeft("正在运行" + runningProcess + "个");
		mPdvProcess.setDesRight("可用进程" + totalProcessCount + "个");
		mPdvProcess.setDesProgress((int) (runningProcess * 100f
				/ totalProcessCount + 0.5f));
	}

	public void clickAll(View view) {

		// 全选
		if (mDatas == null) {
			return;
		}
		if (showSystem) {

			for (ProcessInfo info : mDatas) {
				if (info.packageName.equals(getPackageName())) {
					continue;
				}
				info.checked = true;
			}
		} else {
			for (ProcessInfo info : mUserDatas) {
				if (info.packageName.equals(getPackageName())) {
					continue;
				}
				info.checked = true;
			}
		}
		// UI更新
		mAdapter.notifyDataSetChanged();
	}

	public void clickReverse(View view) {
		// 反选 --> 选中 了的就不选,没选中的就选
		if (mDatas == null) {
			return;
		}
		if (showSystem) {
			for (ProcessInfo info : mDatas) {

				if (info.packageName.equals(getPackageName())) {
					continue;
				}
				info.checked = !info.checked;
			}
		} else {
			for (ProcessInfo info : mUserDatas) {

				if (info.packageName.equals(getPackageName())) {
					continue;
				}
				info.checked = !info.checked;
			}
		}
		// UI更新
		mAdapter.notifyDataSetChanged();
	}

	// 清理 clean
	public void clickClean(View v) {
		if (mDatas == null) {
			return;
		}

		int processCount = 0;
		long mfreeMemory = 0;
		ListIterator<ProcessInfo> iterator = mDatas.listIterator();
		while (iterator.hasNext()) {
			ProcessInfo info = iterator.next();
			if (info.checked) {
				// killed后台进程
				ProcessProvider.cleanProcess(this, info.packageName);
				// 移除   内存(list)也要移除
				iterator.remove();
				mDatas.remove(info);
				processCount++;
				mfreeMemory += info.Memory;
			}
		}

		// 清理的进程数,释放的内存 -Toast
		if (processCount != 0) {
			Toast.makeText(
					this,
					"结束了" + processCount + "个进程,释放了"
							+ Formatter.formatFileSize(this, mfreeMemory)
							+ "内存", Toast.LENGTH_SHORT).show();

			// 进程
			runningProcess -= processCount;
			// 内存
			freeMemory += mfreeMemory;

			initMemoryUI();
			initProcessUI();
		}

		// UI更新
		mAdapter.notifyDataSetChanged();

	}

	private class ProcessAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			if (showSystem) {
				if (mDatas != null) {
					return mDatas.size();
				}
			} else {
				if (mUserDatas != null) {
					return mUserDatas.size();
				}
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (showSystem) {
				if (mDatas != null)
					return mDatas.get(position);
			} else {
				if (mUserDatas != null)
					return mUserDatas.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null || (convertView instanceof TextView)) {

				convertView = View.inflate(getApplicationContext(),
						R.layout.item_process, null);
				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_process_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_process_tv_name);
				holder.tvMemory = (TextView) convertView
						.findViewById(R.id.item_process_tv_memory);
				holder.cbChoice = (CheckBox) convertView
						.findViewById(R.id.item_process_cb);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置数据
			ProcessInfo info = null;

			if (showSystem) {
				info = mDatas.get(position);
			} else {
				info = mUserDatas.get(position);
			}

			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvMemory.setText("占用内存:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.Memory));

			if (info.packageName.equals(getPackageName())) {
				// 那就不显示出来
				holder.cbChoice.setVisibility(View.GONE);
			} else {
				holder.cbChoice.setVisibility(View.VISIBLE);
			}

			// 设置是否选中
			holder.cbChoice.setChecked(info.checked);

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {

			TextView tv = null;
			if (convertView == null || !(convertView instanceof TextView)) {
				convertView = new TextView(getApplicationContext());
				tv = (TextView) convertView;
			} else {
				tv = (TextView) convertView;
			}

			ProcessInfo info = null;

			if (showSystem) {
				info = mDatas.get(position);
			} else {
				info = mUserDatas.get(position);
			}
			boolean isSystem = info.isSystem;

			tv.setText(isSystem ? "系统进程有(" + mSystemDatas.size() + ")个"
					: "用户进程" + mUserDatas.size() + ")个");
			tv.setPadding(4, 4, 4, 4);
			tv.setTextSize(16);
			tv.setBackgroundColor(Color.parseColor("#000000"));

			return convertView;
		}

		// 获取唯一的地方
		@Override
		public long getHeaderId(int position) {
			ProcessInfo info = mDatas.get(position);

			return info.isSystem ? 0 : 1;
		}

	}

	private class ViewHolder {

		ImageView ivIcon;
		TextView tvName;
		TextView tvMemory;
		CheckBox cbChoice;

	}

}
