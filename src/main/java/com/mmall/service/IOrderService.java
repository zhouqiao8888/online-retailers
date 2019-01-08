package com.mmall.service;

import java.util.Map;

import com.mmall.common.ServerResponse;

public interface IOrderService {
	
	ServerResponse<Map<String, String>> payOrder(Long orderNo, Integer userId, String path);

}
