package com.mmall.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

public interface IShippingService {
	
	ServerResponse<Map<String, Integer>> addShippingAddress(Shipping shipping);
	
	ServerResponse<String> delShippingAddress(Integer userId, Integer shippingId);
	
	ServerResponse<String> updateShippingAddress(Shipping shipping);
	
	ServerResponse<PageInfo<Shipping>> listShippingAddresses(Integer userId, Integer pageNum, Integer pageSize);
	
	ServerResponse<Shipping> getShippingAddress(Integer userId, Integer shippingId);
}
