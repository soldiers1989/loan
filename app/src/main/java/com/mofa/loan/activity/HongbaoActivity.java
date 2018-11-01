package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class HongbaoActivity extends BaseActivity {

	private TextView tvHongbaoTime;
	private TextView tvEmptyHongbao;
	private TextView tvHongbaoje;
	private LinearLayout llHongbao;
	private View view;
	private View viewReason;
	
	private String hongbao = "1";
	public static int hongbaoid = 1;
	private String hongbaosj;
	private String fxhongbao;
	private String hongbaoje;
	private String oldhongbao;
	private String hongbaoenddate;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String reString = msg.obj.toString();
			switch (msg.what) {
			case Config.CODE_HOME_INIT:
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(reString);
					hongbao = jsonObject.getString("hongbao");
					hongbaosj = jsonObject.getString("hongbaosj");
					oldhongbao = jsonObject.getString("oldhongbao");
					hongbaoenddate = jsonObject.getString("hongbaoenddate");
					fxhongbao = jsonObject.getString("fxhongbao");
					hongbaoje = jsonObject.getString("hongbaoje");
					tvHongbaoTime.setText(hongbaoenddate);
					tvHongbaoje.setText(hongbaoje + " ₫");
					SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putString("hongbaoenddate", hongbaoenddate);
					editor.putString("hongbao", hongbao);
					editor.putString("hongbaosj", hongbaosj);
					editor.putString("oldhongbao", oldhongbao);
					editor.putString("fxhongbao", fxhongbao);
					editor.commit();
					if ("1".equalsIgnoreCase(fxhongbao)) {

						if ("1".equalsIgnoreCase(oldhongbao)) {
							llHongbao.setVisibility(View.GONE);
							// tvEmptyHongbao.setVisibility(View.VISIBLE);
							setDialog();
						} else {
							if ("1".equalsIgnoreCase(hongbao)) {
								view.setVisibility(View.VISIBLE);
								viewReason.setVisibility(View.VISIBLE);
								viewReason.setBackgroundResource(R.drawable.used);
							} else if ("1".equalsIgnoreCase(hongbaosj)) {
								view.setVisibility(View.VISIBLE);
								viewReason.setVisibility(View.VISIBLE);
								viewReason.setBackgroundResource(R.drawable.overdue);
							}
						}
					} else {
						llHongbao.setVisibility(View.GONE);
						setDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case Config.CODE_ERROR:
				ToastUtil.showShort(HongbaoActivity.this, "system error");
				break;
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(HongbaoActivity.this,
						getResources().getString(R.string.url_error));

				Log.i("MOFA", "-HongbaoActivity"
						+ getResources().getString(R.string.url_error));
				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(HongbaoActivity.this,
						getResources().getString(R.string.network_error));

				Log.i("MOFA", "-HongbaoActivity"
						+ getResources().getString(R.string.network_error));
				break;
			default:
				break;
			}
		}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_choose_hongbao);
		Log.i("MOFA", "HongbaoActivity---onCreate");
		initview();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		String userid = sp.getString("userid", "");
		String url = Config.HOME_INIT + "&jkld=" + userid + "&qwer="
				+ MD5Util.md5(userid);

		HttpUtils.doGetAsyn(url, mHandler, Config.CODE_HOME_INIT);
		super.onResume();
	}
	
	private long timeIn;
	private long timeOut;
	
	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "HongbaoActivity---onStart:" + timeIn);
	}
	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "HongbaoActivity---onStop:" + timeOut);
		Log.i("MOFA", "HongbaoActivity---Show:" + (timeOut - timeIn));
	}

	private void initview() {
		tvHongbaoje = findViewById(R.id.tv_hongbaomoney);
		tvHongbaoTime = findViewById(R.id.tv_hongbao_time);
		tvEmptyHongbao = findViewById(R.id.tv_empty_hongbao);
		llHongbao = findViewById(R.id.ll_hongbao);
		llHongbao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HongbaoActivity.this,
						HongbaoDetailActivity.class);
				startActivity(intent);
			}
		});
		view = findViewById(R.id.view);
		viewReason = findViewById(R.id.view_reason);
		TextView tvCenter = findViewById(R.id.title_txt_center);
		tvCenter.setText("Phiếu khuyến mãi");
//		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
//		hongbaoenddate = sp.getString("hongbaoenddate", "");
//		hongbaosj = sp.getString("hongbaosj", "1");
//		oldhongbao = sp.getString("oldhongbao", "1");
//		hongbao = sp.getString("hongbao", "1");
//		fxhongbao = sp.getString("fxhongbao", "1");
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	

	private void setDialog() {
		new com.mofa.loan.utils.AlertDialog(HongbaoActivity.this)
				.builder()
				.setMsg("Không có phiếu khuyến mãi!")
				.setCancelable(false)
				.setNegativeButton(getResources().getString(R.string.OK),
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								finish();
							}
						}).show();
	}

	@Override
	public void processMessage(Message message) {

	}

}
