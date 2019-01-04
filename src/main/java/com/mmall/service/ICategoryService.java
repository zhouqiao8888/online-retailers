package com.mmall.service;


import java.util.ArrayList;
import java.util.List;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

public interface ICategoryService {

	ServerResponse<String> addCategory(String categoryName, Integer parentId);
	
	ServerResponse<String> updateNameById(Integer categoryId, String categoryName);
	
	ServerResponse<List<Category>> getChildrenCategories(Integer categoryId);
	
	ServerResponse<List<ArrayList<Category>>> getChildrenCategoriesByDFS(Integer categoryId);
}
