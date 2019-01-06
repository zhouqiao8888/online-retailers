package com.mmall.controller.portal;

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
import com.mmall.service.ICartService;
import com.mmall.vo.CartVO;

@Controller
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private ICartService iCartService;

	/**
	 * 往购物车中添加商品
	 * @param session
	 * @param productId
	 * @param quantity
	 * @return
	 */
	@RequestMapping(value="/addProductToCart.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> addProductToCart(HttpSession session, Integer productId, Integer quantity) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.addProductToCart(user.getId(), productId, quantity);
	}
	
	/**
	 * 更新购物车中商品
	 * @param session
	 * @param productId
	 * @param quantity
	 * @return
	 */
	@RequestMapping(value="/updateCartProduct.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> updateCartProduct(HttpSession session, Integer productId, Integer quantity) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.updateCartProduct(user.getId(), productId, quantity);
	}
	
	/**
	 * 删除购物车中多个商品，商品id以逗号分隔
	 * @param session
	 * @param productIds
	 * @return
	 */
	@RequestMapping(value="/deleteCartProducts.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> deleteCartProducts(HttpSession session, String productIds) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.deleteCartProducts(user.getId(), productIds);
	}
	
	/**
	 * 查找购物车中的商品(列表形式返回)
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/listCartProducts.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> listCartProducts(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.listCartProducts(user.getId());
	}
	
	/**
	 * 全选购物车中的商品
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/selectAllProducts.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> selectAllProducts(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.selectOrUnselectProducts(user.getId(), null, Const.Cart.CHECKED);
	}
	
	/**
	 * 全反选购物车中的商品
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/unSelectAllProducts.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> unSelectAllProducts(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.selectOrUnselectProducts(user.getId(), null, Const.Cart.UN_CHECKED);
	}
	
	/**
	 * 单选购物车中的商品
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/selectSingleProduct.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> selectSingleProduct(HttpSession session, Integer productId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.selectOrUnselectProducts(user.getId(), productId, Const.Cart.CHECKED);
	}
	
	/**
	 * 单反选购物车中的商品
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/unSelectSingleProduct.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<CartVO> unSelectSingleProduct(HttpSession session, Integer productId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.selectOrUnselectProducts(user.getId(), productId, Const.Cart.UN_CHECKED);
	}
	
	/**
	 * 获取购物车中产品数量
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/getCartProductsQuantity.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Integer> getCartProductsQuantity(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), 
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.getCartProductsQuantity(user.getId());
	}

}
