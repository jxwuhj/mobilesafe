package com.it.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	public static String stream2String(InputStream in) {
		
		int len = 0;
		byte [] bys = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while((len = in.read(bys)) >0) {
				baos.write(bys, 0, len);
			}
			return baos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
