package com.mofa.loan.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

public class PermissionUtils {
	
	private Context mContext;
	public static final int READ_CONTACT_PERMISSION = 3201;
	public static final int LOCATION_PERMISSION = 3202;
	public static final int STRONGE_PERMISSION = 3203;
	public static final int MEDIA_PERMISSION = 3204;
	public static final int CAMERA_PERMISSION = 3205;
	public static final int MIKE_PERMISSION = 3206;
	public static final int PHONE_PERMISSION = 3207;
	public static final int PERMISSION = 3208;
	public static final int LOCATION_STROGE_PERMISSION = 3209;
	
	public PermissionUtils (Context context) {
		mContext = context;
	}
	

	public static Uri getUriForFile(Context context, File file) {
		if (context == null || file == null) {
			throw new NullPointerException();
		}
		Uri uri;
		if (Build.VERSION.SDK_INT >= 23) {
			uri = FileProvider.getUriForFile(context.getApplicationContext(),
					"com.mofa.loan.fileprovider", file);
		} else {
			uri = Uri.fromFile(file);
		}
		return uri;
	}

}
