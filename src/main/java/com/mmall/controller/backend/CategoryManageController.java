package com.mmall.controller.backend;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
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

	/**
	 * 添加分类
	 * @param session
	 * @param categoryName
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value="/addCategory.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> addCategory(HttpSession session, String categoryName,
			@RequestParam(value="parentId", defaultValue="0") Integer parentId) {
		
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
	
	/**
	 * 更新分类名
	 * @param session
	 * @param categoryId
	 * @param categoryName
	 * @return
	 */
	@RequestMapping(value="/updateCategoryNameById.do", method=RequestMethod.POST)
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
	
	/**
	 * 获取当前分类的所有子分类(不递归，只查找下一级平行节点)
	 * @param session
	 * @param categoryId
	 * @return
	 */
	@RequestMapping(value="/getChildrenCategories.do", method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<List<Category>> getChildrenCategories(HttpSession session, 
			@RequestParam(value="categoryId", defaultValue="0") Integer categoryId) {
		
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，没有权限操作");
		}		
		return iCategoryService.getChildrenCategories(categoryId);	
	}
	
	/**
	 * 获取当前分类的所有子分类树(递归)
	 * @param session
	 * @param categoryId
	 * @return
	 */
	@RequestMapping(value="/getChildrenCategoriesByDFS.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<List<ArrayList<Category>>> getChildrenCategoriesByDFS(HttpSession session, 
			@RequestParam(value="categoryId", defaultValue="0") Integer categoryId) {
		
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，没有权限操作");
		}		
		return iCategoryService.getChildrenCategoriesByDFS(categoryId);
	}
}
