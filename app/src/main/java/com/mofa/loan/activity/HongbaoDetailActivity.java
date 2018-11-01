package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HongbaoDetailActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_hongbao_detail);
		initview();
		super.onCreate(savedInstanceState);
	}
	
	private void initview() {
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		String endtime = sp.getString("hongbaoenddate", "");
		java.text.SimpleDateFormat formatter = new SimpleDateFormat(
                "dd-MM-yyyy");  
		Date date = null;
		try {
			date = formatter.parse(endtime);
		} catch (ParseException e) {
			Log.e("MOFA", e.getMessage());
		}  
		long milliseconds = date.getTime();
		String date3 = TimeUtils.parseDate4(milliseconds - 2L*24*3600*1000);
		final String time = sp.getString("hongbaosj", "1");
		final String oldhongbao = sp.getString("oldhongbao", "1");
		final String hongbao = sp.getString("hongbao", "1");
		final String fxhongbao = sp.getString("fxhongbao", "0");
		TextView tv = findViewById(R.id.tv_attention);
		tv.setText(Html.fromHtml("<font color=\"#DA3636\"> * </font> Olava giữ quyền giải thích các quy định nói trên trong phạm vi pháp luật cho phép."));
		TextView tvTime = findViewById(R.id.tv_time);
		tvTime.setText(Html.fromHtml("2.Thời hạn sử dụng: từ <font color=\"#ff7f27\"> " + date3 + " </font>  đến <font color=\"#DA3636\"> " + endtime + " </font>"));
		Button btn = findViewById(R.id.btn_go);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HongbaoDetailActivity.this, IndexActivity.class);
				intent.putExtra("id", 2);
				startActivity(intent);
			}
		});
		if (!("0".equalsIgnoreCase(hongbao) && "0".equalsIgnoreCase(oldhongbao) && "0".equalsIgnoreCase(time))) {
			btn.setVisibility(View.INVISIBLE);
		}
		if ("0".equalsIgnoreCase(fxhongbao)) {
			btn.setVisibility(View.INVISIBLE);
		}
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void processMessage(Message message) {

	}

}
