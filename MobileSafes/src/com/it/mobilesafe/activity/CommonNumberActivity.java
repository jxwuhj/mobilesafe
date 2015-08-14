package com.it.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.it.mobilesafe.R;
import com.it.mobilesafe.db.CommonNumberDao;

public class CommonNumberActivity extends Activity {
	
	private ExpandableListView mListView;
	private int mCurrentPosition = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_common_number);
		
		mListView = (ExpandableListView) findViewById(R.id.cn_listveiw);
		
		mListView.setAdapter(new CommonNmberAdapter());
		
		mListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				
				if(mCurrentPosition == -1) {
					
					//一个都没有选中
					mListView.expandGroup(groupPosition);
					mCurrentPosition = groupPosition;
					
					mListView.setSelectedGroup(groupPosition);
					
				} else {
					//选中 后,再次点击自己
					if(mCurrentPosition == groupPosition) {
						//关闭当前
						mListView.collapseGroup(groupPosition);
						//标记再次置为-1
						mCurrentPosition = -1;
					} else {
						
						//关闭之前
						mListView.collapseGroup(mCurrentPosition);
						
						//打开当前
						mListView.expandGroup(groupPosition);
						
						//设置当前 
						mListView.setSelectedGroup(groupPosition);
						
						//标记
						mCurrentPosition = groupPosition;
					}
					
				}
				
				return true;
			}
		});
		
		
		mListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				String[]texts = CommonNumberDao.getChildText(getApplicationContext(), 
						groupPosition, childPosition);
						
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:"+texts[1]));
				startActivity(intent);
				
				
				return true;
			}
		});
		
	}
	
	private class CommonNmberAdapter extends BaseExpandableListAdapter {

		//select count(*) from tableName;  // 这里的*如果有自增长类型,或者是一个常量,那么查询速度将加快
		//分组的个数  几个item(一个item下又有几个item)
		@Override
		public int getGroupCount() {
			return CommonNumberDao.findGroupCount(getApplicationContext());
		}

		//select count(1)
		//第groupPosition个组有几个孩子
		@Override
		public int getChildrenCount(int groupPosition) {
			return CommonNumberDao.findChildCount(getApplicationContext(), groupPosition);
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		//第几个的view
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
		/*	TextView tv = new TextView(getApplicationContext());
			tv.setText(""+groupPosition);
			
			return tv;*/
			//TextView 复用
			TextView tv = null;
			
			if(convertView == null) {
				convertView = new TextView(getApplicationContext());
				tv = (TextView) convertView;
			} else {
				tv = (TextView) convertView;
			}
			
			//给textView设置数据
			tv.setText(CommonNumberDao.getGroupText(
					getApplicationContext(), groupPosition));
			tv.setPadding(10, 10, 10, 10);
			tv.setTextSize(20f);
			tv.setBackgroundColor(Color.parseColor("#33000000"));
			tv.setTextColor(Color.BLACK);
			return convertView;
		}

		//select name from classlist where idx=1;   搜索text 根据groupPosition
		//第几个孩子的view
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			//TextView 复用
			TextView tv = null;
			
			if(convertView == null) {
				convertView = new TextView(getApplicationContext());
				tv = (TextView) convertView;
			} else {
				tv = (TextView) convertView;
			}
			
			
			tv.setPadding(10, 10, 10, 10);
			tv.setTextSize(16f);
			tv.setBackgroundColor(Color.parseColor("#330000ff"));
			tv.setTextColor(Color.BLACK);
			
			//给textView设置数据
			String[] texts = CommonNumberDao.getChildText(
					getApplicationContext(), groupPosition,childPosition);
			tv.setText(texts[0]+"\n"+texts[1]);
			
			return convertView;
		}

		//孩子被选中
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
}
