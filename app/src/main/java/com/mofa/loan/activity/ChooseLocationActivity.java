package com.mofa.loan.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mofa.loan.R;
import com.mofa.loan.adapter.LocationAdapter;
import com.mofa.loan.pojo.JsonBean;
import com.mofa.loan.utils.GetJsonDataUtil;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONArray;

import java.util.ArrayList;

public class ChooseLocationActivity extends Activity implements OnClickListener {

	private ArrayList<JsonBean> options1Items = new ArrayList<>();
	private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
	private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
	private Thread thread;
	private static final int MSG_LOAD_DATA = 0x0001;
	private static final int MSG_LOAD_SUCCESS = 0x0002;
	private static final int MSG_LOAD_FAILED = 0x0003;

	private TextView tvProvince;
	private TextView tvJun;
	private TextView tvFang;
	private View viewProvince;
	private View viewJun;
	private View viewFang;
	private ImageView ivChoose1;
	private ImageView ivChoose2;
	private ImageView ivChoose3;
	private ListView lvList;
	private RelativeLayout rl;
	private EditText et;
	private Button btnSure;
	private ImageView btnCancel;

	private LocationAdapter adapter;
	private ArrayList<String> mList;

	private String province;
	private String jun;
	private String fang;
	private String location;
	private int current = 1;
	private int currentP = 0;
	private int currentJ = 0;
	private int currentF = 0;

	private boolean isLoaded = false;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOAD_DATA:
				if (thread == null) {// 如果已创建就不再重新创建子线程了

//					Toast.makeText(ChooseLocationActivity.this,
//							"Begin Parse Data", Toast.LENGTH_SHORT).show();
					thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// 子线程中解析省市区数据
							initJsonData();
						}
					});
					thread.start();
				}
				break;

			case MSG_LOAD_SUCCESS:
//				Toast.makeText(ChooseLocationActivity.this, "Parse Succeed",
//						Toast.LENGTH_SHORT).show();
				isLoaded = true;
				for (int i = 0; i < options1Items.size(); i++) {
					mList.add(options1Items.get(i).getName());
				}
				adapter = new LocationAdapter(ChooseLocationActivity.this,
						mList);
				lvList.setAdapter(adapter);
				
				break;

			case MSG_LOAD_FAILED:
//				Toast.makeText(ChooseLocationActivity.this, "Parse Failed",
//						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_choose_location);
		initview();
		super.onCreate(savedInstanceState);
	}

	private void initview() {
		mHandler.sendEmptyMessage(MSG_LOAD_DATA);
		TextView title = findViewById(R.id.title_txt_center);
		title.setText("Chọn địa chỉ");
		rl = findViewById(R.id.rlInput);
		et = findViewById(R.id.etInput);
		tvProvince = findViewById(R.id.tvProvince);
		tvJun = findViewById(R.id.tvJun);
		tvFang = findViewById(R.id.tvFang);
		findViewById(R.id.rlProvince).setOnClickListener(this);
		findViewById(R.id.rlJun).setOnClickListener(this);
		findViewById(R.id.rlFang).setOnClickListener(this);
		viewProvince = findViewById(R.id.viewProvince);
		viewJun = findViewById(R.id.viewJun);
		viewFang = findViewById(R.id.viewFang);
		ivChoose1 = findViewById(R.id.ivChoose1);
		ivChoose2 = findViewById(R.id.ivChoose2);
		ivChoose3 = findViewById(R.id.ivChoose3);
		lvList = findViewById(R.id.lv);
		btnSure = findViewById(R.id.btnSure);
		btnSure.setOnClickListener(this);
		btnCancel = findViewById(R.id.back);
		btnCancel.setOnClickListener(this);
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				if (current == 1) {
					province = options1Items.get(position).getName();
					current = 2;
					currentP = position;
					mList.clear();
					for (int i = 0; i < options2Items.get(currentP).size(); i++) {
						mList.add(options2Items.get(currentP).get(i));
					}
					adapter.notifyDataSetChanged();
					tvProvince.setText(province);
					viewProvince.setVisibility(View.INVISIBLE);
					viewJun.setVisibility(View.VISIBLE);
					ivChoose2.setVisibility(View.VISIBLE);
				} else if (current == 2) {
					jun = options2Items.get(currentP).get(position);
					current = 3;
					currentJ = position;
					mList.clear();
					for (int i = 0; i < options3Items.get(currentP)
							.get(currentJ).size(); i++) {
						mList.add(options3Items.get(currentP).get(currentJ)
								.get(i));
					}
					adapter.notifyDataSetChanged();
					tvJun.setText(jun);
					viewJun.setVisibility(View.INVISIBLE);
					viewFang.setVisibility(View.VISIBLE);
					ivChoose3.setVisibility(View.VISIBLE);
				} else if (current == 3) {
					fang = options3Items.get(currentP).get(currentJ)
							.get(position);
					currentF = position;
					tvFang.setText(fang);
					current = 4;
					lvList.setVisibility(View.GONE);
					btnSure.setVisibility(View.VISIBLE);
					rl.setVisibility(View.VISIBLE);
					et.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		mList = new ArrayList<>();

	}

	private void initJsonData() {// 解析数据

		/**
		 * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件 关键逻辑在于循环体
		 * 
		 * */
		String JsonData = new GetJsonDataUtil().getJson(this, "code2.json");// 获取assets目录下的json文件数据

		ArrayList<JsonBean> jsonBean = parseData(JsonData);// 用Gson 转成实体

		/*
		 * 添加省份数据
		 * 
		 * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
		 * PickerView会通过getPickerViewText方法获取字符串显示出来。
		 */
		options1Items = jsonBean;

		for (int i = 0; i < jsonBean.size(); i++) {// 遍历省份
			ArrayList<String> CityList = new ArrayList<>();// 该省的城市列表（第二级）
			ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();// 该省的所有地区列表（第三极）

			for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {// 遍历该省份的所有城市
				String CityName = jsonBean.get(i).getCityList().get(c)
						.getName();
				CityList.add(CityName);// 添加城市
				ArrayList<String> City_AreaList = new ArrayList<>();// 该城市的所有地区列表

				// 如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
				if (jsonBean.get(i).getCityList().get(c).getArea() == null
						|| jsonBean.get(i).getCityList().get(c).getArea()
								.size() == 0) {
					City_AreaList.add("");
				} else {
					City_AreaList.addAll(jsonBean.get(i).getCityList().get(c)
							.getArea());
				}
				Province_AreaList.add(City_AreaList);// 添加该省所有地区数据
			}

			/*
			 * 添加城市数据
			 */
			options2Items.add(CityList);

			/*
			 * 添加地区数据
			 */
			options3Items.add(Province_AreaList);
		}

		mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

	}

	public ArrayList<JsonBean> parseData(String result) {// Gson 解析
		ArrayList<JsonBean> detail = new ArrayList<>();
		try {
			JSONArray data = new JSONArray(result);
			Gson gson = new Gson();
			for (int i = 0; i < data.length(); i++) {
				JsonBean entity = gson.fromJson(data.optJSONObject(i)
						.toString(), JsonBean.class);
				detail.add(entity);
			}
		} catch (Exception e) {
			Log.e("MOFA", e.getMessage());
			mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
		}
		return detail;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (current == 1) {
			setResult(RESULT_CANCELED);
			finish();
		}
		lvList.setVisibility(View.VISIBLE);
		btnSure.setVisibility(View.GONE);
		rl.setVisibility(View.GONE);
		current--;
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(rl.getWindowToken(), 0);

		if (current == 1) {
			currentStatus(1);
		} else if (current == 2) {
			currentStatus(2);
		} else if (current == 3) {
			currentStatus(3);
		}
	}

	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(rl.getWindowToken(), 0);
		switch (v.getId()) {
		case R.id.back:
			setResult(RESULT_CANCELED);
			finish();
			break;

		case R.id.rlProvince:
			if (!TextUtils.isEmpty(province)) {
				currentStatus(1);
			}
			break;
			
		case R.id.rlJun:
			if (!TextUtils.isEmpty(jun)) {
				currentStatus(2);
			}
			break;
			
		case R.id.rlFang:
			if (!TextUtils.isEmpty(fang)) {
				currentStatus(3);
			}
			break;
			
		case R.id.btnSure:
			location = et.getText().toString().trim();
			if (TextUtils.isEmpty(location)) {
				ToastUtil.showShort(ChooseLocationActivity.this, "Vui lòng điền số nhà và tên đường cụ thể！");
				return;
			}
//			Toast.makeText(ChooseLocationActivity.this,
//					province + jun + fang + location, Toast.LENGTH_SHORT)
//					.show();
			Intent intent = new Intent();
			intent.putExtra("province", province);
			intent.putExtra("jun", jun);
			intent.putExtra("fang", fang);
			intent.putExtra("location", location);
			setResult(RESULT_OK, intent);
			finish();
			break;
			
		default:
			break;
		}
	}

	private void currentStatus(int j) {
		if (j == 1) {
			current = 1;
			province = options1Items.get(currentP).getName();
			tvProvince.setText(province);
			tvJun.setText("");
			jun = "";
			tvFang.setText("");
			fang = "";
			mList.clear();
			for (int i = 0; i < options1Items.size(); i++) {
				mList.add(options1Items.get(i).getName());
			}
			adapter.notifyDataSetChanged();
			viewProvince.setVisibility(View.VISIBLE);
			viewJun.setVisibility(View.INVISIBLE);
			viewFang.setVisibility(View.INVISIBLE);
			ivChoose1.setVisibility(View.VISIBLE);
			ivChoose2.setVisibility(View.INVISIBLE);
			ivChoose3.setVisibility(View.INVISIBLE);
			lvList.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
			rl.setVisibility(View.GONE);
		} else if (j == 2) {
			current = 2;
			province = options1Items.get(currentP).getName();
			jun = options2Items.get(currentP).get(currentJ);
			tvProvince.setText(province);
			tvJun.setText(jun);
			tvFang.setText("");
			fang = "";
			mList.clear();
			for (int i = 0; i < options2Items.get(currentP).size(); i++) {
				mList.add(options2Items.get(currentP).get(i));
			}
			adapter.notifyDataSetChanged();
			viewProvince.setVisibility(View.INVISIBLE);
			viewJun.setVisibility(View.VISIBLE);
			viewFang.setVisibility(View.INVISIBLE);
			ivChoose1.setVisibility(View.VISIBLE);
			ivChoose2.setVisibility(View.VISIBLE);
			ivChoose3.setVisibility(View.INVISIBLE);
			lvList.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
			rl.setVisibility(View.GONE);
		} else {
			current = 3;
			province = options1Items.get(currentP).getName();
			jun = options2Items.get(currentP).get(currentJ);
			fang = options3Items.get(currentP).get(currentJ)
					.get(currentF);
			tvProvince.setText(province);
			tvJun.setText(jun);
			tvFang.setText(fang);
			viewProvince.setVisibility(View.INVISIBLE);
			viewJun.setVisibility(View.INVISIBLE);
			viewFang.setVisibility(View.VISIBLE);
			ivChoose1.setVisibility(View.VISIBLE);
			ivChoose2.setVisibility(View.VISIBLE);
			ivChoose3.setVisibility(View.VISIBLE);
			lvList.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
			rl.setVisibility(View.GONE);
		}
	}

}
