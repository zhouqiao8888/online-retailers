package com.mmall.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private IOrderService iOrderService;
	
	/**
	 * 支付订单
	 * @param session
	 * @param orderNo
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/payOrder.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Map<String, String>> payOrder(HttpSession session, Long orderNo, HttpServletRequest request) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		String path = request.getSession().getServletContext().getRealPath("upload");
		return iOrderService.payOrder(orderNo, user.getId(), path);
	}
	
}
