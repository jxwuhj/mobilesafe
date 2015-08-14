package com.it.mobilesafe.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusTextView extends TextView {

	// 是用在引用 (new)
	public FocusTextView(Context context) {
		this(context,null);
	}

	// 用在布局里面的
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		/* android:singleLine="true"
	                android:ellipsize="marquee"
	                android:focusableInTouchMode="true"
	                android:marqueeRepeatLimit="marquee_forever"
	                android:focusable="true"*/
		/* public enum TruncateAt {
		        START,
		        MIDDLE,
		        END,
		        MARQUEE,
		        *//**
		         * @hide
		         *//*
		        END_SMALL
		    }*/
		
		setSingleLine();
		setEllipsize(TruncateAt.MARQUEE);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setMarqueeRepeatLimit(-1);
		
	}
	
	@Override
	public boolean isFocused() {
		return true;
	}
	
	//EditText 有抢占焦点的功能,为了不停止,那么重新onFocusChanged()方法
	
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		
		if(focused) {
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}
	
	//为了有对话框叠加就停止滚动,那么重写onWindowFocusChanged()方法
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(hasWindowFocus) {
			super.onWindowFocusChanged(hasWindowFocus);
		}
	}

}
