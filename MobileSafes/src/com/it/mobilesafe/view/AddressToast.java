package com.it.mobilesafe.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.it.mobilesafe.R;

public class AddressToast implements OnTouchListener {

	private static final String TAG = "AddressToast";
	private Context mContext;
	private View mView;
	private WindowManager mWM;
	private WindowManager.LayoutParams params;
	private float startX;
	private float startY;

	public AddressToast(Context context) {
		this.mContext = context;

		// 窗体管理者
		mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

		 |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		params.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		// params.setTitle("Toast");
	}

	public void show(String address) {
		/*
		 * mView = new TextView(mContext); ((TextView)mView).setText(address);
		 */

		mView = View.inflate(mContext, R.layout.address_toast, null);
		TextView mTvAddress = (TextView) mView.findViewById(R.id.at_ev_address);
		mTvAddress.setText(address);

		// 设置按钮的监听事件
		mView.setOnTouchListener(this);

		mWM.addView(mView, params);
	}

	public void hide() {

		if (mView != null) {
			// note: checking parent() just to make sure the view has
			// been added... i have seen cases where we get here when
			// the view isn't yet added, so let's try not to crash.
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}

			mView = null;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		// 按下
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "按下");
			startX = event.getRawX();
			startY = event.getRawY();
			
			break;

		// 移动
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "移动");
			
			float newX = event.getRawX();
			float newY = event.getRawY();
			
			float diffX = newX - startX;
			float diffY = newY - startY;
			
			params.x += (int)(diffX + 0.5f);
			params.y += (int)(diffY + 0.5f);
			
			mWM.updateViewLayout(mView, params);
			
			startX = newX;
			startY = newY;
			break;
		// 抬起来
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "抬起来");

			break;

		default:
			break;
		}

		return true;
	}
}
