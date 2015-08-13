package cn.itcase.safe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.itcase.safe.activity.R;

public class MyProgress extends LinearLayout {

	private TextView mTitle;
	private TextView mAble;
	private TextView mEnable;
	private ProgressBar mPro;
	View view;

	public MyProgress(Context context) {
		super(context, null);
	}

	public MyProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		view = View.inflate(context, R.layout.progress_item, this);
		mTitle = (TextView) view.findViewById(R.id.progress_title);
		mAble = (TextView) view.findViewById(R.id.progress_tv_able);
		mEnable = (TextView) view.findViewById(R.id.progress_endale);
		mPro = (ProgressBar) findViewById(R.id.progress_pro);
	}

	public void setTitle(String title) {
		mTitle.setText(title);
	}

	public void setAble(String ableTitle) {
		mAble.setText(ableTitle);
	}

	public void setEnable(String enableTitle) {
		mEnable.setText(enableTitle);
	}

	public void setProgressBar(int pro) {
		mPro.setProgress(pro);
	}

}
