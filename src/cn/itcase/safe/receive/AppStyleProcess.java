package cn.itcase.safe.receive;

import cn.itcase.safe.service.UpdataWidgetService;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppStyleProcess extends AppWidgetProvider {

	private static final String TAG = "AppStyleProcess";

	// onEnabled --> onUpdate --> onDisabled --> onDeleted

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d(TAG, "onEnabled 执行啦..");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d(TAG, "onUpdate 执行啦..");
		
		// 在这里了做更新的事
		// 开启一个服务
		Intent intent = new Intent(context,UpdataWidgetService.class);
		context.startService(intent);
		Log.d(TAG, "onUpdate 执行啦123..");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.d(TAG, "onDisabled 执行啦..");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.d(TAG, "onDeleted 执行啦..");
	}
}
