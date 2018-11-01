package com.mofa.loan.utils;

import android.app.Activity;
import android.util.Log;

import java.util.LinkedList;

public class ActivityList {
	private static LinkedList<BaseActivity> list = new LinkedList<BaseActivity>();

	public static void addActiviy(BaseActivity a) {
		if (!list.contains(a)) {
			list.add(a);
		}
	}

	public static BaseActivity getLastActivity() {
		return list.getLast();
	}

	public static void removeActivity(BaseActivity a) {
		if (!list.isEmpty()) {
			list.remove(a);
		}
	}

	/**
	 * 退出，结束程序的所有界面
	 */
	public static void tuichu() {
		LogcatHelper.getInstance(getLastActivity()).stop();
		int lenth = list.size();
		for (int i = 0; i < lenth; i++) {
			try {
				list.get(i).finish();
			} catch (Exception e) {
				 Log.e("MOFA", "退出app出现错误！");
			}
		}
		if (list.size()>0) {
			list.clear();
		}
		int l = list.size();
		Log.i("MOFA", "退出app---" + l + "");
	}
	
	/**
	 * 结束指定类名的Activity
	 */
	public static void finishActivity(Class<?> cls) {
		for (Activity activity : list) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}
	
	/**
	 * 结束指定的Activity
	 */
	public static void finishActivity(Activity activity) {
		if (activity != null) {
			list.remove(activity);
			activity.finish();
			activity = null;
		}
	}


	/**
	 * 
	 * 退出登录，留下一个登录界面
	 * 
	 */
	public static void existLogin() {
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				list.get(i).finish();
			}
		}
	}
}
