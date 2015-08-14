package com.it.mobilesafe.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

public class SmsProvider {

	protected static final String TAG = "smsResore";


	/**
	 * 短信备份,数据与页面分离,针对 UI多变,而数据不变的情况下,将数据抽离出来,形成一个独特的业 务    engine
	 * 
	 * 	将数据抽离出来,而要与UI进行通讯,那么采用接口来实现
	 * 
	 * 	面向接口编程 -->标准
	 * @param context
	 */
	public static void smsBackup(final Context context,final OnSmsListener listener) {
		
		new AsyncTask<Void, Integer, Boolean>() {

			private final static int type_size = 0;
			private final static int type_progress = 1;

			@Override
			protected Boolean doInBackground(Void... params) {

				ContentResolver rs = context.getContentResolver();

				// xml 序列化
				XmlSerializer serializer = Xml.newSerializer();
				// 输出流 -- 写到哪里去
				FileOutputStream os = null;
				try {
					os = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"smsbackup.xml"));
					serializer.setOutput(os, "utf-8");
					// 文档开始
					serializer.startDocument("utf-8", true);
					// 开始根节点
					serializer.startTag(null, "list");

					Uri uri = Uri.parse("content://sms/");
					String[] projection = new String[] { "address", "date",
							"type", "body" };

					Cursor cursor = rs.query(uri, projection, null, null, null);

					if (cursor != null) {

						int count = cursor.getCount();

						int progress = 0;

						while (cursor.moveToNext()) {

							publishProgress(type_size, count);
							// 开始sms节点
							serializer.startTag(null, "sms");

							// 开始address节点
							serializer.startTag(null, "address");
							String address = cursor.getString(0);
							
							if(TextUtils.isEmpty(address)) {
								address = "";
							}
							
							serializer.text(address);
							serializer.endTag(null, "address");

							// date
							serializer.startTag(null, "date");
							long date = cursor.getLong(1);
							serializer.text(date + "");
							serializer.endTag(null, "date");

							// type
							serializer.startTag(null, "type");
							int type = cursor.getInt(2);
							serializer.text(type + "");
							serializer.endTag(null, "type");

							// body
							serializer.startTag(null, "body");
							String body = cursor.getString(3);
							if(TextUtils.isEmpty(body)) {
								body = "";
							}
							serializer.text(body + "");
							serializer.endTag(null, "body");

							// 结束sms节点
							serializer.endTag(null, "sms");

							// 每次sleep 0.3s
							SystemClock.sleep(300);

							progress++;

							/*
							 * dialog.setProgress(progress);
							 * mTvProgress.setText("进度"+progress);
							 */
							publishProgress(type_progress, progress);
						}

						cursor.close();
					}
					// 结束根节点
					serializer.endTag(null, "list");
					// 文档结束
					serializer.endDocument();

					return true;

				} catch (Exception e) {
					e.printStackTrace();

					return false;

				} finally {
					try {
						if (os != null) {
							os.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
						os = null;
					}
				}
			}

			protected void onProgressUpdate(Integer[] values) {
				if (values[0] == type_size) {
//					mTvTotalCount.setText("总数 :" + values[1]);
					
					//谨防调用者传空指针
					if(listener != null) {
						listener.onMax(values[1]);
					}
					
				} else {
//					mTvProgress.setText("进度" + values[1]);
					
					if(listener != null) {
						listener.onProgress(values[1]);
					}
				}
			};

			protected void onPostExecute(Boolean result) {
				if (result) {
					if(listener != null) {
						listener.onSuccess();
					}
//					mTvTotalCount.setText("成功");
				} else {
//					mTvTotalCount.setText("失败");
					if(listener != null) {
						listener.onFailed();
					}
				}
			};

		}.execute();

	}
	
	/**
	 * 供调用人员去调用,实现的接口
	 * 
	 * 接口中方法默认形式为:public abstract static 
	 * @author Ethan
	 *
	 */
	public interface OnSmsListener {
		
		void onMax(int max);
		
		void onProgress(int progress);
		
		void onSuccess();
		
		void onFailed();
	}

	
	
	/**
	 * 数据还原 --  Xml反序列化 -- 将数据保存到数据库
	 * @param context
	 */
	public static void smsResore(final Context context,final OnSmsListener listener) {
		
		final int type_size = 0;
		final int type_pogress = 1;
		
		new AsyncTask<Void, Integer, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				
				List<SmsInfo> list = null;
				
				SmsInfo info = null ;
				
				XmlPullParser parser = Xml.newPullParser();
				
				FileInputStream in = null;
				try {
					  in = new FileInputStream(new File(Environment.getExternalStorageDirectory(),"smsbackup.xml"));
					//os = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"smsbackup.xml"));
					//设置input流
					parser.setInput(in,"utf-8");
					
					System.out.println(in);
					
					int type = parser.getEventType();
					
					do {
						
						String tagName = parser.getName();
						
						switch (type) {
						case XmlPullParser.START_DOCUMENT:
							list = new ArrayList<SmsInfo>();
							break;
						case XmlPullParser.START_TAG:
							if("sms".equals(tagName)) {
								
								info = new SmsInfo();
								
							} else if("address".equals(tagName)) {
								// getName(), nextText()
								info.address = parser.nextText();
							} else if("date".equals(tagName)) {
								info.date = Long.valueOf(parser.nextText());
							} else if("type".equals(tagName)) {
								info.type = Integer.valueOf(parser.nextText());
							} else if("body".equals(tagName)) {
								info.body = parser.nextText();
							}
							
							break;
						case XmlPullParser.END_TAG:
							
							if("sms".equals(tagName)) {
								list.add(info);
							}
							break;
						default:
							break;
						}
					
						//改变事件
						type = parser.next();
					} while (type!=XmlPullParser.END_DOCUMENT);
					
				/*	if(listener != null) {
						listener.onMax(list.size());
					}*/
					publishProgress(type_size,list.size());
					
					//拿到了list数据 -- 插入数据到数据库
					
					for (int i = 0; i < list.size(); i++) {
						SmsInfo sms = list.get(i);
						
						/*if(listener != null) {
							listener.onProgress(i);
						}*/
						publishProgress(type_pogress,i);
						
						ContentResolver cr = context.getContentResolver();
						
						Uri url = Uri.parse("content://sms/");
						
						
						ContentValues values = new ContentValues();
						values.put("address", sms.address);
						values.put("date", sms.date);
						values.put("type", sms.type);
						values.put("body", sms.body);
						
						cr.insert(url, values);
					}
					
					/*if(listener != null) {
						listener.onSuccess();
					}*/
					return true;
					
				} catch (Exception e) {
					e.printStackTrace();
					/*if(listener != null) {
						listener.onFailed();
					}*/
					return false;
				} finally {
					if(in!=null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
							
							if(listener != null) {
								listener.onFailed();
							}
							
						}
						in = null;
					}
				}
				
				
			}
			
			protected void onProgressUpdate(Integer[] values) {
				
				if(values[0] == type_size) {
					listener.onMax(values[1]);
				} else if(values[0]== type_pogress) {
					listener.onProgress(values[1]);
				}
				
			};
			
			protected void onPostExecute(Boolean result) {
				if(result) {
					listener.onSuccess();
				} else {
					listener.onFailed();
				}
			};
			
			}.execute();
		
		
	}
	
	
	private static class SmsInfo {
		
		String address;
		long date;
		int type;
		String body;
	}
}

