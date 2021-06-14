package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

	@Resource
	private BrandDao brandDao;

	@Resource
	private CategoryDao categoryDao;

	@Resource
	private CategoryBrandRelationDao categoryBrandRelationDao;

	@Resource
	private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

	/**
	 * 根据获取品牌id 、三级分类id查询对应的名字保存到数据库
	 */
	@Override
	public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
    	// 获取品牌id 、三级分类id
		Long brandId = categoryBrandRelation.getBrandId();
		Long catelogId = categoryBrandRelation.getCatelogId();
		// 根据id查询详细名字
		BrandEntity brandEntity = brandDao.selectById(brandId);
		CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
		categoryBrandRelation.setBrandName(brandEntity.getName());
		categoryBrandRelation.setCatelogName(categoryEntity.getName());
		this.save(categoryBrandRelation);
	}

	@Override
	public void updateBrand(Long brandId, String name) {
		this.baseMapper.updateBrand(brandId,name);
	}

	@Override
	public void updateCategory(Long catId, String name) {
		this.baseMapper.updateCategory(catId, name);
	}

	/**
	 * 获取某个分类下所有品牌信息
	 */
	@Override
	public List<BrandEntity> getBrandsByCatId(Long catId) {
		List<CategoryBrandRelationEntity> catelogId = categoryBrandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
		// 根据品牌id查询详细信息
		List<BrandEntity> collect = catelogId.stream().map(item -> {
			Long brandId = item.getBrandId();
			BrandEntity entity = brandService.getById(brandId);
			return entity;
		}).collect(Collectors.toList());

		return collect;
	}

}