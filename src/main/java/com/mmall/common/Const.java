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

}
