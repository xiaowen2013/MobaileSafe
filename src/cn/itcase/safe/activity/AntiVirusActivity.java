package cn.itcase.safe.activity;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcase.safe.db.AntiVirusDao;
import cn.itcase.safe.domain.VirusInfo;
import cn.itcase.safe.utils.Md5Utils;

import com.github.lzyzsd.circleprogress.CircleProgress;

public class AntiVirusActivity extends Activity implements OnClickListener {

	private TextView mTvName;
	private AsyncTask mTask;
	private PackageManager mPm;
	private List<VirusInfo> mData;
	private VirusAdapter mAdapter;
	private ListView mListView;
	private CircleProgress mCirPro;
	private RelativeLayout mRl;
	private LinearLayout mLl;
	private Button mBtn;
	private int mCountVirus; // 用于记录危险文件的个数
	private TextView mTvSaveTitle;
	private LinearLayout mRlIv;
	private ImageView mIvAnima;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_antivirus);

		mTvName = (TextView) findViewById(R.id.virus_tv_name);
		mListView = (ListView) findViewById(R.id.virus_lv);
		mCirPro = (CircleProgress) findViewById(R.id.virus_circle_progress);
		mRl = (RelativeLayout) findViewById(R.id.virus_rl);
		mLl = (LinearLayout) findViewById(R.id.virus_ll);
		mBtn = (Button) findViewById(R.id.virus_btn_rescan);
		mTvSaveTitle = (TextView) findViewById(R.id.virus_tv_safetitle);
		mIvAnima = (ImageView) findViewById(R.id.virus_iv_animation);
		mRlIv = (LinearLayout) findViewById(R.id.virus_rl_iv);

		mCirPro.setFinishedColor(Color.GREEN);
		mBtn.setOnClickListener(this);

		mPm = getPackageManager();
	}

	private void startScan() {
		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
		mTask = new AsyncTask();
		mTask.execute();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
		startScan();
	}

	private class AsyncTask extends android.os.AsyncTask<Void, VirusInfo, Void> {

		private int max;
		private int process;
		private boolean isFinsh;

		@Override
		protected void onPreExecute() {
			mRl.setVisibility(View.VISIBLE);
			mLl.setVisibility(View.GONE);
			mRlIv.setVisibility(View.GONE);
			mCirPro.setProgress(0); // 设置当前的进度为0
		}

		@Override
		protected Void doInBackground(Void... params) {
			mData = new ArrayList<VirusInfo>();

			List<PackageInfo> list = mPm.getInstalledPackages(0);
			max = list.size();
			process = 1;
			mCountVirus = 0;
			for (PackageInfo packageInfo : list) {

				if (isFinsh) {
					break;
				}

				process++;
				VirusInfo info = new VirusInfo();
				// 获取app 的根目录
				String sourceDir = packageInfo.applicationInfo.sourceDir;
				try {
					String deMd5 = Md5Utils.deMd5(new FileInputStream(new File(
							sourceDir)));

					boolean virus = AntiVirusDao.isVirus(
							getApplicationContext(), deMd5);
					info.isVirus = virus;
					info.name = packageInfo.applicationInfo.loadLabel(mPm)
							.toString();
					info.packageName = packageInfo.packageName;
					info.icon = packageInfo.applicationInfo.loadIcon(mPm);
					if (virus) {
						mData.add(0, info);
						mCountVirus++;
					} else {
						mData.add(info);
					}
					publishProgress(info);
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		public void stop() {
			isFinsh = true;
		}

		@Override
		protected void onProgressUpdate(VirusInfo... values) {
			super.onProgressUpdate(values);

			if (isFinsh) {
				return;
			}
			mTvName.setText(values[0].name);
			int pro = (int) ((process * 100f) / (max + 0.5));
			mCirPro.setProgress(pro);

			if (mAdapter == null) {
				mAdapter = new VirusAdapter();
				mListView.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
			mListView.smoothScrollToPosition(mAdapter.getCount());
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isFinsh) {
				return;
			}

			mListView.smoothScrollToPosition(0);
			mTvName.setText(mData.get(0).name);
			mTvSaveTitle.setText(mCountVirus > 0 ? "您的手机存在安全隐患,请立即查杀!"
					: "您的手机很安全,安全卫士守护您的手机!");
			mTvSaveTitle
					.setTextColor(mCountVirus > 0 ? Color.RED : Color.WHITE);

			// 增加动画
			// 结束时获得动画的容器
			mRl.setDrawingCacheEnabled(true);
			// 设置图片的清晰度 ,高清
			mRl.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
			// 获取bitmap对象
			Bitmap bitmap = mRl.getDrawingCache();
			// 给mIvAnima 设置图片动画
			mIvAnima.setImageBitmap(getBitmap(bitmap));

			mRl.setVisibility(View.GONE);
			mLl.setVisibility(View.VISIBLE);
			mRlIv.setVisibility(View.VISIBLE);

			mRlIv.bringToFront();

			// 显示动画
			showAnimation();
		}
	}

	/**
	 * 显示动画
	 */
	private void showAnimation() {

		// 创建Animations对象
		AnimatorSet am = new AnimatorSet();
		// 将View充气成动画
		mRlIv.measure(0, 0); // 设置初始大小
		am.playTogether(
				ObjectAnimator.ofFloat(mIvAnima, "translationY", 0,
						-mIvAnima.getMeasuredHeight()),
				ObjectAnimator.ofFloat(mIvAnima, "alpha", 1, 0),
				ObjectAnimator.ofFloat(mLl, "alpha", 0, 1));
		am.setDuration(1500);
		am.start();
		
		am.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				
				mCirPro.setProgress(0);
				mTvName.setText(mData.get(0).name);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
				
			}
		});

	}

	/**
	 * 画动画
	 * 
	 * @param srcBitmap
	 * @return
	 */
	private Bitmap getBitmap(Bitmap srcBitmap) {
		// 1. 准备画纸
		int width = srcBitmap.getWidth();
		int height = srcBitmap.getHeight();
		Bitmap destBitmap = Bitmap.createBitmap(width, height,
				srcBitmap.getConfig());

		// 2. 准备画板，把画纸放上去
		Canvas canvas = new Canvas(destBitmap);
		// 3. 准备笔
		Paint paint = new Paint();
		// 4. 准备规则
		Matrix matrix = new Matrix();
		// 5. 绘制
		canvas.drawBitmap(srcBitmap, matrix, paint);

		return destBitmap;
	}

	private class VirusAdapter extends BaseAdapter {

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
						R.layout.item_virus, null);

				holder = new ViewHolder();
				convertView.setTag(holder);
				// 初始化数据

				holder.icon = (ImageView) convertView
						.findViewById(R.id.item_virus_iv_icon);
				holder.name = (TextView) convertView
						.findViewById(R.id.item_virus_tv_name);
				holder.isVirus = (TextView) convertView
						.findViewById(R.id.item_virus_tv_virus);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 设置数据
			VirusInfo virusInfo = mData.get(position);
			holder.icon.setImageDrawable(virusInfo.icon);
			holder.name.setText(virusInfo.name);
			holder.isVirus.setText(virusInfo.isVirus ? "危险" : "安全");
			holder.isVirus.setTextColor(virusInfo.isVirus ? Color.RED
					: Color.GREEN);
			return convertView;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
	}

	private class ViewHolder {
		TextView name;
		TextView isVirus;
		ImageView icon;

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.virus_btn_rescan:

			// 增加动画
			// 结束时获得动画的容器
			 mRl.setDrawingCacheEnabled(true);
			// // 设置图片的清晰度 ,高清
			 mRl.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
			
			 Bitmap bitmap = mRl.getDrawingCache();
			 mIvAnima.setImageBitmap(getBitmap(bitmap));

			 mRlIv.bringToFront();
			AnimatorSet set = new AnimatorSet();
			set.playTogether(ObjectAnimator.ofFloat(mIvAnima, "translationY",
					 -mIvAnima.getHeight(),0), ObjectAnimator.ofFloat(mIvAnima,
					"alpha", 0, 1), ObjectAnimator.ofFloat(mLl, "alpha", 1, 0));
			set.setDuration(1500);
			set.start();
			set.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					

				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					
					// 点击了 重新扫描
					mListView.clearTextFilter();
					startScan();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					

				}
			});

			break;

		default:
			break;
		}

	}
}
