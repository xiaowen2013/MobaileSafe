package cn.itcase.safe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

public class ServiceStartUitls {

	/**
	 * 判断某个服务是否正在运行
	 * 
	 * @param context
	 *            上下文(当前Activity)
	 * @param clazz
	 *            判断的类名
	 * @return 为真说明正在运行 ,为假说明没有运行
	 */
	public static boolean isRunning(Context context,
			Class<? extends Service> clazz) {

		// 获取当前Activity的所有服务
		// 遍历这些服务 , 判断他们的服务的类名和传进来的类名是否一致
		// 一致 , 正在运行
		// 不一致 , 没有运行
		ActivityManager systemService = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = systemService
				.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo Info : list) {
			ComponentName service = Info.service;
			if (service.getClassName().equals(clazz.getName())) {
				// 正在运行
				return true;
			}
		}

		return false;
	}
}
