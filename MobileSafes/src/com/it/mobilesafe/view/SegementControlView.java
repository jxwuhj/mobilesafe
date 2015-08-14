package com.it.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.it.mobilesafe.R;

public class SegementControlView extends LinearLayout implements
		OnClickListener {

	private TextView mTvLeft;
	private TextView mTvRight;
	private boolean isLeftSelected = true;
	
	private OnSelecedListener mListener;

	public SegementControlView(Context context) {
		this(context, null);
	}

	public SegementControlView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View.inflate(context, R.layout.view_segement, this);
		mTvLeft = (TextView) findViewById(R.id.view_tv_left);
		mTvRight = (TextView) findViewById(R.id.view_tv_right);

		// 默认左侧选中
		mTvLeft.setSelected(true);

		// 设置点击事件
		mTvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v == mTvLeft) {
			// 如果左侧是不选中,点击选中左侧

			if (!isLeftSelected) {

				mTvRight.setSelected(false);
				mTvLeft.setSelected(true);

				isLeftSelected = true;
				
				if(mListener != null) {
					mListener.onSelected(true);
				}
			}

		} else if (v == mTvRight) {
			// 如果右侧是不选中的情况,点击选中右侧
			if (isLeftSelected) {

				mTvRight.setSelected(true);
				mTvLeft.setSelected(false);

				isLeftSelected = false;
				
				if(mListener != null) {
					mListener.onSelected(false);
				}
			}
		}
	}
	
	
	/**
	 * 设置监听器
	 * 
	 * @param listener
	 */

	public void setOnSelecedListener(OnSelecedListener listener) {
		this.mListener = listener;
	}
	
	/**
	 * 定义接口
	 * 
	 * @author Ethan
	 *
	 */
	public interface OnSelecedListener {
		//选中,没选中
		void onSelected(boolean isLeftSelected);
	}

}
