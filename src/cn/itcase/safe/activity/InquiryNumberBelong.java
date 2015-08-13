package cn.itcase.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.db.AddressDao;

public class InquiryNumberBelong extends Activity {

	private Button mBut;
	private TextView mTv;
	private EditText mEt_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inquirynumber);
		mBut = (Button) findViewById(R.id.common_bt);
		mTv = (TextView) findViewById(R.id.common_tv);
		mEt_number = (EditText) findViewById(R.id.common_et_number);
		mBut.setOnClickListener(new ButInquiry());
		// 设置输入框的监听事件
		mEt_number.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 输入发生变化的时候
				// 获取到号码了 , 现在需要进行查询
				String address = AddressDao.findAddress(
						getApplicationContext(), s.toString());
				if (address == null) {
					mTv.setText("归属地:未知");
				} else {
					mTv.setText("归属地:" + address);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	
	
	
	private class ButInquiry implements OnClickListener {

		@Override
		public void onClick(View v) {
			String number = mEt_number.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {

				// 开启抖动动画
				Animation shake = AnimationUtils.loadAnimation(
						InquiryNumberBelong.this, R.anim.shake);
				findViewById(R.id.common_et_number).startAnimation(shake);

				Toast.makeText(InquiryNumberBelong.this, "号码不能为空.. ",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 获取到号码了 , 现在需要进行查询
			String address = AddressDao.findAddress(getApplicationContext(),
					number);
			if (address == null) {
				mTv.setText("归属地:未知");
			} else {
				mTv.setText("归属地:" + address);
			}
		}
	}
}
