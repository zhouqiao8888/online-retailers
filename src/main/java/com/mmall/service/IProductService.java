package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;

public interface IProductService {
	
	ServerResponse<String> saveOrUpdateProduct(Product product);
	
	ServerResponse<String> updateSaleStatus(Integer productId, Integer status);
	
	ServerResponse<ProductDetailVO> manageProductDetai(Integer productId);
	
	ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);
	
	ServerResponse<PageInfo> searchProductList(Integer productId, String productName, Integer pageNum, Integer pageSize);
}
