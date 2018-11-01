package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.adapter.Out_MoneyAdapter3;
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


public class OutMoneyRecord3Activity extends BaseActivity implements
        OnClickListener, IXListViewListener {

	private XListView out_money_list;
	private String userId;
	private int curPage = 1;
	private boolean isloadMore;
	private ArrayList<MoneyPojo> arrayList;
	private Out_MoneyAdapter3 adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("MOFA", "OutMoneyRecord---onCreate");
		setContentView(R.layout.activity_out_money);
		initView();
	}


	private long timeIn;
	private long timeOut;

	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "OutMoneyRecord---onStart:" + timeIn);
	}
	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "OutMoneyRecord---onStop:" + timeOut);
		Log.i("MOFA", "OutMoneyRecord---Show:" + (timeOut - timeIn));
	}

	private void initView() {

		SharedPreferences sp = getSharedPreferences("config", 0x0000);
		userId = sp.getString("userid", "");

		type = getIntent().getStringExtra("type");
		TextView title_txt_center = findViewById(R.id.title_txt_center);
		title_txt_center.setText(getResources().getString(R.string.loan_record));
		findViewById(R.id.back).setOnClickListener(this);

		out_money_list = findViewById(R.id.out_money_list);
		out_money_list.setXListViewListener(this);
		out_money_list.setPullLoadEnable(false);
		out_money_list.setPullRefreshEnable(true);

		arrayList = new ArrayList<MoneyPojo>();
		adapter = new Out_MoneyAdapter3(OutMoneyRecord3Activity.this, arrayList);
		out_money_list.setAdapter(adapter);

		HttpUtils.doGetAsyn(Config.JKRECORD_CORD + "&type=0" + "&userid=" + userId
				+ "&curPage=" + curPage + "&fgb=" + MD5Util.md5(userId), mHandler, Config.CODE_JKRECORD);

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(OutMoneyRecord3Activity.this, getResources().getString(R.string.url_error));

				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(OutMoneyRecord3Activity.this, getResources().getString(R.string.network_error));

				break;
			case Config.CODE_ERROR:
				ToastUtil.showShort(OutMoneyRecord3Activity.this, "system error");
				break;
			case Config.CODE_JKRECORD:
				String result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(result);
					JSONObject listObject = json.getJSONObject("list");
					int totalRows = listObject.getInt("totalRows");
					int totalPages = listObject.getInt("totalPages");
					if (curPage < totalPages) {
						out_money_list.setPullLoadEnable(true);
					} else {
						out_money_list.setPullLoadEnable(false);
						// CustomToast.showShortToast(getActivity(),
						// "数据已经加载完毕了!");
					}

					// cl03_status":1,"jk_date":2,"cl_status":1,"create_date":"2017-02-09
					// 09:57:19","cl02_status":1,"jk_money":3300
					ArrayList<MoneyPojo> arrayList2 = new ArrayList<MoneyPojo>();
					JSONArray array = listObject.getJSONArray("data");
					if (array.length() == 0) {
//						setDialog();
						LinearLayout llEmpty = findViewById(R.id.ll_empty);
						out_money_list.setEmptyView(llEmpty);

					} else {
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = (JSONObject) array.get(i);
							MoneyPojo pojo = new MoneyPojo();
							int jk_date = object.getInt("jk_date");
							if (jk_date==1) {
								pojo.setDate("15");
							}else{
								pojo.setDate("30");
							}
							String time = object.getString("create_date");
							// String[] strs=time.split("-");
							// pojo.setTime(strs[0]+"年"+strs[1]+"月"+strs[2]+"日"+);
							pojo.setTime(time);
							int id = object.getInt("id");
							pojo.setId(id);
							
							int one = object.getInt("cl_status");
							int two = object.getInt("cl02_status");
							int three = object.getInt("cl03_status");
							int sfyfk = object.getInt("sfyfk");
							int jksfwc = object.getInt("jksfwc");
							int spzt = object.getInt("spzt");
							int hkqd = object.getInt("hkqd");

							String type = "";
							if(one==0||one==1&&two==0||one==1&&two==1&&three==0){
								type = getResources().getString(R.string.loan_check);
							} 
							else if (one == 1 && two == 1 && three == 0
									&& spzt == 0) {
								type = getResources().getString(R.string.upload_video);
							} else if (one == 1 && two == 1 && three == 1
									&& (sfyfk == 2 || sfyfk == 3)) {
								type = getResources().getString(R.string.waiting_for_the_loan);
							} else if (one == 1 && two == 1 && three == 1
									&& sfyfk == 1&& jksfwc == 0) {
								if (hkqd != 0) {
									type = getResources().getString(R.string.check_repay);
								} else {
									type = getResources().getString(R.string.repay_please);
								}
							} else if (one == 3 || two == 3 || three == 3) {
								type = getResources().getString(R.string.loan_audit_failed);
							} else if (one == 1 && two == 1 && three == 1
									&& jksfwc == 1) {
								type = getResources().getString(R.string.repayment);
							}

							if (one == 1 && two == 1) {
								pojo.setName(getResources().getString(R.string.approve_the_amount));
								pojo.setMoney(object.getString("sjsh_money"));
							} else {
								pojo.setName(getResources().getString(R.string.application_amount));
								pojo.setMoney(object.getString("jk_money"));
							}
							pojo.setType(type);
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
					ToastUtil.showShort(OutMoneyRecord3Activity.this, getResources().getString(R.string.data_parsing_error));

					Log.e("MOFA", e.getMessage());
				}

				break;
			default:
				break;
			}
		}
    };
	private String type;

	private void setDialog() {
		new com.mofa.loan.utils.AlertDialog(OutMoneyRecord3Activity.this)
				.builder().setMsg(getResources().getString(R.string.no_loan_record))
				.setNegativeButton(getResources().getString(R.string.OK), new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (TextUtils.isEmpty(type) || type == null) {
				finish();
				return;
			}
			if (type.equals("wantmoney")) {
				startActivity(new Intent(this, IndexActivity.class));
				// finish();
			} else {
				finish();
			}

			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (TextUtils.isEmpty(type) || type == null) {
				finish();
			} else if (type.equals("wantmoney")) {
				startActivity(new Intent(this, IndexActivity.class));
				// finish();
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRefresh() {
		// adapter.initAd();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// list.clear();
				curPage = 1;
				isloadMore = false;
				HttpUtils
						.doGetAsyn(Config.JKRECORD_CORD + "&userid=" + userId
								+ "&curPage=" + curPage + "&fgb=" + MD5Util.md5(userId), mHandler,
								Config.CODE_JKRECORD);
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
				HttpUtils
						.doGetAsyn(Config.JKRECORD_CORD + "&userid=" + userId
								+ "&curPage=" + curPage + "&fgb=" + MD5Util.md5(userId), mHandler,
								Config.CODE_JKRECORD);
				onLoad();
			}
		}, 2000);
	}

	protected void onLoad() {
		out_money_list.stopRefresh();
		out_money_list.stopLoadMore();
		out_money_list.setRefreshTime(TimeUtils.getDate());
	}

	@Override
	public void processMessage(Message message) {

	}

}
