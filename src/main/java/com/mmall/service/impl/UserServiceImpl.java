package com.mmall.service.impl;

import java.util.UUID;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
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
		user.setPassword(StringUtils.EMPTY);	//返回值不显示密码
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
		
		if(StringUtils.isBlank(type) || StringUtils.isBlank(str))
			return ServerResponse.createByErrorMsg("参数不能为空");
		
		if(Const.USERNAME.equals(type)) {
			int res = userMapper.checkUsername(str);
			if(res > 0) {
				return ServerResponse.createByErrorMsg("用户名已存在");
			}
			return ServerResponse.createBySuccessMsg("校验成功");			

		}
			
		if(Const.EMAIL.equals(type)) {
			int res = userMapper.checkUserEmail(str);
			if(res > 0) {
				return ServerResponse.createByErrorMsg("email已存在");
			}	
			return ServerResponse.createBySuccessMsg("校验成功");			

		}				
		return ServerResponse.createByErrorMsg("校验失败");
		
	}

	@Override
	public ServerResponse<String> getSecurityQuestion(String username) {
		ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
		if(response.isSuccess()) {
			return ServerResponse.createByErrorMsg("用户名不存在");	//校验成功说明用户名不存在
		}
		
		String question = userMapper.selectSecurityQuestion(username);
		if(StringUtils.isNotBlank(question)) {
			return ServerResponse.createBySuccessData(question);
		}
		return ServerResponse.createByErrorMsg("用户密保问题不存在");
	}

	@Override
	public ServerResponse<String> checkSecurityAnswer(String username, String question, String answer) {
		ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
		if(response.isSuccess()) {
			return ServerResponse.createByErrorMsg("用户名不存在");	//校验成功说明用户名不存在
		}		
		
		String res = userMapper.selectSecurityAnswer(username, question);
		if(res.equals(answer)) {
			//将用户名放入缓存中
			String uuid = UUID.randomUUID().toString();
			TokenCache.setKey(TokenCache.TOKENCACHE + username, uuid);
			
			return ServerResponse.createBySuccessMsg("答案校验成功");
		}
		
		return ServerResponse.createByErrorMsg("答案校验失败");
	}

	@Override
	public ServerResponse<String> forgetRestPassword(String username, String newPassword, String userToken) {
		if(StringUtils.isBlank(userToken)) {
			return ServerResponse.createByErrorMsg("参数错误，token需要重新传递");
		}
		
		ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
		if(response.isSuccess()) {
			return ServerResponse.createByErrorMsg("用户名不存在");	//校验成功说明用户名不存在
		}	
		
		String token = TokenCache.TOKENCACHE + username;
		if(StringUtils.equals(token, userToken)) {
			String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
			int resCount = userMapper.updatePasswordByUsername(username, md5Password);
			if(resCount > 0) {
				return ServerResponse.createBySuccessMsg("密码修改成功");
			}
		}
		return ServerResponse.createByErrorMsg("密码修改失败");
	}

	@Override
	public ServerResponse<String> restPassword(User user, String oldPassword, String newPassword) {
		//防止横向越权
		String md5Password_old = MD5Util.MD5EncodeUtf8(oldPassword);
		int resCount = userMapper.checkUserPassword(md5Password_old, user.getId());
		if(resCount == 0)
			return ServerResponse.createByErrorMsg("用户密码错误");
		
		if(oldPassword.equals(newPassword)) 
			return ServerResponse.createByErrorMsg("新密码不能与旧密码一致");
		
		String md5Password_new = MD5Util.MD5EncodeUtf8(newPassword);
		resCount = userMapper.updatePasswordByUsername(user.getUsername(), md5Password_new);
		if(resCount > 0) {
			return ServerResponse.createBySuccessMsg("新密码设置成功");
		}
		return ServerResponse.createByErrorMsg("新密码设置失败");
 
	}

	@Override
	public ServerResponse<User> updateUserInfo(User updateUser) {
		//用户名不能被更新
		//校验email：如果email已经存在，且不是当前用户的email
		int resCount = userMapper.checkToUpdateEmail(updateUser.getEmail(), updateUser.getId());
		if(resCount > 0) {
			return ServerResponse.createByErrorMsg("email已存在，请更换email再尝试更新");
		}	
		
		resCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if(resCount > 0) {
			User retUser = userMapper.selectByPrimaryKey(updateUser.getId());
			retUser.setPassword("");
			return ServerResponse.createBySuccessMsgAndData("用户信息更新成功", retUser);
		}
		return ServerResponse.createByErrorMsg("用户信息更新失败");
	}

}
