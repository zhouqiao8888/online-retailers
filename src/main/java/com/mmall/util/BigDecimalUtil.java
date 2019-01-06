package com.mmall.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
	
	private BigDecimalUtil() {
		
	}
	
	public static BigDecimal add(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.add(b2);
	}
	
	public static BigDecimal substract(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2);
	}
	
	public static BigDecimal multiply(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.multiply(b2);
	}
	
	public static BigDecimal divide(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		//保留两位小数，并四舍五入
		return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);	
	}
}
