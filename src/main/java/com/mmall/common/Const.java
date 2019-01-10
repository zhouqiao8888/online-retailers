package com.mmall.common;

public class Const {
	
	public static final String CURRENT_USER = "currentUser";	
	public static final String EMAIL = "email";
	public static final String USERNAME = "username";
	
	
	public interface Role {
		int ROLE_CUSTOMER = 0;	//普通用户
		int ROLE_ADMIN = 1;	//管理员
	}
	
	public interface Cart {
		int CHECKED = 1;
		int UN_CHECKED = 0;
		
		String LIMIT_COUNT_SUCCESS = "limit count success";
		String LIMIT_COUNT_FAIL = "limit count fail";
	}
	
	public enum ProductStatusEnum {
		
		ONSAIL(1, "在售"),
		UNDERCARRIAGE(2, "下架"),
		DELETE(3, "删除");
		
		private final int code;
		private final String desc;
		
		private ProductStatusEnum(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}		
		
		public static String getStatusDesc(int code) {
			for(ProductStatusEnum productStatusEnum : ProductStatusEnum.values()) {
				if(productStatusEnum.getCode() == code) {
					return productStatusEnum.getDesc();
				}
			}
			
			throw new RuntimeException("没有找到该状态码"); 
		}
	}
	
	
	public enum OrderStatusEnum {
		
		CANCELL(0, "已取消"),
		NO_PAY(10, "未支付"),
		PAY(20, "已支付"),
		SHIPPED(30, "已发货"),
		ORDER_SUCCESS(40, "订单完成"),
		ORDER_CLOSE(50, "订单关闭");
		
		
		private final int code;
		private final String desc;
		
		private OrderStatusEnum(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
		
		public static String getStatusDesc(int code) {
			for(OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
				if(orderStatusEnum.getCode() == code) {
					return orderStatusEnum.getDesc();
				}
			}
			throw new RuntimeException("没有找到该状态码"); 
		}
	}
	
	public interface AlipayCallbackStatus {
		String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
		String TRADE_SUCCESS = "TRADE_SUCCESS";
		String TRADE_FINISHED = "TRADE_FINISHED";
		String TRADE_CLOSED = "TRADE_CLOSED";
	}
	
	public enum PayPlatformEnum {
		
		ALIPAY(1, "支付宝");
		
		
		private final int code;
		private final String desc;
		
		private PayPlatformEnum(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}
	
	public enum PaymentTypeEnum {
		
		ONLINE_PAY(1, "在线支付");
		
		private final int code;
		private final String desc;
		
		private PaymentTypeEnum(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
		
		public static String getStatusDesc(int code) {
			for(PaymentTypeEnum paymentTypeEnum : PaymentTypeEnum.values()) {
				if(paymentTypeEnum.getCode() == code) {
					return paymentTypeEnum.getDesc();
				}
			}
			throw new RuntimeException("没有找到该状态码"); 
		}
	}

}
