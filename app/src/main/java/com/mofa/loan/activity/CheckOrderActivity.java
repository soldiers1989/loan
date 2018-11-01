package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.mofa.loan.R;
import com.mofa.loan.lib.CheckOrderRequest;
import com.mofa.loan.pojo.CheckOrderBean;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Commons;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.Constant;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.RSA.RSAUtils;
import com.mofa.loan.utils.ToastUtil;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by DucChinh on 6/14/2016.
 */
public class CheckOrderActivity extends BaseActivity implements CheckOrderRequest.CheckOrderRequestOnResult, View.OnClickListener {

    public static final String TOKEN_CODE = "token_code";

    private RelativeLayout txtData;
    private ProgressView mProgressView;

    private String mTokenCode = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.CODE_CONTACT_SUCCESS:
                    String returnStr = msg.obj.toString();
                    try {
                        JSONObject obj = new JSONObject(returnStr);
                        int error = obj.getInt("error");
                        if (error == 0) {


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(CheckOrderActivity.this, getResources()
                            .getString(R.string.url_error));

                    break;
                case Config.CODE_ERROR:
                    ToastUtil.showShort(CheckOrderActivity.this, "system error");

                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(CheckOrderActivity.this, getResources()
                            .getString(R.string.network_error));

                    break;

                case Config.CODE_CONTACT_FAILED:
                    ToastUtil.showShort(CheckOrderActivity.this,
                            "something wrong");
                    break;
                case Config.CODE_CONTACT_FAIL:
                    ToastUtil.showShort(CheckOrderActivity.this, "upload wrong");

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkorder);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mTokenCode = extras.getString(TOKEN_CODE, "");
        }

        initView();
    }

    @Override
    public void processMessage(Message message) {

    }

    private void initView() {
        txtData = findViewById(R.id.activity_checkorder_txtData);

        mProgressView = (ProgressView) findViewById(R.id.activity_checkorder_progressView);
        findViewById(R.id.back).setOnClickListener(this);

        findViewById(R.id.ll_success).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_success).setOnClickListener(this);

        checkOrderObject();
    }

    private void checkOrderObject() {
        CheckOrderBean checkOrderBean = new CheckOrderBean();
        checkOrderBean.setFunc("checkOrder");
        checkOrderBean.setVersion("1.0");
        checkOrderBean.setMerchantID(Constant.MERCHANT_ID);
        checkOrderBean.setTokenCode(mTokenCode);

        String checksum = getChecksum(checkOrderBean);
        checkOrderBean.setChecksum(checksum);

        CheckOrderRequest checkOrderRequest = new CheckOrderRequest();
        checkOrderRequest.execute(getApplicationContext(), checkOrderBean);
        checkOrderRequest.getCheckOrderRequestOnResult(this);
        txtData.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    private String getChecksum(CheckOrderBean checkOrderBean) {
        String stringSendOrder = checkOrderBean.getFunc() + "|" +
                checkOrderBean.getVersion() + "|" +
                checkOrderBean.getMerchantID() + "|" +
                checkOrderBean.getTokenCode() + "|" +
                Constant.MERCHANT_PASSWORD;
        String checksum = Commons.md5(stringSendOrder);

        return checksum;
    }

    @Override
    public void onBackPressed() {
        Intent intentMain = new Intent(getApplicationContext(), OnLineBackActivity.class);
        startActivity(intentMain);
        finish();
    }

    @Override
    public void onCheckOrderRequestOnResult(boolean result, String data) {
        if (result == true) {
            try {
                JSONObject objResult = new JSONObject(data);
                String responseCode = objResult.getString("response_code");
                if (responseCode.equalsIgnoreCase("00")) {
//                    String response_code = objResult.getString("response_code");
//                    String receiver_email = objResult.getString("receiver_email");
//                    String order_code = objResult.getString("order_code");
//                    int total_amount = objResult.getInt("total_amount");
//                    String currency = objResult.getString("currency");
//                    String language = objResult.getString("language");
//                    String return_url = objResult.getString("return_url");
//                    String cancel_url = objResult.getString("cancel_url");
//                    String notify_url = objResult.getString("notify_url");
//                    String buyer_full_name = objResult.getString("buyer_fullname");
//                    String buyer_email = objResult.getString("buyer_email");
//                    String buyer_mobile = objResult.getString("buyer_mobile");
//                    String buyer_address = objResult.getString("buyer_address");
                    int transaction_id = objResult.getInt("transaction_id");
//                    int transaction_status = objResult.getInt("transaction_status");
                    int transaction_amount = objResult.getInt("transaction_amount");
//                    String transaction_currency = objResult.getString("transaction_currency");
//                    int transaction_escrow = objResult.getInt("transaction_escrow");
                    JsonObject object = new JsonObject();
                    JsonObject tokenObject = new JsonObject();
                    SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                    String userid = sp.getString("userid", "");
                    String yqstate = sp.getString("yqstate", "");
                    String state = sp.getString("state", "");
                    tokenObject.addProperty("parorder", transaction_id);
                    tokenObject.addProperty("hkmoney", transaction_amount);
                    tokenObject.addProperty("userid", userid);
                    tokenObject.addProperty("yqstate", yqstate);
                    tokenObject.addProperty("state", state);
                    tokenObject.addProperty("edg", MD5Util.md5(userid + "we"));
                    String json = tokenObject.toString();
                    String afterencrypt = RSAUtils.encrypt(Config.PUCLIC_KEY, json);
                    object.addProperty("token", afterencrypt);
                    String url2 = Config.URL
                            + "servlet/current/JBDcms3Action?function=OnlinePayOlavamentl";
                    HttpUtils.httpPostJSONAsync(url2, object.toString(), mHandler);
                    txtData.setVisibility(View.VISIBLE);
                    mProgressView.setVisibility(View.GONE);
                } else {
                    mProgressView.setVisibility(View.GONE);
                    showErrorDialog(Commons.getCodeError(getApplicationContext(), responseCode), false);
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
            }
        }
    }

    private void showErrorDialog(String message, final boolean isExit) {
        final Dialog mSuccessDialog = new Dialog(CheckOrderActivity.this);
        mSuccessDialog.setContentView(R.layout.dialog_success);
        mSuccessDialog.setCancelable(false);
        mSuccessDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mSuccessDialog.getWindow().setGravity(Gravity.CENTER);

        TextView txtContent = (TextView) mSuccessDialog.findViewById(R.id.dialog_success_txtContent);
        txtContent.setText(message);
        Button btnClose = (Button) mSuccessDialog.findViewById(R.id.dialog_success_btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuccessDialog.dismiss();
                if (isExit) {
                    finish();
                }
            }
        });

        mSuccessDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
            case R.id.btn_success:
                Intent intent = new Intent(CheckOrderActivity.this, IndexActivity.class);
                intent.putExtra("id", 2);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }
}
