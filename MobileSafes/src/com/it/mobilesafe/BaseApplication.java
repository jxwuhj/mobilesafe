package com.it.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.os.Environment;

import com.it.mobilesafe.utils.Logger;


@ReportsCrashes(
        formUri = "http://www.188.188.2.72:8080/",
        		 mode = ReportingInteractionMode.DIALOG,
                 resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
                 resDialogText = R.string.crash_dialog_text,
                 resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
                 resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
                 resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
                 resDialogEmailPrompt = R.string.crash_user_email_label,
                 resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
		)
public class BaseApplication extends Application {
	
	private static final String TAG = "BaseApplication";

	//程序入口  -- main函数  -- android是一个应用,清单只是一个描述,BaseApplication是应用的入口,而非程序
	@Override
	public void onCreate() {
		Logger.d(TAG, "应用程序启动");
		
		//监控全局的初始化
		//Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
		
		 // The following line triggers the initialization of ACRA
        ACRA.init(this);
		
        
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	
	final class CrashHandler implements UncaughtExceptionHandler {

		private FileOutputStream fos;

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			Logger.d(TAG, "捕获异常 ");
			
			//字节流,将异常信息保存到流里面
			StringWriter sw = new StringWriter();
			PrintWriter err = new PrintWriter(sw);
			ex.printStackTrace(err);
			
			String result = sw.toString();
			
			try {
				fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"error.log"));
				fos.write(result.getBytes());
				fos.close();
				int myPid = android.os.Process.myPid();
				android.os.Process.killProcess(myPid);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
