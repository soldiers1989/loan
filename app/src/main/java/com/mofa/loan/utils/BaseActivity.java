package com.mofa.loan.utils;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

@TargetApi(23)
public abstract class BaseActivity extends FragmentActivity {

	//沉浸式状态栏？
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityList.addActiviy(this);
		super.onCreate(savedInstanceState);
	}

	public abstract void processMessage(Message message);

	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ActivityList.getLastActivity().processMessage(msg);
		}
	};
	
	@Override
	protected void onStart() {
		//出错
		if (!LogcatHelper.getInstance(this).isLogcat()){
			if (Build.VERSION.SDK_INT >= 23) {
				if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				} else {// 已授权
					LogcatHelper.getInstance(this).start();
				}
			} else {
				LogcatHelper.getInstance(this).start();
			}
		}
		super.onStart();
	}

    public static void sendMsg(Message msg) {
		handler.sendMessage(msg);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// ActivityList.removeActivity(this);
		super.onDestroy();
	}
}
