package com.it.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.ContactInfo;
import com.it.mobilesafe.utils.ContactsUtils;

public class ContactsActivity extends Activity implements OnItemClickListener {
	public static final String KEY_NUMBER = "number";
	private ListView mLv;
	private List<ContactInfo> mDatas;
	private ProgressBar mContactPb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_contact_selected);
		
		//初始化view
		mLv = (ListView) findViewById(R.id.contacts_lv);
		
		mContactPb = (ProgressBar) findViewById(R.id.contacts_pb);
		
		//子线程中执行
		new Thread(){
			public void run() {
				//加载数据
				mDatas = ContactsUtils.getAllPhone(ContactsActivity.this);
				SystemClock.sleep(1000);
				
				//子线程不可以做UI操作
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//隐藏progressBar
						mContactPb.setVisibility(View.GONE);
						
						//给listView设置数据
						mLv.setAdapter(new ContactsAdapater());
					}
				});
				
			};
		}.start();
		
		
		//ListView设置点击事件
		mLv.setOnItemClickListener(this);
		
		
	}
	
	private class ContactsAdapater extends BaseAdapter {

		@Override
		public int getCount() {
			if(mDatas!=null){
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
			
			/*TextView tv = new TextView(ContactsActivity.this);
			ContactInfo info = mDatas.get(position);
			
			tv.setText(info.name+"***"+info.number);*/
			
			/*View view = View.inflate(ContactsActivity.this, R.layout.item_contact, null);
			TextView name = (TextView) view.findViewById(R.id.item_contact_tv_name);
			TextView number = (TextView) view.findViewById(R.id.item_contact_tv_number);
			ImageView icon = (ImageView) view.findViewById(R.id.item_contact_iv_icon);*/
			
			
			
			/*View view;
			if(convertView == null) {
				view = View.inflate(ContactsActivity.this, R.layout.item_contact, null);
			} else {
				view  = convertView;
			}
			
			TextView name = (TextView) view.findViewById(R.id.item_contact_tv_name);
			TextView number = (TextView) view.findViewById(R.id.item_contact_tv_number);
			ImageView icon = (ImageView) view.findViewById(R.id.item_contact_iv_icon);
			
			
			ContactInfo info = mDatas.get(position);
			name.setText(info.name);
			number.setText(info.number);
			
			Bitmap bitmap = ContactsUtils.getContactIcon(ContactsActivity.this, info.contactId);
			if(bitmap != null) {
				icon.setImageBitmap(bitmap);
			} else {
				icon.setImageResource(R.id.item_contact_iv_icon);
			}
			*/
			//初始化ViewHolder  --- 一般而言,item有几个view就有几个view成员,这里有三个
			ViewHolder holder = null;
			
			if(convertView == null) {
				//加载convertView
				convertView = View.inflate(ContactsActivity.this, R.layout.item_contact, null);
				//创建对象
				holder = new ViewHolder();
				
				holder.name = (TextView) convertView.findViewById(R.id.item_contact_tv_name);
				holder.number = (TextView) convertView.findViewById(R.id.item_contact_tv_number);
				holder.icon = (ImageView) convertView.findViewById(R.id.item_contact_iv_icon);
				
				convertView.setTag(holder);
			
			} else {
				holder = (ViewHolder) convertView.getTag();
			} 
			
			ContactInfo info = mDatas.get(position);
			holder.name.setText(info.name);
			holder.number.setText(info.number);
			
			Bitmap bitmap = ContactsUtils.getContactIcon(ContactsActivity.this, info.contactId);
			if(bitmap != null) {
				holder.icon.setImageBitmap(bitmap);
			} else {
				holder.icon.setImageResource(R.id.item_contact_iv_icon);
			}
			
			
			
			return convertView;
		}
		
	}
	
	static class ViewHolder {
		TextView name;
		TextView number;
		ImageView icon;
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//被点击的item
		ContactInfo info = mDatas.get(position);
		//怎么把数据回显回去   --  回显示num
		Intent data = new Intent();
		data.putExtra(KEY_NUMBER, info.number);
		System.out.println("info.number" +info.number);
		setResult(Activity.RESULT_OK, data);
		
		finish();
	}
}
