package cn.itcase.safe.receive;

import java.util.List;

import cn.itcase.safe.domain.ProcessInfo;
import cn.itcase.safe.utils.ProcessInfoProvide;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

public class KillProcessReceiver extends BroadcastReceiver {

	private static final String TAG = "KillProcessReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// 在这里做杀死进程的事情
		Log.d(TAG, "收到广播啦...");
		List<ProcessInfo> runningProcess = ProcessInfoProvide
				.getAllRunningProcess(context);

		int count = 0;
		int momory = 0;
		for (ProcessInfo processInfo : runningProcess) {
			ProcessInfoProvide.killProcess(context, processInfo.packageName);
			count++;
			momory = (int) processInfo.momory;
		}
		Toast.makeText(
				context,
				"共清理了进程" + count + "个, 释放资源"
						+ Formatter.formatFileSize(context, momory),
				Toast.LENGTH_SHORT).show();
	}

}
