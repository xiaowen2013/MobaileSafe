package cn.itcase.safe.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusedTextView extends TextView {

	// 是用在 new
	public FocusedTextView(Context context) {
		this(context, null);
	}

	// 用在布局中的
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// android:ellipsize="marquee"
		// android:focusable="true"
		// android:focusableInTouchMode="true"
		// android:marqueeRepeatLimit="marquee_forever"
		// android:singleLine="true"

		setEllipsize(TruncateAt.MARQUEE);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setMarqueeRepeatLimit(-1);
		setSingleLine();
	}

	@Override
	public boolean isFocused() {
		return true;
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if (focused) {
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (hasWindowFocus) {
			super.onWindowFocusChanged(hasWindowFocus);
		}
	}

}
