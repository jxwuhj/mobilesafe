package com.it.mobilesafe.service;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.PreferenceUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class GpsService extends Service {

	protected static final String TAG = "GpsService";
	private LocationManager lm;
	private GpsLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new GpsLocationListener();
		lm.requestLocationUpdates("gps", 0, 0, listener);

	}

	private class GpsLocationListener implements LocationListener {

		private static final String TAG = "GpsLocationListener";

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {

			// 获取维度
			double latitude = location.getLatitude();

			// 获取经度
			double longitude = location.getLongitude();

			Log.d(TAG, "" + "维度" + latitude);
			Log.d(TAG, "" + "经度" + longitude);

				//访问网络获取地址
				//1.url   ; 2.method  3.请求参数  4.请求头   
				//请求行  :url method
				//请求头:key--value
				//请求参数的内容
				//请求参数 : method  -->get -->url
			
			loadLocation(latitude, longitude);
			
			//TODO :发短信
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
	}

	public void loadLocation(final double latitude, final double longitude) {
		
		// 接口地址：http://lbs.juhe.cn/api/getaddressbylngb
		// 支持格式：JSON/XML
		// 请求方式：GET
		// 请求示例：http://lbs.juhe.cn/api/getaddressbylngb?lngx=116.407431&lngy=39.914492
		// 请求参数：
		// 名称 类型 必填 说明
		// lngx String Y google地图经度 (如：119.9772857)
		// lngy String Y google地图纬度 (如：27.327578)
		// dtype String N 返回数据格式：json或xml,默认json
		
		String url = "http://lbs.juhe.cn/api/getaddressbylngb";
		
		RequestParams parms = new RequestParams();
		
		parms.addQueryStringParameter("lngx",""+longitude);
		parms.addQueryStringParameter("lngy",""+latitude);
		parms.addQueryStringParameter("dtype","Json");
		
		//头
//		parms.addHeader(name, value)
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException e, String msg) {
				e.printStackTrace();
				sendSms(longitude+"longitude"+latitude+"latitude");
				stopSelf();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String json = responseInfo.result;
				Log.d(TAG, json);
				
				//解析json
				try {
					String address = null;
					
					JSONObject root = new JSONObject(json);
					JSONObject rowObj = root.getJSONObject("row");
					JSONObject resultObj = rowObj.getJSONObject("result");
					address = resultObj.getString("formatted_address");
					
					sendSms(address+longitude+"longitude"+latitude+"latitude");
					stopSelf();
					
				} catch (Exception e) {
					e.printStackTrace();
					sendSms(longitude+"   longitude  "+latitude+"  latitude   ");
					stopSelf();
				}
			}

			
		

		
		});
	}
	
	private void sendSms(String msg) {
		SmsManager sm = SmsManager.getDefault();
		//adress  :获取安全号码
		String adress = PreferenceUtils.getString(this, Constants.SJFD_NUMBER);
		sm.sendTextMessage(adress, null, msg, null, null);
	}

}
