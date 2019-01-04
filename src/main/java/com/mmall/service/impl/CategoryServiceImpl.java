package com.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
	
	private static Logger logger = LoggerFactory.getLogger(ICategoryService.class);
	
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

	@Override
	public ServerResponse<List<Category>> getChildrenCategories(Integer categoryId) {
		if(categoryId == null) {
			return ServerResponse.createByErrorMsg("添加品类参数错误");
		}
		
		List<Category> childrenCategroies = categoryMapper.selectChildrenCategories(categoryId);
		if(CollectionUtils.isEmpty(childrenCategroies)) {
			logger.error("未找到当前分类的子分类");
			return ServerResponse.createByErrorMsg("未找到当前分类的子分类");
		}
		return ServerResponse.createBySuccessMsgAndData("找到当前分类的子分类", childrenCategroies);
	}
	
	public ServerResponse<List<ArrayList<Category>>> getChildrenCategoriesByDFS(Integer categoryId) {		
		if(categoryId == null) {
			return ServerResponse.createByErrorMsg("添加品类参数错误");
		}
		
		List<ArrayList<Category>> res = new ArrayList<ArrayList<Category>>();
		List<Category> temp = Lists.newArrayList();
		Category rootCategory = categoryMapper.selectByPrimaryKey(categoryId);
		if(rootCategory != null) {
			temp.add(rootCategory);
		}
		dfs(res, temp, categoryId);
		
		if(CollectionUtils.isEmpty(res)) {
			logger.error("未找到当前分类的子分类");
			return ServerResponse.createByErrorMsg("未找到当前分类的子分类");
		}
		return ServerResponse.createBySuccessMsgAndData("找到当前分类的子分类", res);
	}
	
	public void dfs(List<ArrayList<Category>> res, List<Category> temp, Integer categoryId) {
		//终止条件：当前的节点没有子孩子，为叶子节点
		List<Category> childrenCategroies = categoryMapper.selectChildrenCategories(categoryId);
		if(CollectionUtils.isEmpty(childrenCategroies)) {
			if(!CollectionUtils.isEmpty(temp)) {
//				logger.info("子分类查找成功", temp);
				res.add(new ArrayList<Category>(temp));
			}
			return;
		}
		
		for(Category category : childrenCategroies) {
			temp.add(category);			
			dfs(res, temp, category.getId());
			temp.remove(category);
		}
		
	}
	
}
