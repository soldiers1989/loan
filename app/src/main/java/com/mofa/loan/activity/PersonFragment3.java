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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mofa.loan.R;

public class PersonFragment3 extends Fragment implements OnClickListener {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater
                    .inflate(R.layout.fragment_person3, container, false);
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
        view.findViewById(R.id.back).setVisibility(View.INVISIBLE);
        llID = view.findViewById(R.id.ll_id);
        llID.setOnClickListener(this);
        llBank = view.findViewById(R.id.ll_bank);
        llBank.setOnClickListener(this);
        llContact = view.findViewById(R.id.ll_contact);
        llContact.setOnClickListener(this);
        llProfession = view.findViewById(R.id.ll_profession);
        llProfession.setOnClickListener(this);
        if (isyhbd == 1) {
            ((ImageView) view.findViewById(R.id.iv_bank)).setImageResource(R.drawable.verfiedbank);
            ((TextView) view.findViewById(R.id.tv_bank)).setTextColor(Color.parseColor("#FF444444"));
            view.findViewById(R.id.tv_bank_todo).setVisibility(View.GONE);
            view.findViewById(R.id.iv_verfiedbank).setVisibility(View.VISIBLE);
        }
        if (isshenfen == 1) {
            ((ImageView) view.findViewById(R.id.iv_baseinfo)).setImageResource(R.drawable.verfiedbaseinfo);
            ((TextView) view.findViewById(R.id.tv_id)).setTextColor(Color.parseColor("#FF444444"));
            view.findViewById(R.id.tv_id_todo).setVisibility(View.GONE);
            view.findViewById(R.id.iv_verfiedbaseinfo).setVisibility(View.VISIBLE);
        }
        if (islianxi == 1) {
            ((ImageView) view.findViewById(R.id.iv_relationship)).setImageResource(R.drawable.verfiedrelationship);
            ((TextView) view.findViewById(R.id.tv_contact)).setTextColor(Color.parseColor("#FF444444"));
            view.findViewById(R.id.tv_contact_todo).setVisibility(View.GONE);
            view.findViewById(R.id.iv_verfiedrelationship).setVisibility(View.VISIBLE);
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
            ((ImageView) view.findViewById(R.id.iv_work)).setImageResource(R.drawable.verfiedwork);
            ((TextView) view.findViewById(R.id.tv_profession)).setTextColor(Color.parseColor("#FF444444"));
            view.findViewById(R.id.tv_profession_todo).setVisibility(View.GONE);
            view.findViewById(R.id.iv_verfiedwork).setVisibility(View.VISIBLE);
        } else if (profession == 2 && isjob == 1) {
            ((ImageView) view.findViewById(R.id.iv_work)).setImageResource(R.drawable.verfiedwork);
            ((TextView) view.findViewById(R.id.tv_profession)).setTextColor(Color.parseColor("#FF444444"));
            view.findViewById(R.id.tv_profession_todo).setVisibility(View.GONE);
            view.findViewById(R.id.iv_verfiedwork).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_id:
            case R.id.ll_bank:
                if (isyhbd == 0) {
                    Intent intent = new Intent(ac, BindCard3Activity.class);
                    startActivity(intent);
                } else if (isshenfen == 0) {
                    SharedPreferences sp = ac.getSharedPreferences("config", Context.MODE_PRIVATE);
                    String sfcmnd = sp.getString("sfcmnd", null);
                    //TODO 测试身份认证
//                    sfcmnd = "";
                    if (TextUtils.isEmpty(sfcmnd)) {
                        Intent intent = new Intent(ac,
                                BaseInfo3Activity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ac,
                                IDCard3Activity.class);
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
                    Intent intent = new Intent(ac, RelationInfo3Activity.class);
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
//				Intent intent = new Intent(ac, RelationInfo3Activity.class);
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
