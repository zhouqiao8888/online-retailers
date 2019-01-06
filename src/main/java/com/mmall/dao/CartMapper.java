package com.mmall.dao;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.mmall.pojo.Cart;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
    
    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
    
    List<Cart> selectCartsByUserId(Integer userId);
    
    int checkAllCartsIsSelected(Integer userId);
    
    int deleteByUserIdAndProductIds(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);
    
    int updateAllProductsChecked(Integer userId);
    
    int checkedOrUncheckedProducts(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);
    
    int selectCartProductsQuantity(Integer userId);
    
}