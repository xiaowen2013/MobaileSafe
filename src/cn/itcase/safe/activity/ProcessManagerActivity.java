package cn.itcase.safe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebIconDatabase.IconListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.domain.ProcessInfo;
import cn.itcase.safe.service.AutoClearService;
import cn.itcase.safe.utils.IConstants;
import cn.itcase.safe.utils.PreferenceUtils;
import cn.itcase.safe.utils.ProcessInfoProvide;
import cn.itcase.safe.utils.ServiceStartUitls;
import cn.itcase.safe.view.MyProgress;
import cn.itcase.safe.view.SettingItemView;

public class ProcessManagerActivity extends Activity {

	private static final String TAG = "ProcessManagerActivity";
	private MyProgress mProcessCount;
	private MyProgress mMomory;
	private StickyListHeadersListView mListView;
	private LinearLayout mProLoading;
	private List<ProcessInfo> mProData;
	private List<ProcessInfo> mUser;
	private ProManAdapter mProManAdapter;
	private int allRunCount;
	private int totalPssCount;
	private long totalMomory;
	private long freeMomory;
	private ImageView mArr1;
	private ImageView mArr2;
	private SlidingDrawer mDrawer;
	private SettingItemView mShowSystemPro;
	private SettingItemView mAutoClear;
	private boolean isDisplayAll;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processmanager);

		mProcessCount = (MyProgress) findViewById(R.id.processmanager_count);
		mMomory = (MyProgress) findViewById(R.id.processmanager_memory);
		mListView = (StickyListHeadersListView) findViewById(R.id.proMan_lv);
		mProLoading = (LinearLayout) findViewById(R.id.proMan_loading);
		mArr1 = (ImageView) findViewById(R.id.pro_drawer_arr1);
		mArr2 = (ImageView) findViewById(R.id.pro_drawer_arr2);
		mShowSystemPro = (SettingItemView) findViewById(R.id.pro_siv_systPro);
		mAutoClear = (SettingItemView) findViewById(R.id.pro_siv_autoclear);

		// 设置显示系统进程
		isDisplayAll = PreferenceUtils.getBoolean(getBaseContext(),
				IConstants.SHOW_SYSTEM_PROCESS, true);

		mShowSystemPro.setToggleOn(isDisplayAll);

		allRunCount = ProcessInfoProvide
				.getAllRunningProcessCount(getApplicationContext());
		totalPssCount = ProcessInfoProvide
				.getTotalPssCount(getApplicationContext());

		//
		mMomory.setTitle("内存:");
		totalMomory = ProcessInfoProvide
				.getTotalMomory(getApplicationContext());
		freeMomory = ProcessInfoProvide.getFreeMomory(getApplicationContext());

		// 设置信息
		setProcessTitleUI();
		setRunMomoryUI();

		// 设置动画
		showUpAnamation();

		// 设置点击开启或者关闭显示系统进程的监听事件
		mShowSystemPro.setOnClickListener(new ShowSystemPro());
		// 设置点击开启或者关闭自动清理的监听事件
		mAutoClear.setOnClickListener(new OpenAutoClearPro());

		// 开始加载进度条
		mProLoading.setVisibility(View.VISIBLE);

		// 开启线程去获取数据
		openThreadGetData();

		/*
		 * 打开Drawer的时候执行
		 */
		mDrawer = (SlidingDrawer) findViewById(R.id.pro_drawer);

		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				showAnamation();
			}
		});

		// 关闭Drawer时执行
		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				showUpAnamation();
			}
		});
	}

	/**
	 * 开启线程查询数据
	 */
	private void openThreadGetData() {
		// 开启线程查询数据
		new Thread() {
			public void run() {
				// 睡一会
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 去获取数据
				mProData = ProcessInfoProvide
						.getAllRunningProcess(getApplicationContext());

				// 将数据分为用户的和系统的
				mUser = new ArrayList<ProcessInfo>();

				for (ProcessInfo info : mProData) {
					if (!info.isSystem) {
						mUser.add(info);
					}
				}

				// 设置list的适配器

				if (mProManAdapter == null) {
					mProManAdapter = new ProManAdapter();
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 停止加载进度条
						mProLoading.setVisibility(View.GONE);

						mListView.setAdapter(mProManAdapter);

					}
				});
			};
		}.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 初始化服务
		boolean isRunning = ServiceStartUitls.isRunning(
				ProcessManagerActivity.this, AutoClearService.class);
		// UI变化
		mAutoClear.setToggleOn(isRunning);
		openThreadGetData();
	}

	/**
	 * 
	 * 点击打开或者关闭锁屏自动清理进程
	 */
	private class OpenAutoClearPro implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 点击了
			// 要开启一个 服务 或者关闭一个服务
			// 获取当前的状态
			boolean isRunning = ServiceStartUitls.isRunning(
					ProcessManagerActivity.this, AutoClearService.class);
			if (isRunning) {
				// 正在运行 . 需要关闭它
				stopService(new Intent(getApplicationContext(),
						AutoClearService.class));
			} else {
				// 没有运行 , 需要开启
				startService(new Intent(getApplicationContext(),
						AutoClearService.class));
			}
			// 更改UI
			mAutoClear.setToggleOn(!isRunning);
		}
	}

	/**
	 * 点击打开或者关闭显示系统的进程
	 * 
	 */
	private class ShowSystemPro implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 获取之前的状态
			isDisplayAll = PreferenceUtils.getBoolean(getApplicationContext(),
					IConstants.SHOW_SYSTEM_PROCESS, true);
			// 点击了让你取反 , 然后设置状态
			isDisplayAll = !isDisplayAll;
			mShowSystemPro.setToggleOn(isDisplayAll);

			// 更新
			mProManAdapter.notifyDataSetChanged();

			// 存储当前状态
			PreferenceUtils.putBoolean(getApplicationContext(),
					IConstants.SHOW_SYSTEM_PROCESS, isDisplayAll);
		}
	}

	public void showAnamation() {

		// 先关闭当前动画
		mArr1.clearAnimation();
		mArr2.clearAnimation();

		// 再设置当前的资源 , 打开的时候箭头向下 , 然后再重新设置开始动画
		mArr1.setImageResource(R.drawable.drawer_arrow_down);
		mArr2.setImageResource(R.drawable.drawer_arrow_down);
	}

	public void showUpAnamation() {
		// 初始化为向上的箭头
		mArr1.setImageResource(R.drawable.drawer_arrow_up);
		mArr2.setImageResource(R.drawable.drawer_arrow_up);

		// 设置动画
		AlphaAnimation al = new AlphaAnimation(0.2f, 1f);
		al.setDuration(600);
		al.setRepeatCount(AlphaAnimation.INFINITE);
		al.setRepeatMode(AlphaAnimation.REVERSE);
		mArr1.setAnimation(al);

		AlphaAnimation a2 = new AlphaAnimation(1f, 0.2f);
		a2.setDuration(600);
		a2.setRepeatCount(AlphaAnimation.INFINITE);
		a2.setRepeatMode(AlphaAnimation.REVERSE);
		mArr2.setAnimation(a2);
	}

	private void setRunMomoryUI() {
		// 占用内存
		mMomory.setAble("占用内存:"
				+ Formatter.formatFileSize(getApplicationContext(),
						(totalMomory - freeMomory)));
		// 可用内存
		mMomory.setEnable("可用内存:" + Formatter.formatFileSize(this, freeMomory));
		// 设置进度 , 占用的进度 除以总的进度
		mMomory.setProgressBar((int) (((totalMomory - freeMomory) * 100f) / totalMomory));
	}

	private void setProcessTitleUI() {
		mProcessCount.setTitle("进程数:");
		mProcessCount.setAble("正在运行" + allRunCount + "个");
		mProcessCount.setEnable("所有进程" + totalPssCount + "个");
		// 设置进度
		mProcessCount
				.setProgressBar((int) (allRunCount * 100f) / totalPssCount);
	}

	private class ViewHolder {
		ImageView proIcon;
		TextView proAppName;
		TextView proMomory;
		CheckBox proCb;
	}

	/**
	 * 点击全选
	 * 
	 * @param view
	 */
	public void clickAllCb(View view) {

		if (mProData == null) {
			return;
		}

		for (ProcessInfo info : mProData) {

			Log.d(TAG, mProData.size() + " 总的大小 info.packageName"
					+ info.packageName);
			if (getPackageName().equals(info.packageName)) {
				// 如果这个程序的包名等于我的程序的包名, 则不选择
				continue;
			}
			info.isClick = true;
		}
		// UI 更新
		mProManAdapter.notifyDataSetChanged();
	}

	/**
	 * 点击取反
	 * 
	 * @param view
	 */
	public void clickReverse(View view) {
		if (mProData == null) {
			return;
		}

		for (ProcessInfo info : mProData) {
			if (getPackageName().equals(info.packageName)) {
				// 如果这个程序的包名等于我的程序的包名, 则不选择
				continue;
			}
			info.isClick = !info.isClick;
		}
		// UI 更新
		mProManAdapter.notifyDataSetChanged();
	}

	/**
	 * 清理缓存
	 */
	public void cleanAllCilck(View view) {

		// 点击了清理
		if (isDisplayAll) {
			if (mProData == null) {
				return;
			}
		} else {
			if (mUser == null) {
				return;
			}
		}
		Log.d(TAG, "点击了清理..");
		ListIterator<ProcessInfo> listIterator = null;
		if (isDisplayAll) {
			listIterator = mProData.listIterator();
		} else {
			listIterator = mUser.listIterator();
		}
		int cleanMomory = 0;
		int cleanCount = 0;
		ProcessInfo info = null;
		while (listIterator.hasNext()) {
			info = listIterator.next();
			// 如果选中
			if (info.isClick) {
				// 如果是选中的是这个程序 ,就不执行了
				if (getPackageName().equals(info.packageName)) {
					continue;
				}

				// 杀死进程
				ProcessInfoProvide.killProcess(ProcessManagerActivity.this,
						info.packageName);
				cleanCount++;
				cleanMomory += info.momory;
				// 将这个进程移除
				listIterator.remove();
			}
		}

		if (cleanCount == 0) {
			// 一次也没有选中
			return;
		}

		Toast.makeText(
				getApplicationContext(),
				"共清理了"
						+ cleanCount
						+ "个进程,释放"
						+ Formatter.formatFileSize(getApplicationContext(),
								cleanMomory) + "资源", Toast.LENGTH_SHORT).show();
		allRunCount = allRunCount - cleanCount;
		setProcessTitleUI();
		freeMomory = freeMomory + cleanMomory;
		setRunMomoryUI();
		// 杀死完成 , 更新页面
		mProManAdapter.notifyDataSetChanged();
	}

	private class ProManAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {

			if (isDisplayAll) {
				// 显示全部
				if (mProData != null) {
					// Log.d(TAG, mProData.size() + "===");
					return mProData.size();
				}
			} else {
				if (mUser != null) {
					return mUser.size();
				}
			}

			return 0;
		}

		@Override
		public Object getItem(int position) {

			if (isDisplayAll) {
				if (mProData != null) {
					return mProData.get(position);
				}
			} else {
				if (mUser != null) {
					return mUser.get(position);
				}
			}

			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_processmanager, null);
				holder = new ViewHolder();
				holder.proIcon = (ImageView) convertView
						.findViewById(R.id.item_promam_iv_icon);
				holder.proAppName = (TextView) convertView
						.findViewById(R.id.item_proman_tv_appname);
				holder.proMomory = (TextView) convertView
						.findViewById(R.id.item_proman_tv_momory);
				holder.proCb = (CheckBox) convertView
						.findViewById(R.id.item_promam_cb_click);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 设置数据

			ProcessInfo processInfo = null;
			if (isDisplayAll) {
				processInfo = mProData.get(position);
			} else {
				processInfo = mUser.get(position);
			}
			holder.proIcon.setImageDrawable(processInfo.icon);
			holder.proAppName.setText(processInfo.proName);
			holder.proMomory.setText("占用内存:"
					+ Formatter.formatFileSize(getApplicationContext(),
							processInfo.momory));

			// if (!processInfo.packageName.equals(getPackageName())) {
			if (!getPackageName().equals(processInfo.packageName)) {
				// 如果这个程序的名和我的包名不相等 , 我才选中
				holder.proCb.setChecked(processInfo.isClick);
				// 设置可见
				holder.proCb.setVisibility(View.VISIBLE);
			} else {
				// 设置不可见
				holder.proCb.setChecked(false);
				holder.proCb.setVisibility(View.GONE);
			}

			final ProcessInfo newProInfo = processInfo;
			// 设置item 的点击事件
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Log.d(TAG, "你点击了:" + position);
					if (!getPackageName().equals(newProInfo.packageName)) {
						// 如果这个程序的名和我的包名不相等 , 我才选中
						newProInfo.isClick = !newProInfo.isClick;
						Log.d(TAG, newProInfo.isClick + "===");
						mProManAdapter.notifyDataSetChanged();
					}
				}
			});

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {

			TextView tv = null;
			if (convertView == null) {
				convertView = new TextView(getApplicationContext());
				tv = (TextView) convertView;
				tv.setBackgroundColor(Color.parseColor("#aa000000"));
				tv.setPadding(8, 8, 8, 8);
				tv.setTextColor(Color.parseColor("#ffffff"));
				tv.setTextSize(17);
			} else {
				tv = (TextView) convertView;
			}
			boolean isSystem = mProData.get(position).isSystem;
			if (isSystem) {
				// 系统进程
			} else {
				// 用户进程
			}
			tv.setText(isSystem ? "系统进程" : "用户程序");
			return convertView;
		}

		@Override
		public long getHeaderId(int position) {

			if (isDisplayAll) {
				return mProData.get(position).isSystem ? 0 : 1;
			} else {
				return 0;
			}

		}
	}

}
