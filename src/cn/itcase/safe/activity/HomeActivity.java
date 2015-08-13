package cn.itcase.safe.activity;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.domain.HomeItem;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;

public class HomeActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "HomeActivity";

	private final static String[] TITLES = new String[] { "手机防盗", "骚扰拦截",
			"软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
	private final static String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰",
			"管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };

	private final static int[] ICONS = new int[] { R.drawable.sjfd,
			R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
			R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj };

	private ImageView mIvLogo;
	private GridView mGridView;

	private List<HomeItem> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// View的初始化
		mIvLogo = (ImageView) findViewById(R.id.home_iv_logo);
		mGridView = (GridView) findViewById(R.id.home_gridview);

		// 让imageView做动画
		// mIvLogo.setRotationY(rotationY)
		ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLogo, "rotationY",
				0, 90, 270, 360);
		animator.setDuration(2000);
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setRepeatMode(ObjectAnimator.REVERSE);
		animator.start();

		// list数据的初始化
		mDatas = new ArrayList<HomeItem>();
		for (int i = 0; i < ICONS.length; i++) {
			HomeItem item = new HomeItem();
			item.iconId = ICONS[i];
			item.title = TITLES[i];
			item.desc = DESCS[i];
			// 添加
			mDatas.add(item);
		}

		// ListView
		// GridView
		// 给gridview设置adapter
		mGridView.setAdapter(new HomeAdatper());// adapter ---> List(集合)

		// 设置item的点击事件
		mGridView.setOnItemClickListener(this);
	}

	public void clickSetting(View view) {

		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);

	}

	private class HomeAdatper extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TextView tv = new TextView(HomeActivity.this);
			// tv.setText(item.title + "==" + item.desc);
			View view = View.inflate(HomeActivity.this, R.layout.item_home,
					null);

			ImageView ivIcon = (ImageView) view
					.findViewById(R.id.item_home_iv_icon);
			TextView tvTitle = (TextView) view
					.findViewById(R.id.item_home_tv_title);
			TextView tvDesc = (TextView) view
					.findViewById(R.id.item_home_tv_desc);

			HomeItem item = mDatas.get(position);

			ivIcon.setImageResource(item.iconId);
			tvTitle.setText(item.title);
			tvDesc.setText(item.desc);

			return view;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		switch (position) {
		case 0:
			// 手机防盗
			performSjfd();
			break;
		case 1:
			// 拦截骚扰
			performLjsr();
			break;
		case 2:
			// 软件管家
			performAppManager();
			break;
		case 3:
			// 进程管理
			performProcessManager();
			break;
		case 4:
			// 流量管理
			performTraffic();
			break;
		case 5:
			// 手机杀毒
			performAntiVirus();
			break;
		case 6:
			// 缓存清理
			performCacheClear();
			break;
		case 7:
			// 常用工具
			performCommonUtils();
			break;

		default:
			break;
		}
	}

	private void performAntiVirus() {
		// 手机杀毒
		Intent intent = new Intent(this, AntiVirusActivity.class);
		startActivity(intent);

	}

	private void performTraffic() {
		// 流量管理
		Intent intent = new Intent(this, TrafficActivity.class);
		startActivity(intent);
	}

	private void performCacheClear() {
		// 清理缓存
		Intent intent = new Intent(this, CacheClearActivity.class);
		startActivity(intent);

	}

	private void performProcessManager() {
		// 进程管理
		Intent intent = new Intent(this, ProcessManagerActivity.class);
		startActivity(intent);
	}

	private void performAppManager() {
		// 软件管家
		Intent intent = new Intent(this, AppManagerActivity.class);
		startActivity(intent);
	}

	private void performCommonUtils() {
		// 跳转到常用工具的界面
		Intent intent = new Intent(this, CommonUtilsActivity.class);
		startActivity(intent);

	}

	private void performLjsr() {
		// 跳转到拦截骚扰的界面
		Intent intent = new Intent(this, LjsrActivity.class);
		startActivity(intent);
	}

	private void performSjfd() {
		String pwd = PreferenceUtils.getString(this, IConstants.SJFD_PASSWORD);

		if (TextUtils.isEmpty(pwd)) {
			// 如果是第一次进入，弹出设置密码的对话框
			Log.d(TAG, "弹出设置密码的对话框");
			showInitPwdDialog();
		} else {
			// 否则，输入密码的对话框
			Log.d(TAG, "弹出输入密码的对话框");
			showEnterPwdDialog();
		}
	}

	private void showEnterPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_pwd_enter, null);
		final EditText etPwd = (EditText) view.findViewById(R.id.dialog_tv_pwd);
		Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

		builder.setView(view);

		final AlertDialog dialog = builder.create();

		// 设置点击事件
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 非空判断
				String pwd = etPwd.getText().toString().trim();

				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					etPwd.requestFocus();// 获取焦点
					return;
				}

				// 校验密码是否正确
				String local = PreferenceUtils.getString(HomeActivity.this,
						IConstants.SJFD_PASSWORD);

				if (local.equals(pwd)) {
					// 正确--》 进入手机防盗界面
					enterSjfd();

					dialog.dismiss();
				} else {
					Toast.makeText(HomeActivity.this, "密码不正确",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void showInitPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_pwd_init, null);
		final EditText etPwd = (EditText) view.findViewById(R.id.dialog_tv_pwd);
		final EditText etConfirm = (EditText) view
				.findViewById(R.id.dialog_tv_confirm);
		Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

		builder.setView(view);

		final AlertDialog dialog = builder.create();

		// 设置点击事件
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 非空的校验
				String pwd = etPwd.getText().toString().trim();

				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					etPwd.requestFocus();// 获取焦点
					return;
				}

				String confirm = etConfirm.getText().toString().trim();

				if (TextUtils.isEmpty(confirm)) {
					Toast.makeText(HomeActivity.this, "确认密码不能为空",
							Toast.LENGTH_SHORT).show();
					etConfirm.requestFocus();// 获取焦点
					return;
				}

				// 判断两次是否相同
				if (!pwd.equals(confirm)) {
					Toast.makeText(HomeActivity.this, "两次密码不一致",
							Toast.LENGTH_SHORT).show();
					return;
				}

				// 保存密码
				PreferenceUtils.putString(HomeActivity.this,
						IConstants.SJFD_PASSWORD, pwd);

				enterSjfd();

				// 关闭dialog
				dialog.dismiss();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭dialog
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void enterSjfd() {

		boolean flag = PreferenceUtils.getBoolean(this, IConstants.SJFD_SETUP);
		// 1. 引导页面
		// 2. 最终页面
		if (flag) {
			// 如果用户开启了 防盗保护 --》 最终页面
			Intent intent = new Intent(this, LostSetupFindActivity.class);
			startActivity(intent);
		} else {
			// 否则用户进入引导页面
			// 存储已经初始化的值
			PreferenceUtils.putBoolean(HomeActivity.this,
					IConstants.SJFD_SETUP, true);

			Intent intent = new Intent(this, LostSetup1Activity.class);
			startActivity(intent);
		}

	}
}
