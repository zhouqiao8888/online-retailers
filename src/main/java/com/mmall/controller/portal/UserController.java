package com.mmall.controller.portal;

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
@RequestMapping("/user/")
public class UserController {
	
	@Autowired
	private IUserService iUserService;
	
	/**
	 * 用户登录
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
			session.setAttribute(Const.CURRENT_USER, response.getData());
		}
		return response;
	}
	
	/**
	 * 用户退出
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/loginOut.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> loginOut(HttpSession session) {
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.createBySuccessMsg("您已退出系统");
	}
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/register.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user) {
		return iUserService.register(user);
	}
	
	/**
	 * 根据type校验用户名或者邮件
	 * @param str
	 * @param type
	 * @return
	 */
	@RequestMapping(value="/validate.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkValid(String str, String type) {
		return iUserService.checkValid(str, type);
	}
	
	/**
	 * 获取登陆用户信息
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/getUserInfo.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user != null) {
			return ServerResponse.createBySuccessData(user);
		}
		return ServerResponse.createByErrorMsg("用户未登陆，无法获取当前用户信息");
	}
	
	/**
	 * 获取当前用户的密保问题
	 * @param username
	 * @return
	 */
	@RequestMapping(value="/getSecurityQuestion.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> getSecurityQuestion(String username) {	
		return iUserService.getSecurityQuestion(username);
	}
	
	/**
	 * 校验用户密保答案
	 * @param username
	 * @param question
	 * @param answer
	 * @return
	 */
	@RequestMapping(value="/checkSecurityAnswer.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkSecurityAnswer(String username, String question, String answer) {
		return iUserService.checkSecurityAnswer(username, question, answer);
	}
	
	/**
	 * 修改密码
	 * @param username
	 * @param newPassword
	 * @param userToken
	 * @return
	 */
	@RequestMapping(value="/forgetRestPassword.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetRestPassword(String username, String newPassword, String userToken) {
		return iUserService.forgetRestPassword(username, newPassword, userToken);
	}
}
