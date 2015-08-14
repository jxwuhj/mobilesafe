package com.it.mobilesafe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.it.mobilesafe.bean.ContactInfo;

public class ContactsUtils {
	public static List<ContactInfo> getAllPhone(Context context) {

		// 解析者
		ContentResolver resolver = context.getContentResolver();
		// 获取uri地址
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

		Cursor cursor = resolver.query(uri, projection, null, null, null);

		List<ContactInfo> list = new ArrayList<ContactInfo>();

		if (cursor != null) {
			while (cursor.moveToNext()) {
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				long contactId = cursor.getLong(2);

				ContactInfo info = new ContactInfo();

				info.name = name;
				info.number = number;
				info.contactId = contactId;

				list.add(info);

			}

			cursor.close();
		}

		return list;

	}

	public static Bitmap getContactIcon(Context context, long contactId) {

		// content://contact#101
		// content://contact/101 //可以找到101号uri

		ContentResolver cr = context.getContentResolver();
		Uri contactUri = Uri.withAppendedPath(
				ContactsContract.Contacts.CONTENT_URI, contactId + "");
		InputStream in = null;
		try {
			in = ContactsContract.Contacts.openContactPhotoInputStream(cr,
					contactUri);

			Bitmap bitmap = BitmapFactory.decodeStream(in);
			return bitmap;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}

	}

	public static Cursor getAllPhoneCursor(Context context) {

		// 解析者
		ContentResolver resolver = context.getContentResolver();
		// 获取uri地址
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

		return resolver.query(uri, projection, null, null, null);

	}
	
	public static ContactInfo getContactInfo (Cursor cursor) {
		
		String name = cursor.getString(1);
		String number = cursor.getString(2);
		long contactId = cursor.getLong(3);
		
		ContactInfo info = new ContactInfo();
		
		info.name = name;
		info.number = number;
		info.contactId = contactId;
		
		return info;
		
	}

}
