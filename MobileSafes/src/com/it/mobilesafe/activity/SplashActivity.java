package com.it.mobilesafe.activity;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.it.mobilesafe.R;
import com.it.mobilesafe.engine.ProcessProvider;
import com.it.mobilesafe.service.ProtectedService;
import com.it.mobilesafe.utils.Constants;
import com.it.mobilesafe.utils.GZipUtils;
import com.it.mobilesafe.utils.PackageUtil;
import com.it.mobilesafe.utils.PreferenceUtils;
import com.it.mobilesafe.utils.ServiceStateutUtils;
import com.it.mobilesafe.utils.StreamUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {

	private TextView splash_tv_version;
	private static final String TAG = "SplashActivity";
	public static final int JSONERROR = 0;
	public static final int CLIENTERROR = 1;
	public static final int STREAMERROR = 2;
	public static final int SHOW_UPDATE_DIALOG = 4;
	protected static final int REQUEST_CODE_INSTALL = 100;
	private String mDesc; // 版本更新的描述信息
	public String murl;// 版本下载的地址
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case JSONERROR:
				Toast.makeText(SplashActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				break;
			case CLIENTERROR:
				Toast.makeText(SplashActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				break;
			case STREAMERROR:
				Toast.makeText(SplashActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				break;
			case SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;

			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// 初始化view
		splash_tv_version = (TextView) findViewById(R.id.splash_tv_version);

		// 显示版本号
		splash_tv_version.setText("版本号 :" + PackageUtil.getVersionName(this));

		// 检测是否更新,默认wei更新
		boolean isUpdate = PreferenceUtils.getBoolean(this,
				Constants.AUTO_UPDATE, true);
		if (isUpdate) {
			// 需要检测更新
			System.out.println("需要检测更新");
			checkVersionUpdate();
		} else {
			System.out.println("不需要检测更新");
			camebackHome();
		}

		// 拷贝解压号码归属地数据库
		copyAddressDB();
		
		//拷贝常用号码
		copyCommonNumberDB();
		
		//拷贝病毒库db特征码
		copyVirusDB();
		
		//开启必要的服务
		if(!ServiceStateutUtils.isRunning(getApplicationContext(), ProtectedService.class)) {
			startService(new Intent(getApplicationContext(),ProtectedService.class));
		}
		
		
		boolean flags = PreferenceUtils.getBoolean(SplashActivity.this, Constants.SHORTCUT);
		if(!flags) {
			
			//创建快捷方式
			Intent intent = new Intent();
			
			 /*Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		        String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		        Parcelable bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);*/
			
			//指定名称,图标,行为
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "装B卫士");
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			intent.putExtra("Intent.EXTRA_SHORTCUT_ICON", bitmap);
			
			Intent clickIntent = new Intent(this,SplashActivity.class);
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);
			sendBroadcast(intent);
			
			PreferenceUtils.putBoolean(SplashActivity.this, Constants.SHORTCUT, true);
		}

	}

	private void copyVirusDB() {
		
		File file = new File(getFilesDir(),"antivirus.db");
		InputStream in = null;
		FileOutputStream fos = null ;
		if(!file.exists()) {
			//拷贝
			Log.d(TAG, " antivirus 数据不存在,需要拷贝");
			AssetManager assets = getAssets();
			try {
				 in = assets.open("antivirus.db");
				
				 fos = new FileOutputStream(file);
				
				int len = 0;
				byte[] bys = new byte[1024];
				while((len = in.read(bys)) > 0) {
					fos.write(bys, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				release(fos);
				release(in);
			}
		} else {
			Log.d(TAG, " antivirus 数据存在,不需要拷贝");
		}
	}
	
	

	private void copyCommonNumberDB() {
		File file = new File(getFilesDir(),"commonnum.db");
		InputStream in = null;
		FileOutputStream fos = null ;
		if(!file.exists()) {
			//拷贝
			Log.d(TAG, " commonnum 数据不存在,需要拷贝");
			AssetManager assets = getAssets();
			try {
				 in = assets.open("commonnum.db");
				
				 fos = new FileOutputStream(file);
				
				int len = 0;
				byte[] bys = new byte[1024];
				while((len = in.read(bys)) > 0) {
					fos.write(bys, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				release(fos);
				release(in);
			}
		} else {
			Log.d(TAG, " commonnum 数据存在,不需要拷贝");
		}
	}

	private void copyAddressDB() {
		
		new Thread() {
			public void run() {
				//将资产文件 转移到file 应用下
				//之前先做判断如果存在,则跳过
				File file = new File(getFilesDir(),"address.db");
				if(!file.exists()) {
					source2Db();
				}
				
			}

			
		}.start();
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
	
	//将资产文件 转移到file 应用下
	private void source2Db() {
		InputStream is = null ;
		FileOutputStream fos = null;
		try {
			
			//1 拷贝文件  -- 资产目录管理 getAsserts()
			AssetManager assets = getAssets();
			is = assets.open("address.zip");
			File targetFile = new File(getFilesDir(),"address.zip");
			fos = new FileOutputStream(targetFile);
			
			byte[] bys = new byte[1024];
			int len = 0;
			while((len = is.read(bys))>0) {
				fos.write(bys, 0, len);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			release(fos);
			release(is);
		}
		
		//2 解压文件
		File zipFile = new File(getFilesDir(),"address.zip");
		File targetFile = new File(getFilesDir(),"address.db");
		GZipUtils.unzip(zipFile, targetFile);
		//删除zip文件
		zipFile.delete();
	}

	// 创建一个对话框
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SplashActivity.this);

		// 在进入到更新操作的时候,不希望再通过按钮去改动,所以这里设置为false
		builder.setCancelable(false);
		// 设置标题
		builder.setTitle("版本更新提醒");
		// 设置文本内容
		builder.setMessage(mDesc);
		// System.out.println(mDesc);
		// 设置Button
		// 设置"确认" 按钮
		builder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 当点击确认后,那么就要去服务器下载最新版本
				showProgressDialog();

			}
		});

		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 对话框先消失
				dialog.dismiss();
				camebackHome();
			}
		});

		// 显示出来
		builder.show();
	}

	private void showProgressDialog() {
		// 下载新版本
		final ProgressDialog dialog = new ProgressDialog(SplashActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);

		// 去下载版本
		HttpUtils utils = new HttpUtils();
		String path = murl;
		final String target = new File(
				Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".apk").getAbsolutePath();

		utils.download(path, target, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				// 成功时的回调
				System.out.println("下载成功");
				dialog.dismiss();
				// 下载成功,那么去安装
				// 安装是系统行为
				/*
				 * <intent-filter> <action
				 * android:name="android.intent.action.VIEW" /> <category
				 * android:name="android.intent.category.DEFAULT" /> <data
				 * android:scheme="content" /> <data android:scheme="file" />
				 * <data
				 * android:mimeType="application/vnd.android.package-archive" />
				 * </intent-filter>
				 */

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.parse("file:" + target),
						"application/vnd.android.package-archive");

				// Uri.fromFile(file) // -->file :路径
				startActivityForResult(intent, REQUEST_CODE_INSTALL);

			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// total :下载文件的总大小
				// current : 当前下载到了什么位置
				// isUploading : 可用于上传和下载
				dialog.setMax((int) total);
				dialog.setProgress((int) current);
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// 失败的回调
				System.out.println("下载失败");
				dialog.dismiss();
				camebackHome();
			}
		});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// requestCode :自己发送的请求代码
		// resultCode :结果代码,打开的Activity做的标记
		if (requestCode == REQUEST_CODE_INSTALL) {
			// 安装的程序返回的数据

			switch (resultCode) {
			case Activity.RESULT_OK:
				// 用户成功操作
				System.out.println("用户成功安装");
				break;
			case Activity.RESULT_CANCELED:
				// 用户取消了操作
				System.out.println("用户取消安装");
				camebackHome();
				break;
			default:
				break;
			}
		}
	}

	private void checkVersionUpdate() {
		// 去网络获取最新的信息

		// 读取来自服务器中的文件,并解析获取版本
		// 连接网络 -- 访问网络是一个 耗时的操作,那么需要开启一个线程
		new Thread(new CheckVersionTask()).start();
	}

	private class CheckVersionTask implements Runnable {

		@Override
		public void run() {
			// http://192.168.1.72/updateInfo.txt
			String uri = "http://188.188.2.72/updateInfo.txt";
			// 服务器必须提供的网络接口
			// 发现如果不是通过 new出来的对象,那么可以通过创建实例对象试试 --- 获取网络访问客户端
			AndroidHttpClient client = AndroidHttpClient.newInstance(
					"userAgent", getApplicationContext());

			HttpParams params = client.getParams();
			// 访问网络的超时时间
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			// 设置读取的超时时间
			HttpConnectionParams.setSoTimeout(params, 3000);

			HttpGet get = new HttpGet(uri);

			try {
				HttpResponse response = client.execute(get);

				int code = response.getStatusLine().getStatusCode();

				if (code == 200) {
					// 访问成功
					// 获取网络流中的json数据
					InputStream in = response.getEntity().getContent();
					String result = StreamUtil.stream2String(in);

					Log.d(TAG, "访问结果" + result);
					// 获取json对象
					try {
						JSONObject obj = new JSONObject(result);

						int netCode = obj.getInt("versionCode");

						int localCode = PackageUtil
								.getVersionCode(getApplicationContext());

						if (netCode == localCode) {
							// 则说明无需版本更新,进入主页面

							camebackHome();

						} else {
							// 更新软件版本,先更新下载
							// 需要更新,显示更新的对话框 --- 子线程不可以创建一个对话框

							Message msg = Message.obtain();
							msg.what = SHOW_UPDATE_DIALOG;
							mDesc = obj.getString("desc");
							murl = obj.getString("url");
							handler.sendMessage(msg);
							System.out.println("murl: " + murl);
						}

					} catch (JSONException e) {
						e.printStackTrace();

						Message msg = Message.obtain();
						msg.what = JSONERROR;
						msg.obj = "code:120";
						handler.sendMessage(msg);
						camebackHome();
					}

				} else {
					// 访问不成功
					Message msg = Message.obtain();
					msg.what = CLIENTERROR;
					msg.obj = "code:100";
					handler.sendMessage(msg);
					camebackHome();
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();

				Message msg = Message.obtain();
				msg.what = CLIENTERROR;
				msg.obj = "code:100";
				handler.sendMessage(msg);
				camebackHome();

			} catch (IOException e) {
				e.printStackTrace();
				Message msg = Message.obtain();
				msg.obj = "code:110";
				msg.what = STREAMERROR;
				handler.sendMessage(msg);
				camebackHome();

			} finally {
				if (client != null) {
					// 接口类型没有方法,那么可以看看具体实现类型 ---> 解决leak activity
					client.close();
					client = null;
				}
			}

		}

	}

	private void camebackHome() {
		// 延迟进入主页面
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, HomeActivity.class);
				startActivity(intent);

				// 关闭当前页面
				finish();
			}
		}, 2000);

		System.out.println(Thread.currentThread().getName());
	}

}
