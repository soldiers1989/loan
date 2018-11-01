package com.mofa.loan.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mofa.loan.R;
import com.mofa.loan.listener.CustomListener;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.EditUtils;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.view.MyProgressDialog;
import com.mofa.loan.view.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ResourceAsColor")
public class StudentInfo2Activity extends BaseActivity implements OnClickListener {
	
	
	private LinearLayout llSchoolName;
	private LinearLayout llSchoolAddress;
	private LinearLayout llClassName;
	private LinearLayout llStudentId;
	private LinearLayout llSchoolTime;
	private EditText etSchoolName;
	private EditText etAddress;
	private EditText etClassname;
	private EditText etStudentid;
	private TextView tvTime;
	private Button btnNext;
	private TimePickerView pvTime;
	private FrameLayout llPv;
	
	private MyProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_studentinfo2);
		Log.i("MOFA", "StudentInfo2---onCreate");
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		initview();
	}

	private long timeIn;
	private long timeOut;
	
	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "StudentInfo2---onStart:" + timeIn);
	}
	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "StudentInfo2---onStop:" + timeOut);
		Log.i("MOFA", "StudentInfo2---Show:" + (timeOut - timeIn));
	}
	
	@Override
	protected void onResume() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		super.onResume();
	}
	
	private void initview() {
		
		llPv = findViewById(R.id.ll_time_picker);
		llSchoolName = findViewById(R.id.ll_school);
		llSchoolName.setOnClickListener(this);
		llSchoolAddress = findViewById(R.id.ll_schooladdress);
		llSchoolAddress.setOnClickListener(this);
		llClassName = findViewById(R.id.ll_classroom);
		llClassName.setOnClickListener(this);
		llStudentId = findViewById(R.id.ll_studentid);
		llStudentId.setOnClickListener(this);
		llSchoolTime = findViewById(R.id.ll_time);
		llSchoolTime.setOnClickListener(this);
		etSchoolName = findViewById(R.id.et_schoolname);
		etAddress = findViewById(R.id.et_schooladdress);
		etClassname = findViewById(R.id.et_classroom);
		etStudentid = findViewById(R.id.et_studentid);
		EditUtils.setEtFilter(etSchoolName);
		EditUtils.setEtFilter(etAddress);
		EditUtils.setEtFilter(etClassname);
		EditUtils.setEtFilter(etStudentid);
		tvTime = findViewById(R.id.tv_time);
		btnNext = findViewById(R.id.btn_studentinfo_next);
		btnNext.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		dialog = new MyProgressDialog(StudentInfo2Activity.this, "");
		initTimePicker();
	}
	
	private void initTimePicker() {
		// 控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
		// 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
		Calendar selectedDate = Calendar.getInstance();

		Calendar startDate = Calendar.getInstance();
		startDate.set(1970, 0, 1);

		Calendar endDate = Calendar.getInstance();
		endDate.set(2025, 12, 30);
		// 时间选择器
		pvTime = new TimePickerView.Builder(StudentInfo2Activity.this,
				new TimePickerView.OnTimeSelectListener() {
					@Override
					public void onTimeSelect(Date date, View v) {// 选中事件回调
						// 这里回调过来的v,就是show()方法里面所添加的 View
						// 参数，如果show的时候没有添加参数，v则为null
						/* btn_Time.setText(getTime(date)); */
						tvTime.setText(getTime(date));
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
								tvSubmit.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										pvTime.returnData();
										pvTime.dismiss();
									}
								});
								ivCancel.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										pvTime.dismiss();
									}
								});
							}
						})
				.setType(
						new boolean[] { true, true, false, false, false, false })
				.setLabel("", "", "", "", "", "")
				// 设置空字符串以隐藏单位提示 hide label
				.setDividerColor(Color.DKGRAY).setContentSize(20)
				.setDate(selectedDate).setRangDate(startDate, endDate)
				.setDecorView(llPv)// 非dialog模式下,设置ViewGroup,
									// pickerView将会添加到这个ViewGroup中
				.setBackgroundId(0x00000000).setOutSideCancelable(true).build();

		pvTime.setKeyBackCancelable(false);// 系统返回键监听屏蔽掉
	}
	
	private String getTime(Date date) {// 可根据需要自行截取数据显示
		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		return format.format(date);
	}


	@Override
	public void processMessage(Message message) {
		
	}

	private void showDialog(String Message) {
		new AlertDialog(StudentInfo2Activity.this).builder().setMsg(Message)
				.setNegativeButton(getResources().getString(R.string.OK), new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}).show();
	}

	@Override
	public void onClick(View v) {
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (pvTime.isShowing()) {
			pvTime.dismissDialog();
		}
		switch (v.getId()) {
		case R.id.back:
			Intent intent1 = new Intent(StudentInfo2Activity.this, IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
			break;
			
		case R.id.ll_school:
			etSchoolName.requestFocus();
			break;

		case R.id.ll_schooladdress:
			etAddress.requestFocus();
			break;
			
		case R.id.ll_classroom:
			etClassname.requestFocus();
			break;
			
		case R.id.ll_studentid:
			etStudentid.requestFocus();
			break;
			
		case R.id.ll_time:
			imm.hideSoftInputFromWindow(llSchoolTime.getWindowToken(), 0);
			if (pvTime != null) {
				pvTime.show(v, false);
			}
			break;
			
		case R.id.btn_studentinfo_next:
			String schoolname = etSchoolName.getText().toString().trim();
			String address = etAddress.getText().toString().trim();
			String classname = etClassname.getText().toString().trim();
			String studentid = etStudentid.getText().toString().trim();
			String time = tvTime.getText().toString().trim();
			if (TextUtils.isEmpty(schoolname)) {
				showDialog(getResources().getString(R.string.fill_school_name));
				return;
			}
			if (TextUtils.isEmpty(address)) {
				showDialog(getResources().getString(R.string.fill_school_address));
				return;
			}
			if (TextUtils.isEmpty(classname)) {
				showDialog(getResources().getString(R.string.fill_class_name));
				return;
			}
			if (TextUtils.isEmpty(studentid)) {
				showDialog(getResources().getString(R.string.fill_student_ID));
				return;
			}
			if (TextUtils.isEmpty(time)) {
				showDialog(getResources().getString(R.string.fill_admission_time));
				return;
			}
			dialog.show();
			Intent intent = new Intent(StudentInfo2Activity.this, IDCardActivity.class);
			intent.putExtra("schoolname", schoolname);
			intent.putExtra("address", address);
			intent.putExtra("classname", classname);
			intent.putExtra("studentid", studentid);
			intent.putExtra("time", time);
			intent.putExtra("from", "student");
			startActivity(intent);
			break;
			
		default:
			break;
		}
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
		if (isExit) {
			Intent intent1 = new Intent(StudentInfo2Activity.this,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
		} else {
			isExit = true;
			ToastUtil.showShort(StudentInfo2Activity.this, getResources().getString(R.string.warm_reclick_to_exit_veify));

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
	        InputMethodManager imm = (InputMethodManager) StudentInfo2Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        if (imm != null) {
	            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
	        }
	        return true;
	    } else {
	        return super.onTouchEvent(event);
	    }
	}
	
}
