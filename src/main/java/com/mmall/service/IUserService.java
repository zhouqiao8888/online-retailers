package com.mmall.service;


import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
	
	ServerResponse<User> login(String username, String password);
	
	ServerResponse<String> register(User user);
	
	ServerResponse<String> checkValid(String str, String type);
	
	ServerResponse<String> getSecurityQuestion(String username);
	
	ServerResponse<String> checkSecurityAnswer(String username, String question, String answer);
	
	ServerResponse<String> forgetRestPassword(String username, String newPassword, String userToken);
	
	ServerResponse<String> restPassword(User user, String oldPassword, String newPassword);
	
	ServerResponse<User> updateUserInfo(User updateUser);
	
	ServerResponse<User> getUserInfoById(int id);
	
	ServerResponse<String> checkAdminRole(User user);
}
