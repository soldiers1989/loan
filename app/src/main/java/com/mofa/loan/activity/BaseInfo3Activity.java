package com.mofa.loan.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.utils.Validator;
import com.mofa.loan.view.OptionsPickerView;
import com.mofa.loan.view.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(23)
public class BaseInfo3Activity extends BaseActivity implements OnClickListener {

	private TimePickerView pvTime;
	private OptionsPickerView pvNoLinkOptions;
	private ArrayList<String> cardItem2 = new ArrayList<>();
	private FrameLayout llPv;

	private LinearLayout llGender;
	private LinearLayout llBorn;
	private LinearLayout llAddressNow;
	private Button btnNext;
	private TextView tvName;
	private EditText etCMND;
	private TextView tvAddressNow;
	private TextView tvGender;
	private TextView tvBirthday;

	private String name;
	private String cmnd;
	private String gender;
	private String birthday;
	private String addressNow;
	private String address = "none";
	private String email = "none";
	private String phoneType = "none";

	SharedPreferences sp;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("MOFA", "BaseInfo2---onCreate");
		setContentView(R.layout.activity_base_info3);
		initview();
	}

	@Override
	protected void onResume() {
		cmnd = sp.getString("baseInfocmnd", "");
		gender = sp.getString("baseInfogender", "");
		birthday = sp.getString("baseInfobirthday", "");
		addressNow = sp.getString("baseInfoaddressNow", "");
//		address = sp.getString("baseInfoaddress", "");
//		email = sp.getString("baseInfoemail", "");
//		phoneType = sp.getString("baseInfophoneType", "");
		if (!TextUtils.isEmpty(cmnd)) {
			etCMND.setText(cmnd);
		}
		if (!TextUtils.isEmpty(gender)) {
			tvGender.setText(gender);
		}
		if (!TextUtils.isEmpty(birthday)) {
			tvBirthday.setText(birthday);
		}
//		if (!TextUtils.isEmpty(address)) {
//			etAddress.setText(address);
//		}
		if (!TextUtils.isEmpty(addressNow)) {
			tvAddressNow.setText(addressNow);
		}
//		if (!TextUtils.isEmpty(email)) {
//			etEmail.setText(email);
//		}
//		if (!TextUtils.isEmpty(phoneType)) {
//			etPhoneType.setText(phoneType);
//		}
		Log.i("MOFA", "onResume");
		super.onResume();
	}

	private long timeIn;
	private long timeOut;

	@Override
	protected void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "BaseInfo2---onStart:" + timeIn);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Editor editor = sp.edit();
		editor.putString("baseInfocmnd", etCMND.getText().toString().trim());
		editor.putString("baseInfogender", tvGender.getText().toString().trim());
		editor.putString("baseInfobirthday", tvBirthday.getText().toString().trim());
//		editor.putString("baseInfoaddress", etAddress.getText().toString().trim());
		editor.putString("baseInfoaddressNow", tvAddressNow.getText().toString().trim());
//		editor.putString("baseInfoemail", etEmail.getText().toString().trim());
//		editor.putString("baseInfophoneType", etPhoneType.getText().toString().trim());
		editor.commit();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "BaseInfo2---onStop:" + timeOut);
		Log.i("MOFA", "BaseInfo2---Show:" + (timeOut - timeIn));
	}

	@Override
	protected void onDestroy() {
		Log.i("MOFA", "onDestroy");
		super.onDestroy();
	}

	private void initview() {
		mContext = this;
        ((TextView) findViewById(R.id.title_txt_center)).setText("Xác minh thông tin cá nhân");
		sp = getSharedPreferences("config", MODE_PRIVATE);
//		int profession = sp.getInt("profession", 0);
		name = getIntent().getStringExtra("bankname2");
		Log.i("MOFA", "1-" + name);
		if (TextUtils.isEmpty(name)) {
			name = sp.getString("bankname2", "");
			Log.i("MOFA", "2-" + name);
		}
//		if (profession == 1) {
//			ImageView ivProfession = (ImageView) findViewById(R.id.iv_profession);
//			ivProfession.setImageResource(R.drawable.unveifiedstudent);
//		}
		findViewById(R.id.back).setOnClickListener(this);
		llPv = findViewById(R.id.ll_picker);
		initTimePicker();
		initNoLinkOptionsPicker();
		// llName = (LinearLayout) findViewById(R.id.ll_name);
		// llName.setOnClickListener(this);
		llGender = findViewById(R.id.ll_sex);
		llGender.setOnClickListener(this);
		llBorn = findViewById(R.id.ll_birthday);
		llBorn.setOnClickListener(this);
		llAddressNow = findViewById(R.id.ll_location);
		llAddressNow.setOnClickListener(this);
		btnNext = findViewById(R.id.btn_baseinfo_next);
		btnNext.setOnClickListener(this);
		tvName = findViewById(R.id.tv_name);
		tvGender = findViewById(R.id.tv_sex);
		tvBirthday = findViewById(R.id.tv_birthday);
		// etName = (EditText) findViewById(R.id.et_name);
		// etName.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// String editable = etName.getText().toString();
		// String str = EditUtils.stringFilter(editable.toString());
		// if(!editable.equals(str)){
		// etName.setText(str);
		// //设置新的光标所在位置 www.2cto.com
		// etName.setSelection(str.length());
		// }
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		//
		// }
		// });
		etCMND = findViewById(R.id.et_cmnd);
		tvAddressNow = findViewById(R.id.tv_loacation);
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showShort(this, "no name");
        } else {
            tvName.setText(name);
        }
	}

	@Override
	public void processMessage(Message message) {

	}

	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (pvNoLinkOptions.isShowing()) {
			pvNoLinkOptions.dismissDialog();
		}
		if (pvTime.isShowing()) {
			pvTime.dismissDialog();
		}
		switch (v.getId()) {
		case R.id.back:
			Intent intent1 = new Intent(mContext,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
			Log.i("MOFA", "---BaseInfo2---onClick --> 后退");
			break;
		case R.id.ll_sex:
			imm.hideSoftInputFromWindow(llGender.getWindowToken(), 0);
			if (pvNoLinkOptions != null) {
				pvNoLinkOptions.show();
			}
			break;
		case R.id.ll_birthday:
			imm.hideSoftInputFromWindow(llBorn.getWindowToken(), 0);
			if (pvTime != null) {
				pvTime.show(v, false);
			}
			break;
		case R.id.ll_location:
			// etName.requestFocus();
			Intent locationIntent = new Intent(mContext,
					ChooseLocationActivity.class);
			startActivityForResult(locationIntent, 304);
			break;
//		case R.id.ll_address:
//			Intent locationIntent2 = new Intent(mContext,
//					ChooseLocationActivity.class);
//			startActivityForResult(locationIntent2, 305);
//			// etName.requestFocus();
//			break;
//		case R.id.ll_email:
//			etEmail.requestFocus();
//			break;
//		case R.id.ll_phonetype:
//			etPhoneType.requestFocus();
//			break;
		case R.id.btn_baseinfo_next:
			// name = etName.getText().toString().trim();
			cmnd = etCMND.getText().toString().trim();
			gender = tvGender.getText().toString().trim();
			birthday = tvBirthday.getText().toString().trim();
			if ("Nam".equalsIgnoreCase(gender)) {
				gender = "1";
			} else {
				gender = "2";
			}
			// String
			// regEx="[~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
			// Pattern p = Pattern.compile(regEx);
			// Matcher m = p.matcher(name);
//			if ("".equalsIgnoreCase(email)) {
//
//			} else if (!Validator.isEmail(email)) {
//				ToastUtil.showShort(
//						mContext,
//						mContext.getResources().getString(
//								R.string.warm_wrong_email));
//
//				etEmail.requestFocus();
//				return;
//			}
			if (TextUtils.isEmpty(name)) {
				// ToastUtil.showShort(
				// mContext,
				// mContext.getResources().getString(
				// R.string.warm_realname));
				//
				// etName.requestFocus();
				ToastUtil.showShort(mContext, "no name");
			} else if (TextUtils.isEmpty(cmnd)) {
				// if (TextUtils.isEmpty(cmnd)) {
				ToastUtil.showShort(
						mContext,
						mContext.getResources().getString(
								R.string.warm_IDnumber));

				etCMND.requestFocus();
			} else if (!Validator.isIDCardYueNan(cmnd)) {
				ToastUtil.showShort(
						mContext,
						mContext.getResources().getString(
								R.string.warm_IDnumber_not_match));

				etCMND.requestFocus();
//			} else if (TextUtils.isEmpty(address)) {
//				ToastUtil.showShort(
//						mContext,
//						mContext.getResources().getString(
//								R.string.hint_address));
//
//				etAddress.requestFocus();
			} else if (TextUtils.isEmpty(addressNow)) {
				ToastUtil.showShort(
						mContext,
						mContext.getResources().getString(
								R.string.hint_address));

//				etAddressNow.requestFocus();
			} else if (TextUtils.isEmpty(birthday)) {
				ToastUtil.showShort(
						mContext,
						mContext.getResources().getString(
								R.string.hint_both));

//			} else if (TextUtils.isEmpty(phoneType)) {
//				ToastUtil.showShort(
//						mContext,
//						mContext.getResources().getString(
//								R.string.hint_phone_type));

			} else {
				goIDCard();
			}
			break;
		default:
			break;
		}
	}

	private void goIDCard() {
		Intent intent = new Intent(mContext, IDCard3Activity.class);
		intent.putExtra("username", name);
		intent.putExtra("IDnumber", cmnd);
		intent.putExtra("address", address);
		intent.putExtra("addressnow", addressNow);
		intent.putExtra("gender", gender);
		intent.putExtra("email", email);
		intent.putExtra("both", birthday);
		intent.putExtra("phonetype", phoneType);
		intent.putExtra("from", "base");
		startActivity(intent);
		finish();
	}

	private void initNoLinkOptionsPicker() {// 不联动的多级选项
		cardItem2.add("Nam");
		cardItem2.add("Nữ");
		pvNoLinkOptions = new OptionsPickerView.Builder(this,
				new OptionsPickerView.OnOptionsSelectListener() {

					@Override
					public void onOptionsSelect(int options1, int options2,
							int options3, View v) {
						tvGender.setText(cardItem2.get(options1));
					}
				}).build();
		pvNoLinkOptions.setNPicker(cardItem2, null, null);
	}

	private String getTime(Date date) {// 可根据需要自行截取数据显示
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		return format.format(date);
	}

	private void initTimePicker() {
		// 控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
		// 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
		Calendar selectedDate = Calendar.getInstance();

		Calendar startDate = Calendar.getInstance();
		startDate.set(1960, 0, 1);

		Calendar endDate = Calendar.getInstance();
		// endDate.set(2025, 12, 30);
		endDate.setTime(selectedDate.getTime());
		// 时间选择器
		pvTime = new TimePickerView.Builder(mContext,
				new TimePickerView.OnTimeSelectListener() {
					@Override
					public void onTimeSelect(Date date, View v) {// 选中事件回调
						// 这里回调过来的v,就是show()方法里面所添加的 View
						// 参数，如果show的时候没有添加参数，v则为null
						/* btn_Time.setText(getTime(date)); */
						tvBirthday.setText(getTime(date));
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
						new boolean[] { true, true, true, false, false, false })
				.setLabel("", "", "", "", "", "")
				// 设置空字符串以隐藏单位提示 hide label
				.setDividerColor(Color.DKGRAY).setContentSize(20)
				.setDate(selectedDate).setRangDate(startDate, endDate)
				.setDecorView(llPv)// 非dialog模式下,设置ViewGroup,
									// pickerView将会添加到这个ViewGroup中
				.setBackgroundId(0x00000000).setOutSideCancelable(true).build();

		pvTime.setKeyBackCancelable(false);// 系统返回键监听屏蔽掉
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String province = data.getStringExtra("province");
			String jun = data.getStringExtra("jun");
			String fang = data.getStringExtra("fang");
			String location = data.getStringExtra("location");
			if (requestCode == 304) {
				tvAddressNow.setText(location + " " + fang + " " + jun + " "
						+ province);
				addressNow = location + "-" + fang + "-" + jun + "-" + province;
				Editor editor = sp.edit();
				editor.putString("baseInfoaddressNow", tvAddressNow.getText().toString().trim());
				editor.commit();
			} else if (requestCode == 305) {
//				etAddress.setText(location + " " + fang + " " + jun + " "
//						+ province);
//				address = location + "-" + fang + "-" + jun + "-" + province;
//				Editor editor = sp.edit();
//				editor.putString("baseInfoaddress", etAddress.getText().toString().trim());
//				editor.commit();
			}
		} else {
			if (requestCode == 304) {

			} else if (requestCode == 305) {

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
			Intent intent1 = new Intent(mContext,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
		} else {
			isExit = true;
			ToastUtil.showShort(
					mContext,
					mContext.getResources().getString(
							R.string.warm_reclick_to_exit_veify));

			timeTask = new TimerTask() {

				@Override
				public void run() {
					isExit = false;
				}
			};
			timer.schedule(timeTask, 3000);
		}
		Log.i("MOFA", "---BaseInfo2---onBackPressed");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) mContext
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

}
