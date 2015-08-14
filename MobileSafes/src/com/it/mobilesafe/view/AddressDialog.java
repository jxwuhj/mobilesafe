package com.it.mobilesafe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.it.mobilesafe.R;

public class AddressDialog extends Dialog {
	
	private ListView mLv;
	private ListAdapter mAdapter;
	private OnItemClickListener mListener;

	public AddressDialog(Context context) {
		super(context,R.style.AddressDialogTheme);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.address_dialog);
		
		mLv = (ListView) findViewById(R.id.ad_lv);
		
		setAdapter(mAdapter);
		setOnItemClickListener(mListener);
		
		
		//改变样式-->window的样式
		Window window = getWindow();
		
		LayoutParams params = window.getAttributes();
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		window.setAttributes(params);
		
	}

	/**
	 *	设置Adapter 归属地样式
	 * @param adapter
	 */
	public void setAdapter(ListAdapter adapter) {
		this.mAdapter = adapter;
		if(mLv!=null) {
			mLv.setAdapter(adapter);
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
		if(mLv!=null) {
			mLv.setOnItemClickListener(listener);
		}
	}
	
	


}
