package com.mofa.loan.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EditUtils {

	/**
	 * 过滤掉常见特殊字符,常见的表情
	 */
	public static void setEtFilter(EditText et) {
		if (et == null) {
			return;
		}
		// 表情过滤器
		InputFilter emojiFilter = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
				Pattern emoji = Pattern
						.compile(
								"[\\uD83D\\uDE00-\\uD83D\\uDE4F]",
//								"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
								Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
				Matcher emojiMatcher = emoji.matcher(source);
				if (emojiMatcher.find()) {
					return "";
				}
				return null;
			}
		};
		// 特殊字符过滤器
		InputFilter specialCharFilter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
				String regexStr = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）\"——+|{}【】‘；：”“’。，、？]";
				Pattern pattern = Pattern.compile(regexStr);
				Matcher matcher = pattern.matcher(source.toString());
				if (matcher.find()) {
					return "";
				} else {
					return null;
				}
			}
		};

		et.setFilters(new InputFilter[] { emojiFilter, specialCharFilter });
	}

	public static String stringFilter(String str) throws PatternSyntaxException {
		// 不允许数字
		String regEx = "[0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	public static String NoSpecialString(String str) throws PatternSyntaxException {
		String regEx = "[~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * 手机号码中间添加空格
	 * 
	 * @param s
	 * @return
	 */
	public static String handlePhoneNumber(String s) {
		if (s == null || s.length() == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (i != 3 && i != 8 && s.charAt(i) == ' ') {
				continue;
			} else {
				sb.append(s.charAt(i));
				if ((sb.length() == 4 || sb.length() == 9)
						&& sb.charAt(sb.length() - 1) != ' ') {
					sb.insert(sb.length() - 1, ' ');
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 校验是否是正确的手机号码格式
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {

		if (TextUtils.isEmpty(mobiles)) {
			return false;
		}

		if (mobiles.contains(" ")) {
			mobiles = mobiles.replaceAll(" ", "");
		}

		String regex = "^[0-9]{9,11}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 校验是否是正确的越南手机号码格式
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileYueNan(String mobiles) {

		if (TextUtils.isEmpty(mobiles)) {
			return false;
		}

		if (mobiles.contains(" ")) {
			mobiles = mobiles.replaceAll(" ", "");
		}

		String regex = "^(1[3|4|5|7|8][0-9])\\d{8}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

}
