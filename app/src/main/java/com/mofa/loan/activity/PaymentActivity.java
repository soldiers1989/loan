package com.mofa.loan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mofa.loan.R;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;

public class PaymentActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		initview();
		Log.i("MOFA", "Payment---onCreate");
	}
	
	private long timeIn;
	private long timeOut;
	
	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "Payment---onStart:" + timeIn);
	}
	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "Payment---onStop:" + timeOut);
		Log.i("MOFA", "Payment---Show:" + (timeOut - timeIn));
	}

	private void initview() {
		findViewById(R.id.ll_bankback).setOnClickListener(this);
		findViewById(R.id.ll_otherback).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void processMessage(Message message) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_bankback:
			Intent intent = new Intent(PaymentActivity.this,
					BankPayActivity.class);
			startActivity(intent);
			break;

		case R.id.ll_otherback:
			setDialog(getResources().getString(R.string.wait_version));
			break;

		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	private void setDialog(String Message) {
		new AlertDialog(PaymentActivity.this)
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
