package com.mofa.loan.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.mofa.loan.R;
import com.mofa.loan.adapter.MoneyRecordAdapter;
import com.mofa.loan.pojo.MoneyPojo;
import com.mofa.loan.pojo.MyDialog;
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
import java.util.Calendar;

public class MoneyRecordActivity extends BaseActivity implements OnClickListener, IXListViewListener {

	private XListView money_list;
	private String userId;
	private int curPage=1;
	private boolean isloadMore;
	private ArrayList<MoneyPojo> arrayList;
	private MoneyRecordAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_record);
		initView();
	}

	private void initView() {
		SharedPreferences sp = getSharedPreferences("config", 0x0000);
		userId = sp.getString("userid", "");
		
		final Calendar c = Calendar.getInstance();
		  
	      mYear = c.get(Calendar.YEAR);
	  
	       mMonth = c.get(Calendar.MONTH);
	  
	       mDay = c.get(Calendar.DAY_OF_MONTH)+1;
		
		TextView title_txt_center = findViewById(R.id.title_txt_center);
		title_txt_center.setText("财富记录");
		findViewById(R.id.back).setOnClickListener(this);
		TextView title_right_txt = findViewById(R.id.title_right_txt);
		title_right_txt.setBackgroundResource(R.drawable.cander);
		title_right_txt.setOnClickListener(this);
		title_right_txt.setVisibility(View.INVISIBLE);
		money_list = findViewById(R.id.money_list);
		money_list.setXListViewListener(this);
		money_list.setPullLoadEnable(false);
		money_list.setPullRefreshEnable(true);
		
		
		arrayList = new ArrayList<MoneyPojo>();
		adapter = new MoneyRecordAdapter(MoneyRecordActivity.this, arrayList);
		money_list.setAdapter(adapter);
		
		HttpUtils.doGetAsyn(Config.MONEYRECORDE_CORD + "&userid=" + userId+"&startDate="+"&endDate=", mHandler,
				Config.CODE_MONEYRECORDE);
	
	}
	
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Config.CODE_URL_ERROR:
				Toast.makeText(MoneyRecordActivity.this, getResources().getString(R.string.url_error), Toast.LENGTH_SHORT)
						.show();
				break;
			case Config.CODE_NET_ERROR:
				Toast.makeText(MoneyRecordActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT)
						.show();
				break;
			case Config.CODE_ERROR:
				ToastUtil.showShort(MoneyRecordActivity.this, "system error");
				break;
			case Config.CODE_MONEYRECORDE:
				String result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(result);
					JSONObject listObject = json.getJSONObject("list");
					int totalRows = listObject.getInt("totalRows");
					int totalPages = listObject.getInt("totalPages");
					if (curPage<totalPages) {
						money_list.setPullLoadEnable(true);
					} else {
						money_list.setPullLoadEnable(false);
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
							JSONObject time = object.getJSONObject("recordtime");
							pojo.setTime(TimeUtils.parseDate4(Long.parseLong(time.getString("time"))));
							String type = object.getString("fundmode");
							double money = object.getDouble("handlesum");
							if (type.equals("投资利息")) {
								money+=20;
							}
							pojo.setMoney("+"+money);
							pojo.setName(object.getString("realname"));
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
					Toast.makeText(MoneyRecordActivity.this, getResources().getString(R.string.data_parsing_error),
							Toast.LENGTH_SHORT).show();
					Log.e("MOFA", e.getMessage());
				}

				break;
			default:
				break;
			}
		}
    };
	private int mYear;
	private int mMonth;
	private int mDay;
	
	
	private void setDialog() {
		new com.mofa.loan.utils.AlertDialog(MoneyRecordActivity.this).builder().setMsg("您还没有财富记录")
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
				HttpUtils.doGetAsyn(Config.MONEYRECORDE_CORD + "&userid=" + userId+"&curPage="+curPage, mHandler,
						Config.CODE_MONEYRECORDE);
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
				HttpUtils.doGetAsyn(Config.MONEYRECORDE_CORD + "&userid=" + userId+"&curPage="+curPage, mHandler,
						Config.CODE_MONEYRECORDE);
				onLoad();
			}
		}, 2000);
	}

	protected void onLoad() {
		money_list.stopRefresh();
		money_list.stopLoadMore();
		money_list.setRefreshTime(TimeUtils.getDate());
	}
	 private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

	        @Override
	        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
	            mYear = year;
	            mMonth = monthOfYear;
	            mDay = dayOfMonth;
//	            display();
	        }
	    };

	    
	    private void initPop2() {
//			WindowManager windowManager = (WindowManager) 
//					getSystemService(Context.WINDOW_SERVICE);
//			Display display = windowManager.getDefaultDisplay();
//			View view = LayoutInflater.from(MoneyRecordActivity.this).inflate(
//					R.layout.pop_out_money_time, null);
//			popupWindow = new PopupWindow((int) (display
//					.getWidth() * 0.55), (int)(display.getHeight()*0.5));
//			ColorDrawable cd = new ColorDrawable(0x000000);
//			popupWindow.setBackgroundDrawable(cd);
//			popupWindow.setOutsideTouchable(false);
//			popupWindow.setContentView(view);
//			popupWindow.setFocusable(true);
			}
	    @Override
	public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.title_right_txt:
//				View view = LayoutInflater.from(MoneyRecordActivity.this).inflate(
//						R.layout.pop_out_money_time, null);
//				 new AlertDialog.Builder(this).setTitle("自定义布局").setView(view)  
//			     .setPositiveButton(getResources().getString(R.string.OK), null)  
//			     .setNegativeButton("取消", null).show();
				
				 
				 
//				 初始化一个自定义的Dialog
	                Dialog dialog = new MyDialog(MoneyRecordActivity.this,
	                        R.style.MyDialog);

	                dialog.show();
				 
				break;
			default:
				break;
			}
	}

		@Override
		public void processMessage(Message message) {

		}
	
}
