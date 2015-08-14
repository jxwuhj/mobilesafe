package com.it.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.ContactInfo;
import com.it.mobilesafe.utils.ContactsUtils;

public class ContactsActivity2 extends Activity implements OnItemClickListener {
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
				//加载数据    listview的写法,加载 BaseAdapater  ,获取一个list的集合
				//mDatas = ContactsUtils.getAllPhone(ContactsActivity2.this);
				
				//加载数据,这里加载的是CursorAdapter 适配器, 获取一个cursor
				final Cursor cursor = ContactsUtils.getAllPhoneCursor(ContactsActivity2.this);
				SystemClock.sleep(1000);
				
				//子线程不可以做UI操作
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//隐藏progressBar
						mContactPb.setVisibility(View.GONE);
						
						//给listView设置数据
						mLv.setAdapter(new ContactsAdapater(ContactsActivity2.this,cursor));
					}
				});
				
			};
		}.start();
		
		
		//ListView设置点击事件
		mLv.setOnItemClickListener(this);
		
		
	}
	
	private class ContactsAdapater extends CursorAdapter {

		public ContactsAdapater(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			//创建View
			
			return View.inflate(ContactsActivity2.this,
					R.layout.activity_contact_selected, null);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			//绑定数据
			//view  : 显示的view
			ImageView mIcon = (ImageView) view.findViewById(R.id.item_contact_iv_icon);
			TextView mName = (TextView) view.findViewById(R.id.item_contact_tv_name);
			TextView mNumber = (TextView) view.findViewById(R.id.item_contact_tv_number);
			
			//设置数据  cursor  :数据
			
			ContactInfo info = ContactsUtils.getContactInfo(cursor);
			
			mName.setText(info.name);
			mNumber.setText(info.number);
			
			Bitmap bitmap = ContactsUtils.getContactIcon(ContactsActivity2.this, info.contactId);
			
			if(bitmap ==null) {
				mIcon.setImageResource(R.drawable.ic_contact);
			} else {
				mIcon.setImageBitmap(bitmap);
			}
			
		}


	

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
