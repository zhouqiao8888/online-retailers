package com.mmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
	
	@Autowired
	private CategoryMapper categoryMapper;

	@Override
	public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
		if(StringUtils.isBlank(categoryName) || parentId == null) {
			return ServerResponse.createByErrorMsg("添加品类参数错误");
		}
		
		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);
		
		int resCount = categoryMapper.insertSelective(category);
		if(resCount > 0) {
			return ServerResponse.createBySuccessMsg("添加品类成功");
		}
		return ServerResponse.createByErrorMsg("添加品类失败");
	}

	@Override
	public ServerResponse<String> updateNameById(Integer categoryId, String categoryName) {
		if(StringUtils.isBlank(categoryName) || categoryId == null) {
			return ServerResponse.createByErrorMsg("添加品类参数错误");
		}
		
		Category category = new Category();
		category.setName(categoryName);
		category.setId(categoryId);
		
		int resCount = categoryMapper.updateByPrimaryKeySelective(category);
		if(resCount > 0) {
			return ServerResponse.createBySuccessMsg("更新品类名称成功");
		}
		return ServerResponse.createByErrorMsg("更新品类名称失败");
		
	}
	
}
