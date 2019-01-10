package com.mmall.service;


import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderItemProductVO;
import com.mmall.vo.OrderVO;

public interface IOrderService {
	ServerResponse<OrderVO> createOrder(Integer userId, Integer shippingId);
	
	ServerResponse<String> cancelOrder(Integer userId, long orderNo);
	
	ServerResponse<OrderItemProductVO> getOrderCartProducts(Integer userId);
	
	ServerResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo);
	
	ServerResponse<PageInfo> listOrders(Integer userId, Integer pageNum, Integer pageSize);
	
	ServerResponse<PageInfo> listUserOrders(Integer pageNum, Integer pageSize);
	
    ServerResponse<OrderVO> getOrderDetailByOrderNo(Long orderNo);
    
    ServerResponse<PageInfo> searchOrders(Integer pageNum, Integer pageSize, Long orderNo);
    
    ServerResponse<String> sendGoods(Long orderNo);

}
