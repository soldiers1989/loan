package com.mofa.loan.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;

@TargetApi(23)
public class LocationUtil {

	private static final long REFRESH_TIME = 5000L;
	private static final float METER_POSITION = 0.0f;

	private LocationManager locationManager;
	private String locationProvider;
	private Context context;
	private Handler handler;

	public LocationUtil(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	public void getLocation(Context context, Handler handler) {
		// 获取地理位置管理器
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 获取所有可用的位置提供器
		List<String> providers = locationManager.getProviders(true);
		if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			// 如果是Network
			locationProvider = LocationManager.NETWORK_PROVIDER;
		} else if (providers.contains(LocationManager.GPS_PROVIDER)) {
			// 如果是GPS
			locationProvider = LocationManager.GPS_PROVIDER;
		} else {
			Log.i("MOFA", "LocationUtil---没有可用的位置提供器");
			return;
		}
		// 获取Location
		Location location = locationManager
				.getLastKnownLocation(locationProvider);
		if (location != null) {
			Message msg = Message.obtain();
			msg.what = 201;
			msg.obj = location.getLatitude() + ":" + location.getLongitude();
			handler.sendMessage(msg);
		} else {
			location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				Message msg = Message.obtain();
				msg.what = 201;
				msg.obj = location.getLatitude() + ":"
						+ location.getLongitude();
				handler.sendMessage(msg);
			} else {
				Log.i("MOFA", "Location---"+LocationManager.GPS_PROVIDER + ":没有获取到可用的位置");
			}
		}
		// 监视地理位置变化
		locationManager.requestLocationUpdates(locationProvider, 60000, 10,
				locationListener);
	}

	public void cancleListener() {
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle arg2) {
			Log.i("MOFA", "LocationUtil---onStatusChanged");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("MOFA", "LocationUtil---onProviderEnabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("MOFA", "LocationUtil---onProviderDisabled");
		}

		@Override
		public void onLocationChanged(Location location) {
			// 如果位置发生变化,重新显示
//			Toast.makeText(
//					context,
//					"维度：" + location.getLatitude() + "\n" + "经度："
//							+ location.getLongitude(), Toast.LENGTH_SHORT)
//					.show();
			Log.i("MOFA", "Location---onLocationChanged---"+"维度：" + location.getLatitude() + "\n" + "经度："
					+ location.getLongitude());
			Message msg = Message.obtain();
			msg.what = 200;
			msg.obj = location.getLatitude() + ":" + location.getLongitude();
			handler.sendMessage(msg);
		}
	};

}
