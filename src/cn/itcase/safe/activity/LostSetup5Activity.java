package cn.itcase.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;

public class LostSetup5Activity extends BaseSetupActivity {

	private CheckBox mCb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup5);
		mCb = (CheckBox) findViewById(R.id.setip5_cb);
		// 初始化页面
		boolean flag = PreferenceUtils.getBoolean(this, IConstants.SJFD_SETUP5);
		mCb.setChecked(flag);
		// 动态记录点击, 设置点击事件
		mCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 存储当前标记
				PreferenceUtils.putBoolean(LostSetup5Activity.this,
						IConstants.SJFD_SETUP5, isChecked);
			}
		});

	}

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, LostSetup4Activity.class);
		startActivity(intent);
		return false;

	}

	@Override
	protected boolean performNext() {
		// Intent intent = new Intent(this,LostSetup4Activity.class);
		// startActivity(intent);
		if (!mCb.isChecked()) {
			// 没有构选 , 友好提示
			Toast.makeText(this, "要开启防盗服务,必须勾选", Toast.LENGTH_SHORT).show();
			return true;
		}
		// 存储当前的标记
		PreferenceUtils.putBoolean(this, IConstants.SJFD_SETUP5, true);
		// 跳转到下一个页面
		// TODO
		Intent intent = new Intent(this,LostSetupFindActivity.class);
		startActivity(intent);
		return false;
	}
}
