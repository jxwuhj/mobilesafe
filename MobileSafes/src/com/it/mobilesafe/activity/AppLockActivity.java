package com.it.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.AppInfo;
import com.it.mobilesafe.db.AppLockDao;
import com.it.mobilesafe.engine.AppInfoProvider;
import com.it.mobilesafe.view.SegementControlView;
import com.it.mobilesafe.view.SegementControlView.OnSelecedListener;

public class AppLockActivity extends Activity {

	private SegementControlView msegementControlView;
	private TextView mTvTip;

	private ListView mLvUnlock;
	private ListView mLvlock;

	private List<AppInfo> mUnLockDatas;
	private List<AppInfo> mLockDatas;

	private AppLockDao dao;

	private LinearLayout mLoading;
	
	private boolean isAnimation;
	
	private AppLockAdapter mUnAdapter;
	private AppLockAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);

		msegementControlView = (SegementControlView) findViewById(R.id.al_scv);
		mTvTip = (TextView) findViewById(R.id.al_tv_tip);

		mLvUnlock = (ListView) findViewById(R.id.al_lv_unlock);
		mLvlock = (ListView) findViewById(R.id.al_lv_lock);
		mLoading = (LinearLayout) findViewById(R.id.pl_loading);

		msegementControlView.setOnSelecedListener(new OnSelecedListener() {

			@Override
			public void onSelected(boolean isLeftSelected) {
				if (isLeftSelected) {
					mTvTip.setText("未加锁");
					mLvUnlock.setVisibility(View.VISIBLE);
					mLvlock.setVisibility(View.GONE);
				} else {
					mTvTip.setText("已加锁");
					mLvUnlock.setVisibility(View.GONE);
					mLvlock.setVisibility(View.VISIBLE);
				}
			}
		});

		// List数据的加载
		new AsyncTask<Void, Void, Void>() {

			protected void onPreExecute() {
				// 进度条显示
				mLoading.setVisibility(View.VISIBLE);
			};

			@Override
			protected Void doInBackground(Void... params) {
				// 耗时操作
				List<AppInfo> allApps = AppInfoProvider
						.getAllApps(getApplicationContext());

				mUnLockDatas = new ArrayList<AppInfo>();
				mLockDatas = new ArrayList<AppInfo>();
				dao = new AppLockDao(getApplicationContext());

				for (AppInfo info : allApps) {
					if (dao.findLock(info.packageName)) {
						mLockDatas.add(info);
					} else {
						mUnLockDatas.add(info);
					}
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				// 进度条隐藏
				mLoading.setVisibility(View.GONE);
				mTvTip.setText("未加锁:"+mUnLockDatas.size()+"个");
				
				mUnAdapter = new AppLockAdapter(false);
				mLvUnlock.setAdapter(mUnAdapter); // adapter
				mAdapter = new AppLockAdapter(true);
				mLvlock.setAdapter(mAdapter);
			};

		}.execute();

	}

	private class AppLockAdapter extends BaseAdapter {

		// 针对两种不同的情况,可以在构造方法中去辨别是哪一个
		private boolean mLock;

		// 构造方法给成员变量赋值
		public AppLockAdapter(boolean lock) {
			this.mLock = lock;
		}

		@Override
		public int getCount() {
			if (mLock) {
				if (mLockDatas != null) {
					mTvTip.setText("已加锁:"+mLockDatas.size()+"个");
					return mLockDatas.size();
				} else {
					mTvTip.setText("已加锁:0个");
				}
			} else {
				if (mUnLockDatas != null) {
					mTvTip.setText("未加锁:"+mUnLockDatas.size()+"个");
					return mUnLockDatas.size();
				} else {
					mTvTip.setText("未加锁:0个");
				}
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {

			if (mLock) {
				if (mLockDatas != null) {
					return mLockDatas.get(position);
				}
			} else {
				if (mUnLockDatas != null) {
					return mUnLockDatas.get(position);
				}
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
			if (convertView == null) {
				// 没有复用,加载view
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_app_lock, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				// 初始化holder中的view :

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_al_iv_icon);
				holder.ivLock = (ImageView) convertView
						.findViewById(R.id.item_al_iv_lock);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_tv_appname);

			} else {

				holder = (ViewHolder) convertView.getTag();

			}
			// 给view设置数据
			AppInfo info = null;
			if (mLock) {
				info = mLockDatas.get(position);
			} else {
				info = mUnLockDatas.get(position);
			}

			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);

			// 设置样式
			if (mLock) {
				// 显示解锁
				holder.ivLock.setImageResource(R.drawable.btn_unlock_selector);
			} else {
				// 显示加锁
				holder.ivLock.setImageResource(R.drawable.btn_lock_selector);
			}

			final AppInfo app = info;
			final View view = convertView;
			holder.ivLock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					if(isAnimation) {
						return;
					}
					if (mLock) {
						// 已 锁 --> 要解锁
						
						TranslateAnimation ta = new TranslateAnimation(
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, -1,
								Animation.RELATIVE_TO_PARENT, 0, 
								Animation.RELATIVE_TO_PARENT, 0);
						ta.setDuration(200);
						//加载动画,需要一定的时间,而第一时间已经把UI更新,所有加载监听
						ta.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
								isAnimation = true;
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
								
							}
							
							//在动画停止后,再去更新UI
							@Override
							public void onAnimationEnd(Animation animation) {
								isAnimation = false;
								// list数据存储在内存,故还要持久化操作
								// 从数据中移除
								if (dao.delete(app.packageName)) {
									
									mLockDatas.remove(app);
									mUnLockDatas.add(app);

									mUnAdapter.notifyDataSetChanged();
									mAdapter.notifyDataSetChanged();

								} else {
									Toast.makeText(getApplicationContext(), "移除失败",
											Toast.LENGTH_SHORT).show();
								}
							}
						});
						view.startAnimation(ta);
						
					

					} else {
						// 加锁 -- 未加锁
						// 位移动画 -- 左-->右
						TranslateAnimation ta = new TranslateAnimation(
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 1,
								Animation.RELATIVE_TO_PARENT, 0, 
								Animation.RELATIVE_TO_PARENT, 0);
						ta.setDuration(200);
						//加载动画,需要一定的时间,而第一时间已经把UI更新,所有加载监听
						ta.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
								isAnimation = true;
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
								
							}
							
							//在动画停止后,再去更新UI
							@Override
							public void onAnimationEnd(Animation animation) {
								isAnimation = false;
								if (dao.add(app.packageName)) {

									mUnLockDatas.remove(app);
									mLockDatas.add(app);

									mUnAdapter.notifyDataSetChanged();
									mAdapter.notifyDataSetChanged();
								} else {
									Toast.makeText(getApplicationContext(), "加锁失败",
											Toast.LENGTH_SHORT).show();
								}
							}
						});
						view.startAnimation(ta);

					

					}
				}
			});

			return convertView;
		}

	}

	private class ViewHolder {

		ImageView ivIcon;
		ImageView ivLock;
		TextView tvName;
	}
}
