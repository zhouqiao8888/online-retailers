package com.mmall.util;


import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class DateTimeUtil {
	//joda-time
	public static final String STAND_FORMATE = "yyyy-MM-dd HH:mm:ss";
	
	//str->Date
	public static Date strToDate(String dateTimeStr, String formateStr) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formateStr);
		DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
		return dateTime.toDate();
	}
	
	//Date->str
	public static String dateToStr(Date date, String formateStr) {
		if(date == null) {
			return StringUtils.EMPTY;
		}
		
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(formateStr);
	}
	
	//str->Date
	public static Date strToDate(String dateTimeStr) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STAND_FORMATE);
		DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
		return dateTime.toDate();
	}
	
	//Date->str
	public static String dateToStr(Date date) {
		if(date == null) {
			return StringUtils.EMPTY;
		}
		
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(STAND_FORMATE);
	}
	
//	public static void main(String[] args) {
//		System.out.println(DateTimeUtil.dateToStr(new Date()));
//		System.out.println(DateTimeUtil.strToDate("2018-12-01 11:23:23"));
//	}
}
