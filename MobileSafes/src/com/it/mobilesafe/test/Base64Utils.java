package com.it.mobilesafe.test;

import android.util.Base64;

public class Base64Utils {
	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String encode(String text) {
		byte[] encode = Base64.encode(text.getBytes(), Base64.DEFAULT);
		return new String(encode);
	}
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String decode(String text) {
		byte[] encode = Base64.decode(text.getBytes(), Base64.DEFAULT);
		return new String(encode);
	}
}
