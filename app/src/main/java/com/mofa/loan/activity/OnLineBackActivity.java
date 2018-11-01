package com.mofa.loan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.lib.SendOrderRequest;
import com.mofa.loan.pojo.SendOrderBean;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Commons;
import com.mofa.loan.utils.Constant;
import com.mofa.loan.utils.Formatdou;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.view.MyProgressDialog;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;

import org.json.JSONObject;

public class OnLineBackActivity extends BaseActivity implements View.OnClickListener, SendOrderRequest.SendOrderRequestOnResult {

    private EditText etEmail;
    private EditText etPartMoney;
    private TextView tvBack;

    private MyProgressDialog dialog;

    private int hkState;
    private String backMoney;
    private String realname;
    private String address;
    private String mobilephone;
    private String yqstate = "1";
    private int dataYQ15;
    private int dataYQ30;
    private int overdueLixi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onlineback);
        initview();
    }

    private void initview() {
        hkState = getIntent().getIntExtra("hkState", 0);
        dataYQ15 = getIntent().getIntExtra("dataYQ15", 0);
        dataYQ30 = getIntent().getIntExtra("dataYQ30", 0);
        overdueLixi = getIntent().getIntExtra("overdueLixi", 0);
        backMoney = getIntent().getStringExtra("backmoney");
        realname = getIntent().getStringExtra("realname");
        address = getIntent().getStringExtra("address");
        mobilephone = getIntent().getStringExtra("mobilephone");
        findViewById(R.id.btn_next).setOnClickListener(this);
        etEmail = findViewById(R.id.et_email);
        etPartMoney = findViewById(R.id.et_part_money);
        etPartMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("MOFA", "s:" + s.toString() + "start:" + start + "before:" + before + "count:" + count);
                String a = s.toString().replace(",", "").replace(".", "");
                int selection = 0;
                try {
                    etPartMoney.removeTextChangedListener(this);
                    Log.i("MOFA", a + ":a");
//                if (a.length() > 10) {
//                    etPartMoney.setText(Formatdou.formatdou3(a.substring(0, 10)));
//                } else {
                    if (a.isEmpty()) {
                        etPartMoney.setText(a);
                    } else {
                        selection = Formatdou.formatdou3(a).length();
                        etPartMoney.setText(Formatdou.formatdou3(a));
                    }
//                }
//
//                    if (a.length() % 3 == 1 && a.length() > 3) {
//                        etPartMoney.setSelection(start + count + 1 - before);
//                    } else {
//                        etPartMoney.setSelection(start + count - before);
//                    }
                    etPartMoney.setSelection(etPartMoney.getText().toString().trim().length());
                }catch (IndexOutOfBoundsException e) {
                    Log.i("MOFA","长度超出：" + e.getMessage());
                    e.printStackTrace();
                    ToastUtil.showShort(OnLineBackActivity.this, "max:16");
                    String b = Formatdou.formatdou3(a.substring(0, a.length()-1));
                    etPartMoney.setText(b);
                    selection = b.length();
//                    etPartMoney.setSelection(start+count-1 - before);
                    etPartMoney.setSelection(etPartMoney.getText().toString().trim().length());
                }catch (Exception e) {
                    Log.i("MOFA","错误：" + e.getStackTrace());
                    ToastUtil.showShort(OnLineBackActivity.this, "max:16");
                } finally {
                    etPartMoney.addTextChangedListener(this);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        tvBack = findViewById(R.id.tv_money);
        Log.i("MOFA", "hkstate:" + hkState + "dataYQ15:" + dataYQ15 + "dataYQ30:" + dataYQ30 + "overdueLixi:" + overdueLixi);
        if (hkState == 2) {
            tvBack.setText(Formatdou.formatdou((dataYQ15 + overdueLixi) + ""));
        } else {
            tvBack.setText(backMoney);
        }
        TextView tvBackType = findViewById(R.id.tv_type);
        TextView tv1 = findViewById(R.id.tv_delay_days);
        RadioGroup radioGroup = findViewById(R.id.rg_delay);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_15) {
                    yqstate = "1";
                    tvBack.setText(Formatdou.formatdou((dataYQ15 + overdueLixi) + ""));
                } else {
                    yqstate = "2";
                    tvBack.setText(Formatdou.formatdou((dataYQ30 + overdueLixi) + ""));
                }
            }
        });
        TextView tv2 = findViewById(R.id.tv_part_money);
        LinearLayout linearLayout = findViewById(R.id.ll_part_money);
        if (hkState == 0) {
            tvBackType.setText(getResources().getString(R.string.all_money));
            tv1.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        } else if (hkState == 1) {
            tvBackType.setText(getResources().getString(R.string.part_money));
            tv1.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
        } else {
            tvBackType.setText(getResources().getString(R.string.delay_money));
            tv2.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }

        dialog = new MyProgressDialog(this, "");
    }

    private long timeIn;
    private long timeOut;

    @Override
    public void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "OnLineBackActivity---onStart:" + timeIn);
    }

    @Override
    public void onStop() {
        super.onStop();
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "OnLineBackActivity---onStop:" + timeOut);
        Log.i("MOFA", "OnLineBackActivity---Show:" + (timeOut - timeIn));
    }


    @Override
    public void processMessage(Message message) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                String email = etEmail.getText().toString();
                if (hkState == 1) {
                    backMoney = etPartMoney.getText().toString().trim();
                    backMoney = backMoney.replace(",", "").replace(".", "");
                } else {
                    backMoney = tvBack.getText().toString().trim().replace(",", "").replace(".", "");
                }

                Log.i("MOFA", "还款金额:" + backMoney);

                if (!realname.equalsIgnoreCase("")) {
                    if (!backMoney.equalsIgnoreCase("") && Integer.valueOf(backMoney) >= 100000) {
                        if (!email.equalsIgnoreCase("")) {
                            if (!mobilephone.equalsIgnoreCase("")) {
                                if (!address.equalsIgnoreCase("")) {
                                    dialog.show();
                                    SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("yqstate", yqstate);
                                    editor.putString("state", hkState + "");
                                    editor.commit();
                                    sendOrderObject(realname, backMoney, email, mobilephone, address);
                                } else {
                                    showErrorDialog(getString(R.string.error_address), false);
                                }
                            } else {
                                showErrorDialog(getString(R.string.error_mobile), false);
                            }
                        } else {
                            showErrorDialog(getString(R.string.error_email), false);
                        }
                    } else {
                        showErrorDialog(getString(R.string.error_amount), false);
                    }
                } else {
                    showErrorDialog(getString(R.string.error_name_order), false);
                }

                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    private void sendOrderObject(String fullName, String amount, String email, String phoneNumber, String address) {
        SendOrderBean sendOrderBean = new SendOrderBean();
        sendOrderBean.setFunc("sendOrder");
        sendOrderBean.setVersion("1.0");
        sendOrderBean.setMerchantID(Constant.MERCHANT_ID);
        sendOrderBean.setMerchantAccount("nhant@peacesoft.net");
        sendOrderBean.setOrderCode("123456DEMO");
        sendOrderBean.setTotalAmount(Integer.valueOf(amount));
        sendOrderBean.setCurrency("vnd");
        sendOrderBean.setLanguage("vi");
        sendOrderBean.setReturnUrl(Constant.RETURN_URL);
        sendOrderBean.setCancelUrl(Constant.CANCEL_URL);
        sendOrderBean.setNotifyUrl(Constant.NOTIFY_URL);
        sendOrderBean.setBuyerFullName(fullName);
        sendOrderBean.setBuyerEmail(email);
        sendOrderBean.setBuyerMobile(phoneNumber);
        sendOrderBean.setBuyerAddress(address);

        String checksum = getChecksum(sendOrderBean);
        sendOrderBean.setChecksum(checksum);

        SendOrderRequest sendOrderRequest = new SendOrderRequest();
        sendOrderRequest.execute(getApplicationContext(), sendOrderBean);
        sendOrderRequest.getSendOrderRequestOnResult(this);
        dialog.dismiss();
    }

    @Override
    public void onSendOrderRequestOnResult(boolean result, String data) {
        if (result == true) {
            Log.i("MOFA", data);
            try {
                JSONObject objResult = new JSONObject(data);
                String responseCode = objResult.getString("response_code");
                if (responseCode.equalsIgnoreCase("00")) {
                    String tokenCode = objResult.getString("token_code");
                    String checkoutUrl = objResult.getString("checkout_url");

                    Intent intentCheckout = new Intent(getApplicationContext(), CheckOutActivity.class);
                    intentCheckout.putExtra(CheckOutActivity.TOKEN_CODE, tokenCode);
                    intentCheckout.putExtra(CheckOutActivity.CHECKOUT_URL, checkoutUrl);
                    startActivity(intentCheckout);
                    finish();
                } else {
                    dialog.dismiss();
                    showErrorDialog(Commons.getCodeError(getApplicationContext(), responseCode), false);
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
            }
        }
    }

    private void showErrorDialog(String message, final boolean isExit) {
        new com.mofa.loan.utils.AlertDialog(OnLineBackActivity.this)
                .builder().setMsg(message).setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.OK), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

    private void showErrorDialog2(String message, final boolean isExit) {
        final Dialog mSuccessDialog = new Dialog(OnLineBackActivity.this);
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

    private String getChecksum(SendOrderBean sendOrderBean) {
        String stringSendOrder = sendOrderBean.getFunc() + "|" +
                sendOrderBean.getVersion() + "|" +
                sendOrderBean.getMerchantID() + "|" +
                sendOrderBean.getMerchantAccount() + "|" +
                sendOrderBean.getOrderCode() + "|" +
                sendOrderBean.getTotalAmount() + "|" +
                sendOrderBean.getCurrency() + "|" +
                sendOrderBean.getLanguage() + "|" +
                sendOrderBean.getReturnUrl() + "|" +
                sendOrderBean.getCancelUrl() + "|" +
                sendOrderBean.getNotifyUrl() + "|" +
                sendOrderBean.getBuyerFullName() + "|" +
                sendOrderBean.getBuyerEmail() + "|" +
                sendOrderBean.getBuyerMobile() + "|" +
                sendOrderBean.getBuyerAddress() + "|" +
                Constant.MERCHANT_PASSWORD;
        String checksum = Commons.md5(stringSendOrder);

        return checksum;
    }
}
