package com.it.mobilesafe.utils;

import java.io.InputStream;
import java.security.MessageDigest;

public class MD5Utils {

//	private static final String TAG = "MD5Utils";

	public static String encode(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");

			byte[] buff = digest.digest(text.getBytes());

			StringBuilder sb = new StringBuilder();

			for (byte b : buff) {
				int a = b & 0xff;

				String hex = Integer.toHexString(a);

				if (hex.length() == 1) {
					hex = 0 + hex;
				}

				sb.append(hex);
			}
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	
	public static String encode(InputStream in) {
		MessageDigest digester;
		try {
			digester = MessageDigest.getInstance("MD5");
			byte[] bytes = new byte[8192];// 8k左右的流数据
			int byteCount;
			while ((byteCount = in.read(bytes)) > 0) {
				digester.update(bytes, 0, byteCount);
			}
			byte[] digest = digester.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				int a = b & 0xff;

				String hex = Integer.toHexString(a);
				
				if (hex.length() == 1) {
					hex = 0 + hex;
				}
				sb.append(hex);
			}
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
