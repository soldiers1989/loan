package com.mofa.loan.receiver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mofa.loan.R;
import com.mofa.loan.activity.NewsActivity;
import com.mofa.loan.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MOFA";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
					+ ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID
					.equals(intent.getAction())) {
				String regId = bundle
						.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				// send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
					.getAction())) {
				Logger.d(
						TAG,
						"[MyReceiver] 接收到推送下来的自定义消息: "
								+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED
					.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle
						.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
					.getAction())) {
				Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");

				// 打开自定义的Activity
				Intent i = new Intent(context, NewsActivity.class);
				i.putExtra("from", "push");
				i.putExtras(bundle);
				// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
					.getAction())) {
				Logger.d(
						TAG,
						"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
								+ bundle.getString(JPushInterface.EXTRA_EXTRA));
				// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
				// 打开一个网页等..

			} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
					.getAction())) {
				boolean connected = intent.getBooleanExtra(
						JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Logger.w(TAG, "[MyReceiver]" + intent.getAction()
						+ " connected state change to " + connected);
			} else {
				Logger.d(TAG,
						"[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e) {
			Log.e("MOFA", e.getMessage());
		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle
						.getString(JPushInterface.EXTRA_EXTRA))) {
					Logger.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(
							bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it = json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" + myKey + " - "
								+ json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Logger.e(TAG, "Get message extra JSON error!");
					Log.e("MOFA", e.getMessage());
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		// if (IndexActivity.isForeground) {
		// String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		// // String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		// // Intent msgIntent = new
		// Intent(IndexActivity.MESSAGE_RECEIVED_ACTION);
		// // msgIntent.putExtra(IndexActivity.KEY_MESSAGE, message);
		// // if (!ExampleUtil.isEmpty(extras)) {
		// // try {
		// // JSONObject extraJson = new JSONObject(extras);
		// // if (extraJson.length() > 0) {
		// // msgIntent.putExtra(IndexActivity.KEY_EXTRAS, extras);
		// // }
		// // } catch (JSONException e) {
		// //
		// // }
		// //
		// // }
		// //
		// LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		// setDialog(context, message);
		// }
		Logger.i("MOFA", "接受到推送下来的自定义消息");
		// showNotification(context, bundle);
		NotificationManager notifyManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
				context);

		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		// //自定义信息： 获取
		// if (extras != null) {
		// try {
		// JSONObject object = new JSONObject(extras);
		// url = object.optString("src");
		// } catch (JSONException e) {
		// Log.e("MOFA", e.getMessage());
		// }
		// }
		// Intent i = new Intent(context, NewsActivity.class);
		// bundle.putBoolean("push", true);
		// i.putExtras(bundle);
		// PendingIntent pi = PendingIntent.getActivity(context, 1000, i,
		// PendingIntent.FLAG_UPDATE_CURRENT);
		//
		// notifyBuilder.setContentTitle(title);
		// notifyBuilder.setContentText(message);
		// notifyBuilder.setContentIntent(pi);
		// notifyBuilder.setAutoCancel(true);
		// notifyBuilder.setSmallIcon(R.drawable.jpush_notification_icon);
		// Notification notification = notifyBuilder.build();
		// notification.defaults |= Notification.DEFAULT_SOUND;
		// notification.defaults |= Notification.DEFAULT_VIBRATE;
		// notifyManager.notify(1000, notification);
		showNotification(context, bundle);
	}

	/**
	 * 在状态栏显示通知
	 */
	@SuppressWarnings("deprecation")
	private void showNotification(Context context, Bundle bundle) {
		// 通知栏标题
		String contentTitle = bundle.getString(JPushInterface.EXTRA_TITLE);
		// 通知栏内容
		String contentText = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		PendingIntent pendingIntent = null;
		// 创建一个NotificationManager的引用
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification;
		Intent intent = new Intent(context, NewsActivity.class);
		intent.putExtra("i", 1);
		pendingIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			notification = new NotificationCompat.Builder(context)
					.setContentIntent(pendingIntent)// 跳转的activity
					.setContentTitle(contentTitle).setAutoCancel(true)// 标题和点击消失
					.setContentText(contentText)// 文本
					.setSmallIcon(R.drawable.jpush_notification_icon)// 图标
					.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
					.build();
		} else {
			notification = new Notification.Builder(context)
					.setContentIntent(pendingIntent)
					.setContentTitle(contentTitle).setAutoCancel(true)
					.setContentText(contentText)
					.setSmallIcon(R.drawable.jpush_notification_icon)
					.getNotification();
		}
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.ledOnMS =5000; //闪光时间，毫秒
		manager.notify(123, notification);

	}

	private void setDialog(Context context, String msg) {
		new com.mofa.loan.utils.AlertDialog(context.getApplicationContext())
				.builder().setMsg(msg)
				.setNegativeButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
	}

}
