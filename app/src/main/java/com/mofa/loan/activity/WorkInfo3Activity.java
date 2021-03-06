package com.mofa.loan.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.listener.CustomListener;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.EditUtils;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.view.MyProgressDialog;
import com.mofa.loan.view.OptionsPickerView;
import com.mofa.loan.view.TimePickerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
@SuppressLint("ResourceAsColor")
public class WorkInfo3Activity extends BaseActivity implements OnClickListener {

    private FrameLayout llPv;
    private TimePickerView pvTime;
    private OptionsPickerView pvNoLinkOptions;
    private OptionsPickerView pvNoLinkOptions2;
    private ArrayList<String> cardItem1 = new ArrayList<>();
    private ArrayList<String> cardItem2 = new ArrayList<>();

    private RelativeLayout llIndustry;
    private RelativeLayout llCompany;
    private RelativeLayout llCompanyPhone;
    private RelativeLayout llCompanyAddress;
    private RelativeLayout llPosition;
    private RelativeLayout llTime;
    private RelativeLayout llWage;
    private RelativeLayout llSchoolName;
    private RelativeLayout llClassName;
    private RelativeLayout llSchoolTime;
    private EditText etCompany;
    private EditText etCompanyPhone;
    private TextView etCompanyAddress;
    private EditText etPosition;
    private EditText etSchoolName;
    private EditText etClassName;
    private TextView tvStudentTime;
    private TextView tvTime;
    private TextView tvWage;
    private TextView tvIndustry;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ImageView iv4;
    private ImageView iv5;
    private ImageView iv6;
    private ImageView iv7;
    private ImageView iv8;
    private Button btnNext;

    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_info3);
        initView();
        Log.i("MOFA", "WorkInfo----onCreate");
    }

    @Override
    protected void onResume() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String sx = sp.getString("sx", "");
        String companyname = sp.getString("company", "");
        String companyphone = sp.getString("companyphone", "");
        String companyaddress = sp.getString("companyaddress", "");
        String companyposition = sp.getString("position", "");
        String companywage = sp.getString("wage", "");
        String schoolname = sp.getString("schoolname", "");
        String classname = sp.getString("classname", "");

        if (!TextUtils.isEmpty(sx)) {
            tvIndustry.setText(sx);
        }
        if (!TextUtils.isEmpty(companyname)) {
            etCompany.setText(companyname);
        }
        if (!TextUtils.isEmpty(companyphone)) {
            etCompanyPhone.setText(companyphone);
        }
        if (!TextUtils.isEmpty(companyaddress)) {
            etCompanyAddress.setText(companyaddress);
        }
        if (!TextUtils.isEmpty(companyposition)) {
            etPosition.setText(companyposition);
        }
        if (!TextUtils.isEmpty(companywage)) {
            tvWage.setText(companywage);
        }
        if (!TextUtils.isEmpty(schoolname)) {
            etSchoolName.setText(schoolname);
        }
        if (!TextUtils.isEmpty(classname)) {
            etClassName.setText(classname);
        }

        if (getResources().getString(R.string.category17).equalsIgnoreCase(sx)) {
//			llWork.setVisibility(View.GONE);
//			llStudent.setVisibility(View.VISIBLE);
            switchLayout(1);
        }
        if (getResources().getString(R.string.category19).equalsIgnoreCase(sx)) {
//			llWork.setVisibility(View.GONE);
//			llStudent.setVisibility(View.GONE);
            switchLayout(3);
        }
        super.onResume();
    }

    private long timeIn;
    private long timeOut;

    @Override
    public void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "WorkInfo---onStart:" + timeIn);
    }

    @Override
    public void onStop() {
        super.onStop();
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "WorkInfo---onStop:" + timeOut);
        Log.i("MOFA", "WorkInfo---Show:" + (timeOut - timeIn));
        String sx = tvIndustry.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String companyphone = etCompanyPhone.getText().toString().trim();
        String companyaddress = etCompanyAddress.getText().toString().trim();
        String position = etPosition.getText().toString().trim();
        String wage = tvWage.getText().toString().trim();
        String schoolname = etSchoolName.getText().toString().trim();
        String classname = etClassName.getText().toString().trim();
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("sx", sx);
        editor.putString("company", company);
        editor.putString("companyphone", companyphone);
        editor.putString("companyaddress", companyaddress);
        editor.putString("position", position);
        editor.putString("wage", wage);
        editor.putString("schoolname", schoolname);
        editor.putString("classname", classname);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }


    private void initView() {
        handler = new MyHandler(this);
        findViewById(R.id.back).setOnClickListener(this);
        llPv = findViewById(R.id.ll_time_picker);

        initList();

        initTimePicker();
        initNoLinkOptionsPicker();
        initNoLinkOptionsPicker2();
        ((TextView) findViewById(R.id.title_txt_center)).setText("Xác minh công việc");
//		llStudent = findViewById(R.id.ll_student);
//		llWork = findViewById(R.id.ll_work);
        llIndustry = findViewById(R.id.rl_companytype);
        llIndustry.setOnClickListener(this);
        llCompany = findViewById(R.id.rl_companyname);
        llCompany.setOnClickListener(this);
        llCompanyPhone = findViewById(R.id.rl_companyphone);
        llCompanyPhone.setOnClickListener(this);
        llCompanyAddress = findViewById(R.id.rl_companyaddress);
        llCompanyAddress.setOnClickListener(this);
        llSchoolName = findViewById(R.id.rl_schoolname);
        llSchoolName.setOnClickListener(this);
        llClassName = findViewById(R.id.rl_schoolroom);
        llClassName.setOnClickListener(this);
        llSchoolTime = findViewById(R.id.rl_schooltime);
        llSchoolTime.setOnClickListener(this);
        llPosition = findViewById(R.id.rl_companyposition);
        llPosition.setOnClickListener(this);
        llTime = findViewById(R.id.rl_worktime);
        llTime.setOnClickListener(this);
        llWage = findViewById(R.id.rl_wage);
        llWage.setOnClickListener(this);
        btnNext = findViewById(R.id.btn_workinfo_next);
        btnNext.setOnClickListener(this);
        tvTime = findViewById(R.id.tv_worktime);
        tvWage = findViewById(R.id.tv_wage);
        tvIndustry = findViewById(R.id.tv_companytype);
        etCompany = findViewById(R.id.et_companyname);
        etCompanyAddress = findViewById(R.id.tv_companyaddress);
        etCompanyPhone = findViewById(R.id.et_companyphone);
        etPosition = findViewById(R.id.et_companyposition);
        etSchoolName = findViewById(R.id.et_schoolname);
        etClassName = findViewById(R.id.et_schoolroom);
        tvStudentTime = findViewById(R.id.tv_schooltime);
        EditUtils.setEtFilter(etCompany);
        EditUtils.setEtFilter(etPosition);
        iv1 = findViewById(R.id.dot1);
        iv2 = findViewById(R.id.dot2);
        iv3 = findViewById(R.id.dot3);
        iv4 = findViewById(R.id.dot4);
        iv5 = findViewById(R.id.dot5);
        iv6 = findViewById(R.id.dot6);
        iv7 = findViewById(R.id.dot7);
        iv8 = findViewById(R.id.dot8);
        switchLayout(2);
        dialog = new MyProgressDialog(WorkInfo3Activity.this, "");
    }

    private void initList() {
        cardItem1.add(getResources().getString(R.string.category1));
        cardItem1.add(getResources().getString(R.string.category2));
        cardItem1.add(getResources().getString(R.string.category3));
        cardItem1.add(getResources().getString(R.string.category4));
        cardItem1.add(getResources().getString(R.string.category5));
        cardItem1.add(getResources().getString(R.string.category6));
        cardItem1.add(getResources().getString(R.string.category7));
        cardItem1.add(getResources().getString(R.string.category8));
        cardItem1.add(getResources().getString(R.string.category9));
        cardItem1.add(getResources().getString(R.string.category10));
        cardItem1.add(getResources().getString(R.string.category11));
        cardItem1.add(getResources().getString(R.string.category12));
        cardItem1.add(getResources().getString(R.string.category13));
        cardItem1.add(getResources().getString(R.string.category14));
        cardItem1.add(getResources().getString(R.string.category15));
        cardItem1.add(getResources().getString(R.string.category16));
        cardItem1.add(getResources().getString(R.string.category18));
        cardItem1.add(getResources().getString(R.string.category17));
        cardItem1.add(getResources().getString(R.string.category19));

        cardItem2.add("Dưới 5,000,000 VNĐ");
        cardItem2.add("5,000,000 - 10,000,000 VNĐ");
        cardItem2.add("10,000,000 - 15,000,000 VNĐ");
        cardItem2.add("15,000,000 - 20,000,000 VNĐ");
        cardItem2.add("Trên 20,000,000 VNĐ");
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (pvNoLinkOptions.isShowing()) {
            pvNoLinkOptions.dismissDialog();
        }
        if (pvNoLinkOptions2.isShowing()) {
            pvNoLinkOptions2.dismissDialog();
        }
        if (pvTime.isShowing()) {
            pvTime.dismissDialog();
        }
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent(WorkInfo3Activity.this,
                        IndexActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                finish();
                break;
            case R.id.rl_companytype:
                imm.hideSoftInputFromWindow(llIndustry.getWindowToken(), 0);
                if (pvNoLinkOptions2 != null) {
                    pvNoLinkOptions2.show();
                }
                break;
            case R.id.rl_wage:
                imm.hideSoftInputFromWindow(llWage.getWindowToken(), 0);
                if (pvNoLinkOptions != null) {
                    pvNoLinkOptions.show();
                }
                break;
            case R.id.rl_worktime:
                imm.hideSoftInputFromWindow(llTime.getWindowToken(), 0);
                if (pvTime != null) {
                    pvTime.show(v, false);
                }
                break;
            case R.id.rl_schoolname:
                etSchoolName.requestFocus();
                break;
            case R.id.rl_schoolroom:
                etClassName.requestFocus();
                break;
            case R.id.rl_companyname:
                etCompany.requestFocus();
                break;
            case R.id.rl_companyphone:
                etCompanyPhone.requestFocus();
                break;
            case R.id.rl_companyposition:
                etPosition.requestFocus();
                break;
            case R.id.rl_companyaddress:
                Intent locationIntent2 = new Intent(WorkInfo3Activity.this,
                        ChooseLocationActivity.class);
                startActivityForResult(locationIntent2, 305);
                break;
            case R.id.rl_schooltime:
                imm.hideSoftInputFromWindow(llTime.getWindowToken(), 0);
                if (pvTime != null) {
                    pvTime.show(v, false);
                }
                break;
            case R.id.btn_workinfo_next:
                SharedPreferences sp = getSharedPreferences("config", 0x0000);
                String UserId = sp.getString("userid", "");
                String industry = tvIndustry.getText().toString().trim();
                if (getResources().getString(R.string.category19).equalsIgnoreCase(
                        industry)) {
                    dialog.show();
                    HttpUtils
                            .doGetAsyn(
                                    Config.GETWORK
                                            + "&userid="
                                            + UserId
                                            + "&workName=none&tel=none&address=none&position=none&pay=none&time=none&company=none&p1="
                                            + "" + "&p2=" + "" + "&p3=" + ""
                                            + "&you="
                                            + MD5Util.md5(UserId + "none"),
                                    handler, Config.CODE_GETWORK);
                } else if (getResources().getString(R.string.category17)
                        .equalsIgnoreCase(industry)) {
                    try {
                        String schoolName = etSchoolName.getText().toString()
                                .trim();
                        String className = etClassName.getText().toString().trim();
                        String studentTime = tvStudentTime.getText().toString()
                                .trim();
                        if (TextUtils.isEmpty(schoolName)) {
                            ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                    .getString(R.string.fill_school_name));
                            return;
                        }
                        if (TextUtils.isEmpty(className)) {
                            ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                    .getString(R.string.fill_class_name));
                            return;
                        }
                        if (TextUtils.isEmpty(studentTime)) {
                            ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                    .getString(R.string.fill_admission_time));
                            return;
                        }
                        dialog.show();
                        HttpUtils.doGetAsyn(
                                Config.GETSCHOOL + "&userid=" + UserId
                                        + "&schoolName="
                                        + URLEncoder.encode(schoolName, "UTF-8")
                                        + "&className="
                                        + URLEncoder.encode(className, "UTF-8")
                                        + "&address=none&time=" + studentTime
                                        + "&stuId=none&p1=&p3=&mu="
                                        + MD5Util.md5("none" + UserId), handler,
                                Config.CODE_GETSCHOOL);
                    } catch (UnsupportedEncodingException e) {
                        Log.e("MOFA", e.getMessage());
                    }
                } else {

                    String company = etCompany.getText().toString().trim();
                    String companyPhone = etCompanyPhone.getText().toString()
                            .trim();
                    String companyAddress = etCompanyAddress.getText().toString()
                            .trim();
                    String position = etPosition.getText().toString().trim();
                    String time = tvTime.getText().toString().trim();
                    String wage = tvWage.getText().toString().trim();
                    // if ("Đang tìm việc".equalsIgnoreCase(industry)) {
                    // time = "";
                    // wage = "";
                    // } else {

                    if (TextUtils.isEmpty(industry)
                            || "Nhập tên ngành nghề".equalsIgnoreCase(industry)) {
                        ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                .getString(R.string.fill_company_sax));
                        return;
                    }
                    if (TextUtils.isEmpty(company)) {
                        ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                .getString(R.string.fill_company_name));
                        return;
                    }
                    if (TextUtils.isEmpty(companyPhone)) {
                        ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                .getString(R.string.fill_company_phone));
                        return;
                    }
                    if (TextUtils.isEmpty(companyAddress)) {
                        ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                .getString(R.string.fill_company_address));
                        return;
                    }
                    if (TextUtils.isEmpty(position)) {
                        ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                .getString(R.string.fill_position));
                        return;
                    }
                    if (TextUtils.isEmpty(wage)) {
                        ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                .getString(R.string.fill_salary_range));
                        return;
                    }
                    if (TextUtils.isEmpty(time)) {
                        ToastUtil.showLong(WorkInfo3Activity.this, getResources()
                                .getString(R.string.fill_entry_time));
                        return;
                    }
                    try {
                        dialog.show();
                        HttpUtils.doGetAsyn(
                                Config.GETWORK
                                        + "&userid="
                                        + UserId
                                        + "&workName="
                                        + URLEncoder.encode(company, "UTF-8")
                                        + "&tel="
                                        + companyPhone
                                        + "&address="
                                        + URLEncoder
                                        .encode(companyAddress, "UTF-8")
                                        + "&position="
                                        + URLEncoder.encode(position, "UTF-8")
                                        + "&pay="
                                        + URLEncoder.encode(wage, "UTF-8")
                                        + "&time=" + time + "&company="
                                        + URLEncoder.encode(industry, "UTF-8")
                                        + "&p1=" + "" + "&p2=" + "" + "&p3=" + ""
                                        + "&you="
                                        + MD5Util.md5(UserId + companyPhone),
                                handler, Config.CODE_GETWORK);
                    } catch (UnsupportedEncodingException e) {
                        Log.e("MOFA", e.getMessage());
                    }

                }
                break;
            default:
                break;
        }
    }

    private void initTimePicker() {
        // 控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        // 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();

        Calendar startDate = Calendar.getInstance();
        startDate.set(1970, 0, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.set(2025, 12, 30);
        // 时间选择器
        pvTime = new TimePickerView.Builder(WorkInfo3Activity.this,
                new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {// 选中事件回调
                        // 这里回调过来的v,就是show()方法里面所添加的 View
                        // 参数，如果show的时候没有添加参数，v则为null
                        /* btn_Time.setText(getTime(date)); */
                        tvTime.setText(getTime(date));
                        tvStudentTime.setText(getTime(date));
                    }
                })
                .setLayoutRes(R.layout.pickerview_time_yuenan,
                        new CustomListener() {

                            @Override
                            public void customLayout(View v) {
                                final TextView tvSubmit = v
                                        .findViewById(R.id.btnSubmit);
                                TextView ivCancel = v
                                        .findViewById(R.id.btnCancel);
                                tvSubmit.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pvTime.returnData();
                                        pvTime.dismiss();
                                    }
                                });
                                ivCancel.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pvTime.dismiss();
                                    }
                                });
                            }
                        })
                .setType(
                        new boolean[]{true, true, false, false, false, false})
                .setLabel("", "", "", "", "", "")
                // 设置空字符串以隐藏单位提示 hide label
                .setDividerColor(Color.DKGRAY).setContentSize(20)
                .setDate(selectedDate).setRangDate(startDate, endDate)
                .setDecorView(llPv)// 非dialog模式下,设置ViewGroup,
                // pickerView将会添加到这个ViewGroup中
                .setBackgroundId(0x00000000).setOutSideCancelable(true).build();

        pvTime.setKeyBackCancelable(false);// 系统返回键监听屏蔽掉
    }

    private void initNoLinkOptionsPicker() {// 不联动的多级选项
        pvNoLinkOptions = new OptionsPickerView.Builder(this,
                new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int options1, int options2,
                                                int options3, View v) {
                        tvWage.setText(cardItem2.get(options1));
                    }
                }).build();
        pvNoLinkOptions.setNPicker(cardItem2, null, null);
    }

    private void initNoLinkOptionsPicker2() {// 不联动的多级选项
        pvNoLinkOptions2 = new OptionsPickerView.Builder(this,
                new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int options1, int options2,
                                                int options3, View v) {
                        tvIndustry.setText(cardItem1.get(options1));
                        if (getResources().getString(R.string.category19)
                                .equalsIgnoreCase(cardItem1.get(options1))) {
//							llStudent.setVisibility(View.GONE);
//							llWork.setVisibility(View.GONE);
                            switchLayout(3);
                        } else if (getResources()
                                .getString(R.string.category17)
                                .equalsIgnoreCase(cardItem1.get(options1))) {
//							llStudent.setVisibility(View.VISIBLE);
//							llWork.setVisibility(View.GONE);
                            switchLayout(1);
                        } else {
//							llStudent.setVisibility(View.GONE);
//							llWork.setVisibility(View.VISIBLE);
                            switchLayout(2);
                        }
                    }
                }).build();
        pvNoLinkOptions2.setNPicker(cardItem1, null, null);
    }

    private String getTime(Date date) {// 可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
        return format.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String province = data.getStringExtra("province");
            String jun = data.getStringExtra("jun");
            String fang = data.getStringExtra("fang");
            String location = data.getStringExtra("location");
            if (requestCode == 305) {
                String address = location + " " + fang + " " + jun + " "
                        + province;
                etCompanyAddress.setText(address);
                SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                Editor editor = sp.edit();
                editor.putString("companyaddress", etCompanyAddress.getText().toString().trim());
                editor.commit();
            }
        } else {
            if (requestCode == 304) {

            } else if (requestCode == 305) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private MyProgressDialog dialog;

    private void showDialog(String Message) {
        new AlertDialog(WorkInfo3Activity.this)
                .builder()
                .setMsg(Message)
                .setNegativeButton(getResources().getString(R.string.OK),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
    }

    @Override
    public void processMessage(Message message) {

    }

    private boolean isExit = false;
    private TimerTask timeTask;
    private Timer timer = new Timer();

    @Override
    public void onBackPressed() {
        if (pvTime.isShowing()) {
            pvTime.dismiss();
            return;
        }
        if (pvNoLinkOptions.isShowing()) {
            pvNoLinkOptions.dismiss();
            return;
        }
        if (isExit) {
            Intent intent1 = new Intent(WorkInfo3Activity.this,
                    IndexActivity.class);
            intent1.putExtra("id", 1);
            startActivity(intent1);
            finish();
        } else {
            isExit = true;
            ToastUtil.showShort(WorkInfo3Activity.this, getResources()
                    .getString(R.string.warm_reclick_to_exit_veify));

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
    public boolean onTouchEvent(MotionEvent event) {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) WorkInfo3Activity.this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView()
                        .getWindowToken(), 0);
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    /**
     * 根据行业类型切换布局
     *
     * @param i ->
     *          1:学生
     *          2:工作
     *          3:待业
     */
    private void switchLayout(int i) {
        if (i == 1) {
            llCompany.setVisibility(View.GONE);
            llCompanyPhone.setVisibility(View.GONE);
            llCompanyAddress.setVisibility(View.GONE);
            llPosition.setVisibility(View.GONE);
            llTime.setVisibility(View.GONE);
            llWage.setVisibility(View.GONE);
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.GONE);
            iv3.setVisibility(View.GONE);
            iv4.setVisibility(View.GONE);
            iv5.setVisibility(View.GONE);
            iv6.setVisibility(View.GONE);
            iv7.setVisibility(View.VISIBLE);
            iv8.setVisibility(View.VISIBLE);
            llSchoolName.setVisibility(View.VISIBLE);
            llClassName.setVisibility(View.VISIBLE);
            llSchoolTime.setVisibility(View.VISIBLE);
        } else if (i == 2) {
            llCompany.setVisibility(View.VISIBLE);
            llCompanyPhone.setVisibility(View.VISIBLE);
            llCompanyAddress.setVisibility(View.VISIBLE);
            llPosition.setVisibility(View.VISIBLE);
            llTime.setVisibility(View.VISIBLE);
            llWage.setVisibility(View.VISIBLE);
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);
            iv4.setVisibility(View.VISIBLE);
            iv5.setVisibility(View.VISIBLE);
            iv6.setVisibility(View.VISIBLE);
            iv7.setVisibility(View.GONE);
            iv8.setVisibility(View.GONE);
            llSchoolName.setVisibility(View.GONE);
            llClassName.setVisibility(View.GONE);
            llSchoolTime.setVisibility(View.GONE);
        } else {
            llCompany.setVisibility(View.GONE);
            llCompanyPhone.setVisibility(View.GONE);
            llCompanyAddress.setVisibility(View.GONE);
            llPosition.setVisibility(View.GONE);
            llTime.setVisibility(View.GONE);
            llWage.setVisibility(View.GONE);
            iv1.setVisibility(View.GONE);
            iv2.setVisibility(View.GONE);
            iv3.setVisibility(View.GONE);
            iv4.setVisibility(View.GONE);
            iv5.setVisibility(View.GONE);
            iv6.setVisibility(View.GONE);
            iv7.setVisibility(View.GONE);
            iv8.setVisibility(View.GONE);
            llSchoolName.setVisibility(View.GONE);
            llClassName.setVisibility(View.GONE);
            llSchoolTime.setVisibility(View.GONE);
        }
    }

    private class MyHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;

        private MyHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            switch (msg.what) {
                case Config.CODE_GETSCHOOL:
                    String reString2 = msg.obj.toString();
                    JSONObject jsonObject2;
                    try {
                        jsonObject2 = new JSONObject(reString2);
                        int error = jsonObject2.optInt("error");
                        Log.i("MOFA", "WorkInfo---student" + "error:" + error);
                        if (0 == error) {
                            ToastUtil.showShort(WorkInfo3Activity.this, getResources()
                                    .getString(R.string.upload_successed));
                            SharedPreferences sp = getSharedPreferences("config",
                                    MODE_PRIVATE);
                            Editor editor = sp.edit();
                            editor.putInt("isschool", 1);
                            editor.putInt("isjob", 0);
                            editor.putInt("profession", 1);
                            editor.commit();

                            int isyhbd = sp.getInt("isyhbd", 0);
                            int isshenfen = sp.getInt("isshenfen", 0);
                            int islianxi = sp.getInt("islianxi", 0);
                            if (isyhbd == 0) {
                                Intent intent = new Intent(WorkInfo3Activity.this,
                                        BindCard3Activity.class);
                                startActivity(intent);
                            } else if (isshenfen == 0) {
                                Intent intent = new Intent(WorkInfo3Activity.this,
                                        BaseInfo2Activity.class);
                                startActivity(intent);
                            } else if (islianxi == 0) {
                                Intent intent = new Intent(WorkInfo3Activity.this,
                                        RelationInfo3Activity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(WorkInfo3Activity.this,
                                        IndexActivity.class);
                                intent.putExtra("id", 11);
                                startActivity(intent);
                            }
//						int isfacebook = sp.getInt("isfacebook", 0);
//						if (isfacebook == 0) {
//							startActivity(new Intent(WorkInfo2Activity.this,
//									FacebookAcytivity.class));
//						} else {
//							Intent intent = new Intent(WorkInfo2Activity.this,
//									IndexActivity.class);
//							intent.putExtra("id", 1);
//							startActivity(intent);
//						}
                            finish();
                        } else {
                            ToastUtil.showShort(WorkInfo3Activity.this,
                                    jsonObject2.optString("msg"));
                        }
                    } catch (JSONException e) {
                        Log.e("MOFA", e.getMessage());
                    }
                    break;
                case Config.CODE_GETWORK:
                    String reString = msg.obj.toString();
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(reString);
                        int error = jsonObject.optInt("error");
                        Log.i("MOFA", "WorkInfo---work" + "error:" + error);
                        if (0 == error) {
                            ToastUtil.showShort(
                                    WorkInfo3Activity.this,
                                    getResources().getString(
                                            R.string.bind_successed));

                            SharedPreferences sp = getSharedPreferences("config",
                                    MODE_PRIVATE);
                            Editor editor = sp.edit();
                            editor.putInt("isjob", 1);
                            editor.putInt("isschool", 0);
                            editor.putInt("profession", 2);
                            editor.commit();
//						int isfacebook = sp.getInt("isfacebook", 0);
//						if (isfacebook == 0) {
//							startActivity(new Intent(WorkInfo2Activity.this,
//									FacebookAcytivity.class));
//						} else {
//							Intent intent = new Intent(WorkInfo2Activity.this,
//									IndexActivity.class);
//							intent.putExtra("id", 2);
//							startActivity(intent);
//						}
                            int isyhbd = sp.getInt("isyhbd", 0);
                            int isshenfen = sp.getInt("isshenfen", 0);
                            int islianxi = sp.getInt("islianxi", 0);
                            if (isyhbd == 0) {
                                Intent intent = new Intent(WorkInfo3Activity.this,
                                        BindCard3Activity.class);
                                startActivity(intent);
                            } else if (isshenfen == 0) {
                                Intent intent = new Intent(WorkInfo3Activity.this,
                                        BaseInfo2Activity.class);
                                startActivity(intent);
                            } else if (islianxi == 0) {
                                Intent intent = new Intent(WorkInfo3Activity.this,
                                        RelationInfo3Activity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(WorkInfo3Activity.this,
                                        IndexActivity.class);
                                intent.putExtra("id", 11);
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            ToastUtil.showShort(WorkInfo3Activity.this,
                                    jsonObject.optString("msg"));

                        }
                    } catch (JSONException e) {
                        Log.e("MOFA", e.getMessage());
                    }
                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(WorkInfo3Activity.this, getResources()
                            .getString(R.string.url_error));

                    break;
                case Config.CODE_ERROR:
                    ToastUtil.showShort(WorkInfo3Activity.this, "system error");

                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(WorkInfo3Activity.this, getResources()
                            .getString(R.string.network_error));

                    break;
                default:
                    break;
            }

        }
    }
}
