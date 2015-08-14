package com.it.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.CacheInfo;

public class ClearCacheActivity extends Activity implements OnClickListener {
	
	protected static final String TAG = "ClearCacheActivity 缓存清理";
	private List<CacheInfo> mDatas;
	private PackageManager mPm;
	
	private TextView mTvname;//扫描的应用名称
	private TextView mTvCache;//扫描的线
	private ImageView mIvIcon;//扫描的图标
	private ImageView mIvLine;//扫描的线
	private ProgressBar mProgress;//进度
	
	private ListView mListView;
	
	private ScanTask mTask;
	
	private CacheApapter mDapter;
	
	private RelativeLayout mRlScan;
	private RelativeLayout mRlResult;
	private TextView mTvResult;
	
	private Button mBtnClear;
	
	private int mAppCount;
	private long mCacheSize;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_clear_cache);
		
		
		//初始化view
		mTvname = (TextView) findViewById(R.id.cc_tv_scan_name);
		mTvCache = (TextView) findViewById(R.id.cc_tv_scan_cacheSize);
		mIvIcon = (ImageView) findViewById(R.id.cc_iv_scan_icon);
		mIvLine = (ImageView) findViewById(R.id.cc_iv_scan_line);
		mProgress = (ProgressBar) findViewById(R.id.cc_scan_progress);
		
		mListView = (ListView) findViewById(R.id.cc_listview);
		mRlScan = (RelativeLayout) findViewById(R.id.cc_rl_scan_container);
		mRlResult = (RelativeLayout) findViewById(R.id.cc_rl_result_container);
		mTvResult = (TextView) findViewById(R.id.cc__tv_scan_result);
		
		mBtnClear = (Button) findViewById(R.id.cc_btn_clearall);
		mBtnClear.setOnClickListener(this);
		
		mDapter = new CacheApapter();
		
		
		new AsyncTask<Void, Void, Void>(){
			

			@Override
			protected Void doInBackground(Void... params) {
				
				//1.读取所有数据的信息  -- 主线程之中扫描数据,最好放入到asyncTask中
				mDatas = new ArrayList<CacheInfo>();
				mPm = getPackageManager();
				
				
				startSscan();
				
				return null;
			}

			
			
			}.execute();
	}
	
	/*@Override
	protected void onDestroy() {
		if(mTask!=null) {
			mTask.stop();
			mTask = null;
		}
		super.onDestroy();
	}*/
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(mTask!=null) {
			mTask.stop();
			mTask = null;
		}
		super.onDestroy();
	}
	
	private void startSscan() {
		
		if(mTask!=null) {
			mTask.stop();
			mTask = null;
		}
		mTask = new ScanTask();
		mTask.execute();
	}
	
	public void quickScan(View v) {
		startSscan();
	}
	
	//继承AsyncTask类
	private final class ScanTask extends AsyncTask<Void, CacheInfo, Void> {
		
		private int max = 0;
		private int progress = 0;
		private boolean isFinish; //标记Activity是否有结束
		
		protected void onPreExecute() {
			//在开启前,扫描线,扫描线运动
			TranslateAnimation animation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 1);
			animation.setDuration(1000);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setRepeatMode(Animation.REVERSE);
			mIvLine.startAnimation(animation);
			
			mRlScan.setVisibility(View.VISIBLE);
			mRlResult.setVisibility(View.GONE);
			
			//清零 -- 清理数据
			mAppCount = 0;
			mCacheSize = 0;
			
			//禁用一键清理
			mBtnClear.setEnabled(false);
			
			
		};
		@Override
		protected Void doInBackground(Void... params) {
			
			//1.读取所有数据的信息  -- 主线程之中扫描数据,最好放入到asyncTask中
			mDatas = new ArrayList<CacheInfo>();
			mPm = getPackageManager();
			
			List<PackageInfo> packages = mPm.getInstalledPackages(0);
			
			max = packages.size();
			
			for (PackageInfo pack : packages) {
				
				progress++;
				
				if(isFinish){
					break;
				}
				
				//设置监听
				try {
					Method method = mPm.getClass().getDeclaredMethod
					("getPackageSizeInfo",String.class,IPackageStatsObserver.class);
					
					//当看到log信息时,不清楚时什么错,建议看完log信息
					method.invoke(mPm, pack.packageName,mStatsObserver);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
			}
			
			
			return null;
		}
		
		//更新进度
		public void updateProgress(CacheInfo...progress) {
			publishProgress(progress);
		}
		
		@Override
		protected void onProgressUpdate(CacheInfo... values) {
			super.onProgressUpdate(values);
			
			if(isFinish) {
				return;
			}
			
			/**
			 * 图标变化
			 * 应用名称
			 * 进度条
			 * 应用的缓存数据大小
			 */
			mIvIcon.setImageDrawable(values[0].icon);
			mTvname.setText(values[0].name);
			mTvCache.setText("缓存大小 :"+Formatter.formatFileSize
					(ClearCacheActivity.this, values[0].cacheSize));
			
			//进度条
			mProgress.setMax(max);
			mProgress.setProgress(progress);
			
			//更新
			if(mDatas.size() == 1) {
				//设置adapter
				mListView.setAdapter(mDapter);
				
			} else {
				//adapter更新
				mDapter.notifyDataSetChanged();
				
			}
			
			mListView.smoothScrollToPosition(mDapter.getCount());
			
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			mListView.smoothScrollToPosition(0);
			
			mRlScan.setVisibility(View.GONE);
			mRlResult.setVisibility(View.VISIBLE);
			
			mTvResult.setText("总共有"+mAppCount +"个应用程序有缓存大小为 "+Formatter.formatFileSize(ClearCacheActivity.this, mCacheSize));
			
			//让一键清理可用
			mBtnClear.setEnabled(true);
			
		}
		public void stop() {
			isFinish = true;
		}
		
	}
	
	private class CacheApapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(mDatas!=null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mDatas!=null) {
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
			
			ViewHolder holder = null;
			
			if(convertView == null) {
				
				convertView = View.inflate(ClearCacheActivity.this, R.layout.item_cache_info, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				
				holder.icon = (ImageView) convertView.findViewById(R.id.item_cache_iv_icon);
				holder.clearImg = (ImageView) convertView.findViewById(R.id.item_cache_iv_clear);
				holder.name = (TextView) convertView.findViewById(R.id.item_cache_tv_name);
				holder.cacheSize = (TextView) convertView.findViewById(R.id.item_cache_tv_cacheSize);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			
			final CacheInfo info = mDatas.get(position);
			
			holder.icon.setImageDrawable(info.icon);
			holder.name.setText(info.name);
			holder.cacheSize.setText("缓存大小 :"+Formatter.formatFileSize(ClearCacheActivity.this, info.cacheSize));
			
			holder.clearImg.setVisibility((info.cacheSize>0)?View.VISIBLE:View.GONE);
			
			holder.clearImg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					//因应用属于第三方应用,添加不了  delete_cache_files 权限,故单个的去清理其他软件的缓存做不到
					/*mPm.deleteApplicationCacheFiles(packageName, mClearCacheObserver);
					
					public abstract void deleteApplicationCacheFiles(String packageName,
				            IPackageDataObserver observer);*/
					/*try {
						Method method = mPm.getClass().getDeclaredMethod
						("deleteApplicationCacheFiles", String.class,IPackageDataObserver.class);
						
						
						method.invoke(mPm, info.packageName,new ClearCacheObserver());
						
					} catch (Exception e) {
						e.printStackTrace();
					}*/
					
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					
					intent.setData(Uri.parse("package:"+info.packageName));
					startActivity(intent);
				}
			
			});
			
			return convertView;
		}
		
	}
	
	class ClearCacheObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
        	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "删除缓存成功", Toast.LENGTH_SHORT).show();
				}
			});
         }
     }
	
	private class ViewHolder {
		ImageView icon;
		ImageView clearImg;
		TextView name;
		TextView cacheSize;
	}
	
	
	
	
	 final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
         public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
        	
//        	 String threadName = Thread.currentThread().getName();
//        	 Log.d(TAG, "threadName"+threadName);
        	 
        	 long cacheSize = stats.cacheSize;
        	 String packageName = stats.packageName;
        	 
        		CacheInfo info = new CacheInfo();
        		try {
					ApplicationInfo applicationInfo = 
							mPm.getApplicationInfo(packageName, 0);
					
					info.name = applicationInfo.loadLabel(mPm).toString();
					
					info.icon = applicationInfo.loadIcon(mPm);
					
					info.cacheSize = cacheSize;
					
					info.packageName = packageName;
					
					if(info.cacheSize>0) {
						
						mDatas.add(0, info);
						
						mAppCount++;
						mCacheSize +=info.cacheSize;
					} else {
						mDatas.add(info);
					}
					
					//推出进度信息
					mTask.updateProgress(info);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
        	 
        	 
        	/* Log.d(TAG, "cacheSize" + cacheSize);
        	 Log.d(TAG, "packageName" + packageName);
        	 Log.d(TAG, "-----------------");*/
         }
	 };

	 
	 //一键清理
	@Override
	public void onClick(View v) {
		if(v == mBtnClear) {
			
			//public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);
			
			if(mCacheSize<=0) {
				return;
			}
			
			try {
				Method method = mPm.getClass().getDeclaredMethod
						("freeStorageAndNotify", long.class,IPackageDataObserver.class);
				 method.invoke(mPm, Long.MAX_VALUE,new ClearCacheObserver());
				 
				 //再次刷新
				 startSscan();
				 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
