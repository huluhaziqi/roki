package com.legent.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateUtils;

/**
 * TimeUtils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-8-24
 */
public class TimeUtils {

	public static final SimpleDateFormat SDF_DEFAULT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat SDF_DATE = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.getDefault());
	public static final SimpleDateFormat SDF_TIME = new SimpleDateFormat(
			"HH:mm:ss", Locale.getDefault());

	private TimeUtils() {
		throw new AssertionError();
	}

	/**
	 * long time to string
	 * 
	 * @param timeInMillis
	 * @param dateFormat
	 * @return
	 */
	public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
		return dateFormat.format(new Date(timeInMillis));
	}

	/**
	 * long time to string, format is {@link #SDF_DEFAULT}
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static String getTime(long timeInMillis) {
		return getTime(timeInMillis, SDF_DEFAULT);
	}

	/**
	 * get current time in milliseconds
	 * 
	 * @return
	 */
	public static long getCurrentTimeInLong() {
		return System.currentTimeMillis();
	}

	/**
	 * get current time in milliseconds, format is {@link #SDF_DEFAULT}
	 * 
	 * @return
	 */
	public static String getCurrentTimeInString() {
		return getTime(getCurrentTimeInLong());
	}

	/**
	 * get current time in milliseconds
	 * 
	 * @return
	 */
	public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
		return getTime(getCurrentTimeInLong(), dateFormat);
	}

	/**
	 * 判断是不是今天日期
	 * 
	 */
	public static boolean isToday(long timeInMillis) {
		return DateUtils.isToday(timeInMillis);
	}

	/**
	 * 判断是不是昨天日期
	 */
	public static boolean isYestoday(long timeInMillis) {

		long mills = timeInMillis + 1000 * 60 * 60 * 24;
		return DateUtils.isToday(mills);

//		Calendar today = Calendar.getInstance(); // 今天
//		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
//		today.set(Calendar.HOUR_OF_DAY, 0);
//		today.set(Calendar.MINUTE, 0);
//		today.set(Calendar.SECOND, 0);
//
//		Calendar yesterday = (Calendar) today.clone();// 昨天
//		yesterday.set(Calendar.DAY_OF_MONTH,
//				today.get(Calendar.DAY_OF_MONTH) - 1);
//
//		Calendar current = Calendar.getInstance();
//		current.setTimeInMillis(timeInMillis);
//
//		return current.before(today) && current.after(yesterday);
	}

	/**
	 * 判断是不是前天日期
	 * 
	 */
	public static boolean isBeforeYestoday(long timeInMillis) {
		
		long mills = timeInMillis + 1000 * 60 * 60 * 24 * 2;
		return DateUtils.isToday(mills);

//		Calendar yesterday = Calendar.getInstance(); // 昨天
//		yesterday.set(Calendar.DAY_OF_MONTH,
//				yesterday.get(Calendar.DAY_OF_MONTH) - 1);
//		yesterday.set(Calendar.HOUR_OF_DAY, 0);
//		yesterday.set(Calendar.MINUTE, 0);
//		yesterday.set(Calendar.SECOND, 0);
//
//		Calendar beforeday = (Calendar) yesterday.clone(); // 前天
//		beforeday.set(Calendar.DAY_OF_MONTH,
//				yesterday.get(Calendar.DAY_OF_MONTH) - 1);
//
//		Calendar current = Calendar.getInstance();
//		current.setTimeInMillis(timeInMillis);
//
//		return current.before(yesterday) && current.after(beforeday);
	}

	static public Calendar getZeroTime(Calendar c) {

		Calendar res = (Calendar) c.clone();
		res.set(Calendar.HOUR_OF_DAY, 0);
		res.set(Calendar.MINUTE, 0);
		res.set(Calendar.SECOND, 0);
		return res;
	}

	public static String sec2clock(long sec) {
		if (sec <=0) {
			return "00:00";
		}
		StringBuilder sb = new StringBuilder();
		long min = sec / 60;
		long second = sec % 60;
		if (min < 10) {
			sb.append("0");
		}
		sb.append(min);
		sb.append(":");
		if (second < 10) {
			sb.append("0");
		}
		sb.append(second);
		return sb.toString();
	}
}
