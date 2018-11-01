package com.mofa.loan.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

	public static void paseMill(String dateTime) {
		/**
		 * 将字符串数据转化为毫秒数
		 */
//		String dateTime="20121025112950";
		   Calendar c = Calendar.getInstance();
		  
		try {
			c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(dateTime));
		} catch (ParseException e) {
			Log.e("MOFA", e.getMessage());
		}
		Log.i("MOFA", "时间转化后的毫秒数为："+c.getTimeInMillis());
	}
	
	public static String parseDate(Long time) {
		/**
		* 将毫秒数转化为时间
		*/
		  DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTimeInMillis(time);
		
		return formatter.format(calendar.getTime());
	}
	
	public static String parseDate2(Long time) {
		/**
		* 将毫秒数转化为时间
		*/
		  DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTimeInMillis(time);
		
		return formatter.format(calendar.getTime());
	}
	public static String parseDate4(Long time) {
		/**
		 * 将毫秒数转化为时间
		 */
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return formatter.format(calendar.getTime());
	}
	
	public static String parseDate5(Long time) {

		/**
		* 将毫秒数转化为时间
		*/
		  DateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTimeInMillis(time);
		
		return formatter.format(calendar.getTime());
	}
	
	public static String parseDate3(Long time) {

		/**
		 * 将毫秒数转化为时间
		 */
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return formatter.format(calendar.getTime());
	}
	
	
	public static String parseDate6(Long time) {

		/**
		 * 将毫秒数转化为时间
		 */
		DateFormat formatter = new SimpleDateFormat("dd-MM  HH:mm");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return formatter.format(calendar.getTime());
	}
	
	
	public static String parseDate7(Long time) {

		/**
		 * 将毫秒数转化为时间
		 */
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return formatter.format(calendar.getTime());
	}
	
	/**
	 * ��ȡϵͳʱ��(ȥ������)
	 * 
	 * @return
	 */
	public static String getDate() {
		Date date = new Date();
		Long time = date.getTime();
		Date d = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String r=sdf.format(d);
		return r.substring(0, r.lastIndexOf(":"));
	}
	/**
	 * ��ȡ������
	 * @return
	 */
	public static String getYMD(){
		String data=getDate();
		return data.split(" ")[0];
	}
	/**
	 * ��ȡϵͳʱ��
	 * 
	 * @return
	 */
	public static String getDate2() {
		Date date = new Date();
		Long time = date.getTime();
		Date d = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}
	/**
	 * ��ȡϵͳʱ��� ���
	 * 
	 * @return
	 */
	public static String getYear() {
		String time = getDate2();
		String[] str = time.split("-");
		return str[0];
	}

	/**
	 * ��ȡϵͳʱ��� �·�
	 * 
	 * @return
	 */
	public static String getMonth() {
		String time = getDate2();
		String[] str = time.split("-");
		return str[1];
	}

	/**
	 * ��ȡϵͳʱ��� ����
	 * 
	 * @return
	 */
	public static String getDay() {
		String time = getDate2();
		String[] str = time.split("-");

		return str[2].substring(0, 2);
	}

	/**
	 * ��ȡϵͳʱ��� Сʱ
	 * 
	 * @return
	 */
	public static String getHour() {

		String time = getDate2();
		String[] str = time.split(" ");

		return str[1].substring(0, 2);
	}

	/**
	 * ��ȡϵͳʱ��� ����
	 * 
	 * @return
	 */
	public static String getMinute() {

		String time = getDate2();
		String[] str = time.split(" ");

		return str[1].substring(3, 5);
	}

	/**
	 * ��ȡ��������ʱ��
	 * 
	 * @return
	 */
	public static String getMaxSendTime(String date, String hourAndMinute) {
		String result=null;
		// ��date����һ��Ĭ��ʱ�䣬��ֹ���ת���쳣
		if (date.length() != 10) {
			date = "2015-07-01";
		}
		String time = date + " " + hourAndMinute+":00";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date dt2 = sdf.parse(time);
			long haomiao = dt2.getTime();
			haomiao = haomiao + 30 * 60 * 1000;
			Date d = new Date(haomiao);
			time = sdf.format(d);
			String[] array = time.split(" ");
			result= array[1].substring(0, 5);

		} catch (ParseException e) {
			Log.e("MOFA", e.getMessage());
		}
		
		return result;
	}
	

}
