package com.mofa.loan.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * 银行卡管理
 * 
 * @author Administrator
 * 
 */
public class BankCardManageActivity extends BaseActivity implements
        OnClickListener {

	private TextView tvBankName, tvCardHolder, tvName, tvCMND;
	private ImageView iv1, iv2, iv3, iv4;
	private ImageView[] numberImage = { iv1, iv2, iv3, iv4 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("MOFA", "BankCardManager---onCreate");
		setContentView(R.layout.activity_band_card_manage);
		initView();
	}
	
	private long timeIn;
	private long timeOut;
	
	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "BankCardManager---onStart:" + timeIn);
	}
	@Override
	protected void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "BackMoneyActivity---onStop:" + timeOut);
		Log.i("MOFA", "BackMoneyActivity---Show:" + (timeOut - timeIn));
	}

	private void initView() {
		 TextView title_txt_center = findViewById(R.id.title_txt_center);
		 title_txt_center.setText(getResources().getString(R.string.bank_card_manage));
		findViewById(R.id.back).setOnClickListener(this);
		tvBankName = findViewById(R.id.tv_bankname);
		tvCardHolder = findViewById(R.id.tv_holder);
		tvName = findViewById(R.id.tv_name);
		tvCMND = findViewById(R.id.tv_cmnd);

		iv1 = findViewById(R.id.iv1);
		iv2 = findViewById(R.id.iv2);
		iv3 = findViewById(R.id.iv3);
		iv4 = findViewById(R.id.iv4);

		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		String UserId = sp.getString("userid", "");
		HttpUtils.doGetAsyn(Config.CARD_BANAME_CORD + "&userid=" + UserId
				+ "&type=1" + "&new=" + MD5Util.md5("new" + UserId), mHandler, Config.CODE_CARD_MANAGE);

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String result = msg.obj.toString();
			switch (msg.what) {
			case Config.CODE_ERROR:
				ToastUtil.showShort(BankCardManageActivity.this, "system error");
				break;
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(BankCardManageActivity.this, BankCardManageActivity.this.getResources().getString(R.string.url_error));
				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(BankCardManageActivity.this, BankCardManageActivity.this.getResources().getString(R.string.network_error));
				break;
			case Config.CODE_CARD_MANAGE:
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (0 == jsonObject.getInt("error")) {

						String cardno = jsonObject.getString("cardno");
						String idnoStr = jsonObject.getString("idno");
						String realname = jsonObject.getString("realname");
						String[] bankname = jsonObject.getString("bankname").split("-");
						tvName.setText(realname);
						tvCardHolder.setText(realname);
						tvCMND.setText(idnoStr);
						tvBankName.setText(bankname[0]);
						for (int i = 0; i < 4; i++) {
							setNumber(i,
									Integer.parseInt(cardno.charAt(i) + ""));
						}
					}
				} catch (JSONException e) {
					Log.e("MOFA", e.getMessage());
				}
				break;
			default:
				break;
			}
		}
    };

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

	protected void setNumber(int j, int i) {
		ImageView v = null;
		if (j == 0) {
			v = iv1;
		} else if (j == 1) {
			v = iv2;
		} else if (j == 2) {
			v = iv3;
		} else if (j == 3) {
			v = iv4;
		}
		if (i == 0) {
			v.setImageResource(R.drawable.number0);
		} else if (i == 1) {
			v.setImageResource(R.drawable.number1);
		} else if (i == 2) {
			v.setImageResource(R.drawable.number2);
		} else if (i == 3) {
			v.setImageResource(R.drawable.number3);
		} else if (i == 4) {
			v.setImageResource(R.drawable.number4);
		} else if (i == 5) {
			v.setImageResource(R.drawable.number5);
		} else if (i == 6) {
			v.setImageResource(R.drawable.number6);
		} else if (i == 7) {
			v.setImageResource(R.drawable.number7);
		} else if (i == 8) {
			v.setImageResource(R.drawable.number8);
		} else if (i == 9) {
			v.setImageResource(R.drawable.number9);
		}
	}

	@Override
	public void processMessage(Message message) {

	}

}
