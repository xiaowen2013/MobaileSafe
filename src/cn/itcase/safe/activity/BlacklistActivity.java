package cn.itcase.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.db.BlackDao;
import cn.itcase.safe.domain.BlackInfo;

public class BlacklistActivity extends Activity {

	private static final String TAG = "BlacklistActivity";
	private EditText mEt_number;
	private RadioGroup mRg;
	private Button mSave;
	private Button mCancel;
	private int mCheckedId = -1;
	public static final String EXTRA_NUMBER = "number";
	public static final String EXTRA_TYPE = "type";
	public static final String EXTRA_POSITION = "position";
	public static final String ACTION_ADD = "add";
	public static final String ACTION_UPDATA = "updata";
	private boolean isUpdata = false;
	private TextView mBtitle;
	int position;
	private BlackDao mDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacklist);
		mEt_number = (EditText) findViewById(R.id.cassfe_et_phenum);

		mSave = (Button) findViewById(R.id.black_btn_save);
		mCancel = (Button) findViewById(R.id.black_btn_cancel);
		mRg = (RadioGroup) findViewById(R.id.cassfe_rg);
		mBtitle = (TextView) findViewById(R.id.black_tv_title);

		mDao = new BlackDao(getApplicationContext());

		// 设置取消和保存的监听事件
		mSave.setOnClickListener(new SaveListener());
		mCancel.setOnClickListener(new CancelListener());
		// 设置 RadioGroup 的监听事件
		mRg.setOnCheckedChangeListener(new RgChang());

		// 判断当前的动作是添加还是更新
		// 获取Intent
		Intent intent = getIntent();
		// 获取动作
		String action = intent.getAction();
		if (action.equals(ACTION_UPDATA)) {
			// 更新数据
			isUpdata = true; // 标志是更新
			// 获取第几个item的标识
			position = intent.getIntExtra(EXTRA_POSITION, -1);

			Log.d(TAG, "当前是更新的页面");
			Log.d(TAG, position + "当前点击的值 position");
			mSave.setText("更新");
			mBtitle.setText("更新黑名单");
			// 联系人的输入框不可用
			mEt_number.setEnabled(false);
			// 在这里继续添加
			// 输入框需要更新的号码
			String num = intent.getStringExtra(EXTRA_NUMBER);
			mEt_number.setText(num);
			// 获取单选框的当前状态
			int typeExtra = intent.getIntExtra(EXTRA_TYPE, -1);
			switch (typeExtra) {
			case BlackInfo.TYPE_SMS:
				mCheckedId = R.id.cassfe_rb_sms;
				break;
			case BlackInfo.TYPE_NUMBER:
				mCheckedId = R.id.cassfe_rb_phone;
				break;
			case BlackInfo.TYPE_ALL:
				mCheckedId = R.id.cassfe_rb_all;
				break;
			default:
				break;
			}
			Log.d(TAG, mCheckedId + "设置单选框的当前状态");
			// 设置单选框的当前状态
			mRg.check(mCheckedId);
		} else {
			// 增加数据 , 不需要修改
		}

	}

	/**
	 * 
	 * 点击按钮时触发的事件
	 * 
	 */
	private class RgChang implements OnCheckedChangeListener {

		// checkedId : 的值是对应的xml文件的id的值
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			switch (checkedId) {
			case R.id.cassfe_rb_phone:
				mCheckedId = BlackInfo.TYPE_NUMBER;
				break;
			case R.id.cassfe_rb_sms:
				mCheckedId = BlackInfo.TYPE_SMS;
				break;
			case R.id.cassfe_rb_all:
				mCheckedId = BlackInfo.TYPE_ALL;
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 
	 * 点击保存执行的方法
	 */
	private class SaveListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			// 获取电话号码
			String number = mEt_number.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {

				// 添加抖动动画
				Animation shake = AnimationUtils.loadAnimation(
						BlacklistActivity.this, R.anim.shake);
				findViewById(R.id.cassfe_et_phenum).startAnimation(shake);
				Toast.makeText(BlacklistActivity.this, "电话号码不能为空...",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 校验类型是否为空
			if (mCheckedId == -1) {
				Toast.makeText(BlacklistActivity.this, "类型不能为空...",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// 判断当前是更新还是增加操作
			if (isUpdata) {
				// 执行的是更新
				// 将号码和类型返回 和第几个item

				// 将此数据在数据库进行更新
				if (mDao.updata(number, mCheckedId)) {

					Intent data = new Intent();
					data.putExtra(EXTRA_POSITION, position);
					data.putExtra(EXTRA_TYPE, mCheckedId);
					setResult(Activity.RESULT_OK, data);
				} else {

					// 添加抖动动画
					Animation shake = AnimationUtils.loadAnimation(
							BlacklistActivity.this, R.anim.shake);
					findViewById(R.id.cassfe_et_phenum).startAnimation(shake);

					Toast.makeText(getApplicationContext(), "更新失败",
							Toast.LENGTH_SHORT).show();
					return;
				}

			} else {

				if (mDao.add(number, mCheckedId)) {
					// 将号码和类型返回
					Intent data = new Intent();
					data.putExtra(EXTRA_NUMBER, number);
					data.putExtra(EXTRA_TYPE, mCheckedId);
					setResult(Activity.RESULT_OK, data);
				} else {

					// 添加抖动动画
					Animation shake = AnimationUtils.loadAnimation(
							BlacklistActivity.this, R.anim.shake);
					findViewById(R.id.cassfe_et_phenum).startAnimation(shake);

					Toast.makeText(getApplicationContext(), "已经存在该号码啦...换一个呗!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			// 结束自己
			finish();
		}
	}

	/**
	 * 
	 * 点击取消执行的方法
	 */
	private class CancelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 结束自己
			finish();
		}
	}
}
