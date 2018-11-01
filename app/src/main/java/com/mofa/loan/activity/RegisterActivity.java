package com.mofa.loan.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.mofa.loan.R;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.CheckCodeDialog;
import com.mofa.loan.utils.Code;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.ConnectionDetector;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.PermissionUtils;
import com.mofa.loan.utils.TimeCount;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.utils.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    private LinearLayout llPhone;
    private LinearLayout llOTP;
    private LinearLayout llPassword;
    private LinearLayout llRecommander;
    private EditText etPhone;
    private EditText etOTP;
    private EditText etPassword;
    private EditText etRecommander;
    private Button btnOTP;
    private Button btnRegister;
    private ImageView ivCheck;
    private TextView tvRegisterProtocol;

    private TimeCount time;

    private String profession;
    private String isSendFacebook;
    private String phoneStr;
    private boolean check = true;
    private boolean status = true;

    private String ipAddress = "0.0.0.0";
    private String DEVICE_ID = "";
    private String Sim_Serial_Number = "";

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MOFA", "---Register---onCreate");
        isSendFacebook = getIntent().getStringExtra("isSendFacebook");
        phoneStr = getIntent().getStringExtra("phone");
        setContentView(R.layout.activity_register);
        if ("no".equalsIgnoreCase(isSendFacebook)) {
        } else {
            findViewById(R.id.tv_OTP).setVisibility(View.GONE);
            findViewById(R.id.ll_OTP).setVisibility(View.GONE);
        }
//		profession = getIntent().getStringExtra("profession");
        profession = "2";
        initView();
        getIP();
        if (Build.VERSION.SDK_INT >= 23) {
//            if
//                    (this.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                // 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
//                this.requestPermissions(
//                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
//                        PermissionUtils.PERMISSION);
//            } else {// 已授权
//                getPhoneState();
//            }
        } else {
            getPhoneState();
        }
    }

    private long timeIn;
    private long timeOut;

    @Override
    public void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "Register---onStart:" + timeIn);
    }

    private void initView() {
        if ("no".equalsIgnoreCase(isSendFacebook)) {
            llPhone = findViewById(R.id.ll_phonenumber);
            llPhone.setOnClickListener(this);
        }
        llOTP = findViewById(R.id.ll_otp);
        llOTP.setOnClickListener(this);
        llPassword = findViewById(R.id.ll_pwd);
        llPassword.setOnClickListener(this);
        llRecommander = findViewById(R.id.ll_recommender);
        llRecommander.setOnClickListener(this);
        etPhone = findViewById(R.id.et_phonenumber);
        etOTP = findViewById(R.id.et_otp);
        etPassword = findViewById(R.id.et_pwd);
        etRecommander = findViewById(R.id.et_recommender);
        btnOTP = findViewById(R.id.btn_otp);
        btnOTP.setOnClickListener(this);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        ivCheck = findViewById(R.id.iv_check);
        ivCheck.setOnClickListener(this);
        tvRegisterProtocol = findViewById(R.id.tv_register_protocol);
        tvRegisterProtocol.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        check = true;
        status = true;

        if ("yes".equalsIgnoreCase(isSendFacebook)) {
            etPhone.setText(phoneStr);
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
                etPassword.requestFocus();
                break;

            case R.id.ll_recommender:
                etRecommander.requestFocus();
                break;

            case R.id.iv_check:
                if (check) {
                    check = false;
                    ivCheck.setImageResource(R.drawable.ring);
                } else {
                    check = true;
                    ivCheck.setImageResource(R.drawable.round);
                }
                break;

            case R.id.tv_register_protocol:
                Intent intents = new Intent(RegisterActivity.this,
                        ProblemActivity.class);
                intents.putExtra("title",
                        getResources().getString(R.string.regist_protocol));
                intents.putExtra("url", Config.REGISTERADDRES_CODE);
                startActivity(intents);
                break;

            case R.id.btn_otp:

                phoneStr = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phoneStr)) {
                    ToastUtil.showShort(RegisterActivity.this, getResources()
                            .getString(R.string.warm_no_phonenumber));

                    return;
                }

                /** 去除手机号码中的空格 */
                if (phoneStr.contains(" ")) {
                    phoneStr = phoneStr.replaceAll(" ", "");
                }

                /** 检查手机号码格式是否正确 */
                if (!Validator.isPhoneYueNan(phoneStr)) {
                    ToastUtil.showShort(RegisterActivity.this, getResources()
                            .getString(R.string.warm_phonenumber_not_match));

                    return;
                }
                ConnectionDetector cd = new ConnectionDetector(
                        RegisterActivity.this);
                if (status) {

                    if (cd.isConnectingToInternet()) {
                        time = new TimeCount(120000, 1000, btnOTP, getResources()
                                .getString(R.string.reacquire),
                                getApplicationContext());
                        time.start();
                        SharedPreferences sp = getSharedPreferences("config",
                                MODE_PRIVATE);
                        Editor editor = sp.edit();
                        editor.putLong("OTP", System.currentTimeMillis());
                        editor.commit();

                        String url = Config.URL + Config.SEND_CODE + "&k="
                                + phoneStr + "&l=0" + "&asd="
                                + MD5Util.md5(phoneStr + "0");
                        HttpUtils.doGetAsyn(url, mHandler, Config.CODE_SEND);
                    } else {
                        ToastUtil.showShort(RegisterActivity.this, getResources()
                                .getString(R.string.network_error));

                    }
                } else {
                    showdialog();
                }
                break;

            case R.id.btn_register:
                // ToastUtil.showShort(RegisterActivity.this, "" + status);
                btnRegister.setClickable(false);
                if (status) {
                    register();
                } else {
                    showdialog();
                }
                break;
            default:
                break;
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
                } else {
                    ivCode.setImageBitmap(Code.getInstance().createBitmap());
                    checkDialog.setCode(Code.getInstance().getCode());
                    etCode.setText("");
                    ToastUtil.showLong(RegisterActivity.this,
                            "Sai mã xác nhận!");
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
        checkDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                btnRegister.setClickable(true);
            }
        });
        checkDialog.show();
    }

    private void showdialog2() {
        final android.app.AlertDialog phoneDialog = new android.app.AlertDialog.Builder(
                RegisterActivity.this).create();
        LayoutInflater lay = LayoutInflater.from(RegisterActivity.this);
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

    private void register() {

        String phoneStr = etPhone.getText().toString().trim();
        String codeStr = etOTP.getText().toString().trim();
        String pwdStr = etPassword.getText().toString().trim();
        String refferee = etRecommander.getText().toString().trim();

        /** 去除手机号码中的空格 */
        if (phoneStr.contains(" ")) {
            phoneStr = phoneStr.replaceAll(" ", "");
        }

        if (TextUtils.isEmpty(phoneStr)) {
            ToastUtil.showShort(RegisterActivity.this, getResources()
                    .getString(R.string.warm_no_phonenumber));

            return;
        }

        /** 检查手机号码格式是否正确 */
        if (!Validator.isPhoneYueNan(phoneStr)) {
            ToastUtil.showShort(RegisterActivity.this, getResources()
                    .getString(R.string.warm_phonenumber_not_match));

            return;
        }

        if (TextUtils.isEmpty(codeStr) && "no".equalsIgnoreCase(isSendFacebook)) {
            ToastUtil.showShort(RegisterActivity.this, getResources()
                    .getString(R.string.warm_no_code));

            return;
        }

        if (TextUtils.isEmpty(pwdStr)) {
            ToastUtil.showShort(RegisterActivity.this, getResources()
                    .getString(R.string.warm_no_pwd));

            return;
        }

        if (!check) {
            ToastUtil.showShort(RegisterActivity.this,
                    "Vui lòng chọn đồng ý Thỏa thuận đăng ký!");
            return;
        }

        String url = "";
        String rid = JPushInterface.getRegistrationID(getApplicationContext());

        String ANDROID_ID = Settings.System.getString(getContentResolver(),
                Settings.System.ANDROID_ID);
        String MAC_ADDRESS = getMacAddr().toUpperCase();
        if ("02:00:00:00:00:00".equalsIgnoreCase(MAC_ADDRESS)
                || "".equalsIgnoreCase(MAC_ADDRESS) || null == MAC_ADDRESS) {
            MAC_ADDRESS = getMac().toUpperCase();
            if ("02:00:00:00:00:00".equalsIgnoreCase(MAC_ADDRESS)
                    || "".equalsIgnoreCase(MAC_ADDRESS) || null == MAC_ADDRESS) {
                MAC_ADDRESS = getLocalMacAddress().toUpperCase();
            }
        }

        String phonetype = getSystemModel().replace(" ", "-");
        String systemversion = getSystemVersion().replace(" ", "-");
        String phonebrand = getDeviceBrand().replace(" ", "-");
        int appcode = getVersionCode();
        getIP();
        boolean isEmulator = isEmulator();
        String Serial = getSerial();
        if ("no".equalsIgnoreCase(isSendFacebook)) {

            url = Config.URL + Config.REGISTER + "&mobile=" + phoneStr
                    + "&code=" + codeStr + "&pwd=" + pwdStr + "&profession="
                    + profession + "&refferee=" + refferee + "&phonetype=1"
                    + "&registercome=1" + "&registration=" + rid + "&appcode="
                    + appcode + "&hqphonetype=" + phonetype + "&systemversion="
                    + systemversion + "&phonebrand=" + phonebrand + "&androidid="
                    + ANDROID_ID + "&macaddress=" + MAC_ADDRESS + "&deviceid="
                    + DEVICE_ID + "&simserialnumber=" + Sim_Serial_Number
                    + "&ipAddress=" + ipAddress + "&isemulator=" + isEmulator
                    + "&serial=" + Serial + "&hjgk="
                    + MD5Util.md5(phoneStr + pwdStr + "1");

        } else {
            url = Config.URL + Config.REGISTER_SENDFACEBOOK + "&mobile=" + phoneStr
                    + "&pwd=" + pwdStr + "&profession="
                    + profession + "&refferee=" + refferee + "&phonetype=1"
                    + "&registercome=1" + "&registration=" + rid + "&appcode="
                    + appcode + "&hqphonetype=" + phonetype + "&systemversion="
                    + systemversion + "&phonebrand=" + phonebrand + "&androidid="
                    + ANDROID_ID + "&macaddress=" + MAC_ADDRESS + "&deviceid="
                    + DEVICE_ID + "&simserialnumber=" + Sim_Serial_Number
                    + "&ipAddress=" + ipAddress + "&isemulator=" + isEmulator
                    + "&serial=" + Serial + "&hjgk="
                    + MD5Util.md5(phoneStr + pwdStr + "1");
        }
        HttpUtils.doGetAsyn(url, mHandler, Config.CODE_RGISTER);
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            btnRegister.setClickable(true);
            switch (msg.what) {
                case Config.CODE_GETIP:
                    String ipString = msg.obj.toString();
                    try {
                        JSONObject ipjson = new JSONObject(ipString);
                        ipAddress = ipjson.getString("ip");
                    } catch (JSONException e1) {
                        Log.i("MOFA", e1.getMessage());
                    }
                    break;

                case Config.CODE_SEND:
                    String reString = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(reString);
                        int error = jsonObject.optInt("error");

                        if (error > 0 && error != 5) {
                            ToastUtil.showShort(RegisterActivity.this,
                                    jsonObject.getString("msg"));

                            if (time != null) {
                                time.cancel();
                                time = null;
                            }
                        } else {
                            ToastUtil.showShort(RegisterActivity.this,
                                    jsonObject.getString("msg"));

                        }
                    } catch (JSONException e1) {
                        Log.i("MOFA", e1.getMessage());
                    }

                    break;
                case Config.CODE_ERROR:
                    ToastUtil.showShort(RegisterActivity.this, "system error");

                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(RegisterActivity.this, getResources()
                            .getString(R.string.url_error));

                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(RegisterActivity.this, getResources()
                            .getString(R.string.network_error));

                    break;

                case Config.CODE_RGISTER:
                    String result = msg.obj.toString();
                    try {
                        JSONObject json = new JSONObject(result);
                        int error = json.getInt("error");
                        if (error != 0) {
                            ToastUtil.showShort(RegisterActivity.this,
                                    json.getString("msg"));
                            if (error == 6) {
                                return;
                            }
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            status = false;
                        } else {
                            ToastUtil.showShort(
                                    RegisterActivity.this,
                                    getResources().getString(
                                            R.string.regist_successed));
                            status = true;
                            Map<String, Object> eventValues = new HashMap<>();
                            eventValues.put("Register", "ture");
                            AppsFlyerLib.getInstance().trackEvent(RegisterActivity.this, "Register",
                                    eventValues);
                            String userid = json.getString("ui");
                            String mobile = json.getString("mobile");
                            Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
                            editor.putString("userid", userid);
                            editor.putString("phone", mobile);
                            editor.commit();
                            Intent intent = new Intent(RegisterActivity.this, IndexActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        ToastUtil.showShort(RegisterActivity.this, getResources()
                                .getString(R.string.data_parsing_error));

                        Log.e("MOFA", e.getMessage());
                    }

                    break;

                default:
                    break;
            }
        }

    };

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
        int codetime = sp.getInt("codetime", 0);
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
            editor.putInt("codetime", codetime);
            editor.commit();
        }
        if (time != null) {
            time.cancel();
            time = null;
        }
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "Register---onStop:" + timeOut);
        Log.i("MOFA", "Register---Show:" + (timeOut - timeIn));
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (isExit) {
            finish();
        } else {
            isExit = true;
            ToastUtil.showShort(RegisterActivity.this, getResources()
                    .getString(R.string.warm_reclick_to_exit));

            timeTask = new TimerTask() {

                @Override
                public void run() {
                    isExit = false;
                }
            };
            timer.schedule(timeTask, 3000);
        }
    }

    public static int APP_REQUEST_CODE = 99;

    public void phoneLogin() {
        final Intent intent = new Intent(RegisterActivity.this,
                AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN); // or
        // .ResponseType.TOKEN
        // ... perform additional configuration ...
        configurationBuilder.setInitialPhoneNumber(new PhoneNumber("VN", phoneStr));
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoneState();
            } else {
                Log.i("MOFA", "权限拒绝：登录时获取手机状态信息");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response
            // matches your request
            AccountKitLoginResult loginResult = data
                    .getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType()
                        .getMessage();
                // showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:"
                            + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format("Success:%s...", loginResult
                            .getAuthorizationCode().substring(0, 10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access
                // token.

                // Success! Start your next activity...
                // goToMyLoggedInActivity();
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机串号
     *
     * @return 手机串号
     */
    public String getSerial() {
        return android.os.Build.SERIAL;
    }

    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0"))
                    continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 从本地文件获取手机MAC；
     *
     * @param context
     * @return
     */

    /**
     * 获取手机的MAC地址
     *
     * @return
     */
    public String getMac() {
        String str = "";
        String macSerial = "02:00:00:00:00:00";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)
                || "02:00:00:00:00:00".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                Log.e("MOFA", e.getMessage());

            }

        }
        return macSerial;
    }

    public String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /***
     * 获取网关IP地址
     *
     * @return
     */
    public String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (inetAddress.getHostAddress().indexOf("192.168") >= 0)
                        return inetAddress.getHostAddress();
                }
            }
        } catch (SocketException ex) {
        } catch (Exception e) {
        }
        return null;
    }

    public void getIP() {
        String url = "https://ifconfig.co/json";
        HttpUtils.doGetAsyn(url, mHandler, Config.CODE_GETIP);
    }

    /**
     * 判断是否为模拟器
     *
     * @return
     */
    public boolean isEmulator() {
        String url = "tel:" + "12345678910";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_DIAL);
        // 是否可以处理跳转到拨号的 Intent
        boolean canResolveIntent = intent.resolveActivity(RegisterActivity.this
                .getPackageManager()) != null;

        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.SERIAL.equalsIgnoreCase("unknown")
                || Build.SERIAL.equalsIgnoreCase("android")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE
                .startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || ((TelephonyManager) RegisterActivity.this
                .getSystemService(Context.TELEPHONY_SERVICE))
                .getNetworkOperatorName().toLowerCase()
                .equals("android") || !canResolveIntent;
    }

    /**
     * 获取本地app的版本号
     *
     * @return
     */
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);// 获取包的信息

            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有找到包名的时候会走此异常
            Log.e("MOFA", e.getMessage());
        }

        return -1;
    }

    public String getLocalMacAddress() {

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifi.getConnectionInfo();

        return info.getMacAddress();

    }

    public void getPhoneState() {
        DEVICE_ID = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
                .getDeviceId();
        Sim_Serial_Number = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
                .getSimSerialNumber();
    }

    @Override
    public void processMessage(Message message) {

    }

}
