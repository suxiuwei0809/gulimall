package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    StringRedisTemplate  stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;
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

    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("category-lock");
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();//不参数的 具有看门狗，自动锁续命
        Map<String, List<Catalog2Vo>> categoriesDb;
        try {
            categoriesDb = getCategoriesDb();
        } finally {
            rLock.unlock();
        }
        return categoriesDb;
    }

    //从数据库中查出三级分类
    private  Map<String, List<Catalog2Vo>> getCategoriesDb() {
        //优化业务逻辑，仅查询一次数据库
        List<CategoryEntity> categoryEntities = this.list();
        //查出所有一级分类
        List<CategoryEntity> level1Categories = getCategoryByParentCid(categoryEntities, 0L);
        Map<String, List<Catalog2Vo>> listMap = level1Categories.stream().collect(Collectors.toMap(k->k.getCatId().toString(), v -> {
            //遍历查找出二级分类
            List<CategoryEntity> level2Categories = getCategoryByParentCid(categoryEntities, v.getCatId());
            List<Catalog2Vo> catalog2Vos=null;
            if (level2Categories!=null){
                //封装二级分类到vo并且查出其中的三级分类
                catalog2Vos = level2Categories.stream().map(cat -> {
                    //遍历查出三级分类并封装
                    List<CategoryEntity> level3Catagories = getCategoryByParentCid(categoryEntities, cat.getCatId());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;
                    if (level3Catagories != null) {
                        catalog3Vos = level3Catagories.stream()
                                .map(level3 -> new Catalog2Vo.Catalog3Vo(level3.getParentCid().toString(), level3.getCatId().toString(), level3.getName()))
                                .collect(Collectors.toList());
                    }
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), cat.getCatId().toString(), cat.getName(), catalog3Vos);
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));
        return listMap;
    }

    private List<CategoryEntity> getCategoryByParentCid(List<CategoryEntity> categoryEntities, long l) {
        List<CategoryEntity> collect = categoryEntities.stream().filter(cat -> cat.getParentCid() == l).collect(Collectors.toList());
        return collect;
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