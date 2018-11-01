package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;

import java.util.ArrayList;

public class GuideAcytivity extends BaseActivity {

	private static final int[] mImageIds = new int[] { R.drawable.guide1,
			R.drawable.guide2, R.drawable.guide3 };
	private ViewPager vpGuide;
	private ArrayList<ImageView> mImageViewList;
	private LinearLayout llPointGroup;// 引导圆点的父控件
	private int mPointwidth;// 圆点间的距离
	private View viewRedPoint;// 小红点
	private Button btnStart; // 开始体验

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题
		setContentView(R.layout.activity_guide);
		Log.i("MOFA", "Guide---onCreate");
		vpGuide = findViewById(R.id.vp_guide);
		llPointGroup = findViewById(R.id.ll_point_group);
		viewRedPoint = findViewById(R.id.view_red_point);
		btnStart = findViewById(R.id.btn_start);
		btnStart.setVisibility(View.INVISIBLE);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 更新sp 表示已经展示新手指引页面
				SharedPreferences sp = getSharedPreferences("config",
						MODE_PRIVATE);
				sp.edit().putBoolean("is_user_guide_showed", true).commit();
				// 跳转到下一个页面
				startActivity(new Intent(GuideAcytivity.this,
						LoginActivity.class));
				finish();
			}

		});
		initViews();
		// vpGuide.setAdapter()
		vpGuide.setAdapter(new GuideAdapter());

		vpGuide.setOnPageChangeListener(new GuidPageChangeListener());
	}
	
	private long timeIn;
	private long timeOut;
	
	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "Guide---onStart:" + timeIn);
	}
	@Override
	protected void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "Guide---onStop:" + timeOut);
		Log.i("MOFA", "Guide---Show:" + (timeOut - timeIn));
	}

	/**
	 * 初始化界面
	 */
	private void initViews() {

		mImageViewList = new ArrayList<ImageView>();

		// 初始化引导页的3个页面
		for (int i = 0; i < mImageIds.length; i++) {
			ImageView image = new ImageView(this);
			image.setBackgroundResource(mImageIds[i]);// 设置引导页背景
			mImageViewList.add(image);
		}
		// 初始化引导页的小圆点
		for (int i = 0; i < mImageIds.length; i++) {
			View point = new View(this);
			point.setBackgroundResource(R.drawable.shap_point_gray);// 设置引导页背景
			int width = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources()
							.getDisplayMetrics());
			int height = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources()
							.getDisplayMetrics());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					width, height);
			if (i > 0) {
				params.leftMargin = 25;// 设置圆点间隔
			}
			point.setLayoutParams(params);
			llPointGroup.addView(point);// 添加圆点线性布局
		}

		// 获取视图树，对layout结束事件进行监听
		llPointGroup.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					// layout执行结束后回调此方法
					@Override
					public void onGlobalLayout() {

						// llPointGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						llPointGroup.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						mPointwidth = llPointGroup.getChildAt(1).getLeft()
								- llPointGroup.getChildAt(0).getLeft();
					}
				});
	}

	/**
	 * 
	 * ViewPager数据适配器
	 * 
	 * @author lilei
	 * 
	 */
	class GuideAdapter extends PagerAdapter {

		@Override
		public int getCount() {

			return mImageIds.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			container.addView(mImageViewList.get(position));
			/* return super.instantiateItem(container, position); */
			// return container ;
			return mImageViewList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView((View) object);

		}
	}

	/**
	 * 
	 * Viewpager的滑动监听
	 */
	class GuidPageChangeListener implements OnPageChangeListener {
		// 滑动的时间
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

			int len = (int) (mPointwidth * positionOffset) + position
					* mPointwidth;

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewRedPoint
					.getLayoutParams();// 获取当前红点的布局参数
			params.leftMargin = len;// 设置左边边距
			viewRedPoint.setLayoutParams(params);// 重新给小红点布局参数
		}

		// 某个页面被选中
		@Override
		public void onPageSelected(int position) {
			if (position == mImageIds.length - 1) { // 最后一个页面
				btnStart.setVisibility(View.VISIBLE);// 显示开始体验按钮
			} else {

				btnStart.setVisibility(View.INVISIBLE);
			}

		}

		// 滑动状态被改变
		@Override
		public void onPageScrollStateChanged(int state) {

		}

	}

	@Override
	public void processMessage(Message message) {

	}
}
