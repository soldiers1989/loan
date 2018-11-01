package com.mofa.loan.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TimeService extends Service {

	/** 
     * 创建参数 
     */  
    boolean threadDisable;  
    int count;  
  
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        /** 创建一个线程， 每秒计数器加1， 并在控制台进行Log输出 */  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
                while (!threadDisable) {  
                    try {  
                        Thread.sleep(1000);  
                    } catch (Exception e) {  
                        Log.e("MOFA", e.getMessage());
                    }  
                    count++;  
                    // 判断时间，这里默认的是2min  
                    if (count == 2 * 60) {  
                    	Log.e("驻留时间：", count + "");  
                        //用戶持续使用app已达2min  
                    }  
                }  
            }  
        }).start();  
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.e("onStart()","service的onstart()执行了");  
        return super.onStartCommand(intent, flags, startId);  
    }  
  
    @Override  
    public boolean onUnbind(Intent intent) {  
        /** 服务停止时， 终止计数进程 */  
        this.threadDisable = true;  
        Log.e("onUnbind()","service的onUnbind()执行了");  
        return super.onUnbind(intent);  
    }  
  
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        Log.e("onDestroy()","service的onDestroy()执行了");  
    }  

}
