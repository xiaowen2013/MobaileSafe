package cn.itcase.safe.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.domain.CacheInfo;

public class CacheClearActivity extends Activity {

	private static final String TAG = "CacheClearActivity";
	private ImageView mIvIcon;
	private ImageView mIvLine;
	private TextView mTvName;
	private TextView mTvCacheSize;
	private ListView mListView;
	private ProgressBar mPro;
	private List<CacheInfo> mData;
	private CacheTask mTask;
	private PackageManager mPm;
	private CacheAdapter mAdapter;
	private LinearLayout mLl;
	private RelativeLayout mRl;
	private Button mBtn;
	private TextView mTvScan;
	private long mCache; // 缓存的大小
	private int mCacheCount; // 缓存数
	private Button mBtnClearCache;
	private boolean isRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cacheclear);

		mIvIcon = (ImageView) findViewById(R.id.cache_iv_icon);
		mIvLine = (ImageView) findViewById(R.id.cache_iv_icon_line);
		mTvName = (TextView) findViewById(R.id.cache_tv_name);
		mTvCacheSize = (TextView) findViewById(R.id.cache_tv_momory);
		mListView = (ListView) findViewById(R.id.cache_lv);
		mPro = (ProgressBar) findViewById(R.id.cache_pro);
		mLl = (LinearLayout) findViewById(R.id.cache_ll);
		mRl = (RelativeLayout) findViewById(R.id.cache_rl);
		mBtn = (Button) findViewById(R.id.cache_btn_scan);
		mTvScan = (TextView) findViewById(R.id.cache_tv_scan_desc);
		mBtnClearCache = (Button) findViewById(R.id.cacheClear_btn);

		mBtnClearCache.setOnClickListener(new ClearCache()); // 设置快速清理的监听事件
		mPm = getPackageManager();

		mData = new ArrayList<CacheInfo>();
		startScan();
		mBtn.setOnClickListener(new BtnOnListenter());
	}

	private void startScan() {
		// 创建CacheTask对象
		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
		mTask = new CacheTask();
		mTask.execute();
	}

	// 在开始的时候进行扫描
	// @Override
	// protected void onStart() {
	// super.onStart();
	//
	// }

	// 在不可见的时候停止扫描
	@Override
	protected void onPause() {
		super.onPause();

		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
	}

	private class BtnOnListenter implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 点击了快速扫描 ,清空当前的数据

			if (mTask != null) {
				mTask.stop();
				mTask = null;
			}
			mTask = new CacheTask();
			mTask.execute();

		}
	}

	private final class CacheTask extends AsyncTask<Void, CacheInfo, Void> {

		private int mMax;
		private int mProcess = 1;
		private boolean isFinsh;

		// 开始时调用
		@Override
		protected void onPreExecute() {

			if (isFinsh) {
				return;
			}
			mRl.setVisibility(View.VISIBLE);
			mLl.setVisibility(View.GONE);

			mCache = 0;
			mCacheCount = 0;
			mData.clear();
			isRunning = true; // 设置不让一键清理点击

			// 设置动画
			TranslateAnimation ta = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 1);
			ta.setDuration(800);
			ta.setRepeatMode(Animation.REVERSE);
			ta.setRepeatCount(Animation.INFINITE);
			mIvLine.setAnimation(ta);
			ta.start();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// 读取所有数据
			List<PackageInfo> packages = mPm.getInstalledPackages(0);
			// 进度条的总数
			mMax = packages.size();
			// 遍历数据
			for (PackageInfo packageInfo : packages) {
				// 调用方法 获得每个包的缓存数据
				if (isFinsh) {
					break;
				}
				try {
					// 这里用的是反射的技术
					Method method = mPm.getClass().getMethod(
							"getPackageSizeInfo", String.class,
							IPackageStatsObserver.class);
					// 这个方法会自动调用 IPackageStatsObserver.Stub mStatsObserver
					method.invoke(mPm, packageInfo.packageName, mStatsObserver);

				} catch (Exception e) {
					e.printStackTrace();
				}
				// 睡一会
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 当前进度加一
				mProcess++;
			}
			return null;
		}

		// 更新进度
		public void upDataProcess(CacheInfo cacheInfo) {
			// 这里推出的数据 , 会在结果中得到接收
			publishProgress(cacheInfo);
		}

		public void stop() {
			isFinsh = true;
		}

		@Override
		protected void onProgressUpdate(CacheInfo... values) {

			super.onProgressUpdate(values);

			if (isFinsh) {
				return;
			}
			// 上面的数据加载完成 ,在这里设置数据信息
			// 设置图标
			mIvIcon.setImageDrawable(values[0].icon);
			// 应用名称
			mTvName.setText(values[0].name);
			// 进度条
			mPro.setMax(mMax);
			mPro.setProgress(mProcess);
			// 缓存数据 cacheSize
			mTvCacheSize.setText(""
					+ Formatter.formatFileSize(getApplicationContext(),
							values[0].cacheSize));

			if (mProcess == 1) {
				// 设置适配器
				mAdapter = new CacheAdapter();
				mListView.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
			// 滚动到底部
			mListView.smoothScrollToPosition(mAdapter.getCount());
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// 滚动到顶部
			mListView.smoothScrollToPosition(0);
			mTvScan.setText("总共有缓存:" + mCacheCount + "个,共"
					+ Formatter.formatFileSize(getApplicationContext(), mCache));

			mRl.setVisibility(View.GONE);
			mLl.setVisibility(View.VISIBLE);
			isRunning = false;
		}

	}

	final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
		public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {

			// 需要权限 get pack size
			Long cacheSize = stats.cacheSize;
			String name = stats.packageName;
			CacheInfo info = new CacheInfo();
			ApplicationInfo applicationInfo;
			try {
				// 获取应用的详细信息
				applicationInfo = mPm.getApplicationInfo(name, 0);
				// 获取图标
				info.icon = applicationInfo.loadIcon(mPm);
				// 获取包名
				info.packageName = applicationInfo.packageName;
				// 获取名称
				info.name = applicationInfo.loadLabel(mPm).toString();
				info.cacheSize = cacheSize;
				Log.d(TAG,
						"当前程序: "
								+ info.name
								+ "对应的缓存:"
								+ Formatter
										.formatFileSize(
												getApplicationContext(),
												info.cacheSize));
				// 获取缓存的大小
				if (info.cacheSize > 0) {
					// 有缓存的数据, 将它放在前面
					mData.add(0, info);
					// 保存缓存的数目
					mCacheCount++;
					// 保存缓存的大小
					mCache += cacheSize;
				} else {
					mData.add(info);
				}

				// 推出进度的信息
				mTask.upDataProcess(info);

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

		}
	};

	private class CacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mData != null) {
				return mData.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mData != null) {
				return mData.get(position);
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
						R.layout.item_cache_info, null);
				holder = new ViewHolder();
				convertView.setTag(holder);

				// 初始化数据

				holder.icon = (ImageView) convertView
						.findViewById(R.id.item_cache_iv_icon);
				holder.clear = (ImageView) convertView
						.findViewById(R.id.item_cache_iv_clear);
				holder.name = (TextView) convertView
						.findViewById(R.id.item_cache_tv_name);
				holder.cache = (TextView) convertView
						.findViewById(R.id.item_cache_tv_cache);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置数据
			final CacheInfo cacheInfo = mData.get(position);
			holder.icon.setImageDrawable(cacheInfo.icon);
			holder.name.setText(cacheInfo.name);
			holder.cache.setText("缓存大小:"
					+ Formatter.formatFileSize(getApplicationContext(),
							cacheInfo.cacheSize));
			holder.clear.setVisibility(cacheInfo.cacheSize > 0 ? View.VISIBLE
					: View.GONE);

			// 设置图片的监听器
			holder.clear.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 因为不能直接删除数据 , 得让用户自己清理 , 因此需要发送一个意图
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					intent.setData(Uri
							.parse("package:" + cacheInfo.packageName));
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView icon;
		ImageView clear;
		TextView name;
		TextView cache;
	}

	private class ClearCache implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (isRunning) {
				return; // 有别的东西正在执行
			}
			// 点击了清理
			if (mCache <= 0) {
				return;
			}

			// mPm.deleteApplicationCacheFiles(packageName,
			// mClearCacheObserver);
			// 查询源码发现是调用这个方法进行 删除数据的 ,而这个方法是隐藏的抽象方法 , 因此需要暴力获取

			// public abstract void deleteApplicationCacheFiles(String
			// packageName,IPackageDataObserver observer);
			// Iterator<CacheInfo> iterator = mData.iterator();
			// while (iterator.hasNext()) {
			// CacheInfo info = iterator.next();
			// Log.d(TAG, "点击了清理的按钮5");
			// if (info.cacheSize > 0) {
			// Log.d(TAG, "点击了清理的按钮6");
			// Method method = mPm.getClass().getDeclaredMethod(
			// "deleteApplicationCacheFiles", String.class,
			// IPackageDataObserver.class);
			// method.invoke(mPm, info.packageName,
			// new ClearCacheObserver());
			// iterator.remove();
			// // 更新数据
			// mAdapter.notifyDataSetChanged();
			// Log.d(TAG, "点击了清理的按钮7");
			// }
			// }
			try {
				Method method = mPm.getClass().getDeclaredMethod(
						"freeStorageAndNotify", long.class,
						IPackageDataObserver.class);

				method.invoke(mPm, Long.MAX_VALUE, new ClearCacheObserver());

				// 重新扫描
				startScan();
				// 重新扫描
				// startScan();
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "出异常啦...");
			}
		}
	}

	final class ClearCacheObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(final String packageName,
				final boolean succeeded) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "清理成功",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
