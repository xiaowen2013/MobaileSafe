package cn.itcase.safe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;

public class LostSetup2Activity extends BaseSetupActivity {

	private static final String TAG = "LostSetup2Activity";
	private ImageView mIv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup2);
		mIv = (ImageView) findViewById(R.id.setup2_iv_lock);

		// 初始化mIv
		String bundleSim = PreferenceUtils.getString(this,
				IConstants.BUNDLE_SJFD_SIM);
		Log.d(TAG, bundleSim + "===");
		if (!TextUtils.isEmpty(bundleSim)) {
			mIv.setImageResource(R.drawable.lock);
		} else {
			mIv.setImageResource(R.drawable.unlock);
		}
	}

	public void setup2_bundle(View view) {
		
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String number = tm.getSimSerialNumber();
		
		// 点击了更换图片
		String bundleSim = PreferenceUtils.getString(this,
				IConstants.BUNDLE_SJFD_SIM);
		if (!TextUtils.isEmpty(bundleSim)) {
			// 不为空 , 说明绑定了 , 现在点击了 , 说明要取消绑定
			mIv.setImageResource(R.drawable.unlock);
			PreferenceUtils
					.putString(this, IConstants.BUNDLE_SJFD_SIM,null);
			
		} else {
			// 为空 , 说明没有绑定 , 现在需要绑定
			mIv.setImageResource(R.drawable.lock);
			PreferenceUtils.putString(this, IConstants.BUNDLE_SJFD_SIM, number);
			// 需要绑定,保存的SIM卡标识
		}
	}

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, LostSetup1Activity.class);
		startActivity(intent);
		return false;

	}

	@Override
	protected boolean performNext() {

		String bundleFlag = PreferenceUtils.getString(this,
				IConstants.BUNDLE_SJFD_SIM);

		if (TextUtils.isEmpty(bundleFlag)) {
			// 没有选择开启防盗 , 提示开启防盗
			// 取消了绑定 , 提示 如果开启手机防盗 , 必须绑定手机SIM卡
			Toast.makeText(this, "如果开启手机防盗 , 必须绑定手机SIM卡", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		Log.d(TAG, bundleFlag+"---------------------");

		Intent intent = new Intent(this, LostSetup3Activity.class);
		startActivity(intent);
		return false;

	}

}
