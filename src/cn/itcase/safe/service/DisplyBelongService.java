package cn.itcase.safe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.itcase.safe.db.AddressDao;
import cn.itcase.safe.utils.MyToast;

public class DisplyBelongService extends Service {

	private static final String TAG = "DisplyBelongService";
	private TelephonyManager mtm;
	private MyToast mToast;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "归属地显示开启啦... ");

		// 创建我的自定义Toast
		mToast = new MyToast(getApplicationContext());
		
		mtm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 来电话的监听
		mtm.listen(mCallListener, PhoneStateListener.LISTEN_CALL_STATE);

		// 创建一个广播接收者 , 接收拨出去的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mCallReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "归属地显示关闭啦... ");
		unregisterReceiver(mCallReceiver);
		// 注销服务
		mtm.listen(mCallListener, PhoneStateListener.LISTEN_NONE);
	}


	private PhoneStateListener mCallListener = new PhoneStateListener() {

		/**
		 * Callback invoked when device call state changes.
		 * 
		 * @see TelephonyManager#CALL_STATE_IDLE // 空闲
		 * @see TelephonyManager#CALL_STATE_RINGING //响铃(RINGING 是震荡的意思,因此理解为响铃)
		 * @see TelephonyManager#CALL_STATE_OFFHOOK // 关闭(OFFHOOK 摘机的意思)
		 */
		public void onCallStateChanged(int state, String incomingNumber) {
			
			// 获取到的电话地址
			String address = AddressDao.findAddress(getApplicationContext(),
					incomingNumber);
			
			//mToast.show(address);
			if(state == TelephonyManager.CALL_STATE_IDLE){
				// 空闲状态,隐藏状态
				mToast.hide();
			}else if(state == TelephonyManager.CALL_STATE_RINGING){
				mToast.show(address);
			}else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
				//mToast.hide();
			}
			
		};
	};

	public BroadcastReceiver mCallReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "收到播出电话的广播啦..");
			// 获取电话
			String num = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			String address = AddressDao.findAddress(getApplicationContext(),
					num);
//			Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT)
//					.show();
			// TODO:增加自定义的TOast 即可
			mToast.show(address);
		}
	};

}
