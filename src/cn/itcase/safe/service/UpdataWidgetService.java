package cn.itcase.safe.service;

import cn.itcase.safe.activity.R;
import cn.itcase.safe.receive.AppStyleProcess;
import cn.itcase.safe.utils.ProcessInfoProvide;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdataWidgetService extends Service {

	private static final String TAG = "UpdataWidgetService";
	private AppWidgetManager mWm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "服务开启啦....");
		mWm = AppWidgetManager.getInstance(getApplicationContext());
		startUpdata();
	}

	private void startUpdata() {
		// 开启线程
		new Thread() {

			public void run() {

				while (true) {
					Log.d(TAG, "run运行啦....");
					ComponentName provider = new ComponentName(
							getApplicationContext(), AppStyleProcess.class);

					// 远程的View
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);

					// 获取正在运行的程序的个数
					int processCount = ProcessInfoProvide
							.getAllRunningProcessCount(getApplicationContext());

					// 设置信息
					views.setTextViewText(R.id.process_count, "正在运行的程序: "
							+ processCount + " 个");

					// 获取可用内存
					long freeMomory = ProcessInfoProvide
							.getFreeMomory(getApplicationContext());
					// 设置可用内存的信息
					views.setTextViewText(
							R.id.process_memory,
							"当前可用内存: "
									+ Formatter
											.formatFileSize(
													getApplicationContext(),
													freeMomory));
					// TODO: 注册一个广播 ,然后实现后台进程的杀死 , 未完成
					Log.d(TAG, "发广播咯..");
					Intent intent = new Intent();
					intent.setAction("cn.itcase.cleraprocess");

					PendingIntent broadcast = PendingIntent.getBroadcast(
							getApplicationContext(), 100, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					// 设置点击事件
					views.setOnClickPendingIntent(R.id.btn_clear, broadcast);

					// 清理内存
					mWm.updateAppWidget(provider, views);

					// 睡一会
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "服务关闭啦....");
	}

}
