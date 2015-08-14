package com.it.mobilesafe.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {
	
	/**
	 *  zip压缩流 
	 *  
	 * @param in
	 * @param out
	 */
	public static void zip(InputStream in, OutputStream out) {
		GZIPOutputStream gos = null;
		
		try {
			gos = new GZIPOutputStream(out);
			
			byte[]bys = new byte[1024];
			int len = 0;
			while((len = in.read(bys))>0) {
				gos.write(bys, 0, len);
			}
		
		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			release(gos);
			release(in);
		}
	}
	
	/**
	 * zip 压缩
	 * @param srcFile
	 *            要压缩的源文件
	 * @param zipFile
	 *            :压缩后文件存放的地址
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void zip(File srcFile, File zipFile)  {
		GZIPOutputStream gos = null;
		FileInputStream fis = null;
		
		try {
			
			gos = new GZIPOutputStream(new FileOutputStream(zipFile));
		
			fis = new FileInputStream(srcFile);
		
			byte[]bys = new byte[1024];
			int len = 0;
			while((len = fis.read(bys))>0) {
				gos.write(bys, 0, len);
			}
		
		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			release(gos);
			release(fis);
		}
	}
	
	/**
	 * 
	 * @param zipFile
	 * @param targetFile
	 */
	public static void unzip(File zipFile,File targetFile) {
		GZIPInputStream gis = null;
		FileOutputStream fos = null;
		try {
			gis = new GZIPInputStream(new FileInputStream(zipFile));
			fos = new FileOutputStream(targetFile);
			
			byte[]bys = new byte[1024];
			int len = 0;
			while((len = gis.read(bys))>0) {
				fos.write(bys, 0, len);
			}
			
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			release(fos);
			release(gis);
		}
	}
	
	/**
	 * 解压
	 * @param in
	 * @param out
	 */
	public static void unzip(InputStream in,OutputStream out) {
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(in);
			
			byte[]bys = new byte[1024];
			int len = 0;
			while((len = gis.read(bys))>0) {
				out.write(bys, 0, len);
			}
			
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			release(out);
			release(gis);
		}
	}
	
	
	
	private static void release(Closeable io) {
		if(io!=null) {
			
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
				io = null;
			}
		}
	}
}
