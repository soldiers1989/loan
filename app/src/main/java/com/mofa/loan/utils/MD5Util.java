package com.mofa.loan.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class MD5Util {
//	public static String md5(String str) {
//		if (TextUtils.isEmpty(str)) {
//			return "";
//		}
//		return Encrypt.MD5(str + "G0eHIW3op8dYIWvdXDcePAn8QvfqHVSh");
//	}
//
//	public static String getMD5(String val) throws NoSuchAlgorithmException {
//		MessageDigest md5 = MessageDigest.getInstance("MD5");
//		md5.update(val.getBytes());
//		byte[] m = md5.digest();// 加密
//		return getString(m);
//	}
//
//	private static String getString(byte[] b) {
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < b.length; i++) {
//			sb.append(b[i]);
//		}
//		return sb.toString();
//	}
	
	/**
	 * 32位MD5加密方法 
	 * 16位小写加密只需getMd5Value("xxx").substring(8, 24);即可
	 * 
	 * @param sSecret
	 * @return
	 */
	public static String md5(String sSecret) {
		sSecret = sSecret.replace(" ", "");
		if (TextUtils.isEmpty(sSecret)) {
			Log.i("MOFA", "MD5Utils -- content为空");
			return null;
		}
		try {
			sSecret = sSecret + "G0eHIW3op8dYIWvdXDcePAn8QvfqHVSh";
			MessageDigest bmd5 = MessageDigest.getInstance("MD5");
			bmd5.update(sSecret.getBytes());
			int i;
			StringBuffer buf = new StringBuffer();
			byte[] b = bmd5.digest();// 加密
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String formd5(String content) {
		if (TextUtils.isEmpty(content)) {
			Log.i("MOFA", "MD5Utils -- content为空");
			return null;
		}
		content = content + "G0eHIW3op8dYIWvdXDcePAn8QvfqHVSh";
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
