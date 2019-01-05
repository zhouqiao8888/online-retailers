package com.mmall.controller.backend;

import java.io.FileInputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
	
	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private IProductService iProductService;
	
	@Autowired
	private IFileService iFileService;
	
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
	
	/**测试上传文件的方法用chrome浏览器
	 * 上传文件
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/uploadFile.do", method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Map<String, String>> uploadFile(HttpSession session, HttpServletRequest request,
			@RequestParam(value="upload_file", required=false) MultipartFile file) {
		
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
		
		//获取tomcat下的upload文件夹路径
		String path = request.getSession().getServletContext().getRealPath("upload");
//		System.out.println(path);
		String fileUploadName = iFileService.upload(file, path);
		if(StringUtils.isBlank(fileUploadName)) {
			return ServerResponse.createByErrorMsg("文件上传失败");
		}
		
		String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + fileUploadName;		
		Map<String, String> map = Maps.newHashMap();
		map.put("uri", fileUploadName);
		map.put("url", url);
		
		return ServerResponse.createBySuccessMsgAndData("文件上传成功", map);		
	}
	
	/**
	 * 上传富文本(有自己个返回格式)
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/uploadRichTextFile.do", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadRichTextFile(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="upload_file", required=false) MultipartFile file) {
		
		Map<String, Object> resMap = Maps.newHashMap();
		
		//判断用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			resMap.put("success", false);
			resMap.put("msg", "请重新登陆");
			return resMap;
		}
		
		//判断是否为管理员
		ServerResponse<String> resResponse = iUserService.checkAdminRole(user);
		if(!resResponse.isSuccess()) {
			resMap.put("success", false);
			resMap.put("msg", "不是管理员，没有操作权限");
			return resMap;
		}
		
		//获取tomcat下的upload文件夹路径
		String path = request.getSession().getServletContext().getRealPath("upload");
		String fileUploadName = iFileService.upload(file, path);
		if(StringUtils.isBlank(fileUploadName)) {
			resMap.put("success", false);
			resMap.put("msg", "文件上传失败");
			return resMap;
		}
		
		String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + fileUploadName;		
		
		resMap.put("success", true);
		resMap.put("msg", "文件上传成功");
		resMap.put("file_path", url);
		response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
		return resMap;
	}
	
	
}
