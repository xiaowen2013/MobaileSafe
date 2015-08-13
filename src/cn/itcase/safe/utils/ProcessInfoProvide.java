package cn.itcase.safe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.util.Log;
import cn.itcase.safe.activity.R;
import cn.itcase.safe.domain.ProcessInfo;

public class ProcessInfoProvide {

	private static final String TAG = "ProcessInfoProvide";

	/**
	 * 获取正在运行的程序的个数
	 * 
	 * @param context
	 * @return
	 */
	public static int getAllRunningProcessCount(Context context) {

		// 获取进程服务
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 获取所有运行的服务
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();

		if (appProcesses != null) {
			return appProcesses.size();
		}
		return 0;
	}

	/**
	 * 获取所有的 进程总数
	 * 
	 * @param context
	 * @return
	 */
	public static int getTotalPssCount(Context context) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packages = packageManager.getInstalledPackages(0);

		int count = 0;
		for (PackageInfo pack : packages) {
			// 去重 , 线程的名
			HashSet<String> set = new HashSet<String>();
			set.add(pack.applicationInfo.processName);

			// 一个app 可能有多个进程 , 因此需要逐个判断
			ActivityInfo[] activities = pack.activities;
			if (activities != null) {
				for (ActivityInfo activityInfo : activities) {
					set.add(activityInfo.processName);
				}
			}

			ServiceInfo[] services = pack.services;
			if (services != null) {
				for (ServiceInfo serviceInfo : services) {
					set.add(serviceInfo.processName);
				}
			}

			ProviderInfo[] providers = pack.providers;
			if (providers != null) {
				for (ProviderInfo providerInfo : providers) {
					set.add(providerInfo.processName);
				}
			}

			ActivityInfo[] receivers = pack.receivers;
			if (receivers != null) {
				for (ActivityInfo activityInfo : receivers) {
					set.add(activityInfo.processName);
				}
			}
			count += set.size();
		}
		return count;
	}

	/**
	 * 获取正在运行的程序或者后台服务
	 * 
	 * @param context
	 * @return
	 */
	public static List<ProcessInfo> getAllRunningProcess(Context context) {
		// 获取进程服务
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 获取所有运行的服务
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();

		Log.d(TAG, appProcesses.size() + "  list 的总数");
		// 获取程序的包管理器
		PackageManager packageManager = context.getPackageManager();

		// 创建一个集合用于存储当前的process信息
		List<ProcessInfo> list = new ArrayList<ProcessInfo>();
		if (appProcesses != null) {
			for (RunningAppProcessInfo appRunPro : appProcesses) {

				// 创建一个process 对象
				ProcessInfo proInfo = new ProcessInfo();

				// 获取当前进程的名
				String processName = appRunPro.processName;

				// 获取当前程序详细信息
				try {
					ApplicationInfo info = packageManager.getApplicationInfo(
							processName, 0);
					proInfo.packageName = info.packageName;
					Log.d(TAG, "proInfo.packageName:" + proInfo.packageName);
					proInfo.icon = info.loadIcon(packageManager);
					// 程序的名称是在清单文件 里面有引向的 label 就是程序的键
					// android:label="@string/app_name"
					proInfo.proName = info.loadLabel(packageManager).toString();
					
					// 判断该进程是系统的还是第三方的
					int flags = info.flags;
					if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
						// 是系统的程序
						proInfo.isSystem = true;
					} else {
						// 不是系统的程序
						proInfo.isSystem = false;
					}

					// 获取这个程序的内存信息 , 传入一个整形的pid
					android.os.Debug.MemoryInfo memoryInfo = am
							.getProcessMemoryInfo(new int[] { appRunPro.pid })[0];

//					int importance = appRunPro.importance;
					int totalPss = memoryInfo.getTotalPss();
					proInfo.momory = totalPss * 1024;

				} catch (NameNotFoundException e) {
					e.printStackTrace();
					// 出异常了, 说明找不到这个程序的名称 , 就是没有详细的信息
					// 这类是系统的默认程序 , 需要手动添加一些信息
					proInfo.icon = context.getResources().getDrawable(
							R.drawable.ic_launcher);
					proInfo.proName = processName;
					proInfo.isSystem = true;
				}

				// 把信息添加到list
				list.add(proInfo);
			}
		}

		Log.d(TAG, list.size() + "  list 的总数");
		// // 将应用程序进行拆分
		List<ProcessInfo> newList = new ArrayList<ProcessInfo>();
		List<ProcessInfo> user = new ArrayList<ProcessInfo>();
		List<ProcessInfo> system = new ArrayList<ProcessInfo>();
		for (ProcessInfo Info : list) {
			if (Info.isSystem) {
				system.add(Info);
			} else {
				user.add(Info);
			}
		}
		newList.addAll(user);
		newList.addAll(system);

		return newList;
	}

	/**
	 * 获取可用的内存
	 * 
	 * @param context
	 * @return
	 */
	public static long getFreeMomory(Context context) {

		// 获取包管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 在安卓中 , 所有到out开头的方法或者参数都是一些输出函数 , 意思是你创建这个数值类型穿进去之后
		// 方法结束了 , 这个值就有值了
		// public void getMemoryInfo(MemoryInfo outInfo)
		// 因此这里需要创建一个 MemoryInfo 的对象
		MemoryInfo outInfo = new MemoryInfo();
		// 获取内存的信息
		am.getMemoryInfo(outInfo);

		return outInfo.availMem;
	}

	/**
	 * 获取所有的内存
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static long getTotalMomory(Context context) {

		// 获取包管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 在安卓中 , 所有到out开头的方法或者参数都是一些输出函数 , 意思是你创建这个数值类型穿进去之后
		// 方法结束了 , 这个值就有值了
		// public void getMemoryInfo(MemoryInfo outInfo)
		// 因此这里需要创建一个 MemoryInfo 的对象
		MemoryInfo outInfo = new MemoryInfo();
		// 获取内存的信息
		am.getMemoryInfo(outInfo);

		// 代码适配
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			// 高版本 适配
			return outInfo.totalMem;
		} else {
			return getLowTotalMomory();
		}
	}

	/**
	 * 适配低版本
	 * 
	 * @return
	 */
	private static long getLowTotalMomory() {
		File file = new File("/proc/meminfo");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));

			// MemTotal: 513492 kB

			// String line = reader.readLine();
			// line = line.replace("MemTotal:", "");
			// line = line.replace("kB", "");
			// line = line.trim();
			String line = reader.readLine().replace("MemTotal:", "")
					.replace("kB", "").trim();

			return Long.valueOf(line) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void killProcess(Context context, String packageName) {
		// 获取管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(packageName);

	}

}
