package com.mmall.dao;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    //检查当前用户名是否存在
    int checkUsername(String username);	
    
    //检查当前eamil是否存在
    int checkUserEmail(String email);	
    
    //返回当前用户信息
    User selectLoginUser(@Param("username") String username, @Param("password") String password);
    
    //返回用户密保问题
    String selectSecurityQuestion(String username);
}