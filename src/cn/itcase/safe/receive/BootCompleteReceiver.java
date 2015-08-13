package cn.itcase.safe.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		// 只有是开启服务才才执行
		if (PreferenceUtils.getBoolean(context, IConstants.SJFD_SETUP5, false)) {
			// 接收到手机开机的广播
			Log.d(TAG, "接收到手机开机的广播====");
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String number = tm.getSimSerialNumber();
			String num = PreferenceUtils.getString(context,
					IConstants.BUNDLE_SJFD_SIM);

			if (!number.equals(num)) {
				// SIM卡不一致 , 发送安全短信
				SmsManager sm = SmsManager.getDefault();
				sm.sendTextMessage(IConstants.SJFD_PHONUM, null,
						"shouji beidao la ...", null, null);
			}
		}
	}

}
