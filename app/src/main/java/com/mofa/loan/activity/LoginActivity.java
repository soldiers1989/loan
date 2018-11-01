package com.mofa.loan.activity;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.mofa.loan.R;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.CheckCodeDialog;
import com.mofa.loan.utils.Code;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.ConnectionDetector;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.PermissionUtils;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.utils.Validator;
import com.mofa.loan.view.MyProgressDialog;

import org.apache.commons.lang.math.RandomUtils;
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
import java.util.List;

import cn.jpush.android.api.JPushInterface;

@TargetApi(23)
public class LoginActivity extends BaseActivity implements OnClickListener {

    private Button btnLogin;
    private EditText phone;
    private EditText pwd;
    private String code;
    private String phoneStr;
    private String pwdStr;
    private String msgNet;
    private String phonetype;
    private String systemversion;
    private String phonebrand;
    private int appcode;

    private int isFacebookReg = 0;

    private String ipAddress = "0.0.0.0";
    private String DEVICE_ID;
    private String Sim_Serial_Number;

    private boolean status;
    private boolean isEmulator;

    private MyProgressDialog dialog;
    private ClipboardManager cmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("MOFA", "---Login--onCreate");
        initView();
        getIP();
        isEmulator = isEmulator();
        if (Build.VERSION.SDK_INT >= 23) {
//         if
//         (this.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
//         != PackageManager.PERMISSION_GRANTED) {
//         // 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
//         this.requestPermissions(
//         new String[] { android.Manifest.permission.READ_PHONE_STATE },
//         PermissionUtils.PERMISSION);
//         } else {// 已授权
//         getPhoneState();
//         }
        } else {
            getPhoneState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFacebookReg == 1) {

        }
    }

    private long timeIn;
    private long timeOut;

    @Override
    public void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "Login---onStart:" + timeIn);
    }

    @Override
    public void onStop() {
        super.onStop();
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "Login---onStop:" + timeOut);
        Log.i("MOFA", "Login---Show:" + (timeOut - timeIn));
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

    private void getPhoneState() {
        DEVICE_ID = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
                .getDeviceId();
        Sim_Serial_Number = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
                .getSimSerialNumber();
    }

    private void initView() {
        dialog = new MyProgressDialog(this, "");

        phone = findViewById(R.id.et_phonenumber);
        pwd = findViewById(R.id.et_pwd);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.tv_forget).setOnClickListener(this);
        findViewById(R.id.ll_phone).setOnClickListener(this);
        findViewById(R.id.ll_pwd).setOnClickListener(this);
        findViewById(R.id.iv_contact).setOnClickListener(this);

        status = true;
        phonetype = getSystemModel().replace(" ", "-");
        systemversion = getSystemVersion().replace(" ", "-");
        phonebrand = getDeviceBrand().replace(" ", "-");
        appcode = getVersionCode();
    }

    private void showDialog(String Message) {
        new AlertDialog(LoginActivity.this)
                .builder()
                .setMsg(Message)
                .setNegativeButton(getResources().getString(R.string.OK),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnLogin.setClickable(true);
                            }
                        }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // case R.id.iv_showCode:
            // iv_showCode.setImageBitmap(Code.getInstance().createBitmap());
            // realCode = Code.getInstance().getCode().toLowerCase();
            // Log.v("TAG", "realCode" + realCode);
            // break;
            case R.id.iv_contact:
                cmb = (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE);
                final android.app.AlertDialog phoneDialog = new android.app.AlertDialog.Builder(LoginActivity.this)
                        .create();
                LayoutInflater lay2 = LayoutInflater.from(LoginActivity.this);
                final View inflate2 = lay2.inflate(
                        R.layout.layout_service_dialog, null);
                inflate2.findViewById(R.id.btn_call).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // 拨打电话
                                phoneDialog.dismiss();
                                boolean a = RandomUtils.nextBoolean();
                                if (a) {
                                    startActivity(new Intent(Intent.ACTION_DIAL,
                                            Uri.parse("tel:028-73008816")));
                                } else {
                                    startActivity(new Intent(Intent.ACTION_DIAL,
                                            Uri.parse("tel:028-71009888")));
                                }
                            }
                        });
                inflate2.findViewById(R.id.btn_copy_zalo).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                phoneDialog.dismiss();
                                cmb.setPrimaryClip(ClipData.newPlainText(null,
                                        "0906608816"));
                                ToastUtil.showShort(LoginActivity.this, getResources()
                                        .getString(R.string.copy));
                            }
                        });
                inflate2.findViewById(R.id.btn_copy_facebook)
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                phoneDialog.dismiss();
                                if (SplashActivity.isAvilible(LoginActivity.this,
                                        "com.facebook.katana")) {
                                    Intent intent1 = new Intent(
                                            Intent.ACTION_VIEW);
                                    intent1.setData(Uri
                                            .parse("fb://page/208936099910136"));
                                    // intent1.setData(Uri.parse(getFacebookPageURL(ac)));
                                    boolean canResolveIntent = intent1.resolveActivity(LoginActivity.this
                                            .getPackageManager()) != null;
                                    if (canResolveIntent){
                                        LoginActivity.this.startActivity(intent1);
                                    } else {
                                        cmb.setPrimaryClip(ClipData.newPlainText(
                                                null, "01255949981"));
                                        ToastUtil.showShort(LoginActivity.this, getResources()
                                                .getString(R.string.copy));
                                    }
                                } else {
                                    cmb.setPrimaryClip(ClipData.newPlainText(
                                            null, "01255949981"));
                                    ToastUtil.showShort(LoginActivity.this, getResources()
                                            .getString(R.string.copy));
                                }
                            }
                        });
                inflate2.findViewById(R.id.btn_copy_email).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                phoneDialog.dismiss();
                                cmb.setPrimaryClip(ClipData.newPlainText(null,
                                        "hotro@olava.com.vn"));
                                ToastUtil.showShort(LoginActivity.this, getResources()
                                        .getString(R.string.copy));
                            }
                        });
                inflate2.findViewById(R.id.iv_cancle).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                phoneDialog.dismiss();
                            }
                        });
                phoneDialog.show();
                phoneDialog.setCanceledOnTouchOutside(false);
                phoneDialog.setContentView(inflate2);
                break;
            case R.id.ll_phone:
                phone.requestFocus();
                break;
            case R.id.ll_pwd:
                pwd.requestFocus();
                break;
            case R.id.btn_login:
                btnLogin.setClickable(false);
                // String phoneCode =
                // et_phoneCode.getText().toString().toLowerCase();
                phoneStr = phone.getText().toString().trim();
                pwdStr = pwd.getText().toString().trim();
                if (TextUtils.isEmpty(phoneStr)) {
                    showDialog(getResources().getString(
                            R.string.warm_no_phonenumber));
                    btnLogin.setClickable(true);
                    return;
                }

                /** 去除手机号码中的空格 */
                if (phoneStr.contains(" ")) {
                    phoneStr = phoneStr.replaceAll(" ", "");
                }

                /** 检查手机号码格式是否正确 */
                if (!Validator.isPhoneYueNan(phoneStr)) {
                    showDialog(getResources().getString(
                            R.string.warm_phonenumber_not_match));
                    btnLogin.setClickable(true);
                    return;
                }
                if (TextUtils.isEmpty(pwdStr)) {
                    showDialog(getResources().getString(R.string.warm_no_pwd));
                    btnLogin.setClickable(true);
                    return;
                }
                if (status) {

                    ConnectionDetector cd = new ConnectionDetector(
                            LoginActivity.this);
                    if (cd.isConnectingToInternet()) {
                        login();
                    }
                } else {
                    showdialog();
                }
                break;
            case R.id.tv_register:

                ConnectionDetector cd = new ConnectionDetector(
                        LoginActivity.this);
                if (cd.isConnectingToInternet()) {
                    String url = Config.URL + Config.SEND_FACEBOOK_CODE + "&ljgd=" + MD5Util.md5("rng");
                    HttpUtils.doGetAsyn(url, mHandler, Config.CODE_SENDFACEBOOK);
                    dialog.show();
                } else {
                    ToastUtil.showShort(LoginActivity.this, getResources()
                            .getString(R.string.network_error));
                }
                break;
            case R.id.tv_forget:

                ConnectionDetector cd2 = new ConnectionDetector(
                        LoginActivity.this);
                if (cd2.isConnectingToInternet()) {
                    String url = Config.URL + Config.SEND_FACEBOOK_CODE + "&ljgd=" + MD5Util.md5("rng");
                    HttpUtils.doGetAsyn(url, mHandler, Config.CODE_SENDFACEBOOK_FORGET);
                    dialog.show();
                } else {
                    ToastUtil.showShort(LoginActivity.this, getResources()
                            .getString(R.string.network_error));
                }
                break;
            // case R.id.problem:
            // Intent intents = new Intent(LoginActivity.this,
            // ProblemActivity.class);
            // intents.putExtra("title", "常见问题");
            // intents.putExtra("url",
            // "http://www.lvzbao.com/androidHtml/sdcjwt_app.html");
            // startActivity(intents);
            // break;
            default:
                break;
        }
    }

    private void login() {
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

        String Serial = getSerial();

        String url = Config.URL + Config.LOGIN_CODE + "&mobile=" + phoneStr
                + "&pwd=" + pwdStr + "&registration=" + rid + "&appcode="
                + appcode + "&hqphonetype=" + phonetype + "&systemversion="
                + systemversion + "&phonebrand=" + phonebrand + "&androidid="
                + ANDROID_ID + "&macaddress=" + MAC_ADDRESS + "&deviceid="
                + DEVICE_ID + "&simserialnumber=" + Sim_Serial_Number
                + "&ipAddress=" + ipAddress + "&isemulator=" + isEmulator
                + "&serial=" + Serial + "&lj=" + MD5Util.md5(pwdStr + rid);
        Log.i("MOFA", "---获取信息---" + "mobile=" + phoneStr + "&appcode="
                + appcode + "&hqphonetype=" + phonetype + "&systemversion="
                + systemversion + "&phonebrand=" + phonebrand + "&androidid="
                + ANDROID_ID + "&macaddress=" + MAC_ADDRESS + "&deviceid="
                + DEVICE_ID + "&simserialnumber=" + Sim_Serial_Number
                + "&ipAddress=" + ipAddress + "&isemulator=" + isEmulator
                + "&serial=" + Serial);
        url = url.replace(",", "-");
        HttpUtils.doGetAsyn(url, mHandler, Config.CODE_LOGIN);

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
                    login();
                } else {
                    ivCode.setImageBitmap(Code.getInstance().createBitmap());
                    checkDialog.setCode(Code.getInstance().getCode());
                    etCode.setText("");
                    ToastUtil.showLong(LoginActivity.this, "Sai mã xác nhận!");
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
                btnLogin.setClickable(true);
            }
        });
        checkDialog.show();
    }

    private void showdialog2() {
        final android.app.AlertDialog phoneDialog = new android.app.AlertDialog.Builder(
                LoginActivity.this).create();
        LayoutInflater lay = LayoutInflater.from(LoginActivity.this);
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
                    btnLogin.setClickable(true);
                }
            }
        });
        phoneDialog.show();
        phoneDialog.setCanceledOnTouchOutside(false);
        phoneDialog.setContentView(inflate);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            dialog.dismiss();
            switch (msg.what) {
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(LoginActivity.this, getResources()
                            .getString(R.string.url_error));

                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(LoginActivity.this, getResources()
                            .getString(R.string.network_error));

                    break;
                case Config.CODE_ERROR:
                    ToastUtil.showShort(LoginActivity.this, "system error");
                    break;
                case Config.CODE_TIMEOUT_ERROR:
                    ToastUtil.showShort(LoginActivity.this, "TIME OUT");

                    break;

                case Config.CODE_GETIP:
                    String ipString = msg.obj.toString();
                    try {
                        JSONObject ipjson = new JSONObject(ipString);
                        ipAddress = ipjson.getString("ip");
                    } catch (JSONException e1) {
                        Log.i("MOFA", e1.getMessage());
                    }
                    break;

                case Config.CODE_LOGIN:
                    String result = msg.obj.toString();
                    try {
                        JSONObject json = new JSONObject(result);
                        int error = json.getInt("err");

                        // {"ui":"22","username":"GSD22","wxid":false,
                        // "status":0,"name":"","err":-8,"idno":"","msg":"登录成功",
                        // "business":1,"mobile":"176*****768"}
                        ToastUtil.showShort(LoginActivity.this,
                                json.getString("msg"));

                        if (error == 0) {
                            SharedPreferences sp = getSharedPreferences("config",
                                    MODE_PRIVATE);
                            Editor editor = sp.edit();
                            editor.putString("phone", json.getString("mobile"));
                            editor.putString("userid", json.getString("ui"));
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this,
                                    IndexActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            pwd.setText("");
                            status = false;
                        }

                    } catch (JSONException e) {
                        ToastUtil.showShort(LoginActivity.this, getResources()
                                .getString(R.string.data_parsing_error));

                        Log.e("MOFA", e.getMessage());
                    }

                    break;
                case Config.CODE_SENDFACEBOOK:
                    String smsStr = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(smsStr);
                        String code = jsonObject.getString("keycode");
                        if ("0".equalsIgnoreCase(code)) {
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            intent.putExtra("isSendFacebook", "no");
                            startActivity(intent);
                        } else {
                            phoneLogin();
                        }
                    } catch (JSONException e1) {
                        Log.i("MOFA", e1.getMessage());
                    }

                    break;
                case Config.CODE_SENDFACEBOOK_FORGET:
                    String smsStr2 = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(smsStr2);
                        String code = jsonObject.getString("keycode");
                        if ("0".equalsIgnoreCase(code)) {
                            Intent intent = new Intent(LoginActivity.this, ForgetPwdOneActivity.class);
                            intent.putExtra("isSendFacebook", "no");
                            startActivity(intent);
                        } else {
                            phoneLogin2();
                        }
                    } catch (JSONException e1) {

                        Log.i("MOFA", e1.getMessage());
                    }

                    break;
                default:
                    break;

            }
            btnLogin.setClickable(true);
        }

    };

    public static int APP_REQUEST_CODE = 99;
    public static int APP_REQUEST_CODE2 = 199;

    public void phoneLogin() {
        final Intent intent = new Intent(LoginActivity.this,
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

    public void phoneLogin2() {
        final Intent intent = new Intent(LoginActivity.this,
                AccountKitActivity.class);

        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN); // or
        // .ResponseType.TOKEN
        // ... perform additional configuration ...
        configurationBuilder.setInitialPhoneNumber(new PhoneNumber("VN", phoneStr));
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE2);
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
//                    toastMessage = "Success:"
//                            + loginResult.getAccessToken().getAccountId();
                    isFacebookReg = 1;
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            // Get Account Kit ID
                            String accountKitId = account.getId();

                            // Get phone number
                            PhoneNumber phoneNumber = account.getPhoneNumber();
                            if (phoneNumber != null) {
                                String phoneNumberString = phoneNumber.toString();
                                Log.i("MOFA", "Facebook认证手机号：" + phoneNumberString);
//                    ToastUtil.showLong(LoginActivity.this, phoneNumberString);
                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                intent.putExtra("phone", phoneNumberString.replace("+84", "0"));
                                intent.putExtra("isSendFacebook", "yes");
                                startActivity(intent);
                            }

                            // Get email
                            String email = account.getEmail();
                        }

                        @Override
                        public void onError(final AccountKitError error) {
                            // Handle Error
                        }
                    });
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
        } else if (requestCode == APP_REQUEST_CODE2) {
            { // confirm that this response
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
//                    toastMessage = "Success:"
//                            + loginResult.getAccessToken().getAccountId();
                        isFacebookReg = 1;
                        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                            @Override
                            public void onSuccess(final Account account) {
                                // Get Account Kit ID
                                String accountKitId = account.getId();

                                // Get phone number
                                PhoneNumber phoneNumber = account.getPhoneNumber();
                                if (phoneNumber != null) {
                                    String phoneNumberString = phoneNumber.toString();
                                    Log.i("MOFA", "Facebook认证手机号：" + phoneNumberString);
//                    ToastUtil.showLong(LoginActivity.this, phoneNumberString);
                                    Intent intent = new Intent(LoginActivity.this, ForgetPwdOneActivity.class);
                                    intent.putExtra("phone", phoneNumberString.replace("+84", "0"));
                                    intent.putExtra("isSendFacebook", "yes");
                                    startActivity(intent);
                                }

                                // Get email
                                String email = account.getEmail();
                            }

                            @Override
                            public void onError(final AccountKitError error) {
                                // Handle Error
                            }
                        });
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
            }

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
        boolean canResolveIntent = intent.resolveActivity(LoginActivity.this
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
                || ((TelephonyManager) LoginActivity.this
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
        } catch (NameNotFoundException e) {
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

    @Override
    public void processMessage(Message message) {

    }

}
