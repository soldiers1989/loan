package com.mofa.loan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.CheckCodeDialog;
import com.mofa.loan.utils.Code;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.ConnectionDetector;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.TimeCount;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.utils.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class ForgetPwdOneActivity extends BaseActivity implements
        OnClickListener {

    private EditText etPhone;
    private EditText etOTP;
    private EditText etPWD;
    private EditText etCheckPWD;
    private Button btnOTP;
    private Button btnReset;

    private TimeCount time;

    private boolean status;
    private String phone;
    private String pwd;
    private String pwdCheck;
    private String rephone;
    private String reCode;

    private String isSendFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MOFA", "ForgetPwd---onStart:" + timeIn);
        setContentView(R.layout.activity_forget_pwd);
        isSendFacebook = getIntent().getStringExtra("isSendFacebook");
        phone = getIntent().getStringExtra("phone");
        if ("no".equalsIgnoreCase(isSendFacebook)) {
        } else {
            findViewById(R.id.tv_OTP).setVisibility(View.GONE);
            findViewById(R.id.ll_otp).setVisibility(View.GONE);
        }
        initView();
    }

    private long timeIn;
    private long timeOut;

    @Override
    protected void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "ForgetPwd---onStart:" + timeIn);
    }

    private void initView() {
        mHandler = new MyHandler(this);
        findViewById(R.id.ll_phone).setOnClickListener(this);
        findViewById(R.id.ll_otp).setOnClickListener(this);
        findViewById(R.id.ll_pwd).setOnClickListener(this);
        findViewById(R.id.ll_check_pwd).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        etPhone = findViewById(R.id.et_phonenumber);
        etOTP = findViewById(R.id.et_otp);
        etPWD = findViewById(R.id.et_pwd);
        etCheckPWD = findViewById(R.id.et_check_pwd);
        btnOTP = findViewById(R.id.btn_otp);
        btnOTP.setOnClickListener(this);
        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);

        status = true;

        if ("yes".equalsIgnoreCase(isSendFacebook)) {
            etPhone.setText(phone);
            etPhone.setFocusable(false);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.ll_phonenumber:
                etPhone.requestFocus();
                break;

            case R.id.ll_otp:
                etOTP.requestFocus();
                break;

            case R.id.ll_pwd:
                etPWD.requestFocus();
                break;

            case R.id.ll_check_pwd:
                etCheckPWD.requestFocus();
                break;

            case R.id.btn_otp:
                phone = etPhone.getText().toString().trim();

                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(ForgetPwdOneActivity.this, getResources().getString(R.string.warm_no_phonenumber));

                    return;
                }

                /** 去除手机号码中的空格 */
                if (phone.contains(" ")) {
                    phone = phone.replaceAll(" ", "");
                }

                /** 检查手机号码格式是否正确 */
                if (!Validator.isPhoneYueNan(phone)) {
                    ToastUtil.showShort(ForgetPwdOneActivity.this, getResources().getString(R.string.warm_phonenumber_not_match));

                    return;
                }

                if (status) {

                    ConnectionDetector cd = new ConnectionDetector(
                            ForgetPwdOneActivity.this);
                    if (cd.isConnectingToInternet()) {
                        String url = Config.URL + Config.FORGET_SEND_CODE
                                + "&mobile=" + phone + "&type=0" + "&wdy=" + MD5Util.md5(phone);
                        HttpUtils.doGetAsyn(url, mHandler,
                                Config.CODE_FORGET_SEND_PWD);
                        time = new TimeCount(120000, 1000, btnOTP, getResources()
                                .getString(R.string.reacquire),
                                getApplicationContext());
                        time.start();
                    } else {
                        ToastUtil.showShort(ForgetPwdOneActivity.this, getResources().getString(R.string.network_error));

                    }
                } else {
                    showdialog();
                }
                break;

            case R.id.btn_reset:
                reset();
                break;

            default:
                break;
        }
    }

    private void reset() {
        pwd = etPWD.getText().toString().trim();
        pwdCheck = etCheckPWD.getText().toString().trim();
        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdCheck)) {
            ToastUtil.showShort(ForgetPwdOneActivity.this, getResources().getString(R.string.warm_pwd_not_null));

            return;
        }
        if (!pwd.equalsIgnoreCase(pwdCheck)) {
            ToastUtil.showShort(ForgetPwdOneActivity.this, getResources().getString(R.string.warm_pwd_not_same));

            return;
        }
        phone = etPhone.getText().toString().trim();
        String codeStr = etOTP.getText().toString().trim();
        if (TextUtils.isEmpty(codeStr) && "no".equalsIgnoreCase(isSendFacebook)) {
            ToastUtil.showShort(ForgetPwdOneActivity.this, getResources().getString(R.string.hint_code));

            return;
        }
        if (status) {
            if ("no".equalsIgnoreCase(isSendFacebook)) {
                String urls = Config.URL + Config.CHECK_CODE + "&mobile="
                        + phone + "&u_ip=" + codeStr + "&rephone=" + rephone
                        + "&reCode=" + reCode + "&rng=" + MD5Util.md5(phone + codeStr);
                btnReset.setClickable(false);
                HttpUtils.doGetAsyn(urls, mHandler, Config.CODE_CHECk_SEND_PWD);
            } else {
                String url = Config.URL + Config.FORGET_CODE
                        + "&loginPwd=" + pwd + "&phone=" + phone + "&edg=" + MD5Util.md5(pwd);
                btnReset.setClickable(false);
                HttpUtils.doGetAsyn(url, mHandler,
                        Config.CODE_FORGET_PWD_FACEBOOK);
            }
        } else {
            showdialog();
        }
    }

    private void showdialog() {
        final CheckCodeDialog checkDialog = new CheckCodeDialog(this);
        final EditText etCode = (EditText) checkDialog.getEditText();// 方法在CustomDialog中实现
        final ImageView ivCode = (ImageView) checkDialog.getImageView();

        checkDialog.setOnSureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode = etCode.getText().toString().trim();
                String realCode = checkDialog.getCode();
                if (realCode.equalsIgnoreCase(inputCode)) {
                    status = true;
                    checkDialog.dismiss();
                    reset();
                } else {
                    ivCode.setImageBitmap(Code.getInstance().createBitmap());
                    checkDialog.setCode(Code.getInstance().getCode());
                    etCode.setText("");
                    ToastUtil.showLong(ForgetPwdOneActivity.this, "Sai mã xác nhận!");
                }
            }
        });
        checkDialog.setOnChangeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCode.setImageBitmap(Code.getInstance().createBitmap());
                checkDialog.setCode(Code.getInstance().getCode());
                etCode.setText("");
            }
        });
        checkDialog.setCanceledOnTouchOutside(true);
        checkDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                btnReset.setClickable(true);
            }
        });
        checkDialog.show();
    }

    private void showdialog2() {
        final android.app.AlertDialog phoneDialog = new android.app.AlertDialog.Builder(
                ForgetPwdOneActivity.this).create();
        LayoutInflater lay = LayoutInflater.from(ForgetPwdOneActivity.this);
        final View inflate = lay.inflate(R.layout.layout_check_dialog,
                null);
        SeekBar sb = inflate.findViewById(R.id.sb_check);
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() <= 50) {
                    return;
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress == 1000) {
                    status = true;
                    phoneDialog.dismiss();
                }
            }
        });
        phoneDialog.show();
        phoneDialog.setCanceledOnTouchOutside(false);
        phoneDialog.setContentView(inflate);
    }

    private MyHandler mHandler;

    private boolean isExit = false;
    private TimerTask timeTask;
    private Timer timer = new Timer();

    @Override
    protected void onResume() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        // long codetime = sp.getLong("codetime", 0);
        // if (codetime != 0) {
        // long timenow = System.currentTimeMillis();
        // if (timenow >= codetime) {
        // return;
        // } else {
        // time = new TimeCount(codetime - timenow, 1000, btnOTP,
        // getResources().getString(R.string.reacquire));
        // time.start();
        // }
        // }
        int codetime = sp.getInt("codetimeforget", 0);
        if (codetime != 0) {
            time = new TimeCount(codetime * 1000, 1000, btnOTP, getResources()
                    .getString(R.string.reacquire));
            time.start();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        String text = btnOTP.getText().toString();
        if (getResources().getString(R.string.otp).equalsIgnoreCase(text)
                || getResources().getString(R.string.reacquire)
                .equalsIgnoreCase(text)) {

        } else {
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            Editor editor = sp.edit();
            int codetime = Integer.valueOf(text);
            // editor.putLong("codetime",
            // codetime * 1000 + System.currentTimeMillis());
            editor.putInt("codetimeforget", codetime);
            editor.commit();
        }
        if (time != null) {
            time.cancel();
            time = null;
        }
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "ForgetPwd---onStop:" + timeOut);
        Log.i("MOFA", "ForgetPwd---Show:" + (timeOut - timeIn));
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (isExit) {
            finish();
        } else {
            isExit = true;
            ToastUtil.showShort(ForgetPwdOneActivity.this, getResources().getString(R.string.warm_reclick_to_exit));

            timeTask = new TimerTask() {

                @Override
                public void run() {
                    isExit = false;
                }
            };
            timer.schedule(timeTask, 3000);
        }
    }

    @Override
    public void processMessage(Message message) {

    }

    private class MyHandler extends Handler {

        private WeakReference<Activity> activityWeakReference;

        private MyHandler (Activity activity) {
            activityWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            String reString = msg.obj.toString();
            btnReset.setClickable(true);
            switch (msg.what) {
                case Config.CODE_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), "system error");
                    break;
                case Config.CODE_FORGET_SEND_PWD:
                    try {
                        JSONObject jsonObject = new JSONObject(reString);
                        int error = jsonObject.optInt("error");

                        if (error > 0) {
                            ToastUtil.showShort(activityWeakReference.get(), jsonObject.getString("msg"));
                        } else {
                            rephone = jsonObject.optString("recivePhone");
                            reCode = jsonObject.optString("randomCode");
                        }
                    } catch (JSONException e1) {
                        Log.i("MOFA", e1.getMessage());
                    }

                    break;
                case Config.CODE_FORGET_PWD:
                case Config.CODE_FORGET_PWD_FACEBOOK:
                    try {
                        JSONObject jsonObject = new JSONObject(reString);
                        int error = jsonObject.getInt("error");
                        if (error == 3) {
                            startActivity(new Intent(activityWeakReference.get(),
                                    LoginActivity.class));
                            if (!activityWeakReference.get().isDestroyed()) {
                                finish();
                            }
                        }
                        ToastUtil.showShort(activityWeakReference.get(), jsonObject.getString("msg"));

                    } catch (JSONException e1) {
                        Log.i("MOFA", e1.getMessage());
                    }

                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.url_error));

                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.network_error));

                    break;
                case Config.CODE_CHECk_SEND_PWD:
                    String result = msg.obj.toString();
                    try {
                        JSONObject json = new JSONObject(result);
                        int error = json.optInt("error");

                        if (error < 0) {
                            if (time != null) {
                                time.cancel();
                                time = null;
                            }
                            String url = Config.URL + Config.FORGET_CODE
                                    + "&loginPwd=" + pwd + "&phone=" + phone + "&edg=" + MD5Util.md5(pwd);
                            btnReset.setClickable(false);
                            HttpUtils.doGetAsyn(url, mHandler,
                                    Config.CODE_FORGET_PWD);
                        } else {
                            ToastUtil.showShort(activityWeakReference.get(), json.getString("msg"));

                            status = false;
                            showdialog();
                        }
                    } catch (JSONException e) {
                        ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.data_parsing_error));

                        Log.e("MOFA", e.getMessage());
                    }

                    break;

                default:
                    break;
            }
        }
    }
}
