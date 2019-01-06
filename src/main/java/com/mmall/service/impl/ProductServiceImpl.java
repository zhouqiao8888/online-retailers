package com.mmall.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	public ServerResponse<String> saveOrUpdateProduct(Product product) {
		if(product == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		if(StringUtils.isNotBlank(product.getSubImages())) {
			String[] subImagesArr = product.getSubImages().split(",");
			if(subImagesArr.length > 0) {
				product.setMainImage(subImagesArr[0]);
			}
		}
		
		//输入id，表示需要更新，否则表示插入
		if(product.getId() != null) {
			int resCount = productMapper.updateByPrimaryKeySelective(product);
			if(resCount > 0) {
				return ServerResponse.createBySuccessMsg("产品更新成功");
			}
			return ServerResponse.createByErrorMsg("产品更新失败");
		}
		else {
			int resCount = productMapper.insertSelective(product);
			if(resCount > 0) {
				return ServerResponse.createBySuccessMsg("产品上架成功");
			}
			return ServerResponse.createByErrorMsg("产品上架失败");
		}		
	}
	
	public ServerResponse<String> updateSaleStatus(Integer productId, Integer status) {
		if(productId == null || status == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		
		int resCount = productMapper.updateByPrimaryKeySelective(product);
		if(resCount > 0) {
			return ServerResponse.createBySuccessMsg("状态修改成功");
		}
		return ServerResponse.createByErrorMsg("状态修改失败");
			
	}
	
	public ServerResponse<ProductDetailVO> manageProductDetai(Integer productId) {
		if(productId == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null) {
			return ServerResponse.createByErrorMsg("产品信息查找失败");
		}
		else {
			ProductDetailVO productDetailVO = this.assembleProductDetailVO(product);
			return ServerResponse.createBySuccessMsgAndData("产品信息查找成功", productDetailVO);
		}
		
	}
	
	public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
		if(pageNum == null || pageSize == null) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		/*
		 * PageHelper.startPage
		 * sql逻辑
		 * new PageInfo
		 */
		PageHelper.startPage(pageNum, pageSize);
		
		List<Product> productList = productMapper.selectProductList();
		List<ProductListVO> productListVOList = Lists.newArrayList();
		for(Product product : productList) {
			ProductListVO productListVO = this.assembleProductListVO(product);
			productListVOList.add(productListVO);
		}
		
		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVOList);
		
		return ServerResponse.createBySuccessMsgAndData("产品列表获取成功", pageInfo);		
	}
	
	public ServerResponse<PageInfo> searchProductList(Integer productId, String productName, Integer pageNum, Integer pageSize) {
		if(productId == null && StringUtils.isBlank(productName)) {
			return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
					ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		PageHelper.startPage(pageNum, pageSize);
		
		if(StringUtils.isNotBlank(productName)) {
			productName = new StringBuilder("%" + productName + "%").toString();
		}
		
		List<Product> productList = productMapper.selectProductListByNameOrId(productName, productId);
		if(CollectionUtils.isNotEmpty(productList)) {
			List<ProductListVO> productListVOList = Lists.newArrayList();
			for(Product product : productList) {
				productListVOList.add(this.assembleProductListVO(product));
			}
			
			PageInfo pageInfo = new PageInfo(productList);
			pageInfo.setList(productListVOList);
			return ServerResponse.createBySuccessMsgAndData("产品搜索成功", pageInfo);
		}
		return ServerResponse.createByErrorMsg("没有符合搜索条件的商品");	

//		//若产品名不为空，则先对产品名进行模糊查询，不将id写入sql语句判断条件中	
//		if(StringUtils.isNotBlank(productName)) {
//			String productName_search = new StringBuilder("%" + productName + "%").toString();
//			List<Product> productList = productMapper.selectProductListByName(productName_search);
//			
//			if(CollectionUtils.isNotEmpty(productList)) {
//				List<ProductListVO> productListVOList = Lists.newArrayList();
//				for(Product product : productList) {
//					productListVOList.add(this.assembleProductListVO(product));
//				}
//				
//				PageInfo pageInfo = new PageInfo(productList);
//				pageInfo.setList(productListVOList);
//				return ServerResponse.createBySuccessMsgAndData("产品搜索成功", pageInfo);
//			}
//		}
//		
//		//若通过产品名没有搜索到，再根据id进行定向搜索
//		if(productId != null) {
//			Product product = productMapper.selectByPrimaryKey(productId);
//			if(product != null) {
//				PageInfo pageInfo = new PageInfo(Lists.newArrayList(product));
//				ProductListVO productListVO = this.assembleProductListVO(product);
//				pageInfo.setList(Lists.newArrayList(productListVO));
//				return ServerResponse.createBySuccessMsgAndData("产品搜索成功", pageInfo);
//			}
//		}	
//		return ServerResponse.createByErrorMsg("没有符合搜索条件的商品");			
	}
	
	public ProductListVO assembleProductListVO(Product product) {
		ProductListVO productListVO = new ProductListVO();
		
		productListVO.setCategoryId(product.getCategoryId());
		productListVO.setId(product.getId());
		productListVO.setMainImage(product.getMainImage());
		productListVO.setName(product.getName());
		productListVO.setPrice(product.getPrice());
		productListVO.setStatus(product.getStatus());
		productListVO.setSubtitle(product.getSubtitle());
		
		productListVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		
		return productListVO;
	}
	
	//po -> vo
	public ProductDetailVO assembleProductDetailVO(Product product) {
		ProductDetailVO productDetailVO = new ProductDetailVO();
		
		productDetailVO.setId(product.getId());
		productDetailVO.setName(product.getName());
		productDetailVO.setCategoryId(product.getCategoryId());
		productDetailVO.setPrice(product.getPrice());
		productDetailVO.setDetail(product.getDetail());
		productDetailVO.setMainImage(product.getMainImage());
		productDetailVO.setSubImages(product.getSubImages());
		productDetailVO.setSubtitle(product.getSubtitle());
		productDetailVO.setStock(product.getStock());
		productDetailVO.setStatus(product.getStatus());
		
		//设置图片服务器地址
		productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		
		//校验parentCategoryId是否存在
		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if(category != null) {
			productDetailVO.setParenetCategoryId(category.getParentId());
		}
		else {
			productDetailVO.setParenetCategoryId(0); //找不到父亲分类，默认设置为0
		}
		
		//处理日期：将日期转化为字符串
		productDetailVO.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productDetailVO.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
		
		return productDetailVO;
	}

}
