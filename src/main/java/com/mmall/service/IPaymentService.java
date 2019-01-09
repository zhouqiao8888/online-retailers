package com.mmall.service;

import java.util.Map;

import com.mmall.common.ServerResponse;

public interface IPaymentService {
	
	ServerResponse<Map<String, String>> payOrder(Long orderNo, Integer userId, String path);

    ServerResponse<String> aliPayCallback(Map<String, String> params);
    
    ServerResponse<Boolean> queryOrderStatus(Long orderNo, Integer userId);

}
