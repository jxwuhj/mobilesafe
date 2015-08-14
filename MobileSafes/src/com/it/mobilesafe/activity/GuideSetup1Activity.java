package com.it.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.it.mobilesafe.R;

public class GuideSetup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_guide_setup1);
	}


	@Override
	protected boolean clickNext() {
		
		Intent intent = new Intent(this, GuideSetup2Activity.class);
		startActivity(intent);
		
		return false;
		
	}

	@Override
	protected boolean clickPre() {
		return true;
	}

/*	public void performNext(View v) {

		Intent intent = new Intent(this, GuideSetup2Activity.class);
		startActivity(intent);

		// 设置动画
		// 指尖动画,考虑谁进谁出  
		// enterAnim : 进入
		//exitAnim : 出去
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
	}
*/
}
