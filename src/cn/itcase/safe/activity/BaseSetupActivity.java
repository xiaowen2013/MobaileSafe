package cn.itcase.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {

	protected static final String TAG = "BaseSetupActivity";
	GestureDetector mGd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGd = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				float rawX1 = e1.getRawX();
				float rawX2 = e2.getRawX();

				float rawY1 = e1.getRawY();
				float rawY2 = e2.getRawY();

				if (Math.abs(rawY1 - rawY2) > Math.abs(rawX1 - rawX2)) {
					return false;
				}

				if (rawX2 + 50 < rawX1) {
					// 从右往左
					// Toast.makeText(BaseSetupActivity.this, "从右往左",
					// Toast.LENGTH_SHORT).show();

					doNext();
				}

				if (rawX2 > rawX1 + 50) {
					// 从左往右
					// Toast.makeText(BaseSetupActivity.this, "从左往右",
					// Toast.LENGTH_SHORT).show();
					doPro();
				}
				return true;
			}

		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGd.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void clickNext(View view) {
		// 点击了下一步
		// Intent intent = new Intent(this, LostSetup4Activity.class);
		// startActivity(intent);
		doNext();
	}

	public void clickPre(View view) {
		// 点击了上一步
		// Intent intent = new Intent(this, LostSetup2Activity.class);
		// startActivity(intent);
		// performPre();
		// overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		// finish();
		doPro();
	}

	public void doNext() {
		if (performNext()) {
			return;
		}
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		finish();
		Log.d(TAG, "doNext() 这里执行finish 啦....");
	}

	public void doPro() {
		if (performPre()) {
			return;
		}
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
		Log.d(TAG, "doPro() 这里执行finish 啦....");
	}

	/**
	 * 让孩子去执行上一步的操作
	 * 
	 * @return 如果返回true 就不继续向下执行
	 */
	protected abstract boolean performPre();

	/**
	 * 让孩子去执行下一步的操作
	 * 
	 * @return 如果返回true 就不继续向下执行
	 */
	protected abstract boolean performNext();
}
