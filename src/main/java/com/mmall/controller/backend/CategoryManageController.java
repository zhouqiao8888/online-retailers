package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private ICategoryService iCategoryService;

	@RequestMapping(value="addCategory.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> addCategory(HttpSession session, String categoryName,
			@RequestParam(value="parentId", defaultValue="0") int parentId) {
		
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return response;
		}
		
		return iCategoryService.addCategory(categoryName, parentId);
	}
	
	@RequestMapping(value="updateCategoryNameById.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> updateCategoryNameById(HttpSession session, Integer categoryId, String categoryName) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return response;
		}
		
		return iCategoryService.updateNameById(categoryId, categoryName);
	}
}
