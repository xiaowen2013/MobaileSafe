package cn.itcase.safe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.itcase.safe.service.OpenLockService1;
import cn.itcase.safe.service.OpenLockService2;
import cn.itcase.safe.utils.ServiceStartUitls;
import cn.itcase.safe.utils.SmsProvider;
import cn.itcase.safe.utils.SmsProvider.OnSmsListener;
import cn.itcase.safe.view.SettingItemView;

public class CommonUtilsActivity extends Activity {

	private static final String TAG = "CommonUtilsActivity";
	private SettingItemView mComutilInquiry;
	private SettingItemView mComutilQuery;
	private SettingItemView mSmsBackups;
	private SettingItemView mSmsRecovery;
	private SettingItemView mAppLock;
	private SettingItemView mOpenLockService1;
	private SettingItemView mOpenLockService2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_commonutils);

		mComutilInquiry = (SettingItemView) findViewById(R.id.comutils_siv_inquiry);
		mComutilQuery = (SettingItemView) findViewById(R.id.common_siv_query);
		mSmsRecovery = (SettingItemView) findViewById(R.id.common_siv_sms_recovery);
		mSmsBackups = (SettingItemView) findViewById(R.id.comutils_siv_sms_backups);
		mAppLock = (SettingItemView) findViewById(R.id.comutils_siv_app_lock);
		mOpenLockService1 = (SettingItemView) findViewById(R.id.comutils_op_lock1);
		mOpenLockService2 = (SettingItemView) findViewById(R.id.comutils_op_lock2);

		mComutilInquiry.setOnClickListener(new Inquiry());
		mComutilQuery.setOnClickListener(new CommonQuery());

		mSmsBackups.setOnClickListener(new SmsBackups());
		mSmsRecovery.setOnClickListener(new SmsRecovery());
		mAppLock.setOnClickListener(new AppLockManager());
	}

	/**
	 * 
	 * 程序锁的管理
	 */
	private class AppLockManager implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 打开程序锁的界面
			Intent intent = new Intent(CommonUtilsActivity.this,
					AppLockManagerActivity.class);
			startActivity(intent);

		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 初始化数据
		// 判断服务是否开启
		if (ServiceStartUitls.isRunning(this, OpenLockService1.class)) {
			// 正在运行
			// UI变化
			mOpenLockService1.setToggleOn(true);
		} else {
			mOpenLockService1.setToggleOn(false);
		}
		
		if (ServiceStartUitls.isRunning(this, OpenLockService2.class)) {
			// 正在运行
			// UI变化
			mOpenLockService2.setToggleOn(true);
		} else {
			mOpenLockService2.setToggleOn(false);
		}
	}

	/**
	 * 点击 开启电子狗服务1
	 * 
	 * @param view
	 */
	public void openLockService1(View view) {
		// 判断服务是否开启
		if (ServiceStartUitls.isRunning(this, OpenLockService1.class)) {
			// 正在运行
			// UI变化
			mOpenLockService1.setToggleOn(false);
			// 关闭服务
			Intent intent = new Intent(this, OpenLockService1.class);
			stopService(intent);

		} else {
			mOpenLockService1.setToggleOn(true);

			// 开启服务
			Intent intent = new Intent(this, OpenLockService1.class);
			startService(intent);
		}
	}

	/**
	 * 点击 开启电子狗服务2 TODO: 未完成
	 * 
	 * @param view
	 */
	public void openLockService2(View view) {

		// 这里因为使用的是辅助功能里面的页面,需要用户去自己手动去开启服务
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
		startActivity(intent);
	}

	/**
	 * 
	 * 点击了短信恢复
	 * 
	 */
	private class SmsRecovery implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 点击了短信恢复
			// 将Xml的内容写到短信
			smsRecovery();
		}

	}

	/**
	 * 短信恢复
	 */
	private void smsRecovery() {

		// 创建一个dialog用来显示进度
		final ProgressDialog dialog = new ProgressDialog(this);
		// 设置样式为横向
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 设置其他的地方不可用
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// 需要提供数据操作的进度
		SmsProvider.recoveryAllSms(CommonUtilsActivity.this,
				new OnSmsListener() {

					@Override
					public void onMax(int max) {
						dialog.setMax(max);
					}

					@Override
					public void onProgress(int progress) {
						dialog.setProgress(progress);
					}

					@Override
					public void onSucess() {

						Toast.makeText(getApplicationContext(), "备份成功",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}

					@Override
					public void onFailed() {

						Toast.makeText(getApplicationContext(), "备份失败",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
	}

	/**
	 * 
	 * 点击了短信备份
	 * 
	 */
	private class SmsBackups implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 点击了短信备份
			// 读取短信的内容,并存到本地的xml文件下
			smsbackups();
		}

	}

	private void smsbackups() {

		// 创建一个dialog用来显示进度
		final ProgressDialog dialog = new ProgressDialog(this);
		// 设置样式为横向
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 设置其他的地方不可用
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// 需要提供数据操作的进度
		SmsProvider.saveAllSms(CommonUtilsActivity.this, new OnSmsListener() {

			@Override
			public void onMax(int max) {

				dialog.setMax(max);
			}

			@Override
			public void onProgress(int progress) {

				dialog.setProgress(progress);
			}

			@Override
			public void onSucess() {

				Toast.makeText(getApplicationContext(), "备份成功",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}

			@Override
			public void onFailed() {

				Toast.makeText(getApplicationContext(), "备份失败",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
	}

	private class CommonQuery implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(TAG, "点击了查询常用号码...");
			Intent intent = new Intent(CommonUtilsActivity.this,
					CommonQueryNumber.class);
			startActivity(intent);
		}
	}

	private class Inquiry implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 点击了查询
			Log.d(TAG, "点击了查询号码归属地");
			Intent intent = new Intent(CommonUtilsActivity.this,
					InquiryNumberBelong.class);
			startActivity(intent);
		}
	}
}
