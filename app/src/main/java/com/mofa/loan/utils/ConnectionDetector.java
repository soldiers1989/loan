package com.mofa.loan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mofa.loan.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author 作者 maqinghua
 * @version 创建时间:Jul 25, 2012 4:44:30 PM 
 * 类说明 网络连接检查器
 */
public class ConnectionDetector {

	private Context context;

	public ConnectionDetector(Context context) {
		this.context = context;
	}

	/**
	 * @author 作者 maqinghua
	 * @version 创建时间:Jul 25, 2012 4:44:30 PM 
	 * 方法说明 检查是否有网络连接
	 */
	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			Log.i("MOFA", "有可用网络");
			return true;
		} else {
			Log.i("MOFA", "无可用网络");
//			Toast.makeText(context, "无可用网络", 2000).show();
			new AlertDialog(context).builder().setMsg(context.getResources().getString(R.string.timeout_of_network))
			.setNegativeButton(context.getResources().getString(R.string.OK), new OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent intent =  new Intent(AskMoneyActivity.this, BindCardActivity.class);
//					startActivity(intent);
//					finish();
				}
			}).show();
			return false;
		}
	}
	
	public boolean isConnectingToInternet2() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
	}
	public boolean isConnectingToInternet3() {
		  ConnectivityManager manager = (ConnectivityManager) context
	                .getApplicationContext().getSystemService(  
	                        Context.CONNECTIVITY_SERVICE);
	  
	        TelephonyManager mTelephony = (TelephonyManager) context
	                .getSystemService(Context.TELEPHONY_SERVICE);
	  
	        // 检查网络连接，如果无网络可用，就不需要进行连网操作等  
	        NetworkInfo info = manager.getActiveNetworkInfo();
	  
	        if (info == null || !manager.getBackgroundDataSetting()) {  
	            return false;  
	        }  
	  
	        // 判断网络连接类型，只有在3G或wifi里进行一些数据更新。  
	        int netType = info.getType();  
	        int netSubtype = info.getSubtype();  
	  
	        if (netType == ConnectivityManager.TYPE_WIFI) {
	            return info.isConnected();  
	        } else if (netType == ConnectivityManager.TYPE_MOBILE
	                && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
	                && !mTelephony.isNetworkRoaming()) {  
	            return info.isConnected();  
	        } else {  
	            return false;  
	        }  
	}
	
	/**
	 * @author 作者 maqinghua
	 * @version 创建时间:Jul 25, 2012 4:44:30 PM 
	 * 方法说明 检查指定ip地址是否有效/是否有连接
	 */	
	public boolean checkURL(String url){
		boolean result = false;
		try {
			HttpURLConnection conn=(HttpURLConnection)new URL(url).openConnection();
			conn.setConnectTimeout(30000);
			int code = conn.getResponseCode();
			if(code!=200){
				result=false;
				ToastUtil.showShort(context, context.getResources().getString(R.string.no_network));
			   }else{
				result=true;
			   }
		} catch (MalformedURLException e) {
			Log.e("MOFA", e.getMessage());
		} catch (IOException e) {
			Log.e("MOFA", e.getMessage());
		}
		return result;
	}

}
