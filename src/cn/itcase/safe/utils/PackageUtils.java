package cn.itcase.safe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {

	/**
	 * 获得版本号信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {

		PackageManager pm = context.getPackageManager();

		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 版本的代码
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {

		PackageManager pm = context.getPackageManager();

		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
