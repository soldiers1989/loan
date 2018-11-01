package com.mofa.loan.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mofa.loan.R;
import com.mofa.loan.adapter.GetMoneyRecordAdapter;
import com.mofa.loan.pojo.MoneyPojo;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.TimeUtils;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.xlistview.XListView;
import com.mofa.loan.xlistview.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetMoneyRecordActivity extends BaseActivity implements OnClickListener, IXListViewListener {

	private XListView getmoney_list;
	private String userId;
	private int curPage=1;
	private boolean isloadMore;
	private ArrayList<MoneyPojo> arrayList;
	private GetMoneyRecordAdapter adapter;
	private TextView get_money_value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_money_record);
		initView();
	}

	private void initView() {
		SharedPreferences sp = getSharedPreferences("config", 0x0000);
		userId = sp.getString("userid", "");
		
		
		TextView title_txt_center = findViewById(R.id.title_txt_center);
		title_txt_center.setText("提现记录");
		findViewById(R.id.back).setOnClickListener(this);
		get_money_value = findViewById(R.id.get_money_value);
		getmoney_list = findViewById(R.id.getmoney_list);
		
		
		getmoney_list.setXListViewListener(this);
		getmoney_list.setPullLoadEnable(false);
		getmoney_list.setPullRefreshEnable(true);
		
		
		arrayList = new ArrayList<MoneyPojo>();
		adapter = new GetMoneyRecordAdapter(GetMoneyRecordActivity.this, arrayList);
		getmoney_list.setAdapter(adapter);
		
		HttpUtils.doGetAsyn(Config.TXMONEY_CORD + "&userid=" + userId+"&curPage="+curPage, mHandler,
				Config.CODE_TXMONEY);
	
	}
	
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Config.CODE_ERROR:
				ToastUtil.showShort(GetMoneyRecordActivity.this, "system error");
				break;
			case Config.CODE_URL_ERROR:
				Toast.makeText(GetMoneyRecordActivity.this, getResources().getString(R.string.url_error), Toast.LENGTH_SHORT)
						.show();
				break;
			case Config.CODE_NET_ERROR:
				Toast.makeText(GetMoneyRecordActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT)
						.show();
				break;

			case Config.CODE_TXMONEY:
				String result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(result);
					JSONObject listObject = json.getJSONObject("list");
					get_money_value.setText(json.getString("presentationTotal")+getResources().getString(R.string.symbol));
					int totalRows = listObject.getInt("totalRows");
					int totalPages = listObject.getInt("totalPages");
					if (curPage<totalPages) {
						getmoney_list.setPullLoadEnable(true);
					} else {
						getmoney_list.setPullLoadEnable(false);
//						CustomToast.showShortToast(getActivity(), "数据已经加载完毕了!");
					}
//					cl03_status":1,"jk_date":2,"cl_status":1,"create_date":"2017-02-09 09:57:19","cl02_status":1,"jk_money":3300
					ArrayList<MoneyPojo> arrayList2 = new ArrayList<MoneyPojo>();
					JSONArray array = listObject.getJSONArray("data");
					if (array.length()==0) {
						setDialog();
					}else {
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = (JSONObject) array.get(i);
							MoneyPojo pojo = new MoneyPojo();
							String type = object.getString("status");
							JSONObject time = null;
							if(type.equals("1")){//applytime
								time=object.getJSONObject("applytime");
								pojo.setTime("时间:"+TimeUtils.parseDate3(Long.parseLong(time.getString("time"))));
							}else {
								time=object.getJSONObject("checktime");
								pojo.setTime("时间:"+TimeUtils.parseDate3(Long.parseLong(time.getString("time"))));
							}
							double money = object.getDouble("sum");
							String statu="";
							switch (type) {
							case "4":
								statu="支付正在处理";
								break;
							case "6":
								statu="等待支付处理";
								break;
							case "5":
								statu="提现失败";
								break;
							case "2":
								statu="提现成功";
								break;
							case "1":
								statu="正在审核";
								break;
							default:
								break;
							}
							pojo.setMoney("￥"+money);
							pojo.setType(statu);
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
					Toast.makeText(GetMoneyRecordActivity.this, getResources().getString(R.string.data_parsing_error),
							Toast.LENGTH_SHORT).show();
					Log.e("MOFA", e.getMessage());
				}

				break;
			default:
				break;
			}
		}
    };
	
	
	
	private void setDialog() {
		new com.mofa.loan.utils.AlertDialog(GetMoneyRecordActivity.this).builder().setMsg("您还没有提现记录")
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
				HttpUtils.doGetAsyn(Config.TXMONEY_CORD + "&userid=" + userId+"&curPage="+curPage, mHandler,
						Config.CODE_TXMONEY);
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
				HttpUtils.doGetAsyn(Config.TXMONEY_CORD + "&userid=" + userId+"&curPage="+curPage, mHandler,
						Config.CODE_TXMONEY);
				onLoad();
			}
		}, 2000);
	}

	protected void onLoad() {
		getmoney_list.stopRefresh();
		getmoney_list.stopLoadMore();
		getmoney_list.setRefreshTime(TimeUtils.getDate());
	}
	 
	    @Override
	public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
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
