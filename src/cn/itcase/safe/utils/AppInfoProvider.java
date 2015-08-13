package cn.itcase.safe.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import cn.itcase.safe.domain.AppLockInfo;
import cn.itcase.safe.domain.AppManagerInfo;

public class AppInfoProvider {

	/**
	 * 获取本机所有的app
	 * 
	 * @param context
	 * @return 返回app 的list集合
	 */
	public static List<AppManagerInfo> getAllApps(Context context) {

		// 获取包管理器
		PackageManager packageManager = context.getPackageManager();
		// 获取所有的已安装的包
		List<PackageInfo> packages = packageManager.getInstalledPackages(0);

		// 创建一个list ,用于接收所有的程序信息
		List<AppManagerInfo> list = new ArrayList<AppManagerInfo>();
		for (PackageInfo packageInfo : packages) {
			// 创建一个list , 用于接收包
			AppManagerInfo info = new AppManagerInfo();
			info.appPackage = packageInfo.packageName;

			// 获取应用的详细信息
			ApplicationInfo app = packageInfo.applicationInfo;
			// 获取应用的名称
			info.appName = app.loadLabel(packageManager).toString();
			// 获取图标
			info.icon = app.loadIcon(packageManager);
			// 获取应用程序的大小
			String sourceDir = app.sourceDir;
			info.appSize = new File(sourceDir).length();

			// 判断是否是系统的程序
			int flags = app.flags;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// 是系统程序
				info.isSystem = true;
			} else {
				// 不是系统程序
				info.isSystem = false;
			}

			// 获取安装位置
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				// 是安装在sd卡
				info.isInstallSD = true;
			} else {
				// 不是安装在sd卡
				info.isInstallSD = false;
			}
			list.add(info);
		}
		List<AppManagerInfo> newlist = new ArrayList<AppManagerInfo>();
		List<AppManagerInfo> userlist = new ArrayList<AppManagerInfo>();
		List<AppManagerInfo> systemlist = new ArrayList<AppManagerInfo>();
		for (AppManagerInfo appInfo : list) {
			if (appInfo.isSystem) {
				// 是系统的
				systemlist.add(appInfo);
			} else {
				// 使用户的
				userlist.add(appInfo);
			}
		}
		// 主要是将系统的程序和用户的程序分开
		newlist.addAll(userlist);
		newlist.addAll(systemlist);
		return newlist;
	}

	/**
	 * 获取程序的图标和包名
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppLockInfo> getAllAppsLock(Context context) {

		// 获取包管理器
		PackageManager packageManager = context.getPackageManager();
		// 获取所有的已安装的包
		List<PackageInfo> packages = packageManager.getInstalledPackages(0);

		// 创建一个list ,用于接收所有的程序信息
		List<AppLockInfo> list = new ArrayList<AppLockInfo>();
		for (PackageInfo packageInfo : packages) {

			// 创建一个list , 用于接收包
			AppLockInfo info = new AppLockInfo();
			// 存储程序的包名
			info.packageName = packageInfo.packageName;
			// 获取应用的详细信息
			ApplicationInfo app = packageInfo.applicationInfo;
			// 获取应用的名称
			info.appName = app.loadLabel(packageManager).toString();
			// 获取图标
			info.icon = app.loadIcon(packageManager);

			list.add(info);
		}
		return list;
	}

	/**
	 * 根据包名获取程序的图标和包名
	 * 
	 * @param context
	 * @return
	 */
	public static AppLockInfo getAppInfo(Context context, String appPackName) {

		// 获取包管理器
		PackageManager packageManager = context.getPackageManager();

		ApplicationInfo info = null;
		try {
			info = packageManager.getApplicationInfo(appPackName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		AppLockInfo appInfo = new AppLockInfo();
		appInfo.appName = info.loadLabel(packageManager).toString();
		appInfo.packageName = appPackName;
		appInfo.icon = info.loadIcon(packageManager);

		return appInfo;
	}

}
