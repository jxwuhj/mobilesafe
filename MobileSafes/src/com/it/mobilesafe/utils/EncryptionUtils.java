package com.it.mobilesafe.utils;

public class EncryptionUtils {

	/**
	 * a^b = c;  c^b =a;
	 * 	a^b^b =a;
	 * @param text
	 * @param key
	 * @return
	 */
	public static String encode(String text,int key) {
		byte[] bys = text.getBytes();
		
		for (int i = 0; i < bys.length; i++) {
			bys[i]^= key;
		}
		return new String(bys);
	}
	
	public static String dncode(String text,int key) {
		byte[] bys = text.getBytes();
		
		for (int i = 0; i < bys.length; i++) {
			bys[i]^= key;
		}
		return new String(bys);
	}
}
