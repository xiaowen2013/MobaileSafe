package cn.itcase.safe.activity;

import cn.itcase.safe.receive.SafeAdminReceive;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class LostSetup4Activity extends BaseSetupActivity {

	private String TAG = "LostSetup4Activity";
	private static final int REQUEST_CODE_ENABLE_ADMIN = 110;
	private ImageView set_admin;
	DevicePolicyManager dpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup4);
		set_admin = (ImageView) findViewById(R.id.setp4_lv_admin);

		// 获取管理员设备
		dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		// 初始化状态
		ComponentName cn = new ComponentName(this, SafeAdminReceive.class);
		if (dpm.isAdminActive(cn)) {
			set_admin.setImageResource(R.drawable.admin_activated);
		} else {
			set_admin.setImageResource(R.drawable.admin_inactivated);
		}
	}

	/**
	 * 点击后绑定或取消管理员设备
	 * 
	 * @param view
	 */
	public void setp4_admin(View view) {
		ComponentName cn = new ComponentName(this, SafeAdminReceive.class);
		if (dpm.isAdminActive(cn)) {
			// 当前是锁着的
			// 点击了这个 是要解除锁定
			dpm.removeActiveAdmin(cn);
			// 取消锁屏密码
			dpm.resetPassword("", 0);
			// UI 要变化
			set_admin.setImageResource(R.drawable.admin_inactivated);
		} else {
			// 当前没有绑定 , 点击了说明需要绑定
			// 需要跳转页面 , 进行绑定 , 从官方文档 copy 的如下信息
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "小温卫士");
			startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
		}
	}

	// 页面跳转返回调用的方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ComponentName cn = new ComponentName(this, SafeAdminReceive.class);
		Log.d(TAG, "当前状态  88" + dpm.isAdminActive(cn));
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 激活成功 , UI 需要变化
				set_admin.setImageResource(R.drawable.admin_activated);
				Log.d(TAG, "当前状态  44" + dpm.isAdminActive(cn));
				break;
			case Activity.RESULT_CANCELED:
				// 激活失败 , 不需要变化
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, LostSetup3Activity.class);
		startActivity(intent);
		return false;

	}

	@Override
	protected boolean performNext() {
		// 检测是否开启了管理员 , 如果没有没有开启 ,则需要提示
		// 且不允许跳转
		ComponentName cn = new ComponentName(this, SafeAdminReceive.class);
		if (!dpm.isAdminActive(cn)) {
			// 没有开启
			Toast.makeText(this, "要开启手机防盗,必须开启设备管理员", Toast.LENGTH_SHORT)
					.show();
			return true;
		}

		Intent intent = new Intent(this, LostSetup5Activity.class);
		startActivity(intent);
		return false;

	}

}
