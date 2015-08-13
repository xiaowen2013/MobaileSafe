package cn.itcase.safe.receive;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;
import cn.itcase.safe.activity.R;
import cn.itcase.safe.service.GPSService;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// 接收短信广播
		Log.d(TAG, "接收到短信的广播啦... ");
		// 获取短信的内容
		Object[] object = (Object[]) intent.getExtras().get("pdus");

		Log.d(TAG, object + "短信的内容... ");

		// 判断是否有开启手机保护
		Boolean flag = PreferenceUtils.getBoolean(context,
				IConstants.SJFD_SETUP5);
		if (!flag) {
			// 没有开启
			return;
		}
		// 开启啦
		// 判断短信内容
		for (Object obj : object) {

			// 获取短信
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) obj);
			// 获取短信内容
			// 获取短信发送人
			String body = msg.getMessageBody();
			String address = msg.getOriginatingAddress();

			Log.d(TAG, "短信的内容...== " + body);
			Log.d(TAG, "发送人...== " + address);

			if ("#*location*#".equals(body)) {
				// GPS 追踪
				// 开启GPS服务
				Intent intent_gps = new Intent(context, GPSService.class);
				context.startService(intent_gps);
				abortBroadcast();
			} else if ("#*wipedata*#".equals(body)) {
				// 远程删除数据
				// 获得管理员
				Log.d(TAG, "远程删除数据");
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);

				ComponentName cn = new ComponentName(context,
						SafeAdminReceive.class);
				// 判断是否获取管理员 , 然后再执行如下语句
				if (dpm.isAdminActive(cn)) {
					// 写 0 为擦除sd卡的所有信息 , 手机将恢复出厂设置
					dpm.wipeData(0);
				}

				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				// 播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
				// 永远循环
				player.setLooping(true);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();

			} else if ("#*lockscreen*#".equals(body)) {
				// 远程锁屏
				// 获得管理员
				Log.d(TAG, "远程锁屏");
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);

				ComponentName cn = new ComponentName(context,
						SafeAdminReceive.class);
				// 判断是否获取管理员 , 然后再执行如下语句
				if (dpm.isAdminActive(cn)) {
					// 设置锁屏密码 ,为 123
					dpm.resetPassword("123", 0);
					// 锁屏
					dpm.lockNow();
				}
				abortBroadcast();
			}
		}

	}

}
