package cn.itcase.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;

public class LostSetupFindActivity extends Activity {

	private static final String TAG = "LostSetupFindActivity";
	private TextView mTv_num;
	private ImageView mIvIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setupfind);
		
		mTv_num = (TextView) findViewById(R.id.setfin_safeNum);
		mIvIcon = (ImageView) findViewById(R.id.setfin_iv_safeicon);

		// 初始化相应的控件
		String phonum = PreferenceUtils.getString(this, IConstants.SJFD_PHONUM);
		mTv_num.setText(phonum);
		boolean isOpen = PreferenceUtils.getBoolean(this,
				IConstants.SJFD_SETUP5);
		if (isOpen) {
			// 开启
			mIvIcon.setImageResource(R.drawable.lock);
		} else {
			// 没开启
			mIvIcon.setImageResource(R.drawable.unlock);
		}

	}
	/**
	 * 是否开启服务
	 * 
	 * @param view
	 */
	public void isUnsealSafe(View view) {

		// 点击了是否开启服务 , 是要切换开启和不开启
		// 获取当前的状态, 然后再判断
		boolean isOpen = PreferenceUtils.getBoolean(this,
				IConstants.SJFD_SETUP5);
		if (isOpen) {
			// 开启
			mIvIcon.setImageResource(R.drawable.unlock);
			Log.d(TAG, "当前状态 ====lock");
		} else {
			// 没开启
			mIvIcon.setImageResource(R.drawable.lock);
		}
		PreferenceUtils.putBoolean(this, IConstants.SJFD_SETUP5, !isOpen);
	}

	/**
	 * 从新设置
	 * 
	 * @param view
	 */
	public void reInstall(View view) {
		// 点击了重新设置 , 跳转到设置页面一
		Intent intent = new Intent(this, LostSetup1Activity.class);
		startActivity(intent);
		finish();
	}

}
