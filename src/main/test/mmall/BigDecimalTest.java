package mmall;

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalTest {

	@Test
	public void test() {
		System.out.println(0.5 + 0.1);
		System.out.println(12.03 / 100);
	}
	
	@Test
	public void test2() {
		BigDecimal a = new BigDecimal(0.5);
		BigDecimal b = new BigDecimal(0.1);
		System.out.println(a.add(b));
	}
	
	@Test
	public void test3() {
		BigDecimal a = new BigDecimal("12.03");
		BigDecimal b = new BigDecimal("100");
		System.out.println(a.divide(b));
	}
}
