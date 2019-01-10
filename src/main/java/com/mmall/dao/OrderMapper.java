package com.mmall.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
    
    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);
    
    Order selectByOrderNo(Long orderNo);
    
    int deleteByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);
    
    List<Order> selectOrdersByUserId(Integer userId);
    
    List<Order> selectOrders();
    
    
}