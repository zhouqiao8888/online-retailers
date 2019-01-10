package com.mmall.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.Product;
import com.mmall.pojo.Shipping;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemProductVO;
import com.mmall.vo.OrderItemVO;
import com.mmall.vo.OrderVO;
import com.mmall.vo.ShippingVO;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
	

	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private ShippingMapper shippingMapper;
	
	public ServerResponse<OrderVO> createOrder(Integer userId, Integer shippingId) {
		//校验一下shippingId是不是当前用户的
		Shipping shipping = shippingMapper.selectByShippingIdAndUserId(shippingId, userId);
		if(shipping == null) {
			return ServerResponse.createByErrorMsg("不是当前用户的收货地址，请重新输入");
		}
				
		//获取选中的购物车
		List<Cart> cartList = cartMapper.selectCheckedCartsByUserId(userId);
		
		//获取orderItemList
		ServerResponse<List<OrderItem>> response = this.getOrderItemList(userId, cartList);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("用户购物车为空");
		}
		
		List<OrderItem> orderItemList = (List<OrderItem>) response.getData();	
		
		//组装并插入order
		Order order = this.assembleOrder(orderItemList, userId, shippingId);
		int resCount = orderMapper.insertSelective(order);
		if(resCount == 0) {
			return ServerResponse.createByErrorMsg("订单持久化失败");
		}
		
		//批量插入orderItem		
		resCount = orderItemMapper.batchInsert(orderItemList);
		if(resCount == 0) {
			return ServerResponse.createByErrorMsg("订单持久化失败");
		}	
		
		//扣减商品库存
		ServerResponse<String> responseTemp = this.reduceProductStock(orderItemList);
		if(!responseTemp.isSuccess()) {
			return ServerResponse.createByErrorMsg("商品库存扣减失败");
		}
		
		//清空用户已经选中的购物车
		responseTemp = this.deleteCheckedCarts(cartList);
		if(!responseTemp.isSuccess()) {
			return ServerResponse.createByErrorMsg("购物车商品删除失败");
		}
		
		//返回给前端明细
		OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
		return ServerResponse.createBySuccessMsgAndData("订单创建成功", orderVO);
	}
	
	public ServerResponse<String> cancelOrder(Integer userId, long orderNo) {
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if(order == null) {
			return ServerResponse.createByErrorMsg("该用户没有此订单");
		}
		
		//判断订单是不是未付款状态
		if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
			return ServerResponse.createBySuccessMsg("此订单已支付或者已取消");
		}
		
		//商品库存回滚
		List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByUserIdAndOrderNo(userId, orderNo);
		for(OrderItem orderItem : orderItemList) {
			Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
			product.setStock(product.getStock() + orderItem.getQuantity());
			int resCount = productMapper.updateByPrimaryKeySelective(product);
			if(resCount == 0) {
				return ServerResponse.createByErrorMsg("商品数量回滚失败");
			}
		}
		
//		//删除订单
//		int resCount = orderMapper.deleteByUserIdAndOrderNo(userId, orderNo);
//		if(resCount == 0) {
//			return ServerResponse.createByErrorMsg("订单取消失败");
//		}
//		
//		//删除具体的子订单项目
//		resCount = orderItemMapper.deleteByUserIdAndOrderNo(userId, orderNo);
//		if(resCount == 0) {
//			return ServerResponse.createByErrorMsg("订单取消失败");
//		}
		
		//不删除，只是更新订单状态
		order.setStatus(Const.OrderStatusEnum.CANCELL.getCode());	
		int resCount = orderMapper.updateByPrimaryKeySelective(order);
		if(resCount == 0) {
			return ServerResponse.createByErrorMsg("订单更新失败");
		}
		
		return ServerResponse.createBySuccessMsg("订单取消成功");
	}
	
	private ServerResponse<String> deleteCheckedCarts(List<Cart> cartList) {
		for(Cart cartItem : cartList) {
			int resCount = cartMapper.deleteByPrimaryKey(cartItem.getId());
			if(resCount == 0) {
				return ServerResponse.createByErrorMsg("购物车商品删除失败");
			}
		}
		
		return ServerResponse.createBySuccessMsg("购物车商品删除成功");
	}
	
	public ServerResponse<OrderItemProductVO> getOrderCartProducts(Integer userId) {
		//选出已选中的商品
		List<Cart> cartList = cartMapper.selectCheckedCartsByUserId(userId);
		if(CollectionUtils.isEmpty(cartList)) {
			return ServerResponse.createByErrorMsg("该用户购物车为空");
		}
				
		//获取用户的orderItemList -> orderItemProductVO
		ServerResponse<List<OrderItem>> response = this.getOrderItemList(userId, cartList);
		List<OrderItem> orderItemList = response.getData();
		OrderItemProductVO orderItemProductVO = this.assembleOrderItemProductVO(orderItemList);
		return ServerResponse.createBySuccessData(orderItemProductVO);
	}
	
	public ServerResponse<OrderVO> getOrderDetail(Integer userId, Long orderNo) {
				
		List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByUserIdAndOrderNo(userId, orderNo);
		if(CollectionUtils.isEmpty(orderItemList)) {
			return ServerResponse.createByErrorMsg("该用户订单不存在");
		}
		
		//获取orderVO
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
		return ServerResponse.createBySuccessData(orderVO);
	}
	
	public ServerResponse<PageInfo> listOrders(Integer userId, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		List<Order> orderList = orderMapper.selectOrdersByUserId(userId);
		if(CollectionUtils.isEmpty(orderList)) {
			return ServerResponse.createByErrorMsg("该用户订单不存在");
		}
		
		List<OrderVO> orderVOList = this.assembleOrderVOList(userId, orderList);		
		PageInfo pageInfo = new PageInfo(orderList);
		pageInfo.setList(orderVOList);		
		return ServerResponse.createBySuccessData(pageInfo);		
	}
	
	//backend
	public ServerResponse<PageInfo> listUserOrders(Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		List<Order> orderList = orderMapper.selectOrders();		
		if(CollectionUtils.isEmpty(orderList)) {
			return ServerResponse.createByErrorMsg("订单不存在");
		}
		
		List<OrderVO> orderVOList = this.assembleOrderVOList(null, orderList);		
		PageInfo pageInfo = new PageInfo(orderList);
		pageInfo.setList(orderVOList);		
		return ServerResponse.createBySuccessData(pageInfo);
	}	
	
	public ServerResponse<OrderVO> getOrderDetailByOrderNo(Long orderNo) {
		Order order = orderMapper.selectByOrderNo(orderNo);
		List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderNo(orderNo);
		
		if(order == null || CollectionUtils.isEmpty(orderItemList)) {
			return ServerResponse.createByErrorMsg("订单不存在");
		}
		
		OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
		return ServerResponse.createBySuccessData(orderVO);
	}
	
	public ServerResponse<PageInfo> searchOrders(Integer pageNum, Integer pageSize, Long orderNo) {
		PageHelper.startPage(pageNum, pageSize);
		
		Order order = orderMapper.selectByOrderNo(orderNo);
		List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderNo(orderNo);
		
		if(order == null || CollectionUtils.isEmpty(orderItemList)) {
			return ServerResponse.createByErrorMsg("订单不存在");
		}
		
		OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
		PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
		pageInfo.setList(Lists.newArrayList(orderVO));
		
		return ServerResponse.createBySuccessData(pageInfo);

	}
	
	public ServerResponse<String> sendGoods(Long orderNo) {
		Order order = orderMapper.selectByOrderNo(orderNo);
		
		//已支付状态才发货
		if(order != null) {
			if(Const.OrderStatusEnum.PAY.getCode() == order.getStatus()) {
				order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
				order.setSendTime(new Date());
				int resCount = orderMapper.updateByPrimaryKey(order);
				if(resCount > 0) {
					return ServerResponse.createBySuccessMsg("发货成功");
				}
			}
			return ServerResponse.createByErrorMsg("发货失败");
		}	
		return ServerResponse.createByErrorMsg("订单不存在");
	}
	
	//分割线
	private List<OrderVO> assembleOrderVOList(Integer userId, List<Order> orderList) {
		List<OrderVO> orderVOList = Lists.newArrayList();
		
		for(Order order : orderList) {
			if(userId != null) {
				List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByUserIdAndOrderNo(userId, order.getOrderNo());
				OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
				orderVOList.add(orderVO);
			}
			else {
				//管理员查看订单列表
				List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderNo(order.getOrderNo());
				OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
				orderVOList.add(orderVO);
			}
		}
		
		return orderVOList;
	}
	
	private OrderItemProductVO assembleOrderItemProductVO(List<OrderItem> orderItemList) {
		OrderItemProductVO orderItemProductVO = new OrderItemProductVO();
		List<OrderItemVO> orderItemVOList = Lists.newArrayList();
		BigDecimal productTotalPrice = new BigDecimal("0");
		
		for(OrderItem orderItem : orderItemList) {
			orderItemVOList.add(this.assembleOrderItemVO(orderItem));
			productTotalPrice = BigDecimalUtil.add(productTotalPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}
		
		orderItemProductVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		orderItemProductVO.setOrderItemVOList(orderItemVOList);
		orderItemProductVO.setProductTotalPrice(productTotalPrice);
		
		return orderItemProductVO;
		 
	}

	
	private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItemList) {
		OrderVO orderVO = new OrderVO();
		
		orderVO.setOrderNo(order.getOrderNo());
		orderVO.setPostage(order.getPostage());
		orderVO.setPayment(order.getPayment());
		orderVO.setPaymentType(order.getPaymentType());
		orderVO.setPaymentTypeDesc(Const.PaymentTypeEnum.getStatusDesc(order.getPaymentType()));
		orderVO.setStatus(order.getStatus());
		orderVO.setStatusDesc(Const.OrderStatusEnum.getStatusDesc(order.getStatus()));
		
		orderVO.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
		orderVO.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
		orderVO.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
		orderVO.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
		orderVO.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
		
		orderVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		
		//构造orderItemVOList
		List<OrderItemVO> orderItemVOList = Lists.newArrayList();
		for(OrderItem orderItem : orderItemList) {			
			orderItemVOList.add(this.assembleOrderItemVO(orderItem));
		}
		
		orderVO.setOrderItemVOList(orderItemVOList);
		
		//设置shippingVO
		orderVO.setShippingId(order.getShippingId());
		Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
		if(shipping != null) {
			orderVO.setShippingVO(this.assembleShippingVO(shipping));
		}
		
		return orderVO;
	}
	
	private OrderItemVO assembleOrderItemVO(OrderItem orderItem) {
		OrderItemVO orderItemVO = new OrderItemVO();
		
		orderItemVO.setOrderNo(orderItem.getOrderNo());
		orderItemVO.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
		orderItemVO.setProductId(orderItem.getProductId());
		orderItemVO.setProductImage(orderItem.getProductImage());
		orderItemVO.setProductName(orderItem.getProductName());
		orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
		orderItemVO.setTotalPrice(orderItem.getTotalPrice());
		orderItemVO.setQuantity(orderItem.getQuantity());
		
		return orderItemVO;
	}
	
	private ShippingVO assembleShippingVO(Shipping shipping) {
		ShippingVO shippingVO = new ShippingVO();
		
		shippingVO.setReceiverAddress(shipping.getReceiverAddress());
		shippingVO.setReceiverCity(shipping.getReceiverCity());
		shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
		shippingVO.setReceiverMobile(shipping.getReceiverMobile());
		shippingVO.setReceiverName(shipping.getReceiverName());
		shippingVO.setReceiverPhone(shipping.getReceiverPhone());
		shippingVO.setReceiverProvince(shipping.getReceiverProvince());
		shippingVO.setReceiverZip(shipping.getReceiverZip());
					
		return shippingVO;
	}
	
	
	private ServerResponse<String> reduceProductStock(List<OrderItem> orderItemList) {
		for(OrderItem orderItem : orderItemList) {
			Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
			product.setStock(product.getStock() - orderItem.getQuantity());
			int resCount = productMapper.updateByPrimaryKeySelective(product);
			if(resCount == 0) {
				return ServerResponse.createByErrorMsg("商品库存扣减失败");
			}
		}
		
		return ServerResponse.createBySuccessMsg("商品库存扣减成功");
	}
	
	private Order assembleOrder(List<OrderItem> orderItemList, Integer userId, Integer shippingId) {
		Order order = new Order();
		Long orderNo = this.getOrderNo();
		
		order.setOrderNo(orderNo);		
		for(OrderItem orderItem : orderItemList) {
			orderItem.setOrderNo(orderNo);
		}

//		BigDecimal orderTotalPrice = this.getOrderTotalPrice(orderItemList);
//		order.setPayment(orderTotalPrice);
//		logger.info("订单总价：{}", orderTotalPrice);
		
		order.setPayment(this.getOrderTotalPrice(orderItemList));
		order.setUserId(userId);
		order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());		
		order.setShippingId(shippingId);		
		order.setPostage(0);
		order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		return order;
	}
	
	
	private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
		BigDecimal orderTotalPrice = new BigDecimal("0");
		for(OrderItem orderItem : orderItemList) {
			orderTotalPrice = BigDecimalUtil.add(orderTotalPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}
		
		return orderTotalPrice; 
	}
	
	//返回没有orderNo的orderItemList
	private ServerResponse<List<OrderItem>> getOrderItemList(Integer userId, List<Cart> cartList) {
		if(CollectionUtils.isEmpty(cartList)) {
			return ServerResponse.createByErrorMsg("用户购物车为空");
		}
		
		List<OrderItem> orderItemList = Lists.newArrayList();
		for(Cart cartItem : cartList) {
			Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
			if(product != null) {
				//校验商品状态
				if(Const.ProductStatusEnum.ONSAIL.getCode() != product.getStatus()) {
					return ServerResponse.createByErrorMsg("商品不是在售状态，请尝试重新购买");
				}
				
				//校验商品库存是否充足
				if(product.getStock() < cartItem.getQuantity()) {
					return ServerResponse.createByErrorMsg("商品库存不够，请尝试重新购买");
				}
				
				OrderItem orderItem = new OrderItem();
				orderItem.setUserId(userId);
				orderItem.setProductId(product.getId());
				orderItem.setCurrentUnitPrice(product.getPrice());
				orderItem.setProductImage(product.getMainImage());
				orderItem.setProductName(product.getName());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), 
						cartItem.getQuantity().doubleValue()));
				orderItemList.add(orderItem);
			}
			else {
				return ServerResponse.createByErrorMsg("该购物车中的商品不存在，请尝试重新购买");
			}
		}
		return ServerResponse.createBySuccessData(orderItemList);
	}
	
	//简单订单号生成器
	private Long getOrderNo() {
		Long currentTime = System.currentTimeMillis();
		return currentTime + new Random().nextInt(100);
	}
}
