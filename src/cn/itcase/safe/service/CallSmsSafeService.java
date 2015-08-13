package cn.itcase.safe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.itcase.safe.db.BlackDao;
import cn.itcase.safe.domain.BlackInfo;

public class CallSmsSafeService extends Service {

	private static final String TAG = "CallSmsSafeService";
	private TelephonyManager mTm;
	private BlackDao mBao;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "服务开启啦....");

		// 获取短信 电话的系统服务
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// 创建服务器对象
		mBao = new BlackDao(this);

		// 1.拦截短信
		// 注册广播
		IntentFilter filter = new IntentFilter();
		// 设置该广播的优先权
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		// 添加动作
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(mCallSmsReceiver, filter);

		// 2.拦截电话 TODO:
	}

	private BroadcastReceiver mCallSmsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收到广播
			Log.d(TAG, "接收到广播la.....");
			// 获取广播的内容
			Bundle extras = intent.getExtras();
			Object[] objects = (Object[]) extras.get("pdus");

			for (Object obj : objects) {
				// 获取短信对象
				SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
				// 获取发送地址(号码)
				String address = message.getOriginatingAddress();

				// 根据电话号码来判断类型 , 如果有查到 ,则不是 负一
				// 如果查到的类型是短信或者全部 , 则都需要拦截
				int type = mBao.findType(address);
				Log.d(TAG, "查询到的当前类型:" + address);
				if (type == BlackInfo.TYPE_SMS || type == BlackInfo.TYPE_ALL) {
					// 拦截短信
					// 停止广播的发送
					abortBroadcast();
				}
			}

		}
	};

	@Override
	public void onDestroy() {
		Log.d(TAG, "服务关启啦....");
	}

}
