package com.mmall.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
	
	private static final Logger logger = LoggerFactory.getLogger("CartServiceImpl.class");
	
	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private ProductMapper productMapper;

	public ServerResponse<CartVO> addProductToCart(Integer userId, Integer productId, Integer quantity) {
		if(productId == null || quantity == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		Cart cartTemp = cartMapper.selectCartByUserIdAndProductId(userId, productId);
		if(cartTemp == null) {	
			Cart cart = new Cart();
			cart.setUserId(userId);
			cart.setProductId(productId);
			cart.setQuantity(quantity);
			cart.setChecked(Const.Cart.CHECKED);
			
			int resCount = cartMapper.insertSelective(cart);
			if(resCount > 0) {
				CartVO cartVO = this.createCartVO(userId);
				return ServerResponse.createBySuccessMsgAndData("商品添加成功", cartVO);
			}
			return ServerResponse.createByErrorMsg("商品添加失败");
		}
		else { //能找到就更新数量
			quantity = quantity + cartTemp.getQuantity();
			cartTemp.setQuantity(quantity);
			int resCount = cartMapper.updateByPrimaryKeySelective(cartTemp);
			if(resCount > 0) {
				CartVO cartVO = this.createCartVO(userId);
				return ServerResponse.createBySuccessMsgAndData("商品数量更新成功", cartVO);
			}
			return ServerResponse.createByErrorMsg("商品数量更新失败");		
		}		
	}
	
	public ServerResponse<CartVO> updateCartProduct(Integer userId, Integer productId, Integer quantity) {
		if(productId == null || quantity == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
		if(cart != null) {
			cart.setQuantity(quantity);
			int resCount = cartMapper.updateByPrimaryKeySelective(cart);
			if(resCount > 0) {
				CartVO cartVO = this.createCartVO(userId);
				return ServerResponse.createBySuccessMsgAndData("商品数量更新成功", cartVO);
			}
			return ServerResponse.createByErrorMsg("商品数量更新失败");		
		}
		return ServerResponse.createByErrorMsg("用户的购物车不存在");
	}
	
	public ServerResponse<CartVO> deleteCartProducts(Integer userId, String productIds) {
		if(StringUtils.isBlank(productIds)) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		//productIds格式处理
//		String[] productIdArr = productIds.split(",");
//		List<String> productIdList = Lists.newArrayList(productIdArr);	
		List<String> productIdList = Splitter.on(",").splitToList(productIds);
		
		int resCount = cartMapper.deleteByUserIdAndProductIds(userId, productIdList);
		if(resCount > 0) {
			CartVO cartVO = this.createCartVO(userId);
			return ServerResponse.createBySuccessMsgAndData("商品删除成功", cartVO);
		}
		return ServerResponse.createByErrorMsg("商品删除失败");			
	}
	
	public ServerResponse<CartVO> listCartProducts(Integer userId) {
		CartVO cartVO = this.createCartVO(userId);
		return ServerResponse.createBySuccessMsgAndData("购物车商品查找成功", cartVO);
	}
	
	public ServerResponse<CartVO> selectOrUnselectProducts(Integer userId, Integer productId, Integer checked) {		
		int resCount = cartMapper.checkedOrUncheckedProducts(userId, productId, checked);
		if(resCount > 0) {
			CartVO cartVO = this.createCartVO(userId);
			return ServerResponse.createBySuccessMsgAndData("设置成功", cartVO);
		}
		return ServerResponse.createByErrorMsg("设置失败");
	}
	
	public ServerResponse<Integer> getCartProductsQuantity(Integer userId) {
		int totalQuantity = cartMapper.selectCartProductsQuantity(userId);
		return ServerResponse.createBySuccessData(totalQuantity);
	}
	
	private CartVO createCartVO(Integer userId) {
		CartVO cartVO = new CartVO();
		List<CartProductVO> cartProductVOList = Lists.newArrayList();
		BigDecimal cartTotalPrice = new BigDecimal("0");
		Boolean allChecked = true;
		
		//查找当前用户购物车的所有商品
		List<Cart> cartList = cartMapper.selectCartsByUserId(userId);
		for(Cart cartItem : cartList) {
			CartProductVO cartProductVO = new CartProductVO();
			cartProductVO.setId(cartItem.getId());
			cartProductVO.setUserId(cartItem.getUserId());
			cartProductVO.setProductId(cartItem.getProductId());
			cartProductVO.setChecked(cartItem.getChecked());
			
			Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
			if(product != null) {
				cartProductVO.setProductName(product.getName());
				cartProductVO.setProductSubtitle(product.getSubtitle());
				cartProductVO.setProductMainImage(product.getMainImage());
				cartProductVO.setProductPrice(product.getPrice());
				cartProductVO.setProductStatus(product.getStatus());
				cartProductVO.setProductStock(product.getStock());
				
				//判断商品库存是否充足
				Integer actualStock = null;
				//产品库存不足，取最大库存，同时更新商品购物车
				if(product.getStock() < cartItem.getQuantity()) {
					actualStock = product.getStock();
					cartProductVO.setLimitQuantity(Const.Cart.LIMIT_COUNT_FAIL);
					Cart cartItem_temp = new Cart();
					cartItem_temp.setQuantity(actualStock);
					cartItem_temp.setId(cartItem.getId());
					int resCount = cartMapper.updateByPrimaryKeySelective(cartItem_temp);
					if(resCount == 0) {
						logger.error("购物车更新失败");
					}
				}
				else {
					cartProductVO.setLimitQuantity(Const.Cart.LIMIT_COUNT_SUCCESS);
					actualStock = cartItem.getQuantity();
				}
				cartProductVO.setQuantity(actualStock);
				
				//计算商品总价
				BigDecimal productTotalPrice = BigDecimalUtil.multiply(product.getPrice().doubleValue(), actualStock);
				cartProductVO.setProductTotalPrice(productTotalPrice);
				cartProductVOList.add(cartProductVO);
				
				//若商品被选中，添加到购物车总价中
				if(cartItem.getChecked() == Const.Cart.CHECKED) {
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), productTotalPrice.doubleValue());		
				}	
			}
		}
		
		//判断购物车中的商品是否全部被选中
		allChecked = this.checkAllCartsIsSelected(userId);
		cartVO.setAllChecked(allChecked);
		cartVO.setCartProductVOList(cartProductVOList);
		cartVO.setCartTotalPrice(cartTotalPrice);
		cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		return cartVO;
	}
	
	private boolean checkAllCartsIsSelected(Integer userId) {
		if(userId == null) {
			return false;
		}
		
		return cartMapper.checkAllCartsIsSelected(userId) == 0;
	}

	
}
