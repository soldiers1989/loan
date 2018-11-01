package com.mofa.loan.utils;

import java.text.DecimalFormat;

public class Formatdou {

	public static final String formatDouble(Double str) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(str);
	}

	public static final String getDouString(Double str) {
		DecimalFormat df = new DecimalFormat("######.##");
		return df.format(str);
	}
	
	public static final String getDouStringOne(Double str) {
		DecimalFormat df = new DecimalFormat("######.#");
		return df.format(str);
	}

	public static final String formatdou(String str) {
		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(Double.parseDouble(str));
	}
	
	public static final String formatdou2(String str) {
		DecimalFormat df = new DecimalFormat("###.###");
		return df.format(Double.parseDouble(str));
	}

	public static final String formatdou3(String str) {
		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(Long.parseLong(str));
	}
}
