package cn.itcase.safe.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import cn.itcase.safe.activity.R;

public class MyToast implements OnTouchListener {

	private static final String TAG = "MyToast";
	WindowManager mWM;
	Context mContext;
	WindowManager.LayoutParams mParams;
	private View mView;

	public MyToast(Context context) {

		this.mContext = context;

		// 窗体管理者
		mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

		// 准备 LayoutParams
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		// params.setTitle("Toast");
	}

	public void show(String title) {

		mView = View.inflate(mContext, R.layout.mytoast, null);
		
		// 获取选定的样式
		int style = PreferenceUtils.getInt(mContext, IConstants.ADDRESS_STYLE,  R.drawable.toast_address_normal);
		mView.setBackgroundResource(style);
		
		TextView tv = (TextView) mView.findViewById(R.id.mytoast_title);
		tv.setText(title);
		// 创建移动的监听
		mView.setOnTouchListener(this);
		Log.d(TAG, "开启监听啦....");
		mWM.addView(mView, mParams);
	}
	public void hide() {
		// if (localLOGV) Log.v(TAG, "HANDLE HIDE: " + this + " mView=" +
		// mView);
		if (mView != null) {
			// note: checking parent() just to make sure the view has
			// been added... i have seen cases where we get here when
			// the view isn't yet added, so let's try not to crash.
			if (mView.getParent() != null) {
				// if (localLOGV) Log.v(TAG, "REMOVE! " + mView + " in " +
				// this);
				mWM.removeView(mView);
			}
			mView = null;
		}
	}

	float startX;
	float startY;
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		Log.d(TAG, "点击啦....");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 获取当前的指针位置
			startX = event.getRawX();
			startY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			// 获取当前移动的位置
			float rawX = event.getRawX();
			float rawY = event.getRawY();
			// 从按下到现在的差值
			float x = rawX - startX;
			float y = rawY - startY;
			// 赋值给mParams
			mParams.x += (int) (x + 0.5);
			mParams.y += (int) (y + 0.5);

//			mWM.addView(mView, mParams);
			mWM.updateViewLayout(mView, mParams);
			// 再讲开始的位置设置为当前的位置
			startX = rawX;
			startY = rawY;
			break;
		case MotionEvent.ACTION_UP:
			// 抬起
			Log.d(TAG, "抬起啦....");
			break;

		default:
			break;
		}

		return true;
	}
	
}











