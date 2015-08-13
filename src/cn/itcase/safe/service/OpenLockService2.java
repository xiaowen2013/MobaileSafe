package cn.itcase.safe.service;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import cn.itcase.safe.activity.LockServiceActivity;
import cn.itcase.safe.db.AppLockDao;

public class OpenLockService2 extends AccessibilityService {

	private AppLockDao mDao;
	private List<String> mData; // 用于记录当前加锁的程序进入后已经解锁了的包名

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册一个广播 , 用于接收用于密码登陆成功,不在将这个程序锁屏
		IntentFilter filter = new IntentFilter();
		filter.addAction("cn.itcase.unlockapp");
		// 添加锁屏的广播
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, filter);
		mData = new ArrayList<String>();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收密码登陆成功的广播
			// 获取动作
			String action = intent.getAction();
			// 如果这个动作是登陆页面发送过来的就执行
			if (action.equals("cn.itcase.unlockapp")) {
				// 获取包名
				String extra = intent
						.getStringExtra(OpenLockService1.EXTRA_PACKNAME);
				// 将包名添加到list中
				mData.add(extra);
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				// 屏幕关
				// 将刚刚添加的锁屏程序的集合清空
				mData.clear();
			} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
				// 屏幕开
			}
		}
	};

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// event 是一个正在运行且可见的事件
		// 获取到了包名 , 查询数据库,这个程序的是否添加锁了
		String packageName = event.getPackageName().toString();
		mDao = new AppLockDao(getApplicationContext());
		boolean result = mDao.select(packageName);
		if (result) {

			// 判断当前是否解锁过
			if (mData.contains(packageName)) {
				// 解锁过 , 不往下走
				return;
			}
			// 是加锁的程序
			// 跳出一个锁屏的页面
			Intent intent = new Intent(OpenLockService2.this,
					LockServiceActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// 将包名带过去
			// 那边密码确定后会发送一个广播 ,说已经登录成功 , 进入了页面
			intent.putExtra(OpenLockService1.EXTRA_PACKNAME, packageName);
			startActivity(intent);
		}
	}

	@Override
	public void onInterrupt() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 注销广播
		unregisterReceiver(mReceiver);
	}

}
