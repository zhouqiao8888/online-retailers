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
    
    //校验用户密保答案
    int checkSecurityAnswer(@Param("username") String username, @Param("question") String question, 
    		@Param("answer") String answer);
    
    //通过用户名查找用户
    User selectByUsername(String username);
    
    //修改用户密码
    int updatePasswordByUsername(@Param("username") String username, @Param("newPassword") String newPassword);
    
    //校验用户密码
    int checkUserPassword(@Param("password") String password, @Param("id") int id);
    
    //校验待更新的email是否已经存在
    int checkToUpdateEmail(@Param("email") String eamil, @Param("id") int id);
}