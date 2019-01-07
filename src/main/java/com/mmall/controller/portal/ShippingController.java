package com.mmall.controller.portal;

import java.util.Map;

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
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;

@Controller
@RequestMapping("/shipping")
public class ShippingController {
	
	@Autowired
	private IShippingService iShippingService;
	
	/**
	 * 创建收货地址，并返回收货地址id
	 * @param session
	 * @param shipping
	 * @return
	 */
	@RequestMapping(value="/addShippingAddress", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Map<String, Integer>> addShippingAddress(HttpSession session, Shipping shipping) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		shipping.setUserId(user.getId());
		return iShippingService.addShippingAddress(shipping);
	}
	
	/**
	 * 删除收货地址
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@RequestMapping(value="/delShippingAddress", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> delShippingAddress(HttpSession session, Integer shippingId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iShippingService.delShippingAddress(user.getId(), shippingId);
	}
	
	/**
	 * 更新收货地址
	 * @param session
	 * @param shipping
	 * @return
	 */
	@RequestMapping(value="/updateShippingAddress", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> updateShippingAddress(HttpSession session, Shipping shipping) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		shipping.setUserId(user.getId());
		return iShippingService.updateShippingAddress(shipping);
	}
	
	/**
	 * 获取收货地址列表(分页展示)
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/listShippingAddresses", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<PageInfo<Shipping>> listShippingAddresses(HttpSession session, 
			@RequestParam(value="pageNum", defaultValue="1") Integer pageNum, 
			@RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
		
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iShippingService.listShippingAddresses(user.getId(), pageNum, pageSize);
	}
	
	/**
	 * 获取指定收货地址
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@RequestMapping(value="/getShippingAddress", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Shipping> getShippingAddress(HttpSession session, Integer shippingId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iShippingService.getShippingAddress(user.getId(), shippingId);
	}
}
