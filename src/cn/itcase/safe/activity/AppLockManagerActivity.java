package cn.itcase.safe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.db.AppLockDao;
import cn.itcase.safe.domain.AppLockInfo;
import cn.itcase.safe.utils.AppInfoProvider;
import cn.itcase.safe.view.AppLockManagerView;
import cn.itcase.safe.view.AppLockManagerView.SelectLockListent;

public class AppLockManagerActivity extends Activity {

	protected static final String TAG = "AppLockManagerActivity";
	private List<AppLockInfo> mDataUnLock;
	private List<AppLockInfo> mDataLock;
	private TextView mTitle;
	private AppLockManagerView mLockChoice;
	private boolean mFlag = true; // 用于标记选中的是左边还是右边
	private ListView mUnLock;
	private ListView mLock;
	private AppLockDao mDao;
	private AppLockListView mUnLockAdapter;
	private AppLockListView mLockAdapter;
	private boolean isAnimation = false;
	private LinearLayout mLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appamager_lock);
		mUnLock = (ListView) findViewById(R.id.app_unlock_lv);
		mLock = (ListView) findViewById(R.id.app_lock_lv);
		mTitle = (TextView) findViewById(R.id.lock_tv_title);
		mLockChoice = (AppLockManagerView) findViewById(R.id.applock_lock_choice);
		mLoading = (LinearLayout) findViewById(R.id.app_lock_loading);

		mDao = new AppLockDao(AppLockManagerActivity.this);

		mDataLock = new ArrayList<AppLockInfo>();
		mDataUnLock = new ArrayList<AppLockInfo>();

		mLoading.setVisibility(View.VISIBLE);
		mTitle.setVisibility(View.GONE);
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				// 耗时的操作
				// 获取到所欲的数据
				List<AppLockInfo> allapp = AppInfoProvider
						.getAllAppsLock(AppLockManagerActivity.this);

				for (AppLockInfo appLockInfo : allapp) {
					// 判断程序的包名 , 而不是程序的应用名
					if (mDao.select(appLockInfo.packageName)) {
						// 上锁了的
						mDataLock.add(appLockInfo);
					} else {
						mDataUnLock.add(appLockInfo);

					}
				}

				return true;
			}

			protected void onPostExecute(Boolean result) {

				mLoading.setVisibility(View.GONE);
				if (mUnLockAdapter == null) {
					mUnLockAdapter = new AppLockListView();
				}
				mUnLock.setAdapter(mUnLockAdapter);
				mUnLock.setVisibility(View.VISIBLE);

				if (mLockAdapter == null) {
					mLockAdapter = new AppLockListView();
				}
				mLock.setAdapter(mLockAdapter);
				mLock.setVisibility(View.GONE);

				//
			};

		}.execute();

		// 设置监听
		mLockChoice.selectLock(new SelectLockListent() {

			@Override
			public void onIsLife(boolean isLife) {
				if (isLife) {
					// Toast.makeText(getApplicationContext(), "点击了未加锁..",
					// Toast.LENGTH_SHORT).show();
					mFlag = true;
					mUnLock.setVisibility(View.VISIBLE);
					mUnLockAdapter.notifyDataSetChanged();
					mLock.setVisibility(View.GONE);

				} else {
					// Toast.makeText(getApplicationContext(), "点击了加锁..",
					// Toast.LENGTH_SHORT).show();

					mFlag = false;
					mLock.setVisibility(View.VISIBLE);
					mLockAdapter.notifyDataSetChanged();
					mUnLock.setVisibility(View.GONE);
				}
			}
		});

	}

	public class AppLockListView extends BaseAdapter {

		@Override
		public int getCount() {
			if (mFlag) {
				if (mDataUnLock != null) {
					mTitle.setVisibility(View.VISIBLE);
					mTitle.setText("未加锁的程序: " + mDataUnLock.size());
					return mDataUnLock.size();
				}
			} else {
				if (mDataLock != null) {
					mTitle.setText("加锁的程序: " + mDataLock.size());
					return mDataLock.size();
				}

			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mFlag) {
				if (mDataUnLock != null) {
					return mDataUnLock.get(position);
				}
			} else {
				if (mDataLock != null) {
					return mDataLock.get(position);
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
						R.layout.item_applock_info, null);

				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.item_applock_iv_icon);
				holder.lock = (ImageView) convertView
						.findViewById(R.id.item_applock_iv_lock_);
				holder.name = (TextView) convertView
						.findViewById(R.id.item_applock_tv_name);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 给View设定值
			AppLockInfo info = null;
			if (mFlag) {
				info = mDataUnLock.get(position);
				holder.lock.setImageResource(R.drawable.btn_unlock_select);
			} else {
				info = mDataLock.get(position);
				holder.lock.setImageResource(R.drawable.btn_lock_select);
			}
			holder.name.setText(info.appName);
			holder.icon.setImageDrawable(info.icon);

			// 设置图片的监听事件
			final AppLockInfo app = info;
			final View view = convertView;
			holder.lock.setTag(app);
			holder.lock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d(TAG, "点击了图片..");
					// 如果有动画正在执行 , 则不向下处理
					if (isAnimation) {
						return;
					}
					// 点击是要将图片设置为未加锁或者是加锁的状态
					if (mFlag) {
						// 将这个程序设置为加锁
						// 移除这个 -->将数据添加到锁-->UI变化
						// 添加动画 从左到右
						TranslateAnimation ta = new TranslateAnimation(
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 1,
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 0);
						ta.setDuration(200);
						view.setAnimation(ta);
						// ta.start();
						view.startAnimation(ta);
						ta.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								isAnimation = true;
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {

								AppLockInfo app = (AppLockInfo) ((ViewHolder) view
										.getTag()).lock.getTag();
								if (mDao.add(app.packageName)) {
									mDataUnLock.remove(app);
									mDataLock.add(app);
									Log.d(TAG, "走到了notifyDataSetChanged..");
									mUnLockAdapter.notifyDataSetChanged();
									mLockAdapter.notifyDataSetChanged();

									isAnimation = false;
								} else {
									// 数据添加失败
									Toast.makeText(getApplicationContext(),
											"添加失败...", Toast.LENGTH_SHORT)
											.show();
								}

							}
						});
					} else {
						// 将这个程序设置为未加锁
						// 添加动画 从左到右
						TranslateAnimation ta1 = new TranslateAnimation(
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, -1,
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 0);
						ta1.setDuration(200);
						// view.setAnimation(ta1);
						view.startAnimation(ta1);
						ta1.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								isAnimation = true;
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								AppLockInfo app = (AppLockInfo) ((ViewHolder) view
										.getTag()).lock.getTag();

								if (mDao.delect(app.packageName)) {
									mDataLock.remove(app);
									mDataUnLock.add(app);

									mLockAdapter.notifyDataSetChanged();
									mUnLockAdapter.notifyDataSetChanged();
									isAnimation = false;
								} else {
									// 数据移除失败
									Toast.makeText(getApplicationContext(),
											"移除失败..", Toast.LENGTH_SHORT)
											.show();
								}

							}
						});

					}
				}
			});

			return convertView;
		}
	}

	private class ViewHolder {
		ImageView icon;
		ImageView lock;
		TextView name;

	}
}
