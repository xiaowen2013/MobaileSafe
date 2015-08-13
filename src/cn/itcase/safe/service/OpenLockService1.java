package cn.itcase.safe.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import cn.itcase.safe.activity.LockServiceActivity;
import cn.itcase.safe.db.AppLockDao;

public class OpenLockService1 extends Service {

	private static final String TAG = "OpenLockService";
	public static final String EXTRA_PACKNAME = "extra_packname";
	private List<String> mLockApp;
	private List<String> mFreeList = new ArrayList<String>();
	private boolean isRun = true;
	private AppLockDao mDao;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "接收到广播啦....");
			// 获取动作
			String action = intent.getAction();
			if ("cn.itcase.unlockapp".equals(action)) {
				// 接收到发送过来的广播
				String stringExtra = intent.getStringExtra(EXTRA_PACKNAME);
				// 将这个包名添加到 集合
				mFreeList.add(stringExtra);
			} else if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// 屏幕开
				// 开始监测
				startWatch();
				isRun = true;
				Log.d(TAG, "屏幕开的广播..");
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// 屏幕关
				// 停止操作
				isRun = false;
				// 清空当前的 mFreeList
				mFreeList.clear();
				Log.d(TAG, "屏幕关的广播..");
			}
		}
	};
	
	/**
	 *  当数据库的内容发送改变时执行的方法 , 去更新list
	 */
	private ContentObserver mReceiverOb = new ContentObserver(new Handler()){
		public void onChange(boolean selfChange) {
			mLockApp = mDao.getAllLockApp();
		};
	};

	@Override
	public void onCreate() {

		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("cn.itcase.unlockapp");
		// 屏幕的开和关
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, filter);
		
		// 注册内容观察者
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://cn.itcase.lock");
		resolver.registerContentObserver(uri, true, mReceiverOb);

		Log.d(TAG, "服务开启啦..");
		startWatch();
	}

	private void startWatch() {

		mDao = new AppLockDao(getApplicationContext());
		mLockApp = mDao.getAllLockApp();
		new Thread() {
			public void run() {
				while (isRun) {
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo taskInfo = runningTasks.get(0);
					String packageName = taskInfo.topActivity.getPackageName();

					Log.d(TAG, packageName + ":当前应用程序");

					if (mFreeList.contains(packageName)) {
						// 如果当前的 mFreeList 中包含 packageName 说明是已经密码验证成功了的 ,
						// 接收向下的校验
						continue;
					}

					if (mLockApp.contains(packageName)) {
						// 是加锁的程序 , 跳出一个锁屏的页面
						Intent intent = new Intent(OpenLockService1.this,
								LockServiceActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(EXTRA_PACKNAME, packageName);
						startActivity(intent);
					}

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "服务关闭啦..");
		// 注销广播
		unregisterReceiver(mReceiver);
		isRun = false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
