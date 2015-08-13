package cn.itcase.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;

public class LostSetup3Activity extends BaseSetupActivity {

	private static final int RESULT = 100;
	private EditText phoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_setup3);
		phoneNum = (EditText) findViewById(R.id.setup3_number);
		
		// 出事化号码
		String number = PreferenceUtils.getString(this, IConstants.SJFD_PHONUM);
		phoneNum.setText(number);
	}

	public void clickContact(View v) {
		// 跳转到可以选择联系人的页面
		Intent intent = new Intent(this, ContactSelectActivity.class);
		startActivityForResult(intent, RESULT);
	}

	/**
	 * 返回选择的结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 返回来了结果
				String num = data.getStringExtra(ContactSelectActivity.KEY_NUM);
				phoneNum.setText(num);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, LostSetup2Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean performNext() {
		// 判断电话号码是否为空 , 如果为空 , 则不执行向下页面的跳转
		// 获取电话号码
		String phoneNumber = phoneNum.getText().toString().trim();
		if (TextUtils.isEmpty(phoneNumber)) {
			// 为空
			Toast.makeText(this, "如果开启手机防盗,必须设置安全号码", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		
		// 记录当前号码
		PreferenceUtils.putString(this, IConstants.SJFD_PHONUM, phoneNumber);

		Intent intent = new Intent(this, LostSetup4Activity.class);
		startActivity(intent);
		return false;
	}
}
