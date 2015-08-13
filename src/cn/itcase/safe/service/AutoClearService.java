package cn.itcase.safe.service;

import java.util.List;

import cn.itcase.safe.domain.ProcessInfo;
import cn.itcase.safe.utils.ProcessInfoProvide;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoClearService extends Service {

	private static final String TAG = "AutoClearService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "服务开启啦...");
		// 注册一个锁屏的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		// intentFilter.addAction(Intent.ACTION_SCREEN_ON); 屏幕关
		registerReceiver(mReceiver, intentFilter);
	}

	// BroadcastReceiver
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 清理进程
			// 获取正在运行的进程
			List<ProcessInfo> runningProcess = ProcessInfoProvide
					.getAllRunningProcess(getApplicationContext());
			// 遍历删除
			for (ProcessInfo processInfo : runningProcess) {
				// 获取包管理器
				ActivityManager am = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);

				am.killBackgroundProcesses(processInfo.packageName);
				Log.d(TAG, "删除程序啦..." + processInfo.packageName);
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "服务关闭啦...");
		// 记得注销广播
		unregisterReceiver(mReceiver);
	}

}
