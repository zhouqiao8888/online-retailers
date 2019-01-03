package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {
	
	@Autowired
	private IUserService iUserService;
	
	/**
	 * 管理员登录
	 * @param username
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/login.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username, String password, HttpSession session) {
		ServerResponse<User> response =  iUserService.login(username, password);
		if(response.isSuccess()) {
			User user = response.getData();
			ServerResponse<String> res = iUserService.checkAdminRole(user);
			if(!res.isSuccess()) {
				return ServerResponse.createByErrorMsg("不是管理员，非法登陆");
			}	
			session.setAttribute(Const.CURRENT_USER, user);
		}
		return response;
	}
	
	/**
	 * 管理员注册
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/register.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user) {
		return iUserService.register(user);
	}
}
