package com.game.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期
 * @author JiangBangMing
 *
 * 2018年4月8日 下午3:13:04
 */
public class DateUtile {
	private static final SimpleDateFormat format = new SimpleDateFormat(
	    "yyyy-MM-dd HH:mm:ss");
	public static Date stringToDate(String value) {
		try {
			return format.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
