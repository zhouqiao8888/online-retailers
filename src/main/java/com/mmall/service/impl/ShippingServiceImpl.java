package com.mmall.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
	
	@Autowired 
	private ShippingMapper shippingMapper;
	
	public ServerResponse<Map<String, Integer>> addShippingAddress(Shipping shipping) {
		if(shipping == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		int resCount = shippingMapper.insertSelective(shipping);
		if(resCount > 0) {
			Map<String, Integer> resMap = Maps.newHashMap();
			resMap.put("shippingId", shipping.getId());
			return ServerResponse.createBySuccessMsgAndData("收货地址创建成功", resMap);
		}
		return ServerResponse.createByErrorMsg("收货地址创建失败");
	}
	
	public ServerResponse<String> delShippingAddress(Integer userId, Integer shippingId) {
		if(shippingId == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		int resCount = shippingMapper.delByShippingIdAndUserId(shippingId, userId);
		if(resCount > 0) { 
			return ServerResponse.createBySuccessMsg("收货地址删除成功");
		}
		return ServerResponse.createByErrorMsg("收货地址删除失败");
	}
	
	public ServerResponse<String> updateShippingAddress(Shipping shipping) {
		if(shipping == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		int resCount = shippingMapper.updateByPrimaryKeyAndUserIdSelective(shipping);
		if(resCount > 0) { 
			return ServerResponse.createBySuccessMsg("收货地址更新成功");
		}
		return ServerResponse.createByErrorMsg("收货地址更新失败");
	}
	
	public ServerResponse<PageInfo<Shipping>> listShippingAddresses(Integer userId, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
		if(CollectionUtils.isNotEmpty(shippingList)) {
			PageInfo<Shipping> pageInfo = new PageInfo<Shipping>(shippingList);
			return ServerResponse.createBySuccessMsgAndData("获取收货地址列表成功", pageInfo);
		}
		return ServerResponse.createByErrorMsg("获取收货地址列表失败");
	}
	
	public ServerResponse<Shipping> getShippingAddress(Integer userId, Integer shippingId) {
		if(shippingId == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		Shipping shipping = shippingMapper.selectByShippingIdAndUserId(shippingId, userId);
		if(shipping != null) {
			return ServerResponse.createBySuccessMsgAndData("获取收货地址成功", shipping);
		}
		return ServerResponse.createByErrorMsg("获取收货地址失败");
	}	

}
