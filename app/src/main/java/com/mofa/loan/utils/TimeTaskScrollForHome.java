package com.mofa.loan.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.mofa.loan.adapter.HomeAdapterForHome;
import com.mofa.loan.pojo.PersonForHome;

import java.util.ArrayList;
import java.util.TimerTask;

public class TimeTaskScrollForHome extends TimerTask {

	private ListView listView;

	public TimeTaskScrollForHome(Context context, ListView listView,
                                 ArrayList<PersonForHome> pojo) {
		this.listView = listView;
		listView.setAdapter(new HomeAdapterForHome(context, pojo));
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
