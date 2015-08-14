package com.it.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.it.mobilesafe.R;

public class ProgressDesView extends LinearLayout {
	
	private TextView mTvTitle;
	private TextView mTvLeft;
	private TextView mTvRight;
	private ProgressBar mPb;
	
	public ProgressDesView(Context context) {
		this(context,null);
	}

	public ProgressDesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//类和xml绑定
		View.inflate(context, R.layout.view_progress_des, this);
		
		//初始化view
		mTvTitle = (TextView) findViewById(R.id.progress_title);
		mTvLeft = (TextView) findViewById(R.id.progress_left);
		mTvRight = (TextView) findViewById(R.id.progress_right);
		mPb = (ProgressBar) findViewById(R.id.progress_pb);
		
	}
	
	//对外提供方法来设置view
	
	/**
	 * 
	 * @param title
	 */
	public void setDesTitle(String title) {
		mTvTitle.setText(title);
	}
	
	/**
	 * 
	 * @param left
	 */
	public void setDesLeft(String left) {
		mTvLeft.setText(left);
	}
	
	/**
	 * 
	 * @param right
	 */
	public void setDesRight(String right) {
		mTvRight.setText(right);
	}
	
	/**
	 * 
	 * @param progress
	 */
	public void setDesProgress(int progress) {
		mPb.setProgress(progress);
	}
	
	

	

}
