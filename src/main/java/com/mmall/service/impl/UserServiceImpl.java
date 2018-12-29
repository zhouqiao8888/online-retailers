package com.mmall.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserMapper userMapper;
	
	@Override
	public ServerResponse<User> login(String username, String password) {
		if(userMapper.checkUsername(username) == 0) {
			return ServerResponse.createByErrorMsg("用户名不存在");
		}
		
		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectLoginUser(username, md5Password);
		if(user == null) {
			return ServerResponse.createByErrorMsg("密码错误");
		}
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccessMsgAndData("登陆成功", user);
	}

	@Override
	public ServerResponse<String> register(User user) {
		ServerResponse<String> response = this.checkValid(user.getUsername(), Const.USERNAME);
		if(!response.isSuccess()) {
			return response;
		}
		
		response = this.checkValid(user.getEmail(), Const.EMAIL);
		if(!response.isSuccess()) {
			return response;
		}
		
		user.setRole(Const.Role.ROLE_CUSTOMER);
		//MD5加密
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		
		int resultCount = userMapper.insert(user);
		if(resultCount == 0) {
			return ServerResponse.createByErrorMsg("注册失败");
		}
		return ServerResponse.createBySuccessMsg("注册成功");
	}

	@Override
	public ServerResponse<String> checkValid(String str, String type) {
		boolean flag = false;
		
		if(StringUtils.isBlank(type))
			return ServerResponse.createByErrorMsg("类型不能为空");
		
		if(!Const.USERNAME.equals(type) && !Const.EMAIL.equals(type))
			return ServerResponse.createByErrorMsg("类型不存在");
		
		if(Const.USERNAME.equals(type)) {
			int res = userMapper.checkUsername(str);
			if(res > 0) {
				return ServerResponse.createByErrorMsg("用户名已存在");
			}
			flag = true;
		}
			
		if(Const.EMAIL.equals(type)) {
			int res = userMapper.checkUserEmail(str);
			if(res > 0) {
				return ServerResponse.createByErrorMsg("email已存在");
			}	
			flag = true;
		}		
		
		if(!flag) 
			return ServerResponse.createByErrorMsg("校验失败");
		
		return ServerResponse.createBySuccessMsg("校验成功");			
	}

	@Override
	public ServerResponse<String> getSecurityQuestion(String username) {
		ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
		if(!response.isSuccess()) {
			return response;
		}
		
		String question = userMapper.selectSecurityQuestion(username);
		if(StringUtils.isNotBlank(question)) {
			return ServerResponse.createBySuccessData(question);
		}
		return ServerResponse.createByErrorMsg("用户密保问题不存在");
	}

}
