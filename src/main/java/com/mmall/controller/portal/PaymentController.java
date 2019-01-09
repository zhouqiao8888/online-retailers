package com.mmall.controller.portal;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IPaymentService;

@Controller
@RequestMapping("/payment")
public class PaymentController {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
	private IPaymentService iPaymentService;

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
		return iPaymentService.payOrder(orderNo, user.getId(), path);
	}
	
	/**
	 * 支付宝回调接口:支付宝收到支付信息后主动回调，不用手动测试
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/alipay_callback.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> alipayCallback(HttpServletRequest request) {
		Map<String, String> params = Maps.newHashMap();

		Map<String, String[]> parameterMap = request.getParameterMap();
		Iterator<String> paramKeyIterator = parameterMap.keySet().iterator();
				
		//重组value，若有多个值，以逗号分隔
		while(paramKeyIterator.hasNext()) {
			String key = paramKeyIterator.next();
			String[] valueArr = parameterMap.get(key);
			String value = "";
			
			for(int i = 0;i < valueArr.length - 1;i ++) {
				value += valueArr[i] + ",";
			}
			value += valueArr[valueArr.length - 1];
			
			params.put(key, value);
		}
		
		logger.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());
		
		//回调RSA验签
		params.remove("sign_type");
		try {
			boolean validateFlag = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
			if(!validateFlag) {
				logger.info("回调验证失败");
				return ServerResponse.createByErrorMsg("回调验证失败");
			}
		} catch (AlipayApiException e) {
			logger.error("回调验证失败", e.getMessage());
			return ServerResponse.createByErrorMsg("回调验证失败");
		}
		
		//todo 验证其他参数：out_trade_no, trade_no, trade_status			
		return iPaymentService.aliPayCallback(params);
	}
	
	/**
	 * 查询订单状态
	 * @param session
	 * @param orderNo
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/queryOrderStatus.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Boolean> queryOrderStatus(HttpSession session, Long orderNo, HttpServletRequest request) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		return iPaymentService.queryOrderStatus(orderNo, user.getId());
	}
	
}
