package cn.itcase.safe.activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.itcase.safe.domain.TrafficInfo;

public class TrafficActivity extends Activity {

	private static final String TAG = "TrafficActivity";
	private ListView mListView;
	private LinearLayout mProcess;
	private TrafficAsync mTask;
	private PackageManager mPm;
	private List<TrafficInfo> mData;
	private TrafficAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);

		mListView = (ListView) findViewById(R.id.traffic_lv);
		mProcess = (LinearLayout) findViewById(R.id.traffic_loading);

		mTask = new TrafficAsync();
		mTask.execute();
	}

	private class TrafficAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProcess.setVisibility(View.VISIBLE);
			mData = new ArrayList<TrafficInfo>();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// 获取包管理器
			mPm = getPackageManager();
			// 获取所有安装的包
			List<PackageInfo> list = mPm.getInstalledPackages(0);
			for (PackageInfo packageInfo : list) {
				TrafficInfo info = new TrafficInfo();
				// 获取UID
				int uid = packageInfo.applicationInfo.uid;
				String name = packageInfo.applicationInfo.loadLabel(mPm)
						.toString();
				String packagerName = packageInfo.packageName;
				Drawable icon = packageInfo.applicationInfo.loadIcon(mPm);

				// 获取具体的流量值
				long rcv = getRcv(uid);
				long snd = getSnd(uid);

				// 封装信息
				info.name = name;
				info.packagerName = packagerName;
				info.icon = icon;
				info.rcv = rcv;
				info.snd = snd;
				if (rcv > 0 || snd > 0) {
					mData.add(0, info);
				} else {
					mData.add(info);
				}

				Log.d(TAG, "name: " + name);
				Log.d(TAG, "packagerName: " + packagerName);
				Log.d(TAG, "rcv: " + rcv);
				Log.d(TAG, "snd: " + snd);
				Log.d(TAG, "uid: " + uid);
				Log.d(TAG, "------------------------------------------");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			mProcess.setVisibility(View.GONE);

			if (mAdapter == null) {
				mAdapter = new TrafficAdapter();
				mListView.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	// 设置适配器
	private class TrafficAdapter extends BaseAdapter {

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
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_traffic, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				// 初始化数据
				holder.icon = (ImageView) convertView
						.findViewById(R.id.item_traffic_iv_icon);
				holder.name = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_name);
				holder.rcv = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_rcv);
				holder.snd = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_snd);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TrafficInfo info = mData.get(position);
			// 设置数据
			holder.icon.setImageDrawable(info.icon);
			holder.name.setText(info.name);
			holder.rcv.setText("接收: "
					+ Formatter.formatFileSize(getApplicationContext(),
							info.rcv));
			holder.snd.setText("接收: "
					+ Formatter.formatFileSize(getApplicationContext(),
							info.snd));

			return convertView;
		}
	}

	private class ViewHolder {
		TextView name;
		TextView rcv;
		TextView snd;
		ImageView icon;
	}

	/**
	 * 获取接收的流量值
	 * 
	 * @param uid
	 * @return
	 */
	private long getRcv(int uid) {

		BufferedReader br = null;
		// String fileName = "/proc/uid_stat/" + uid + "/tcp_rcv";
		try {

			br = new BufferedReader(new FileReader("/proc/uid_stat/" + uid
					+ "/tcp_rcv"));
			String readLine = br.readLine();
			return Long.valueOf(readLine);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;

	}

	/**
	 * 获取发送的流量值
	 * 
	 * @param uid
	 * @return
	 */
	private long getSnd(int uid) {

		BufferedReader br = null;
		try {

			br = new BufferedReader(new FileReader("/proc/uid_stat/" + uid
					+ "/tcp_snd"));
			String readLine = br.readLine();
			return Long.valueOf(readLine);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;

	}
}
