package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        List<CategoryEntity> all = baseMapper.selectList(null);
        List<CategoryEntity> levelMenus = all.stream().filter(m ->
                m.getParentCid() == 0).map((m) -> {
            //找儿子
            m.setChildren(getChildrens(m, all));
            return m;
        }).collect(Collectors.toList());//获取所有的一级分类
        return levelMenus;
    }

    //得到完整路径回显
    @Override
    public Long[] findCagelogPath(Long catelogId) {
        ArrayList path = new <Long>ArrayList();
        ArrayList<Long> parentPath = getParentPath(catelogId, path);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    @Override
    public List<CategoryEntity> getLevel1Catagories() {

//        long start = System.currentTimeMillis();
        List<CategoryEntity> parent_cid = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
//        System.out.println("查询一级菜单时间:"+(System.currentTimeMillis()-start));
        return parent_cid;


    }

    /**
     * 递归查找父id并收集
     *
     * @param catelogId
     * @param path
     * @return
     */
    public ArrayList<Long> getParentPath(Long catelogId, ArrayList<Long> path) {
        path.add(catelogId);
        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0) {
            getParentPath(category.getParentCid(), path);
        }
        return path;
    }

    public List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(m -> root.getCatId() == m.getParentCid()).map(m -> {
            m.setChildren(getChildrens(m, all));
            return m;
        }).sorted((m1, m2) -> {
            return m1.getSort() - m2.getSort();
        }).collect(Collectors.toList());

        return children;
    }
}