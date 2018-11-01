package com.mofa.loan.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerTrackingRequestListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

@TargetApi(23)
public class BaseApplication extends Application {
    private static Context mContext;
    private boolean isDownload;
    private static BaseApplication mApplication;
    private static int SIMPLIFIED_CHINESE = 301;
    private static int ENGLISH = 302;
    private static int YUENAN = 303;
    private static int TRADITIONAL_CHINESE = 304;

    private static final String AF_DEV_KEY = "vp6BJsTNhvEa6PVVsCoNvM";

    public synchronized static BaseApplication getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        // ShareSDK.initSDK(this);
        Log.i("MOFA", "onCreate app");
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                LogcatHelper.getInstance(this).start();
            }
        } else {
            LogcatHelper.getInstance(this).start();
        }
        // 是否开启debug模式
        mContext = this;
        isDownload = false;
        mApplication = this;
        AppsFlyerLib.getInstance().setDebugLog(false);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        super.onCreate();
        // MoxieSDK.init(this);
        // TelephonyManager tm = (TelephonyManager) this
        // .getSystemService(Context.TELEPHONY_SERVICE);
        // String deviceId = tm.getDeviceId();
        // JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        int isFirst = sp.getInt("isFirstIn", 0);
        String userid = sp.getString("userid", "");
        Editor editor = sp.edit();
        editor.putInt("isFirstIn", isFirst + 1);
        editor.commit();
        Log.i("MOFA", "isFirst:" + (isFirst + 1));
        //
        // Configuration config = getResources().getConfiguration();
        // if (!"vi".equalsIgnoreCase(config.locale.getLanguage())) {
        // changeAppLanguage(getResources(), YUENAN);
        // }
        AppsFlyerConversionListener conversionDataListener = new AppsFlyerConversionListener() {
            @Override
            public void onAttributionFailure(String arg0) {
            }

            @Override
            public void onInstallConversionFailure(String arg0) {
            }

            @Override
            public void onInstallConversionDataLoaded(
                    Map<String, String> paramMap) {
                for (String attrName : paramMap.keySet()) {
                    Log.d(AppsFlyerLib.LOG_TAG, "attribute: " + attrName
                            + " = " + paramMap.get(attrName));
                }
                Map<String, Object> eventValues = new HashMap<>();
                eventValues.put("testInstall", "ture");
                AppsFlyerLib.getInstance().trackEvent(mApplication,
                        "Install",
                        eventValues);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> paramMap) {
                Map<String, Object> eventValues = new HashMap<>();
                for (String attrName : paramMap.keySet()) {
                    Log.d(AppsFlyerLib.LOG_TAG, "attribute: " + attrName
                            + " = " + paramMap.get(attrName));
                    eventValues.put(attrName, paramMap.get(attrName));
                }
                AppsFlyerLib.getInstance().trackEvent(mApplication, "APP_OPEN",
                        eventValues);
            }

        };
        AppsFlyerTrackingRequestListener listener = new AppsFlyerTrackingRequestListener() {
            @Override
            public void onTrackingRequestSuccess() {

            }

            @Override
            public void onTrackingRequestFailure(String s) {

            }
        };
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionDataListener,
                getApplicationContext());
        if (!userid.isEmpty()) {
            AppsFlyerLib.getInstance().setCustomerUserId(userid);
        }
//        // String ANDROID_ID = Settings.System.getString(getContentResolver(),
//        // Settings.System.ANDROID_ID);
//         AppsFlyerLib.getInstance().setAndroidIdData(ANDROID_ID);
//        AppsFlyerLib.getInstance().setImeiData("IMEI_DATA_HERE");
//        AppsFlyerLib.getInstance().setAndroidIdData("ANDROID_ID_DATA_HERE");
//        AppsFlyerLib.getInstance().setCollectAndroidID(true);
        AppsFlyerLib.getInstance().startTracking(this);
        if (0 == isFirst) {
            Map<String, Object> eventValues = new HashMap<>();
            eventValues.put("testInstalled", "ture");
            AppsFlyerLib.getInstance().trackEvent(mApplication, "Installed",
                    eventValues);
        }
    }

    /**
     * 修改语言设置
     *
     * @param resources
     * @param type
     */
    public void changeAppLanguage(Resources resources, int type) {
        Configuration config = getResources().getConfiguration();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (type == SIMPLIFIED_CHINESE) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (type == ENGLISH) {
            config.locale = Locale.US;
        } else if (type == YUENAN) {
            config.locale = new Locale("vi", "VN");
        } else if (type == TRADITIONAL_CHINESE) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
        Intent i = getAppContext().getPackageManager()
                .getLaunchIntentForPackage(getAppContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getAppContext().startActivity(i);
    }

    public static Context getAppContext() {
        return mContext;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    /**
     * 检测授权
     *
     * @param context
     * @param ManifestPermission
     * @param requestCode        请求吗
     * @return true:已经授权 false没有授权
     */
    public boolean insertDummyContactWrapper(Activity context,
                                             String ManifestPermission, int requestCode) {
        int hasWriteContactsPermission = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWriteContactsPermission = context
                    .checkSelfPermission(ManifestPermission);
        }
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.requestPermissions(new String[]{ManifestPermission},
                        requestCode);
            }
            return false;
        } else {
            return true;
        }

    }

}
