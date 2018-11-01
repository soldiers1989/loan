/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.mofa.loan.xlistview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mofa.loan.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class XListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private Context mContext;

	private View mContentView;
	private TextView xlistview_footer_tv;
	private TextView mHintView;

	public XListViewFooter(Context context) {
		super(context);
		initView(context);
	}

	public XListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setState(int state) {
		mHintView.setVisibility(View.INVISIBLE);
		xlistview_footer_tv.setVisibility(View.INVISIBLE);

		mHintView.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.xlistview_footer_hint_ready);
		} else if (state == STATE_LOADING) {
			xlistview_footer_tv.setVisibility(View.VISIBLE);
			startSchudeExcute();
		} else {
			mHintView.setVisibility(View.VISIBLE);
			stopSchudeExcute();
			mHintView.setText(R.string.xlistview_footer_hint_normal);
		}
	}

	public void setBottomMargin(int height) {
		if (height < 0)
			return;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		xlistview_footer_tv.setVisibility(View.GONE);
		stopSchudeExcute();
	}

	/**
	 * loading status
	 */
	public void loading() {
		mHintView.setVisibility(View.GONE);
		xlistview_footer_tv.setVisibility(View.VISIBLE);
		isboolean = true;
		startSchudeExcute();
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}

	private void initView(Context context) {
		mContext = context;
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.xlistview_footer, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		mContentView = moreView.findViewById(R.id.xlistview_footer_content);
		xlistview_footer_tv = moreView
				.findViewById(R.id.xlistview_footer_tv);
		mHintView = moreView
				.findViewById(R.id.xlistview_footer_hint_textview);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				xlistview_footer_tv.setText("Đang tải" + ".");
				break;
			case 1:
				xlistview_footer_tv.setText("Đang tải" + "..");
				break;
			case 2:
				xlistview_footer_tv.setText("Đang tải" + "...");
				break;
			default:
				break;
			}
		}
    };

	private Thread thread;
	private boolean isboolean = false;

	// private Thread getThread() {
	//
	// if (thread != null && thread.isInterrupted()) {
	// return thread;
	// } else {
	// thread = new Thread() {
	// public void run() {
	// for (int i = 0; isboolean; i++) {
	// try {
	// Thread.sleep(500);
	//
	//
	// } catch (InterruptedException e) {
	// Log.e("MOFA", e.getMessage());
	// }
	//
	// }
	// };
	// };
	// }
	// return thread;
	// }
	private int i = 0;
	private ScheduledExecutorService scheduler;

	private void startSchudeExcute() {
		// 1表示保存在线程池中的数量
		
		Runnable beeper = new Runnable() {
			public void run() {

				if (i == 0) {
					handler.sendEmptyMessage(0);
				} else if (i == 1) {
					handler.sendEmptyMessage(1);
				} else {
					handler.sendEmptyMessage(2);
					i = -1;
				}
			
				i++;

			}
		};
		scheduler = Executors.newSingleThreadScheduledExecutor();
	
		scheduler.scheduleWithFixedDelay(beeper, 0, 400, TimeUnit.MILLISECONDS);
	}

	private void stopSchudeExcute(){
		if (scheduler!=null) {
			scheduler.shutdownNow();
			scheduler=null;
		}
	}
}
