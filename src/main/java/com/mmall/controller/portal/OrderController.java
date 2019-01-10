package com.mmall.controller.portal;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.vo.OrderItemProductVO;
import com.mmall.vo.OrderVO;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private IOrderService iOrderService;
	
	/**
	 * 创建订单，指定收货地址
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@RequestMapping(value="/createOrder.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<OrderVO> createOrder(HttpSession session, Integer shippingId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		return iOrderService.createOrder(user.getId(), shippingId);
	}
	
	/**
	 * 取消订单
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="/cancelOrder.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> cancelOrder(HttpSession session, long orderNo) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		return iOrderService.cancelOrder(user.getId(), orderNo);
	}
	
	/**
	 * 获取购物车选中的商品信息
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/getOrderCartProducts.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<OrderItemProductVO> getOrderCartProducts(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		return iOrderService.getOrderCartProducts(user.getId());
	}
	
	/**
	 * 查询订单详情
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="/getOrderDetail.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<OrderVO> getOrderDetail(HttpSession session, Long orderNo) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		return iOrderService.getOrderDetail(user.getId(), orderNo);
	}
	
	/**
	 * 列出当前用户的所有订单
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/listOrders.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<PageInfo> listOrders(HttpSession session, 
			@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
			@RequestParam(value="pageSize", defaultValue="5") Integer pageSize) {
		
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		return iOrderService.listOrders(user.getId(), pageNum, pageSize);
	}
}
