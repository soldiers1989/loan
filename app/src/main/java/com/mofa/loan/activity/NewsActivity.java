package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.adapter.NewsAdapter2;
import com.mofa.loan.pojo.MoneyPojo;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.TimeUtils;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.xlistview.XListView;
import com.mofa.loan.xlistview.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsActivity extends BaseActivity implements OnClickListener,
        IXListViewListener {

	private XListView news_list;
	private String userId;
	private String from;
	private int curPage = 1;
	private boolean isloadMore;
	private ArrayList<MoneyPojo> arrayList;
	private NewsAdapter2 adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		Log.i("MOFA", "---News---onCreate");
		from = getIntent().getStringExtra("from");
		
		initView();
	}

	private long timeIn;
	private long timeOut;
	
	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "News---onStart:" + timeIn);
	}
	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "News---onStop:" + timeOut);
		Log.i("MOFA", "News---Show:" + (timeOut - timeIn));
	}

	private void initView() {
		SharedPreferences sp = getSharedPreferences("config", 0x0000);
		userId = sp.getString("userid", "");

		TextView title_txt_center = findViewById(R.id.title_txt_center);
		title_txt_center.setText(getResources().getString(R.string.msg_center));
		ImageView backpress = findViewById(R.id.back);
		backpress.setOnClickListener(this);

		news_list = findViewById(R.id.news_list);
		news_list.setXListViewListener(this);
		news_list.setPullLoadEnable(false);
		news_list.setPullRefreshEnable(true);

		arrayList = new ArrayList<MoneyPojo>();
		adapter = new NewsAdapter2(NewsActivity.this, arrayList);
		news_list.setAdapter(adapter);
		news_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
					TextView tvContent = arg1.findViewById(R.id.tv_content);
					tvContent.setSingleLine(false);
			}
		});

		HttpUtils.doGetAsyn(Config.NEWS_CORD + "&userid=" + userId+"&curPage="+curPage + "&py=" + MD5Util.md5(userId), mHandler,
				Config.CODE_NEWS);

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(NewsActivity.this, getResources().getString(R.string.url_error));

				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(NewsActivity.this, getResources().getString(R.string.network_error));

				break;
			case Config.CODE_ERROR:
				ToastUtil.showShort(NewsActivity.this, "system error");
				break;
			case Config.CODE_NEWS:
				String result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(result);
					JSONObject listObject = json.getJSONObject("list");
					int totalRows = listObject.getInt("totalRows");
					int totalPages = listObject.getInt("totalPages");
					if (curPage < totalPages) {
						news_list.setPullLoadEnable(true);
					} else {
						news_list.setPullLoadEnable(false);
						// CustomToast.showShortToast(getActivity(),
						// "数据已经加载完毕了!");
					}
					ArrayList<MoneyPojo> arrayList2 = new ArrayList<MoneyPojo>();
					JSONArray array = listObject.getJSONArray("data");
					if (array.length() == 0) {
						setDialog();
//						LinearLayout llEmpty = (LinearLayout) findViewById(R.id.ll_empty);
//						news_list.setEmptyView(llEmpty);
					} else {
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = (JSONObject) array.get(i);
							MoneyPojo pojo = new MoneyPojo();
//							JSONObject time = object.getJSONObject("fb_time");
//							pojo.setTime(TimeUtils.parseDate3(Long
//									.parseLong(time.getString("time"))));
							pojo.setTime(object.getString("fb_time"));
							pojo.setName(object.getString("title"));
							pojo.setType(object.getString("neirong"));
							arrayList2.add(pojo);
						}

						if (isloadMore) {
							arrayList.addAll(arrayList2);

						} else {
							arrayList = arrayList2;

						}

						adapter.setArrayList(arrayList);
						adapter.notifyDataSetChanged();

					}

				} catch (JSONException e) {
					ToastUtil.showShort(NewsActivity.this, getResources().getString(R.string.data_parsing_error));

					Log.e("MOFA", e.getMessage());
				}

				break;
			default:
				break;
			}
		}
    };

	private void setDialog() {
		new com.mofa.loan.utils.AlertDialog(NewsActivity.this)
				.builder().setMsg(getResources().getString(R.string.no_msg)).setCancelable(false)
				.setNegativeButton(getResources().getString(R.string.OK), new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}).show();
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// list.clear();
				curPage = 1;
				isloadMore = false;
				HttpUtils.doGetAsyn(Config.NEWS_CORD + "&userid=" + userId+"&curPage="+curPage + "&py=" + MD5Util.md5(userId),
						mHandler, Config.CODE_NEWS);
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				curPage += 1;
				isloadMore = true;
				HttpUtils.doGetAsyn(Config.NEWS_CORD + "&userid=" + userId+"&curPage="+curPage + "&py=" + MD5Util.md5(userId),
						mHandler, Config.CODE_NEWS);
				onLoad();
			}
		}, 2000);
	}

	protected void onLoad() {
		news_list.stopRefresh();
		news_list.stopLoadMore();
		news_list.setRefreshTime(TimeUtils.getDate());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
//        	startActivity(new Intent(this, IndexActivity.class));
			finish();
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
//			Intent intent = new Intent(this, IndexActivity.class);
//			if ("More".equalsIgnoreCase(from)) {
//				intent.putExtra("id", 3);
//			} else {
//				intent.putExtra("id", 2);
//			}
//			startActivity(intent);
			if ("push".equals(from)) {
				Intent i = new Intent(NewsActivity.this, IndexActivity.class);
				startActivity(i);
			}
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void processMessage(Message message) {

	}

}
