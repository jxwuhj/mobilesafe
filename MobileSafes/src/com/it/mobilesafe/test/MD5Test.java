package com.it.mobilesafe.test;

import com.it.mobilesafe.utils.MD5Utils;

import android.test.AndroidTestCase;
import android.util.Log;

public class MD5Test extends AndroidTestCase {

	private static final String TAG = "MD5Utils";

	public void testMD5() {
		String text = "123456";
		Log.d(TAG,  MD5Utils.encode(text));
		
	}
}
