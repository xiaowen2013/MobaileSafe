package cn.itcase.safe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.itcase.safe.activity.R;

public class AppLockManagerView extends LinearLayout {

	View mView;
	private TextView mTvLock;
	private TextView mTvUnLock;
	private boolean isLift = true;
	private SelectLockListent mListent;

	public AppLockManagerView(Context context) {
		this(context, null);

	}

	public AppLockManagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 绑定view
		mView = View.inflate(context, R.layout.item_applock_view, this);
		mTvLock = (TextView) mView.findViewById(R.id.item_lock);
		mTvUnLock = (TextView) mView.findViewById(R.id.item_unlock);

		// 设置未加锁的为默认选定
		mTvUnLock.setSelected(isLift);
		mTvLock.setOnClickListener(new Lock());
		mTvUnLock.setOnClickListener(new UnLock());
	}

	public interface SelectLockListent {
		public void onIsLife(boolean isLife);
	}
	
	public void selectLock(SelectLockListent listent){
		this.mListent = listent;
//		listent.onIsLife(isLift);
		
	}

	private class Lock implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 如果当前是选中的这边,再次点击则没有效果 ,
			// 如果当前不是显示的这变,点击了,说明是要显示这变
			if (isLift) {
				// 当前显示的是左边,现在点击了,是要显示右边
				mTvLock.setSelected(isLift);
				isLift = !isLift;
				mTvUnLock.setSelected(isLift);
				mListent.onIsLife(isLift);
			}
		}
	}

	private class UnLock implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 如果当前是选中的这边,再次点击则没有效果 ,
			// 如果当前不是显示的这变,点击了,说明是要显示这变
			if (!isLift) {
				// 当前状态是右边(加锁),现在点击了是要显示未加锁
				isLift = !isLift;
				mTvUnLock.setSelected(isLift);
				// 设置左边为没选定
				mTvLock.setSelected(!isLift);
				mListent.onIsLife(isLift);
			}

		}
	}

}
