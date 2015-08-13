package cn.itcase.safe.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcase.safe.db.BlackDao;
import cn.itcase.safe.domain.CallSmsSafeInfo;

public class LjsrActivity extends Activity implements OnItemClickListener {

	private static final String TAG = "LjsrActivity";
	private ImageView mIv;
	private ImageView mIvEmpty;
	private static final int REQUESTCODE_ADD = 100;
	private static final int REQUESTCODE_UPDATA = 200;
	private ListView mLv;
	private List<CallSmsSafeInfo> mList;
	private BlackDao mDao;
	private LinearLayout mLlLoading;
	private SmsCallAdapter mAdapter;
	private int FINDPART = 25;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsms_safe);

		mIv = (ImageView) findViewById(R.id.callsms_iv);
		mLv = (ListView) findViewById(R.id.callsms_lv);
		mIvEmpty = (ImageView) findViewById(R.id.css_iv_empty);

		// 设置 mLv 的item 点击事件
		mLv.setOnItemClickListener(this);
		// 设置listView的滚动监听事件
		mLv.setOnScrollListener(new ListScrollListener());
		// 加载进度条
		mLlLoading = (LinearLayout) findViewById(R.id.css_loading);
		// 找到lv
		mIv.setOnClickListener(new MCallSmsIv());

		mDao = new BlackDao(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		startInit();
	}

	/**
	 * 初始化数据
	 */
	private void startInit() {
		mLlLoading.setVisibility(View.VISIBLE);

		new Thread() {
			public void run() {
				try {
					sleep(700);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 获取所有的黑名单
				// mList = dao.getAllInfo();
				// 去查询部分数据
				mList = mDao.findPart(FINDPART, 0);
				runOnUiThread(new Runnable() {
					public void run() {
						mLlLoading.setVisibility(View.GONE);
						mAdapter = new SmsCallAdapter();
						mLv.setAdapter(mAdapter);
						// 设置空的view
						mLv.setEmptyView(mIvEmpty);
					}
				});
			};
		}.start();
		// 设置适配器
	}

	/**
	 * listView的滚动监听事件
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// Log.d(TAG, "onScrollStateChanged方法的 scrollState ===" +
			// scrollState);
			// scrollState:当前的状态
			// SCROLL_STATE_IDLE : 闲置空闲状态
			// SCROLL_STATE_TOUCH_SCROLL: 触摸滚动状态
			// SCROLL_STATE_FLING: 惯性滑动状态
			// 什么时候需要加载数据?
			// 当前item 的position 等于list的最后一个 并且处于空闲状态
			// 获取当前的position(最后一个item)
			int lastVisiblePosition = mLv.getLastVisiblePosition();
			if ((scrollState == OnScrollListener.SCROLL_STATE_IDLE)
					&& lastVisiblePosition == (mList.size() - 1)) {
				// 加载数据
				// 显示进度条
				mLlLoading.setVisibility(View.VISIBLE);
				// 开启线程去加载
				new Thread() {
					public void run() {

						try {
							// 休眠一秒
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// 加载的数据时当前的 mList 的最后一个
						int index = mList.size();
						List<CallSmsSafeInfo> findList = mDao.findPart(
								FINDPART, index);

						// 判断此list是否为空 , 为空说明没有更多的数据了
						if (findList.size() == 0) {
							// 没有数据
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(LjsrActivity.this,
											"没有更多的数据啦啦...", Toast.LENGTH_SHORT)
											.show();
									return;
								}
							});
						}

						// 有数据 , 将findList 的数据添加到 mList , 然后通知去更新数据
						mList.addAll(findList);
						// UI 的更新需要在主线程
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// 更新数据
								mAdapter.notifyDataSetChanged();
								// 关闭进度条
								mLlLoading.setVisibility(View.GONE);
							}
						});
					};
				}.start();

			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// firstVisibleItem : 第一个可见的item 的position
			// visibleItemCount : 当前可见的数量
			// totalItemCount : list 的总数

			// Log.d(TAG, "onScroll方法的 firstVisibleItem ===" +
			// firstVisibleItem);
			// Log.d(TAG, "onScroll方法的 visibleItemCount ===" +
			// visibleItemCount);
			// Log.d(TAG, "onScroll方法的 totalItemCount ===" + totalItemCount);
			// 正在滑动 , 不需要处理
		}

	}

	/**
	 * 适配器
	 * 
	 */
	private class SmsCallAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			if (mList != null) {
				return mList.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {

			if (mList != null) {
				return mList.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHandler handler = null;

			if (convertView == null) {

				convertView = View.inflate(LjsrActivity.this,
						R.layout.callsmssafe_item, null);

				handler = new ViewHandler();

				handler.item_tv_number = (TextView) convertView
						.findViewById(R.id.callsms_iv_item_phoneNumer);

				handler.item_tv_type = (TextView) convertView
						.findViewById(R.id.callsms_iv_item_type);

				handler.item_iv_icon = (ImageView) convertView
						.findViewById(R.id.callsms_iv_item);

				convertView.setTag(handler);

			} else {

				handler = (ViewHandler) convertView.getTag();

			}

			final CallSmsSafeInfo safeinfo = mList.get(position);

			// 关闭
			mLlLoading.setVisibility(View.GONE);

			switch (safeinfo.type) {
			case CallSmsSafeInfo.TYPE_NUMBER:
				// 电话
				handler.item_tv_type.setText("电话拦截");
				break;
			case CallSmsSafeInfo.TYPE_SMS:
				// 短信
				handler.item_tv_type.setText("短信拦截");
				break;
			case CallSmsSafeInfo.TYPE_ALL:
				// 全部
				handler.item_tv_type.setText("电话+短信拦截");
				break;
			default:
				break;
			}

			handler.item_tv_number.setText(safeinfo.phoneNumber);

			final String number = safeinfo.phoneNumber;

			// 设置图片的监听器
			handler.item_iv_icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// 弹出对话框提示是否删除数据
					AlertDialog.Builder bu = new AlertDialog.Builder(
							LjsrActivity.this);
					bu.setTitle("是否删除数据????");
					bu.setMessage("请慎重,删除后将无法恢复....");
					bu.setNegativeButton("取消", null);
					bu.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 点击了确定
									// 点击了删除的图标 , 将此数据从数据库中删除
									boolean delect = mDao.delect(number);
									// 判断是否删除
									if (delect) {
										mList.remove(safeinfo);
										// 删除成功
										Toast.makeText(LjsrActivity.this,
												"数据删除成功..", Toast.LENGTH_SHORT)
												.show();
										
										// 每次删除一条记录就添加一条数据
										List<CallSmsSafeInfo> part = mDao
												.findPart(1, mList.size());
										if (part.size() > 0) {
											mList.addAll(part);
										}
										// Ui需要改变 ,通知更新
										mAdapter.notifyDataSetChanged();

									} else {
										// 删除失败
										Toast.makeText(LjsrActivity.this,
												"数据删除成功..", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});
					bu.show();
				}
			});

			return convertView;
		}

	}

	static class ViewHandler {
		TextView item_tv_number;
		TextView item_tv_type;
		ImageView item_iv_icon;
	}

	/**
	 * 将结果返回的方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (REQUESTCODE_ADD == requestCode) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				String number = data
						.getStringExtra(BlacklistActivity.EXTRA_NUMBER);
				int type = data.getIntExtra(BlacklistActivity.EXTRA_TYPE, -1);

				CallSmsSafeInfo csf = new CallSmsSafeInfo();
				csf.phoneNumber = number;
				csf.type = type;
				mList.add(csf);
				// 更新数据
				mAdapter.notifyDataSetChanged();
				// 并且将此数据添加到数据库
				// dao.add(number, type);
				break;

			default:
				break;
			}
		} else if (REQUESTCODE_UPDATA == requestCode) {

			switch (resultCode) {
			case Activity.RESULT_OK:

				// 更新的操作返回来的结果
				int position = data.getIntExtra(
						BlacklistActivity.EXTRA_POSITION, -1);
				int type = data.getIntExtra(BlacklistActivity.EXTRA_TYPE, -1);
				// 将这个 type 的数据重新赋值
				mList.get(position).type = type;

				// 将此数据在数据库进行更新
				// boolean updata = mDao.updata(mList.get(position).phoneNumber,
				// type);
				// Log.d(TAG, "数据更新后保存的状态:" + updata);
				// 更新页面
				mAdapter.notifyDataSetChanged();

				break;

			default:
				break;
			}
		}
	}

	/**
	 * 点击图片进入黑名单设置页面
	 * 
	 */
	private class MCallSmsIv implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 点击了进入
			Intent intent = new Intent(LjsrActivity.this,
					BlacklistActivity.class);
			// 添加动作的标志位 , 以至于页面那边可以判断是添加还是 修改
			intent.setAction(BlacklistActivity.ACTION_ADD);
			startActivityForResult(intent, REQUESTCODE_ADD);
		}
	}

	/**
	 * 条目的监听事件 , 点击后进入更新的页面
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// 获取当前点击的数据
		CallSmsSafeInfo safeinfo = mList.get(position);
		// 点击后进入更新的页面
		Intent intent = new Intent(this, BlacklistActivity.class);
		// 将数据添加过去
		// 添加动作的标志位 , 以至于页面那边可以判断是添加还是 修改
		intent.setAction(BlacklistActivity.ACTION_UPDATA);
		// 添加数据 , 号码和类型
		intent.putExtra(BlacklistActivity.EXTRA_NUMBER, safeinfo.phoneNumber);
		intent.putExtra(BlacklistActivity.EXTRA_TYPE, safeinfo.type);
		intent.putExtra(BlacklistActivity.EXTRA_POSITION, position);
		// 页面跳转
		startActivityForResult(intent, REQUESTCODE_UPDATA);
	}
}
