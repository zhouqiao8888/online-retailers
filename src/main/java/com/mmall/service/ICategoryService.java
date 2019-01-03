package com.mmall.service;


import com.mmall.common.ServerResponse;

public interface ICategoryService {

	ServerResponse<String> addCategory(String categoryName, Integer parentId);
	
	ServerResponse<String> updateNameById(Integer categoryId, String categoryName);
}
