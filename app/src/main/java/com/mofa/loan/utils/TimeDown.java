package com.mofa.loan.utils;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class TimeDown extends CountDownTimer {
	
	private TextView day;
	private TextView time;
	private String title;
	
	private int year;
	private int months;
	private int days;
	private int hours;
	private int mins;
	private int seconds;

	public TimeDown(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}
	
	public TimeDown(long millisInFuture, long countDownInterval, TextView day, TextView time, String title) {
		super(millisInFuture, countDownInterval);
		this.day = day;
		this.time = time;
		this.title = title;
	}

	@Override
	public void onFinish() {
		day.setVisibility(View.GONE);
		time.setText(title);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		String Time = TimeUtils.parseDate(millisUntilFinished);
		String[] split = Time.split("-");
		year = Integer.valueOf(split[0]);
		months = Integer.valueOf(split[1]);
		days = Integer.valueOf(split[2]);
		hours = Integer.valueOf(split[3]);
		mins = Integer.valueOf(split[4]);
		seconds = Integer.valueOf(split[5]);
		time.setText(days + "Ngày\n" + hours + "时" + mins + "分" + seconds + "秒");
	}

}
