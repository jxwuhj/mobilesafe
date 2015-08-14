package com.it.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.BlackInfo;
import com.it.mobilesafe.db.BlackDao;

public class CallSmsActivity extends Activity implements OnItemClickListener  {
	
	private static final int REQUEST_ADD = 998;
	private static final int REQUEST_UPDATE = 666;
	private static final String TAG = "CallSmsActivity";
	
	private static final int querySize = 12;
	
	
	private ImageView mIvAdd;
	private ListView mLvShowData;
	private LinearLayout mllLoading;
	private ImageView mIvEmpty;
	
	private List<BlackInfo> mDatas;
	private CallSmsAdapter csAdapter;
	private BlackDao dao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsms);
		//
		mIvAdd = (ImageView)findViewById(R.id.css_iv_add);
		
		mLvShowData = (ListView) findViewById(R.id.cs_lv_showdata);
		
		mIvEmpty =(ImageView) findViewById(R.id.cs_iv_empty);
		
		mllLoading = (LinearLayout) findViewById(R.id.cs_ll);
		mllLoading.setVisibility(View.VISIBLE);
		
		dao = new BlackDao(this);
		
		//获取到查询的数据  --  开线程加载数据
		new Thread(){
			public void run() {
				
				SystemClock.sleep(1000);
				
				//接口访问 -- >net  
				mDatas = dao.getPartinfo(querySize, 0);
				
				
				//初始化List
				//mDatas = dao.getAllInfo();
				
				//在主线程中设置Adapter
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						mllLoading.setVisibility(View.GONE);
						//给listView设置数据
						
						csAdapter = new CallSmsAdapter();
						mLvShowData.setAdapter(csAdapter);
						
						//设置空的view  当adapter 为null
						mLvShowData.setEmptyView(mIvEmpty);
					}
				});
			};
		}.start();
		
		
		
		mIvAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CallSmsActivity.this,BlacklistActivity.class);
				startActivityForResult(intent, REQUEST_ADD);
			}
		});
		
		
		//用来监听ListView滑动
		mLvShowData.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//滑动状态时的回调
				//SROll_state_idle  闲置
				//scroll-state_touch_scroll 触摸滚动状态
				//scroll_state_fling : 惯性滑动状态
				Log.d(TAG, "idle"+OnScrollListener.SCROLL_STATE_IDLE);
				Log.d(TAG, "fling"+OnScrollListener.SCROLL_STATE_FLING);
				Log.d(TAG, "scroll"+OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
				
				//空闲要看到最后一个
				//最后一个item
				int lastVisiblePosition = mLvShowData.getLastVisiblePosition();
				int idle = OnScrollListener.SCROLL_STATE_IDLE;
				if((scrollState == idle) &&(mDatas.size()-1 == lastVisiblePosition)) {
					startQuery();
				}
				
			}

			
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		mLvShowData.setOnItemClickListener(this);
	}
	
	
	class ViewHolder {
		TextView tvNumber;
		ImageView ivDelete;
		TextView tvType;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_ADD) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				String number = data.getStringExtra(BlacklistActivity.NUMBER);
				int type = data.getIntExtra(BlacklistActivity.TYPE, -1);
				//把数据添加到mDatas
				BlackInfo info = new BlackInfo();
				info.number = number;
				info.type = type;
				
				mDatas.add(info);
				
				//更新Adapter
				if(csAdapter!=null)
					csAdapter.notifyDataSetChanged();
				
				
				break;

			default:
				break;
			}
		} else if(requestCode == REQUEST_UPDATE) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				
				
				int position = data.getIntExtra(BlacklistActivity.POSITION, -1);
				int type = data.getIntExtra(BlacklistActivity.TYPE, -1);
				
				
				mDatas.get(position).type = type;
				Log.d(TAG, "1111type :" +type);
				csAdapter.notifyDataSetChanged();
				
				break;

			default:
				break;
			}
			
		}
	}
	
	private void startQuery() {
		//显示 加载
		mllLoading.setVisibility(View.VISIBLE);
		
		//加载数据
		new Thread(){
			public void run() {
			
				SystemClock.sleep(1000);
				int indexOffset = mDatas.size();
				List<BlackInfo> list =  dao.getPartinfo(querySize, indexOffset);
				
				if(list.size()==0) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							mllLoading.setVisibility(View.GONE);
							Toast.makeText(CallSmsActivity.this,
									"没有更多的数据了	", Toast.LENGTH_SHORT).show();
							
						}
					});
					return;
				}
				
				//添加当前的list中
				mDatas.addAll(list);
				//UI更新
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mllLoading.setVisibility(View.GONE);
						csAdapter.notifyDataSetChanged();
					}
				});
				
				
		};
		}.start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//跳转到更新界面
		Intent intent = new Intent(this,BlacklistActivity.class);
		//et,radiobutton需要数据
		BlackInfo info = mDatas.get(position);
		
		//跳转到更新界面
		intent.setAction(BlacklistActivity.ACTION_UPDATE);
		intent.putExtra(BlacklistActivity.NUMBER, info.number);
		intent.putExtra(BlacklistActivity.TYPE, info.type);
		intent.putExtra(BlacklistActivity.POSITION, position);
		Log.d(TAG, "onItemClick  **position   = "+position);
		
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivityForResult(intent, REQUEST_UPDATE);
	}

	private class CallSmsAdapter extends BaseAdapter {

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
			
			/*TextView tv = new TextView(getApplicationContext());
			
			BlackInfo info = mDatas.get(position);
			
			if(info.type == BlackInfo.TYPE_SMS) {
				
				tv.setText(info.number+" ***  短信!");
				
			} else if(info.type == BlackInfo.TYPE_CALL) {
				
				tv.setText(info.number+" ***  电话!");
				
			} else if(info.type == BlackInfo.TYPE_ALL) {
				
				tv.setText(info.number+" ***  短信+电话!");
			}
			return tv;
			*/
			ViewHolder holder = null;
			
			if(convertView == null) {
				
				//没有复用,就加载布局
				convertView = View.inflate(CallSmsActivity.this, R.layout.item_callsms, null);
				//初始化holder
				holder = new ViewHolder();
				//绑定holder,打标记
				
				//初始化holder中的view
				holder.tvNumber = (TextView) convertView.findViewById(R.id.item_cs_number);
				holder.tvType = (TextView) convertView.findViewById(R.id.item_cs_type);
				holder.ivDelete = (ImageView) convertView.findViewById(R.id.item_cs_ivDelete);
				convertView.setTag(holder);
				
				
			} 
				
				//复用
				holder = (ViewHolder) convertView.getTag();
				final BlackInfo info = mDatas.get(position);
				
				holder.tvNumber.setText(info.number);
				switch (info.type) {
				case BlackInfo.TYPE_CALL:
					holder.tvType.setText("电话拦截");
					break;
				case BlackInfo.TYPE_SMS:
					holder.tvType.setText("短信拦截");
					break;
				case BlackInfo.TYPE_ALL:
					holder.tvType.setText("电话+短信 拦截");
					break;
					
				default:
					break;
				}
			//设置删除点击
				
				holder.ivDelete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//数据库删除
						boolean delete = dao.delete(info.number);
						if(delete) {
							//内存中List更新
							mDatas.remove(info);
							
							if(mDatas.size() == 0) {
								startQuery();
							}
							
							//更新UI
							csAdapter.notifyDataSetChanged();
							
							Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
							
						}
						
					}
				});
			
			return convertView;
		}
		
	}
	

}
