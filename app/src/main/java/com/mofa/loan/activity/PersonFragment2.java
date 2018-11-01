package com.mofa.loan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mofa.loan.R;

public class PersonFragment2 extends Fragment implements OnClickListener {

	private View view;
	private Activity ac;
	private int username;
	private int isyhbd;
	private int profession;
	private int isshenfen;
	private int isjob;
	private int islianxi, iseducation, isshebao, isschool, isfacebook;


	private RelativeLayout llID;
	private RelativeLayout llBank;
	private RelativeLayout llContact;
	private RelativeLayout llProfession;
	private RelativeLayout llFacebook;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if (view == null) {
			view = inflater
					.inflate(R.layout.fragment_person2, container, false);
		} else {
			((ViewGroup) view.getParent()).removeView(view);
		}
		Log.i("MOFA", "PersonFragment2---onCreate");
		return view;
	}
	
	@Override
	public void onResume() {
		Log.i("MOFA", "PersonFragment2---onResume:" + System.currentTimeMillis());
		super.onResume();
	}

	private long timeIn;
	private long timeOut;
	
	@Override
	public void onStart() {
		super.onStart();
		initData();
		initView();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "PersonFragment2---onStart:" + timeIn);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "PersonFragment2---onStop:" + timeOut);
		Log.i("MOFA", "PersonFragment2---Show:" + (timeOut - timeIn));
	}

	private void initData() {
		ac = getActivity();
		SharedPreferences sp = ac.getSharedPreferences("config",
                Context.MODE_PRIVATE);
		isshenfen = sp.getInt("isshenfen", 0);
		isyhbd = sp.getInt("isyhbd", 0);
		islianxi = sp.getInt("islianxi", 0);
		isschool = sp.getInt("isschool", 0);
		isjob = sp.getInt("isjob", 0);
		profession = sp.getInt("profession", 0);
		isfacebook = sp.getInt("isfacebook", 0);
		
	}

	private void initView() {
		llID = view.findViewById(R.id.ll_id);
		llID.setOnClickListener(this);
		llBank = view.findViewById(R.id.ll_bank);
		llBank.setOnClickListener(this);
		llContact = view.findViewById(R.id.ll_contact);
		llContact.setOnClickListener(this);
		llProfession = view.findViewById(R.id.ll_profession);
		llProfession.setOnClickListener(this);
		llFacebook = view.findViewById(R.id.ll_facebook);
		llFacebook.setOnClickListener(this);
		if (isyhbd == 1) {
			llBank.setBackgroundResource(R.drawable.veifiedbankback);
			TextView tv = view.findViewById(R.id.tv_bank);
			tv.setTextColor(Color.rgb(255, 255, 255));
			view.findViewById(R.id.tv_bank_todo).setVisibility(View.GONE);
		}
		if (isshenfen == 1) {
			llID.setBackgroundResource(R.drawable.veifiedidback);
			TextView tv = view.findViewById(R.id.tv_id);
			tv.setTextColor(Color.rgb(255, 255, 255));
			view.findViewById(R.id.tv_id_todo).setVisibility(View.GONE);
		}
		if (islianxi == 1) {
			llContact.setBackgroundResource(R.drawable.veifiedcontactback);
			TextView tv = view.findViewById(R.id.tv_contact);
			tv.setTextColor(Color.rgb(255, 255, 255));
			view.findViewById(R.id.tv_contact_todo).setVisibility(View.GONE);
		}
//		if (profession == 1 && isschool == 1) {
//			llProfession.setBackgroundResource(R.drawable.veifiedstudentback);
//			TextView tv = (TextView) view.findViewById(R.id.tv_profession);
//			tv.setTextColor(Color.rgb(255, 255, 255));
//			tv.setText("Xác minh trường học");
//			view.findViewById(R.id.tv_profession_todo).setVisibility(view.GONE);
//		} else if (profession == 1 && isschool == 0) {
//			llProfession.setBackgroundResource(R.drawable.unveifystudentback);
//			TextView tv = (TextView) view.findViewById(R.id.tv_profession);
//			tv.setTextColor(Color.rgb(141, 146, 158));
//			tv.setText("Xác minh trường học");
//			view.findViewById(R.id.tv_profession_todo).setVisibility(
//					view.VISIBLE);
//		} else if (profession == 2 && isjob == 1) {
//			llProfession.setBackgroundResource(R.drawable.veifiedworkback);
//			TextView tv = (TextView) view.findViewById(R.id.tv_profession);
//			tv.setTextColor(Color.rgb(255, 255, 255));
//			view.findViewById(R.id.tv_profession_todo).setVisibility(view.GONE);
//		}
//		if (isfacebook == 1) {
//			llFacebook.setBackgroundResource(R.drawable.veifiedfacebookback);
//			TextView tv = (TextView) view.findViewById(R.id.tv_facebook);
//			tv.setTextColor(Color.rgb(255, 255, 255));
//			view.findViewById(R.id.tv_facebook_todo).setVisibility(view.GONE);
//		}
		if (profession == 1 && isschool == 1) {
			llProfession.setBackgroundResource(R.drawable.veifiedworkback);
			TextView tv = view.findViewById(R.id.tv_profession);
			tv.setTextColor(Color.rgb(255, 255, 255));
			view.findViewById(R.id.tv_profession_todo).setVisibility(View.GONE);
		} else if (profession == 2 && isjob == 1) {
			llProfession.setBackgroundResource(R.drawable.veifiedworkback);
			TextView tv = view.findViewById(R.id.tv_profession);
			tv.setTextColor(Color.rgb(255, 255, 255));
			view.findViewById(R.id.tv_profession_todo).setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_id:
		case R.id.ll_bank:
			if (isyhbd == 0) {
				Intent intent = new Intent(ac, BindCardActivity.class);
				startActivity(intent);
			} else if (isshenfen == 0) {
				SharedPreferences sp = ac.getSharedPreferences("config", Context.MODE_PRIVATE);
				String sfcmnd = sp.getString("sfcmnd", null);
				if (TextUtils.isEmpty(sfcmnd)) {
					Intent intent = new Intent(ac,
							BaseInfo2Activity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(ac,
							IDCardActivity.class);
					intent.putExtra("username", sp.getString("bankname2", null));
					intent.putExtra("IDnumber", sp.getString("sfcmnd", null));
					intent.putExtra("address", sp.getString("sfaddress", null));
					intent.putExtra("addressnow", sp.getString("sfhomeaddress", null));
					intent.putExtra("gender", sp.getString("sfsex", null));
					intent.putExtra("email", sp.getString("sfemail", null));
					intent.putExtra("both", sp.getString("sfage", null));
					intent.putExtra("phonetype", sp.getString("sfphonetype", null));
					intent.putExtra("from", "base");
					startActivity(intent);
				}
			}
			break;
		case R.id.ll_contact:
			if (islianxi == 0) {
				Intent intent = new Intent(ac, RelationInfo2Activity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_profession:
			if (profession == 2 && isjob == 0) {
				Intent intent = new Intent(ac, WorkInfo3Activity.class);
				startActivity(intent);
			} else if (profession == 1 && isschool == 0) {
				Intent intent = new Intent(ac, WorkInfo3Activity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_facebook:
			if (isfacebook == 0) {
				startActivity(new Intent(ac, FacebookAcytivity.class));
			}
			break;

		default:
			break;
		}
	}
//	
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.ll_id:
//		case R.id.ll_bank:
//			if (isyhbd == 0) {
//				Intent intent = new Intent(ac, BindCardActivity.class);
//				startActivity(intent);
//			} else if (isshenfen == 0) {
//				SharedPreferences sp = ac.getSharedPreferences("config", ac.MODE_PRIVATE);
//				String sfname = sp.getString("sfname", null);
//				if (TextUtils.isEmpty(sfname)) {
//					Intent intent = new Intent(ac,
//							BaseInfo2Activity.class);
//					startActivity(intent);
//				} else {
//					Intent intent = new Intent(ac,
//							IDCardActivity.class);
//					intent.putExtra("username", sp.getString("sfname", null));
//					intent.putExtra("IDnumber", sp.getString("sfcmnd", null));
//					intent.putExtra("address", sp.getString("sfaddress", null));
//					intent.putExtra("addressnow", sp.getString("sfhomeaddress", null));
//					intent.putExtra("gender", sp.getString("sfsex", null));
//					intent.putExtra("email", sp.getString("sfemail", null));
//					intent.putExtra("both", sp.getString("sfage", null));
//					intent.putExtra("phonetype", sp.getString("sfphonetype", null));
//					intent.putExtra("from", "base");
//					startActivity(intent);
//				}
//			}
//			break;
//		case R.id.ll_contact:
//			if (islianxi == 0) {
//				Intent intent = new Intent(ac, RelationInfo2Activity.class);
//				startActivity(intent);
//			}
//			break;
//		case R.id.ll_profession:
//			if (profession == 2 && isjob == 0) {
//				Intent intent = new Intent(ac, WorkInfo2Activity.class);
//				startActivity(intent);
//			} else if (profession == 1 && isschool == 0) {
//				Intent intent = new Intent(ac, StudentInfo2Activity.class);
//				startActivity(intent);
//			}
//			break;
//		case R.id.ll_facebook:
//			if (isfacebook == 0) {
//				startActivity(new Intent(ac, FacebookAcytivity.class));
//			}
//			break;
//			
//		default:
//			break;
//		}
//	}

}
