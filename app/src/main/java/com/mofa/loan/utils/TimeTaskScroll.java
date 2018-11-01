package com.mofa.loan.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.mofa.loan.adapter.HomeAdapter;
import com.mofa.loan.pojo.PersonPojo;

import java.util.ArrayList;
import java.util.TimerTask;

public class TimeTaskScroll extends TimerTask {

	private ListView listView;

	public TimeTaskScroll(Context context, ListView listView,
                          ArrayList<PersonPojo> pojo) {
		this.listView = listView;
		listView.setAdapter(new HomeAdapter(context, pojo));
	}

	private Handler handler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(android.os.Message msg) {
			listView.smoothScrollBy(1, 0);
		}
    };

	@Override
	public void run() {
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
	}

}
