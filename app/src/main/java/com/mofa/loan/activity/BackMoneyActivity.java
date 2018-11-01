package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.Formatdou;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 还款
 *
 * @author Administrator
 */
public class BackMoneyActivity extends BaseActivity implements OnClickListener {

    private TextView tvBackMoney;
    private TextView tvLoanDay;
    private TextView tvBackDay;
    private TextView tvOverdueMoney;
    private TextView tvOverdueDay;
    private LinearLayout llBankBack;
    private LinearLayout llOtherBack;

    private String back;
    private int hkState;
    private int dataYQ15;
    private int dataYQ30;
    private int overdueLixi;
    private String realname;
    private String address;
    private String mobilephone;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            String result = msg.obj.toString();
            switch (msg.what) {
                case Config.CODE_ERROR:
                    ToastUtil.showShort(BackMoneyActivity.this, "system error");
                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(BackMoneyActivity.this, BackMoneyActivity.this.getResources().getString(R.string.url_error));
                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(BackMoneyActivity.this, BackMoneyActivity.this.getResources().getString(R.string.network_error));
                    break;
                case Config.CODE_BACKMONEYINIT:
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        // JSONObject backtime =
                        // jsonObject.getJSONObject("fkdz_time");
                        // JSONObject outtime =
                        // jsonObject.getJSONObject("hkyq_time");
                        tvLoanDay.setText(jsonObject.getString("fkdz_time")
                                .substring(0, 10));
                        tvBackDay.setText(jsonObject.getString("hkyq_time")
                                .substring(0, 10));

                        int all = Integer.valueOf(jsonObject
                                .getString("sjsh_money").replace(",", ""));

                        int day = jsonObject.getInt("yuq_ts");
                        tvOverdueDay.setText(day + " "
                                + getResources().getString(R.string.day));
                        overdueLixi = Integer.valueOf(jsonObject.getString("yuq_lx")
                                .replace(",", ""));
                        // int annualrate = jsonObject.getInt("annualrate");
                        int date = jsonObject.getInt("jk_date");
                        hkState = jsonObject.getInt("hkstate");
                        realname = jsonObject.getString("realname");
                        address = jsonObject.getString("address");
                        mobilephone = jsonObject.getString("mobilephone");
                        dataYQ15 = jsonObject.getInt("dataYQ15");
                        dataYQ30 = jsonObject.getInt("dataYQ30");
                        tvOverdueMoney.setText(Formatdou.formatdou(overdueLixi + "")
                                + BackMoneyActivity.this.getResources().getString(
                                R.string.yuan2));
                        back = Formatdou.formatdou((all + overdueLixi) + "");
                        // Amt="0.01";
                        tvBackMoney.setText(back + "");
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
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_back_money);
        super.onCreate(savedInstanceState);
        Log.i("MOFA", "BackMoneyActivity---onCreate");
        initview();
        initdata();
    }

    private long timeIn;
    private long timeOut;

    @Override
    protected void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "BackMoneyActivity---onStart:" + timeIn);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "BackMoneyActivity---onStop:" + timeOut);
        Log.i("MOFA", "BackMoneyActivity---Show:" + (timeOut - timeIn));
    }

    private void initdata() {
        String jkid = getIntent().getStringExtra("jkid");
        HttpUtils.doGetAsyn(Config.BACKMONEYINIT_CORD + "&jkid="
                        + jkid + "&hv=" + MD5Util.md5(jkid), mHandler,
                Config.CODE_BACKMONEYINIT);
    }

    private void initview() {
        findViewById(R.id.back).setOnClickListener(this);

        tvBackMoney = findViewById(R.id.tv_money);
        tvLoanDay = findViewById(R.id.tv_loan_time);
        tvBackDay = findViewById(R.id.tv_back_time);
        tvOverdueMoney = findViewById(R.id.tv_overdue_money);
        tvOverdueDay = findViewById(R.id.tv_overdue_day);
        llBankBack = findViewById(R.id.ll_bankback);
        llBankBack.setOnClickListener(this);
        llOtherBack = findViewById(R.id.ll_otherback);
        llOtherBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Log.i("MOFA", "BackMoneyActivity---onClick -->  后退");
                finish();
                break;

            case R.id.ll_bankback:
                Log.i("MOFA", "BackMoneyActivity---onClick -->  去银行还款");
                Intent intentBank = new Intent(BackMoneyActivity.this,
                        BankPayActivity.class);
                startActivity(intentBank);
                break;

            case R.id.ll_otherback:
                Log.i("MOFA", "BackMoneyActivity---onClick -->  去线下还款");
                Log.i("MOFA", "" + overdueLixi);

                SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                String onlinepay = sp.getString("onlinepay", "0");
                if ("1".equals(onlinepay) || "3".equals(onlinepay)) {
                    Intent intentOther = new Intent(BackMoneyActivity.this,
                            OnLineBackActivity.class);
                    intentOther.putExtra("backmoney", back);
                    intentOther.putExtra("hkState", hkState);
                    intentOther.putExtra("realname", realname);
                    intentOther.putExtra("address", address);
                    intentOther.putExtra("mobilephone", mobilephone);
                    intentOther.putExtra("dataYQ15", dataYQ15);
                    intentOther.putExtra("dataYQ30", dataYQ30);
                    intentOther.putExtra("overdueLixi", overdueLixi);
                    startActivity(intentOther);
                } else {
                    setDialog(getResources().getString(R.string.function_not_open));
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void processMessage(Message message) {

    }

    private void setDialog(String Message) {
        new AlertDialog(BackMoneyActivity.this)
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
