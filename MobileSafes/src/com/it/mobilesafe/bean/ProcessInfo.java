package com.it.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	
	public Drawable icon; // 应用图标
	public String name; // 应用名称
	public long Memory; // 当前占用的内存
	public boolean isSystem;//是否是系统进程
	public String packageName; //包名
	public boolean isForeground;//是否是前台进程
	
	public boolean checked; // checkbox 是否被勾选
	
}
