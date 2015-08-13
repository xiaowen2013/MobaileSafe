package cn.itcase.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.domain.AppLockInfo;
import cn.itcase.safe.service.OpenLockService1;
import cn.itcase.safe.utils.AppInfoProvider;

public class LockServiceActivity extends Activity {

	private static final String TAG = "LockServiceActivity";
	private AppLockInfo mInfo;
	private String mApppackge;
	private ImageView mIvIcon;
	private EditText mEdInText;
	private TextView mTvName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lockappsplash);
		mIvIcon = (ImageView) findViewById(R.id.app_lock_sp_icon);
		mEdInText = (EditText) findViewById(R.id.app_lock_sp_intext);
		mTvName = (TextView) findViewById(R.id.app_lock_sp_name);

		mApppackge = getIntent().getStringExtra(OpenLockService1.EXTRA_PACKNAME);
		mInfo = AppInfoProvider.getAppInfo(this, mApppackge);
		
		Log.d(TAG, "接收到的包名:" + mApppackge);
		mIvIcon.setImageDrawable(mInfo.icon);
		mTvName.setText(mInfo.appName);
	}

	public void cilckOk(View view) {
		String string = mEdInText.getText().toString();
		if (TextUtils.isEmpty(string)) {
			Toast.makeText(getApplicationContext(), "密码不能为空...",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if ("123".equals(string)) {
			// 点击了确定 , 发送一条广播 , 然后结束自己
			Intent intent = new Intent();
			intent.setAction("cn.itcase.unlockapp");
			intent.putExtra(OpenLockService1.EXTRA_PACKNAME, mApppackge);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// 点击返回键 , 返回桌面
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);

		finish();
	}
}
