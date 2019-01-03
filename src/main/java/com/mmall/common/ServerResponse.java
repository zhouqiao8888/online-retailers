package com.mmall.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
/**
 * 
 * @author 周乔
 *
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)	//json序列化时忽略空对象
public class ServerResponse<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int status;
	private String msg;
	private T data;
	
	private ServerResponse(int status) {
		super();
		this.status = status;
	}

	private ServerResponse(int status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}
	
	private ServerResponse(int status, T data) {
		super();
		this.status = status;
		this.data = data;
	}

	private ServerResponse(int status, String msg, T data) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
	
	@JsonIgnore		//json序列化时忽略这个方法，且不能序列化static方法
	public boolean isSuccess() {
		return this.status == ResponseCode.SUCCESS.getCode();
	}

	public int getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}
	
	/**
	 * 返回成功响应：状态码，状态信息
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccess() {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), 
				ResponseCode.SUCCESS.getDesc());
	}
	
	/**
	 * 返回成功响应：状态码，提示信息
	 * @param msg
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccessMsg(String msg) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
	}
	
	/**
	 * 返回成功响应：状态码，数据
	 * @param data
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccessData(T data) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
	}
	
	/**
	 * 返回成功响应：状态码，提示信息，数据
	 * @param msg
	 * @param data
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccessMsgAndData(String msg, T data) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
	}
	
	/**
	 * 返回失败响应：状态码，状态信息
	 * @return
	 */
	public static <T> ServerResponse<T> createByError() {
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(), 
				ResponseCode.ERROR.getDesc());
	}
	
	/**
	 * 返回失败响应：状态码，提示信息
	 * @param errorMsg
	 * @return
	 */
	public static <T> ServerResponse<T> createByErrorMsg(String errorMsg) {
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMsg);
	}
	
	/**
	 * 返回失败响应：自定义：错误码，提示信息
	 * @param code
	 * @param errorMsg
	 * @return
	 */
	public static <T> ServerResponse<T> createByErrorCodeMsg(int code, String errorMsg) {
		return new ServerResponse<T>(code, errorMsg);
	}
	
}
