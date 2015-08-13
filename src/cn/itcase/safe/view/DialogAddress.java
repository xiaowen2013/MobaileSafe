package cn.itcase.safe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import cn.itcase.safe.activity.R;

public class DialogAddress extends Dialog {

	private static final String TAG = "DialogAddress";

	public DialogAddress(Context context) {
		super(context, R.style.dialogTheme);
	}

	private OnItemClickListener mListener;
	private ListAdapter mListadapter;
	private ListView mDialogLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_address);
		mDialogLv = (ListView) findViewById(R.id.dialog_lv);

		// 改变window的弹出位置
		Window window = getWindow();
		LayoutParams params = window.getAttributes();
		// 设置弹出的位置 , 底部居中
		params.gravity = Gravity.BOTTOM | Gravity.HORIZONTAL_GRAVITY_MASK;
		window.setAttributes(params);

		// 设置适配器
		setAdapter(mListadapter);
		// 设置点击事件
		setOnItemClickListener(mListener);
	}

	/**
	 * 设置adapter
	 * 
	 * @param adapter
	 */
	public void setAdapter(ListAdapter adapter) {
		Log.d(TAG, "走父类 这里来啦...");
		this.mListadapter = adapter;
		if (mDialogLv != null) {
			mDialogLv.setAdapter(adapter);
			Log.d(TAG, "走父类 这里来啦=====...");
		}
	}

	/**
	 * 设置mListener
	 * 
	 * @param adapter
	 */
	public void setOnItemClickListener(OnItemClickListener item) {
		Log.d(TAG, "走父类 这里来啦...");
		this.mListener = item;
		if (mDialogLv != null) {
			mDialogLv.setOnItemClickListener(item);
			Log.d(TAG, "走父类 这里来啦=====...");
		}
	}

}
