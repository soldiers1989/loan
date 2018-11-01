package com.mofa.loan.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

/**
 * 倒计时工具类
 * 
 * @author jy qiu
 * 
 */
public class TimeCount extends CountDownTimer {
	private Button mBtn;
	private String title;
	private Context mContext;

	public TimeCount(long millisInFuture, long countDownInterval, Button btn,
			String title) {
		super(millisInFuture, countDownInterval);
		this.mBtn = btn;
		this.title = title;
	}

	public TimeCount(long millisInFuture, long countDownInterval, Button btn,
                     String title, Context context) {
		super(millisInFuture, countDownInterval);
		this.mBtn = btn;
		this.title = title;
		this.mContext = context;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		mBtn.setText(millisUntilFinished / 1000 + "");
		mBtn.setClickable(false);

	}

	@Override
	public void onFinish() {
		mBtn.setText(title);
		mBtn.setClickable(true);
	}

}
