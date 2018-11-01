package com.mofa.loan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;
import java.util.Map;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // pdus短信单位pdu
        // 解析短信内容
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu : pdus) {
            // 封装短信参数的对象
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String number = sms.getOriginatingAddress();
            String body = sms.getMessageBody();
            // 写自己的处理逻辑
            Log.i("MOFA", "短信号码：" + number);
            if ("+84899581888".equals(number)) {

                if (!body.isEmpty()) {

                    if (body.contains("Ban da thanh toan so tien")) {
                        String a = "Ban da thanh toan so tien";
                        int i = body.indexOf(a);
                        int j = body.indexOf(". Moi");
                        Map<String, Object> eventValues = new HashMap<>();
                        try {
                            int money = Integer.valueOf(body.substring(i + a.length(), j).replace(",", "").trim());
                            eventValues.put(AFInAppEventParameterName.REVENUE, "" + money);
                            eventValues.put(AFInAppEventParameterName.CURRENCY, "VND");
                        } catch (Exception e) {
                            Log.e("MOFA", e.getMessage());
                        } finally {
                            eventValues.put("backmoneydone", "ture");
                            AppsFlyerLib.getInstance().trackEvent(context, "backmoneydone",
                                    eventValues);
                        }
                    } else if (body.contains("xin thong bao: So tien vay la")) {
                        String a = "So tien vay la";
                        int i = body.indexOf(a);
                        int j = body.indexOf("cua ban da");
                        Map<String, Object> eventValues = new HashMap<>();
                        try {
                            int money = Integer.valueOf(body.substring(i + a.length(), j).replace(",", "").trim());
                            eventValues.put(AFInAppEventParameterName.REVENUE, "" + money);
                            eventValues.put(AFInAppEventParameterName.CURRENCY, "VND");
                        } catch (Exception e) {
                            Log.e("MOFA", e.getMessage());
                        } finally {
                            eventValues.put("loandone", "ture");
                            AppsFlyerLib.getInstance().trackEvent(context, "loandone",
                                    eventValues);
                        }
                    }
                }
            }
        }
    }
}
