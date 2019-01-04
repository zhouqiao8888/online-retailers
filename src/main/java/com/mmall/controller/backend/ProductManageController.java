package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.vo.ProductDetailVO;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IProductService iProductService;
	
	/**
	 * 上架或更新产品
	 */
	@RequestMapping(value="/saveOrUpdateProduct.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> saveOrUpdateProduct(HttpSession session, Product product) {
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return response;
		}		
		return iProductService.saveOrUpdateProduct(product);		
	}
	
	/**
	 * 更新产品状态
	 */
	@RequestMapping(value="/setSaleStatus.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> setSaleStatus(HttpSession session, Integer productId, Integer status) {
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return response;
		}
		return iProductService.updateSaleStatus(productId, status);
	}
	
	/**
	 * 返回产品信息，同时将po->vo(适合前端展示)
	 * @param session
	 * @param productId
	 * @return
	 */
	@RequestMapping(value="/getProductDetail.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<ProductDetailVO> getProductDetail(HttpSession session, Integer productId) {
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，没有操作权限");
		}
		return iProductService.manageProductDetai(productId);
	}
	
	/**
	 * 返回产品信息列表，使用PageHelper进行分页
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/getProductList.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<PageInfo> getProductList(HttpSession session, 
			@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
			@RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
		
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，没有操作权限");
		}
		return iProductService.getProductList(pageNum, pageSize);
	}
	
	/**
	 * 根据id和name搜索产品列表
	 * @param session
	 * @param productId
	 * @param productName
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/searchProductList.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<PageInfo> searchProductList(HttpSession session, Integer productId, String productName,
			@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
			@RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
		
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMsg("用户未登陆，请重新登陆");
		}
		
		//判断是否为管理员
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(!response.isSuccess()) {
			return ServerResponse.createByErrorMsg("不是管理员，没有操作权限");
		}
		return iProductService.searchProductList(productId, productName, pageNum, pageSize);
	}
	
	
}
