package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVO;

public interface ICartService {

	ServerResponse<CartVO> addProductToCart(Integer userId, Integer productId, Integer quantity);
	
	ServerResponse<CartVO> updateCartProduct(Integer userId, Integer productId, Integer quantity);
	
	ServerResponse<CartVO> deleteCartProducts(Integer userId, String productIds);
	
	ServerResponse<CartVO> listCartProducts(Integer userId);
	
	ServerResponse<CartVO> selectOrUnselectProducts(Integer userId, Integer productId, Integer checked);
	
	ServerResponse<Integer> getCartProductsQuantity(Integer userId);
	
}
