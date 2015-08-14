package com.it.mobilesafe.activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.TrafficInfo;

public class TrafficActivity extends Activity {
//	private static final String TAG = "TrafficActivity";
	private PackageManager mPm;
	
	private List<TrafficInfo> mDatas;
	private ListView mListView;
	private LinearLayout mLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_traffic);
		
		//读取proc/uid_stat/用户id/tcp_rcv 接收的
		//读取proc/uid_stat/用户id/tcp_snd 发送的
		
		mPm = getPackageManager();
		mListView = (ListView) findViewById(R.id.at_listview);
		mLoading = (LinearLayout) findViewById(R.id.pl_loading);
		
		mDatas = new ArrayList<TrafficInfo>();
		
		new AsyncTask<Void, Void, Void>(){
			
			protected void onPreExecute() {
				mLoading.setVisibility(View.VISIBLE);
			};

			@Override
			protected Void doInBackground(Void... params) {
				
				List<PackageInfo> packages = mPm.getInstalledPackages(0);
				
				for (PackageInfo pack : packages) {
					
					TrafficInfo info = new TrafficInfo();
					
					int uid = pack.applicationInfo.uid;
					String packageName = pack.packageName;
					String name = pack.applicationInfo.loadLabel(mPm).toString();
					Drawable icon = pack.applicationInfo.loadIcon(mPm);
					long rcv = getRcv(uid);
					long snd = getSnd(uid);
					
					info.uid = uid;
					info.packageName = packageName;
					info.name = name;
					info.rcv = rcv;
					info.snd = snd;
					info.icon = icon;
					
					if(info.snd>0 && info.rcv>0){
						mDatas.add(info);
					}
					
				}
				
				return null;
			} 
			
			protected void onPostExecute(Void result) {
				
				mLoading.setVisibility(View.GONE);
				//设置Adapter
				mListView.setAdapter(new TrafficAdapter());
			};
			
		}.execute();
		
		
		
		
		
	}
	
	private class TrafficAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(mDatas != null) {
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
			
			ViewHolder holder = null;
			
			if(convertView == null) {
				
				//复用
				holder = new ViewHolder();
				
				convertView = View.inflate(TrafficActivity.this, R.layout.item_traffic_info, null);
				//mark
				convertView.setTag(holder);
				
				holder.mIvIcon = (ImageView) convertView.findViewById(R.id.item_traffic_iv_icon);
				holder.mTvName = (TextView) convertView.findViewById(R.id.item_traffic_tv_name);
				holder.mTvRcv = (TextView) convertView.findViewById(R.id.item_traffic_tv_rcv);
				holder.mTvSnd = (TextView) convertView.findViewById(R.id.item_traffic_tv_snd);
				
			}  else {
				
				holder = (ViewHolder) convertView.getTag();
			}
			
				TrafficInfo info = mDatas.get(position);
				holder.mIvIcon.setImageDrawable(info.icon);
				holder.mTvName.setText(info.name);
				
				if(info.snd>0 && info.rcv>0) {
					
					holder.mTvRcv.setText("接收"+Formatter.formatFileSize(TrafficActivity.this, info.rcv));
					holder.mTvSnd.setText("发送"+Formatter.formatFileSize(TrafficActivity.this, info.snd));
				}
				
				
			
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		
		ImageView mIvIcon;
		TextView mTvName;
		TextView mTvRcv;
		TextView mTvSnd;
		
	}
	
	/**
	 *	根据uid获取到接收的流量信息
	 * 
	 * @param uid
	 * @return
	 */
	public long getRcv(int uid) {
		
		BufferedReader br = null ;
		
		String fileName = "/proc/uid_stat/"+uid+"/tcp_rcv";
		
		try {
			 br = new BufferedReader(new FileReader(fileName));
			 
			 String line = br.readLine().toString();
			 
			 return Long.valueOf(line);
			 
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				br = null;
			}
		}
		
		return 0;
	}
	
	/**
	 * 根据uid获取到发送的数据流量
	 * 
	 * @param uid
	 * @return
	 */
	public long getSnd(int uid) {
		
		BufferedReader br = null ;
		
		String fileName = "/proc/uid_stat/"+uid+"/tcp_snd";
		
		try {
			 br = new BufferedReader(new FileReader(fileName));
			 
			 String line = br.readLine().toString();
			 
			 return Long.valueOf(line);
			 
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				br = null;
			}
		}
		
		return 0;
	}
	
}
