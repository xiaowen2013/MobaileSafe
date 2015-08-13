package cn.itcase.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.itcase.safe.service.CallSmsSafeService;
import cn.itcase.safe.service.DisplyBelongService;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;
import cn.itcase.safe.utils.ServiceStartUitls;
import cn.itcase.safe.view.DialogAddress;
import cn.itcase.safe.view.SettingItemView;

//设置页面
public class SettingActivity extends Activity {

	protected static final String TAG = "SettingActivity";
	private SettingItemView mSivAutoUpdate;
	private SettingItemView mCallSmsSafeToggle;
	private SettingItemView mBelong; // 显示归属地
	private SettingItemView mBelongStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_update);
		mCallSmsSafeToggle = (SettingItemView) findViewById(R.id.setting_siv_callSmsSafe);

		mBelong = (SettingItemView) findViewById(R.id.setting_siv_viewbelong);
		mBelongStyle = (SettingItemView) findViewById(R.id.setting_siv_viewstyle);

		/**
		 * 显示归属地的监听事件
		 */
		mBelong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击了
				// 判断当前是开启还是关闭
				// UI 的改变
				// 判断服务是否开启
				if (ServiceStartUitls.isRunning(getApplicationContext(),
						DisplyBelongService.class)) {
					// 正在运行
					// 点击了 , 说明是要关闭服务TODO
					Intent intent = new Intent(SettingActivity.this,
							DisplyBelongService.class);
					stopService(intent);
					mBelong.setToggleOn(false);
				} else {
					// 没有运行 , 说明要开启服务
					Intent intent = new Intent(getApplicationContext(),
							DisplyBelongService.class);
					startService(intent);
					// UI 需要改变
					mBelong.setToggleOn(true);
				}
			}
		});

		mSivAutoUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果是打开，就关闭，如果是关闭就打开
				// if (mSivAutoUpdate.isToggleOn()) {
				// mSivAutoUpdate.setToggleOn(false);
				// } else {
				// mSivAutoUpdate.setToggleOn(true);
				// }
				mSivAutoUpdate.toggle();

				// 存储状态信息
				boolean toggleOn = mSivAutoUpdate.isToggleOn();

				// SharedPreferences sp = getSharedPreferences("config",
				// Context.MODE_PRIVATE);
				// Editor edit = sp.edit();
				// edit.putBoolean("setting_update", toggleOn);
				// edit.commit();

				PreferenceUtils.putBoolean(SettingActivity.this,
						IConstants.AUTO_UPDATE, toggleOn);
			}
		});

		// 设置开启的监听事件
		mCallSmsSafeToggle.setOnClickListener(new CallSmsSafe());

		// 设置样式的监听
		mBelongStyle.setOnClickListener(new DialogStyle());
	}

	private String[] titles = new String[] { "半透明", "卫士蓝", "活力橙", "金属灰", "苹果绿" };
	private int[] icons = new int[] { R.drawable.toast_address_normal,
			R.drawable.toast_address_orange, R.drawable.toast_address_blue,
			R.drawable.toast_address_gray, R.drawable.toast_address_green };

	private class DialogStyle implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 弹出Dialog
			Log.d(TAG, "点击了样式..");
			final DialogAddress da = new DialogAddress(SettingActivity.this);
			da.setAdapter(new DialogAdapter());
			// 设置item的点击事件
			da.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// 点击了这个条目 ,关闭dialog
					da.dismiss();
					PreferenceUtils.putInt(getApplicationContext(),
							IConstants.ADDRESS_STYLE, icons[position]);

				}
			});

			da.show();
		}
	}

	private class DialogAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			Log.d(TAG, "走了适配器这里..getCount");
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "走了适配器这里..");
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_dialog, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.dialog_iv_icon);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.dialog_tv_title);
				holder.isSelected = (ImageView) convertView
						.findViewById(R.id.dialog_iv_isselect);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.icon.setImageResource(icons[position]);
			holder.tvTitle.setText(titles[position]);
			int style = PreferenceUtils.getInt(getApplicationContext(),
					IConstants.ADDRESS_STYLE, R.drawable.toast_address_normal);
			if (style == icons[position]) {
				holder.isSelected.setVisibility(View.VISIBLE);
			} else {
				holder.isSelected.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	class ViewHolder {
		ImageView icon;
		ImageView isSelected;
		TextView tvTitle;
	}

	@Override
	protected void onStart() {

		super.onStart();
		// 可见的时候进行初始化数据

		// 设置当前更新的状态
		boolean update = PreferenceUtils.getBoolean(this,
				IConstants.AUTO_UPDATE, true);
		mSivAutoUpdate.setToggleOn(update);

		// 手机拦截服务
		if (ServiceStartUitls.isRunning(getApplicationContext(),
				CallSmsSafeService.class)) {
			// 服务开启中
			mCallSmsSafeToggle.setToggleOn(true);
		} else {
			mCallSmsSafeToggle.setToggleOn(false);
		}

		// 是否开启归属地显示
		if (ServiceStartUitls.isRunning(getApplicationContext(),
				DisplyBelongService.class)) {
			// 服务开启中
			mBelong.setToggleOn(true);
		} else {
			mBelong.setToggleOn(false);
		}

	}

	private class CallSmsSafe implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 点击了
			// 判断当前是开启还是关闭
			// UI 的改变
			// 判断服务是否开启
			if (ServiceStartUitls.isRunning(getApplicationContext(),
					CallSmsSafeService.class)) {
				// 正在运行
				// 点击了 , 说明是要关闭服务TODO
				Intent intent = new Intent(getApplicationContext(),
						CallSmsSafeService.class);
				stopService(intent);
				mCallSmsSafeToggle.setToggleOn(false);
			} else {
				// 没有运行 , 说明要开启服务
				Intent intent = new Intent(getApplicationContext(),
						CallSmsSafeService.class);
				startService(intent);
				// UI 需要改变
				mCallSmsSafeToggle.setToggleOn(true);
			}
		}
	}

}
