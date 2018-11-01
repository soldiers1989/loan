package com.mofa.loan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.utils.AlertDialog;

public class MoneyDetailActivity extends Activity implements OnClickListener {

	private TextView tvAllCash;
	private TextView tvCash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_money_detail);
		super.onCreate(savedInstanceState);
		initview();
	}
	
	private long timeIn;
	private long timeOut;

	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "ShareActivity---onStart:" + timeIn);
	}

	@Override
	protected void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "ShareActivity---onStop:" + timeOut);
		Log.i("MOFA", "ShareActivity---Show:" + (timeOut - timeIn));
	}
	

	private void initview() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.btn_cash).setOnClickListener(this);
		findViewById(R.id.ll_refer).setOnClickListener(this);
		findViewById(R.id.ll_cash_record).setOnClickListener(this);
		findViewById(R.id.ll_sale).setOnClickListener(this);
		findViewById(R.id.ll_share).setOnClickListener(this);
		tvAllCash = findViewById(R.id.tv_myallmoney);
		tvCash = findViewById(R.id.tv_mymoney);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.btn_cash:
			setDialog(getResources().getString(R.string.wait_version));
			break;

		case R.id.ll_refer:
			setDialog(getResources().getString(R.string.wait_version));
			break;

		case R.id.ll_cash_record:
			setDialog(getResources().getString(R.string.wait_version));
			break;

		case R.id.ll_sale:
			Intent intent3 = new Intent(MoneyDetailActivity.this,
					HongbaoActivity.class);
			startActivity(intent3);
			break;

		case R.id.ll_share:
			Intent intent4 = new Intent(MoneyDetailActivity.this,
					ShareActivity.class);
			startActivity(intent4);
			break;

		default:
			break;
		}
	}

	private void setDialog(String Message) {
		new AlertDialog(MoneyDetailActivity.this)
				.builder()
				.setMsg(Message)
				.setNegativeButton(getResources().getString(R.string.OK),
						new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}).show();
	}

}
