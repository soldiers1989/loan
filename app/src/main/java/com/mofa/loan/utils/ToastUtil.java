package com.mofa.loan.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil {
	
	private static Toast toastShort;  
	private static Toast toastLong;  
	  
    public static void showShort(Context context, String msg) {  
        if (toastShort == null) {  
            toastShort = Toast.makeText(context, msg, Toast.LENGTH_LONG);  
        } else {  
            toastShort.setText(msg);  
        }  
        toastShort.show();  
        Log.i("MOFA", "ToastShort:" + msg);
    }
    
    public static void showLong(Context context, String msg) {  
    	if (toastLong == null) {  
    		toastLong = Toast.makeText(context, msg, Toast.LENGTH_LONG);  
    	} else {  
    		toastLong.setText(msg);  
    	}  
    	toastLong.show();  
    	Log.i("MOFA", "ToastLong:" + msg);
    }  
}
