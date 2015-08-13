package cn.itcase.safe.domain;

import android.graphics.drawable.Drawable;

public class AppManagerInfo {

	public String appName; // 应用程序的名称
	public String appPackage; // 应用程序的包名
	public Drawable icon; // 图标
	public boolean isInstallSD;// 是否安装在sd卡
	public boolean isSystem;// 是否安装在系统
	public long appSize; // 程序的大小
}
