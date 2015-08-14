package com.it.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.it.mobilesafe.R;

public class SettingItemView extends RelativeLayout {

	private final int bkg_first = 0;
	private final int bkg_middle = 1;
	private final int bkg_end = 2;
	private boolean isToggleOn ;

	private ImageView mIvToggle;
	private TextView mTvTitle;
	
	private boolean isToggleEnable = true;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {

		super(context, attrs);

		// 将布局和view关联起来
		View view = View.inflate(context, R.layout.view_setting_item, this);

		mIvToggle = (ImageView) findViewById(R.id.view_iv_toggle);
		mTvTitle = (TextView) findViewById(R.id.view_tv_title);
		
		
		
		// 读取自定的属性
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SettingItemView);

		//自定义属性 1-- title
		String title = ta.getString(R.styleable.SettingItemView_title);
		//自定义属性2 -- background style
		int bkg = ta.getInt(R.styleable.SettingItemView_itbackground, 0);
		//自定义属性3 -- 是否隐藏Button
		isToggleEnable = ta.getBoolean(R.styleable.SettingItemView_toggleEnable, isToggleEnable);
		
		
		// 回收
		ta.recycle();
		mTvTitle.setText(title);

		// 设置背景

		switch (bkg) {
		case bkg_first:
			view.setBackgroundResource(R.drawable.setting_first_selector);
			break;
		case bkg_middle:
			view.setBackgroundResource(R.drawable.setting_middle_selector);
			break;
		case bkg_end:
			view.setBackgroundResource(R.drawable.setting_last_selector);
			break;
		default:
			view.setBackgroundResource(R.drawable.setting_first_selector);
			break;
		}
		// 设置默认值
		setToggleOn(isToggleOn);
		
		mIvToggle.setVisibility(isToggleEnable?View.VISIBLE:View.GONE);
	}

	// 设置开关打开或者关闭
	public void setToggleOn(boolean on) {
		 this.isToggleOn = on ;
		if (on) {
			mIvToggle.setImageResource(R.drawable.on);
		} else {
			mIvToggle.setImageResource(R.drawable.off);
		}
	}

	//如果打开就关闭,如果关闭就打开
	public void toggle() {
		setToggleOn(!isToggleOn);
	}

	//返回当前开关状态
	public boolean isToggleOn() {
		return isToggleOn;
	}

}
