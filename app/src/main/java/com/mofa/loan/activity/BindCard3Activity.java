package com.mofa.loan.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.adapter.BankSelectAdapter;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.EditUtils;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.utils.Validator;
import com.mofa.loan.view.MyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 银行绑定支付
 *
 * @author Administrator
 */
public class BindCard3Activity extends BaseActivity implements OnClickListener,
        OnItemClickListener {

    private RelativeLayout llChooseBank;
    private ListView lvBank;
    private ArrayList<String> bankList;
    private BankSelectAdapter bankAdapter;

    private String UserId = "";// Id
    private String name = "";
    private RelativeLayout llBankcardNumber;
    private RelativeLayout llBankcardNumberCheck;
    private RelativeLayout llBankMaster;
    private RelativeLayout llBankBranch;
    private RelativeLayout llName;
    private EditText etName;
    private EditText etBankcardNumber;
    private EditText etBankcardNumberCheck;
    private EditText etBankBranch;
    private TextView tvBankMaster;
    private Button btnBindBankNext;

    private android.app.AlertDialog phoneDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MOFA", "BindCardActivity---onCreate");
        setContentView(R.layout.activity_add_card3);
        initView();
    }

    private long timeIn;
    private long timeOut;

    @Override
    protected void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "BindCardActivity---onStart:" + timeIn);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "BindCardActivity---onStop:" + timeOut);
        Log.i("MOFA", "BindCardActivity---Show:" + (timeOut - timeIn));
    }

    private void initView() {
        mHandler = new MyHandler(this);
        dialog = new MyProgressDialog(this, "");

        SharedPreferences sp = getSharedPreferences("config", 0x0000);
        UserId = sp.getString("userid", "");
        findViewById(R.id.back).setOnClickListener(this);
        // findViewById(R.id.iv_cancle).setOnClickListener(this);
        // TextView fuwu = (TextView) findViewById(R.id.fuwu);
        // fuwu.setOnClickListener(this);
        // fuwu.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
//		llChooseBank = (RelativeLayout) findViewById(R.id.ll_choose_bank);
//		lvBank = (ListView) findViewById(R.id.lv_bank_name);
//		getData();
//		bankAdapter = new BankSelectAdapter(this, bankList);
//		lvBank.setAdapter(bankAdapter);
//		lvBank.setOnItemClickListener(this);

        llName = findViewById(R.id.ll_name);
        llName.setOnClickListener(this);
        llBankcardNumber = findViewById(R.id.ll_bankcardnumber);
        llBankcardNumber.setOnClickListener(this);
        llBankcardNumberCheck = findViewById(R.id.ll_bankcardnumber_check);
        llBankcardNumberCheck.setOnClickListener(this);
        llBankMaster = findViewById(R.id.ll_bankmaster);
        llBankMaster.setOnClickListener(this);
        llBankBranch = findViewById(R.id.ll_bankbranch);
        llBankBranch.setOnClickListener(this);
        etName = findViewById(R.id.et_name);
        etBankcardNumber = findViewById(R.id.et_bankcardnumber);
        etBankcardNumberCheck = findViewById(R.id.et_bankcardnumber_check);
        etBankBranch = findViewById(R.id.et_bankbranch);
        EditUtils.setEtFilter(etBankBranch);
        tvBankMaster = findViewById(R.id.tv_bankmaster);
        btnBindBankNext = findViewById(R.id.btn_bankinfo_next);
        btnBindBankNext.setOnClickListener(this);

        TextView tv = findViewById(R.id.tv_attention);
        tv.setText(Html.fromHtml("<font color=\"#DA3636\"> * </font> Nếu mở tài khoản ở phòng giao dịch, vui lòng nhập tên phòng giao dịch kèm tỉnh thành"));
        ((TextView) findViewById(R.id.title_txt_center)).setText("Xác minh thông tin ngân hàng");
    }

    private ArrayList<String> getData() {
        if (bankList == null || bankList.size() <= 0) {
            bankList = new ArrayList<>();
            bankList.add("ACB - TMCP Á Châu");
            bankList.add("VietcomBank - TMCP Ngoại Thương Việt Nam");
            bankList.add("VietinBank - TMCP Công Thương Việt Nam");
            bankList.add("Techcombank - TMCP Kỹ Thương Việt Nam");
            bankList.add("BIDV - TMCP Đầu Tư Và Phát Triển Việt Nam");
            bankList.add("MaritimeBank - TMCP Hàng Hải Việt Nam");
            bankList.add("VPBank - Việt Nam Thịnh Vượng");
            bankList.add("Agribank - Nông nghiệp và Phát triển Việt Nam");
            bankList.add("Eximbank - TMCP Xuất nhập khẩu Việt Nam");
            bankList.add("Sacombank - TMCP Sài Gòn Thương Tín");
            bankList.add("DongA Bank - TMCP Đông Á");
            bankList.add("VIB - TMCP Quốc tế Việt Nam");
            bankList.add("MB Bank - thương mại cổ phần Quân đội");
            bankList.add("Viet Capital Bank - TMCP Bản Việt");
            bankList.add("OceanBank - TM TNHH 1 thành viên Đại Dương");
            bankList.add("VietABank - TMCP Việt Á");
            bankList.add("TPBank - TMCP Tiên Phong");
            bankList.add("HDBank - TMCP Phát triển Thành phố Hồ Chí Minh");
            bankList.add("SCB - TMCP Sài Gòn");
            bankList.add("CBBank - thương mại TNHH MTV Xây dựng Việt Nam");
            bankList.add("LienVietPostBank - TMCP Bưu Điện Liên Việt");
            bankList.add("SeABank - TMCP Đông Nam Á");
            bankList.add("ABBank - TMCP An Bình");
            bankList.add("Nam A Bank - Ngân hàng Thương Mại cổ phần Nam Á");
            bankList.add("OCB - TMCP Phương Đông");
            bankList.add("GBBank - Dầu khí toàn cầu");
            bankList.add("PG Bank - TMCP Xăng dầu Petrolimex");
            bankList.add("SHBank - Thương Mại cổ phần Sài Gòn – Hà Nội");
            bankList.add("Saigon Bank - TMCP Sài Gòn Công Thương");
            bankList.add("Kien Long Bank - Thương mại Cổ phần Kiên Long");
        }
        return null;
    }

    @Override
    public void onClick(View v) {
//		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent(BindCard3Activity.this,
                        IndexActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                finish();
                break;
            // case R.id.fuwu:
            // Intent intent = new Intent(BindCardActivity.this,
            // ProblemActivity.class);
            // intent.putExtra("title",
            // getResources().getString(R.string.bank_protocol));
            // intent.putExtra("url", Config.BANKCARDPROTOL_CODE);
            // startActivity(intent);
            // break;
            case R.id.btn_bankinfo_next:
                dialog.show();
                String bankcardNumber = etBankcardNumber.getText().toString()
                        .trim();
                String bankcardNumberCheck = etBankcardNumberCheck.getText()
                        .toString().trim();
                String bankMaster = tvBankMaster.getText().toString().trim();
                String bankBranch = etBankBranch.getText().toString().trim();
                name = etName.getText().toString().trim();
                Log.i("MOFA", name);
                if (TextUtils.isEmpty(bankcardNumber)
                        || !Validator.isBankCardYueNan(bankcardNumber)) {
                    showDialog(getResources().getString(R.string.hint_bank_number));
                    dialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(bankMaster) || TextUtils.isEmpty(bankBranch)) {
                    showDialog(getResources().getString(R.string.hint_bank_name));
                    dialog.dismiss();
                    return;
                }
                if (!bankcardNumber.equalsIgnoreCase(bankcardNumberCheck)) {
                    showDialog(getResources().getString(
                            R.string.warm_check_bank_card));
                    dialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    showDialog(getResources().getString(R.string.warm_realname));
                    dialog.dismiss();
                    return;
                }
                // 暂时不用勾选
                // if (!check.isChecked()) {
                // showDialog(getResources().getString(R.string.warm_check_protocol));
                // dialog.dismiss();
                // return;
                // }
                Log.i("MOFA", "BankPayActivity---onClick --> 绑定银行卡");

                try {
                    HttpUtils.doGetAsyn(
                            Config.GETBANKCARD2
                                    + "&userid="
                                    + UserId
                                    + "&cardId="
                                    + bankcardNumber
                                    + "&name="
                                    + URLEncoder.encode(name, "UTF-8")
                                    + "&cardName="
                                    + URLEncoder.encode(bankMaster + " "
                                    + bankBranch, "UTF-8") + "&love=" + MD5Util.md5(UserId + bankcardNumber), mHandler,
                            Config.CODE_GETBANKCARD);
                } catch (UnsupportedEncodingException e) {
                    Log.e("MOFA", e.getMessage());
                }

                break;
            case R.id.ll_name:
                etName.requestFocus();
                break;
            case R.id.ll_bankcardnumber:
                etBankcardNumber.requestFocus();
                break;
            case R.id.ll_bankcardnumber_check:
                etBankcardNumberCheck.requestFocus();
                break;
            case R.id.ll_bankbranch:
                etBankBranch.requestFocus();
                break;
            case R.id.ll_bankmaster:
//			llChooseBank.setVisibility(View.VISIBLE);
//			imm.hideSoftInputFromWindow(llBankMaster.getWindowToken(), 0);
                this.phoneDialog = new android.app.AlertDialog.Builder(BindCard3Activity.this).create();
                LayoutInflater lay = LayoutInflater.from(BindCard3Activity.this);
                final View inflate = lay.inflate(R.layout.layout_choose_bank_dialog,
                        null);
                ListView lv = inflate.findViewById(R.id.lv_bank_name);
                getData();
                bankAdapter = new BankSelectAdapter(this, bankList);
                lv.setAdapter(bankAdapter);
                lv.setOnItemClickListener(this);
                this.phoneDialog.show();
                this.phoneDialog.setCanceledOnTouchOutside(true);
                this.phoneDialog.setContentView(inflate);
                break;
            default:
                break;
        }
    }

    private Handler mHandler;
    private MyProgressDialog dialog;
    private AlertDialog ad;

    private void showDialog(String Message) {
        new AlertDialog(BindCard3Activity.this)
                .builder()
                .setMsg(Message)
                .setPositiveButton3(getResources().getString(R.string.agree),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
    }

    @Override
    public void processMessage(Message message) {

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
//		llChooseBank.setVisibility(View.GONE);
        tvBankMaster.setText(bankList.get(position));
        phoneDialog.dismiss();
    }

    private boolean isExit = false;
    private TimerTask timeTask;
    private Timer timer = new Timer();

    // 监听返回键是否退出
    @Override
    public void onBackPressed() {
//		if (llChooseBank.getVisibility() == View.VISIBLE) {
//			llChooseBank.setVisibility(View.GONE);
//			Log.i("Olava---backpress", "1");
//			return;
//		}
        if (isExit) {
            Intent intent1 = new Intent(BindCard3Activity.this,
                    IndexActivity.class);
            intent1.putExtra("id", 1);
            startActivity(intent1);
            finish();
        } else {
            isExit = true;
            ToastUtil.showShort(BindCard3Activity.this, getResources().getString(R.string.warm_reclick_to_exit_veify));

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
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }

    private class MyHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;

        public MyHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            switch (msg.what) {
                case Config.CODE_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), "system error");
                    break;
                case Config.CODE_TIMEOUT_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.timeout_of_network));

                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.url_error));

                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.network_error));

                    break;

                case Config.CODE_GETBANKCARD:
                    String result = msg.obj.toString();
                    try {
                        JSONObject object = new JSONObject(result);
                        if (0 == object.getInt("error")) {
                            ToastUtil.showShort(activityWeakReference.get(), getResources().getString(R.string.bind_successed));

                            SharedPreferences sp = getSharedPreferences("config",
                                    MODE_PRIVATE);
                            Editor editor = sp.edit();
                            editor.putInt("isyhbd", 1);
                            editor.putString("bankname2", name);
                            Log.i("MOFA", name + "11");
                            editor.commit();
                            int isshenfen = sp.getInt("isshenfen", 0);
                            int isjob = sp.getInt("isjob", 0);
                            int islianxi = sp.getInt("islianxi", 0);
                            int isschool = sp.getInt("isschool", 0);
//						int isfacebook = sp.getInt("isfacebook", 0);
                            int profession = sp.getInt("profession", 0);
                            if (isshenfen == 0) {
                                //TODO  测试填写身份

                                if (!"".equals(sp.getString("sfcmnd", ""))) {
//							if ("".equals(sp.getString("sfcmnd", ""))) {
                                    Intent intent = new Intent(activityWeakReference.get(), IDCard3Activity.class);
//								intent.putExtra("username", sp.getString("bankname2", ""));
                                    intent.putExtra("username", name);
                                    intent.putExtra("IDnumber", sp.getString("sfcmnd", ""));
                                    intent.putExtra("address", sp.getString("sfaddress", ""));
                                    intent.putExtra("addressnow", sp.getString("sfhomeaddress", ""));
                                    intent.putExtra("gender", sp.getString("sfsex", ""));
                                    intent.putExtra("email", sp.getString("sfemail", ""));
                                    intent.putExtra("both", sp.getString("sfage", ""));
                                    intent.putExtra("phonetype", sp.getString("sfphonetype", ""));
                                    intent.putExtra("from", "base");
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(activityWeakReference.get(),
                                            BaseInfo3Activity.class);
                                    intent.putExtra("bankname2", name);
                                    startActivity(intent);
                                }
                            } else if (islianxi == 0) {
                                Intent intent = new Intent(activityWeakReference.get(),
                                        RelationInfo3Activity.class);
                                startActivity(intent);
                            } else if ((profession == 2 && isjob == 0) || (profession == 1 && isschool == 0)) {
                                Intent intent = new Intent(activityWeakReference.get(),
                                        WorkInfo3Activity.class);
                                startActivity(intent);
//						} else if (profession == 1 && isschool == 0) {
//							Intent intent = new Intent(IDCardActivity.this,
//									StudentInfo2Activity.class);
//							startActivity(intent);
//						}
//						else if (isfacebook == 0) {
//							startActivity(new Intent(IDCardActivity.this,
//									FacebookAcytivity.class));
                            } else {
                                Intent intent = new Intent(activityWeakReference.get(),
                                        IndexActivity.class);
                                intent.putExtra("id", 11);
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            ToastUtil.showShort(activityWeakReference.get(), object.getString("msg"));
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
