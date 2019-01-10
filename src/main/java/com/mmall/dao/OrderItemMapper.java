package com.mmall.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.OrderItem;

public interface OrderItemMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
    
    List<OrderItem> selectOrderItemsByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);
    
    int batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);
    
    BigDecimal sumOrderItemPrice(@Param("userId") Integer userId, @Param("orderNo") long orderNo);
    
    int deleteByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") long orderNo);
    
    List<OrderItem> selectOrderItemsByOrderNo(Long orderNo);
    
    
}