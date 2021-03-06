package com.mofa.loan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerificationSeekBar extends SeekBar {
	// 这两个值为用算法使用的2空间复杂度
	private int index = 100;
	private boolean k = true;

	public VerificationSeekBar(Context context) {
		super(context);
	}

	public VerificationSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VerificationSeekBar(Context context, AttributeSet attrs,
                               int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			k = true;
			if (x - index > 20) {
				k = false;
				return true;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (!k) {
				return true;
			}
		}
		return super.dispatchTouchEvent(event);
	}
}