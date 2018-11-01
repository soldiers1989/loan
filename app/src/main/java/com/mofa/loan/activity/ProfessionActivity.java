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

import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfessionActivity extends BaseActivity implements OnClickListener {

	private boolean choose;
	private String userid;
	private int profession;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(ProfessionActivity.this, getResources().getString(R.string.url_error));

				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(ProfessionActivity.this, getResources().getString(R.string.network_error));

				break;

			case Config.CODE_TIMEOUT_ERROR:
				ToastUtil.showShort(ProfessionActivity.this, "TIME OUT");

				break;

			case Config.CODE_GETPROFESSION:
				String result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(result);
					int error = json.getInt("err");

					ToastUtil.showShort(ProfessionActivity.this, json.getString("msg"));

					if (error == 0) {
						SharedPreferences sp = getSharedPreferences("config",
								MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putInt("profession", profession);
						editor.commit();
						finish();
					} else {
					}

				} catch (JSONException e) {
					ToastUtil.showShort(ProfessionActivity.this, getResources().getString(R.string.data_parsing_error));

					Log.e("MOFA", e.getMessage());
				}

				break;

			default:
				break;
				
			}
		}
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profession);
		initview();
		choose = getIntent().getBooleanExtra("chooseprofession", false);
		userid = getIntent().getStringExtra("userid");
	}

	private void initview() {
		findViewById(R.id.iv_student).setOnClickListener(this);
		findViewById(R.id.iv_worker).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void processMessage(Message message) {

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.iv_worker:
			profession = 2;
			if (choose) {
				//H5页面注册的人，需要补充选择职业
				String url = Config.GETPROFESSION + "&userid=" + userid + "&profession=2";
				HttpUtils.doGetAsyn(url, mHandler, Config.CODE_GETPROFESSION);
			} else {
				intent.setClass(ProfessionActivity.this, RegisterActivity.class);
				intent.putExtra("profession", "2");
				startActivity(intent);
				finish();
			}
			break;

		case R.id.iv_student:
			profession = 1;
			if (choose) {
				String url = Config.GETPROFESSION + "&userid=" + userid + "&profession=1";
				HttpUtils.doGetAsyn(url, mHandler, Config.CODE_GETPROFESSION);
			} else {
				intent.setClass(ProfessionActivity.this, RegisterActivity.class);
				intent.putExtra("profession", "1");
				startActivity(intent);
				finish();
			}
			break;

		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

}
