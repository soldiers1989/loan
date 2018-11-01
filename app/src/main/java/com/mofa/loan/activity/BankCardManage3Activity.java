package com.mofa.loan.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * 银行卡管理
 *
 * @author Administrator
 */
public class BankCardManage3Activity extends BaseActivity implements
        OnClickListener {

    private TextView tvBankName, tvBankCard, tvName, tvCMND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MOFA", "BankCardManager---onCreate");
        setContentView(R.layout.activity_band_card_manage3);
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
        mHandler = new MyHandler(this);
        TextView title_txt_center = findViewById(R.id.title_txt_center);
        title_txt_center.setText(getResources().getString(R.string.bank_card_manage));
        findViewById(R.id.back).setOnClickListener(this);
        tvBankName = findViewById(R.id.tv_bankname);
        tvBankCard = findViewById(R.id.tv_bankcard);
        tvName = findViewById(R.id.tv_name);
        tvCMND = findViewById(R.id.tv_cmnd);

        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String UserId = sp.getString("userid", "");
        HttpUtils.doGetAsyn(Config.CARD_BANAME_CORD + "&userid=" + UserId
                + "&type=1" + "&new=" + MD5Util.md5("new" + UserId), mHandler, Config.CODE_CARD_MANAGE);

    }

    private MyHandler mHandler;

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


    @Override
    public void processMessage(Message message) {

    }

    private class MyHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;
        public MyHandler (Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            String result = msg.obj.toString();
            switch (msg.what) {
                case Config.CODE_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), "system error");
                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), activityWeakReference.get().getResources().getString(R.string.url_error));
                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), activityWeakReference.get().getResources().getString(R.string.network_error));
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
                            tvCMND.setText(idnoStr);
                            tvBankName.setText(bankname[0]);
                            tvBankCard.setText(cardno);
                        }
                    } catch (JSONException e) {
                        Log.e("MOFA", e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
