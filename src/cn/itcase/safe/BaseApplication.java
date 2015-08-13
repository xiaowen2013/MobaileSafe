package cn.itcase.safe;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.util.Log;
import cn.itcase.safe.activity.R;

/*
 *  第三方开源的捕获异常和回传异常
 */
@ReportsCrashes(formUri = "http://188.188.2.84:8080/Version/version",
mode = ReportingInteractionMode.DIALOG,
resToastText = R.string.crash_toast_text, 
resDialogText = R.string.crash_dialog_text,
resDialogIcon = android.R.drawable.ic_dialog_info, 
resDialogTitle = R.string.crash_dialog_title,
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
resDialogEmailPrompt = R.string.crash_user_email_label, 
resDialogOkToast = R.string.crash_dialog_ok_toast
) 
public class BaseApplication extends Application {

	private static final String TAG = "BaseApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		// 程序的入口 , 
		Log.d(TAG, "程序启动啦...");
		ACRA.init(this);
	}
}
