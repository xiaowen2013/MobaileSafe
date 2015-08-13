package cn.itcase.safe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.itcase.safe.domain.AppManagerInfo;
import cn.itcase.safe.utils.AppInfoProvider;
import cn.itcase.safe.view.MyProgress;

public class AppManagerActivity extends Activity {

	protected static final String TAG = "AppManagerActivity";
	private MyProgress mRom;
	private MyProgress mSd;
	private StickyListHeadersListView mListView;
	private List<AppManagerInfo> mList;
	private List<AppManagerInfo> mUser;
	private List<AppManagerInfo> mSystem;
	private LinearLayout mLlLoading;
	private PopupWindow mWindow;
	private AppManagerAdapter mAdapter;

	private BroadcastReceiver mBroadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收卸载的广播
			String dataString = intent.getDataString();
			Log.d(TAG, "卸载程序啦...");
			// 获取卸载的包名用于更新UI
			dataString = dataString.replace("package:", "");
			// UI 更新
			// 获取ArrayList 的一个迭代器
			ListIterator<AppManagerInfo> iterator = mUser.listIterator();
			while (iterator.hasNext()) {
				AppManagerInfo next = iterator.next();
				if (next.appPackage.equals(dataString)) {
					// 找到了卸载的程序 , 将它从list删除
					iterator.remove();
					break;
				}
			}
			// UI变化
			mAdapter.notifyDataSetChanged();
		}
	};

	// protected void onDestroy() {
	//
	// // 注销广播
	// unregisterReceiver(mBroadReceiver);
	// // 结束自己
	// finish();
	// };

	@Override
	protected void onDestroy() {

		super.onDestroy();
		// 注销广播
		unregisterReceiver(mBroadReceiver);
		// // 结束自己
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanager);
		mRom = (MyProgress) findViewById(R.id.appmanager_rom);
		mSd = (MyProgress) findViewById(R.id.appmanager_sd);
		mListView = (StickyListHeadersListView) findViewById(R.id.appmanager_lv);
		mLlLoading = (LinearLayout) findViewById(R.id.app_loading);

		// 注册收听卸载app的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addDataScheme("package");
		registerReceiver(mBroadReceiver, intentFilter);

		// 获取内部存储的值
		File dataDirectory = Environment.getDataDirectory();
		long freeSpace = dataDirectory.getFreeSpace(); // 剩余的空间
		long totalSpace = dataDirectory.getTotalSpace(); // 总空间
		long romUsedSpace = totalSpace - freeSpace; // 已使用的

		mRom.setTitle("内存:");
		mRom.setAble(Formatter.formatFileSize(this, romUsedSpace) + "已用");
		mRom.setEnable(Formatter.formatFileSize(this, freeSpace) + "剩余");
		int progress = (int) ((romUsedSpace * 100f) / (totalSpace + 0.5));
		mRom.setProgressBar(progress);

		// 获取外部sd卡的值
		File sdDirectory = Environment.getExternalStorageDirectory();
		long sdTotal = sdDirectory.getTotalSpace(); // 总大小
		long sdFreeSpace = sdDirectory.getFreeSpace(); // 剩余的空间
		int sdProgress = (int) (((sdTotal - sdFreeSpace) * 100f) / (sdTotal + 0.5));

		mSd.setTitle("sd卡:");
		mSd.setAble(Formatter.formatFileSize(this, sdTotal - sdFreeSpace)
				+ "已用");
		mSd.setEnable(Formatter.formatFileSize(this, sdFreeSpace) + "可用");
		mSd.setProgressBar(sdProgress);

		mList = new ArrayList<AppManagerInfo>();

		// 数据的加载 , 耗时的操作
		mLlLoading.setVisibility(View.VISIBLE);
		new Thread() {

			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mList = AppInfoProvider.getAllApps(getApplicationContext());
				mUser = new ArrayList<AppManagerInfo>();
				mSystem = new ArrayList<AppManagerInfo>();

				for (AppManagerInfo info : mList) {
					if (info.isSystem) {
						// 系统的程序
						mSystem.add(info);
					} else {
						// 第三方的程序
						mUser.add(info);
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// 关闭加载进度条
							mLlLoading.setVisibility(View.GONE);
							if (mAdapter == null) {
								mAdapter = new AppManagerAdapter();
							} else {
								mAdapter.notifyDataSetChanged();
							}
							mListView.setAdapter(mAdapter);
						}
					});
				}
			};
		}.start();

		// 设置item 的点击事件
		mListView.setOnItemClickListener(new AppOnTouthListener());
	}

	private class AppOnTouthListener implements OnItemClickListener {

		private View contentView;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 点击了当前的 position
			// Toast.makeText(getApplicationContext(),
			// "你点击了" + mList.get(position + 1).appName + "程序..",
			// Toast.LENGTH_SHORT).show();
			if (mWindow == null) {
				contentView = View.inflate(getApplicationContext(),
						R.layout.poput_item_app, null);

				// 展示View 的宽度和高度
				int width = LayoutParams.MATCH_PARENT;
				int height = LayoutParams.WRAP_CONTENT;

				// 获得 Window 对象
				mWindow = new PopupWindow(contentView, width, height);
				// 设置获取焦点
				mWindow.setFocusable(true);

				// 设置其他地方可点
				mWindow.setOutsideTouchable(true);
				// 设置背景动画
				mWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));

				// 设置动画
				mWindow.setAnimationStyle(R.style.MyPopAnimation);
			}
			// 获取当前的app 信息 , 是哪个
			final AppManagerInfo appInfo = mList.get(position);

			// 设置点击事件
			contentView.findViewById(R.id.app_item_pro_uninstall)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 点击了卸载
							// <action
							// android:name="android.intent.action.DELETE"
							// />
							// <category
							// android:name="android.intent.category.DEFAULT"
							// />
							// <data android:scheme="package" />
							Intent intent = new Intent();
							intent.setAction("android.intent.action.DELETE");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setData(Uri.parse("package:"
									+ appInfo.appName));
							// 关闭这个
							mWindow.dismiss();
						}
					});
			contentView.findViewById(R.id.app_item_pro_open)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 点击了打开

							// 获取包管理器
							PackageManager packageManager = getPackageManager();
							Intent intent = packageManager
									.getLaunchIntentForPackage(appInfo.appPackage);
							if (intent != null) {
								startActivity(intent);
							}
							// 关闭这个
							mWindow.dismiss();
						}
					});
			contentView.findViewById(R.id.app_item_pro_share)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 点击了分享
							/*
							 * <action android:name=
							 * "android.settings.APPLICATION_DETAILS_SETTINGS"
							 * /> <category
							 * android:name="android.intent.category.DEFAULT" />
							 * <data android:scheme="package" />
							 */

							// No Activity found to handle Intent {
							// act=android.content.Intent.ACTION_VIEW
							// cat=[android.intent.category.DEFAULT]
							// typ=vnd.android-dir/mms-sms
							// (has extras) }

							// Intent intent = new Intent();
							// intent.setAction("android.content.Intent.ACTION_VIEW");
							// intent.addCategory("android.intent.category.DEFAULT");
							// intent.setType("vnd.android-dir/mms-sms");
							// intent.putExtra("XXXX", "YYYYY");
							// startActivity(intent);
							// TODO:分享的意图还没有 , 到时候在完善
							// 关闭这个
							mWindow.dismiss();
						}
					});
			contentView.findViewById(R.id.app_item_pro_appinfo)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 点击了信息
							/*
							 * <action android:name=
							 * "android.settings.APPLICATION_DETAILS_SETTINGS"
							 * /> <category
							 * android:name="android.intent.category.DEFAULT" />
							 * <data android:scheme="package" />
							 */
							Intent intent = new Intent();
							intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setData(Uri.parse("package:"
									+ appInfo.appPackage));
							startActivity(intent);
							// 关闭这个
							mWindow.dismiss();
						}
					});

			// 设置各个图片的点击事件
			// 在anchor控件的下方显示
			// window.showAsDropDown(view);
			// 在点击的item下方显示
			mWindow.showAsDropDown(view, 100, 0);
		}
	}

	class ViewHolder {
		ImageView ivIcon;
		TextView tvAppName;
		TextView tvAppRom;
		TextView tvAppSize;
	}

	/**
	 * 
	 * AppManagerAdapter 的适配器
	 */
	private class AppManagerAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {

			if (mList != null) {
				return mList.size();
			}
			return 0;
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
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_appmanager, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_app_iv_icon);
				holder.tvAppName = (TextView) convertView
						.findViewById(R.id.item_app_tv_appname);
				holder.tvAppRom = (TextView) convertView
						.findViewById(R.id.item_app_tv_memory);
				holder.tvAppSize = (TextView) convertView
						.findViewById(R.id.item_app_tv_appsize);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppManagerInfo info = mList.get(position);
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvAppName.setText(info.appName);
			holder.tvAppSize.setText(Formatter.formatFileSize(
					getApplicationContext(), info.appSize));
			if (info.isInstallSD) {
				holder.tvAppRom.setText("手机SD卡安装");
			} else {
				holder.tvAppRom.setText("手机内存");
			}
			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {

			TextView tv = null;
			if (convertView == null) {
				convertView = new TextView(getApplicationContext());
				tv = (TextView) convertView;
				tv.setBackgroundColor(Color.parseColor("#66000000"));
				tv.setPadding(8, 8, 9, 9);
				tv.setTextColor(Color.parseColor("#ffffff"));
				tv.setTextSize(17);
			} else {
				tv = (TextView) convertView;
			}
			boolean isSystem = mList.get(position).isSystem;
			tv.setText(isSystem ? "系统程序(" + mSystem.size() + ")个" : "用户程序("
					+ mUser.size() + ")个");
			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			// 如果是系统的程序 , 就返回0
			// 主要是用于控制父类的条目
			return mList.get(position).isSystem ? 0 : 1;
		}
	}
}
