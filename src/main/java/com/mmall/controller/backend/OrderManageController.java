package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVO;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IOrderService iOrderService;
	
	/**
	 * 列出所有订单
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/listUserOrders.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<PageInfo> listUserOrders(HttpSession session,
			@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
			@RequestParam(value="pageSize", defaultValue="5") Integer pageSize) {
		
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，非法登陆");
		}	
		
		return iOrderService.listUserOrders(pageNum, pageSize);
	}
	
	/**
	 * 获取订单详情
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="/getOrderDetailByOrderNo.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<OrderVO> getOrderDetailByOrderNo(HttpSession session, Long orderNo) {
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，非法登陆");
		}	
		
		return iOrderService.getOrderDetailByOrderNo(orderNo);
	}
	
	/**
	 * 根据orderNo进行匹配，以后扩展模糊匹配
	 * @param session
	 * @param orderNo
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/searchOrders.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<PageInfo> searchOrders(HttpSession session, Long orderNo,
			@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
			@RequestParam(value="pageSize", defaultValue="5") Integer pageSize) {
		
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，非法登陆");
		}	
		
		return iOrderService.searchOrders(pageNum, pageSize, orderNo);
	}
	
	/**
	 * 发货商品
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@RequestMapping(value="/sendGoods.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> sendGoods(HttpSession session, Long orderNo) {
		
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，非法登陆");
		}	
		
		return iOrderService.sendGoods(orderNo);
	}
}
