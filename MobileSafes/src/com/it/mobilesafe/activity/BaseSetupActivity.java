package com.it.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.it.mobilesafe.R;

public abstract class BaseSetupActivity extends Activity {

	protected static final String TAG = "BaseSetupActivity";
	// GestureDetector : 手语检测者
	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建实例
		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// e1 : MotionEvent, 事件的数据载体,down -- 按下
				// e2 : move
				// velocityX : x方向 --- 单位是速率

				float x1 = e1.getRawX();
				float x2 = e2.getRawX();

				float y1 = e1.getRawY();
				float y2 = e2.getRawY();

				if (Math.abs(y1 - y2) > Math.abs(x1 - x2)) {
					// y轴运动
					return false;
				}

				if (x1 > x2 + 50) {
					// 说明是从右往左滑动,进入下一页面 +50 是由于灵敏度的问题,没有达到一定程序,那么则不做操作
					/*Toast.makeText(BaseSetupActivity.this, "从右往左滑滑动,进入下一个页面", 0)
							.show();*/
					doNext();
					return true;
				}

				if (x1 < x2 - 50) {
					// 说明从左往右滑动,进入上一个页面
					/*Toast.makeText(BaseSetupActivity.this, "左往右滑动,进入下一个页面", 0)
							.show();*/
					doPre();
					return true;
				}

				// onFling()方法默认返回一个false值
				return false;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mDetector.onTouchEvent(event);
	}

	
	
	// 让继承的子类去实现, 如果返回true,就不继续向下执行
	protected abstract boolean clickNext();

	public void performNext(View v) {
		doNext();
	}
	
	public void doNext() {
		
		if(clickNext()) {
			return;
		}
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		finish();
	}
	
	
	
	
	protected abstract boolean clickPre();
	
	public void performPre(View v) {
		doPre();
	}

	public void doPre() {
		if(clickPre()) {
			return;
		}
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
	}
	
}
