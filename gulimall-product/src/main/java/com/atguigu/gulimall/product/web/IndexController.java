package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @GetMapping({"/", "index.html"})
    public String getIndex(Model model) {
        //获取所有的一级分类
        List<CategoryEntity> categorys = categoryService.getLevel1Catagories();
        model.addAttribute("categorys", categorys);
        return "index";
    }

    @GetMapping("index/json/catalog.json")
    @ResponseBody
    @Cacheable(value = "category" ,key="#root.methodName")
    public Map<String, List<Catalog2Vo>> getCategoryMap() {
        return categoryService.getCatalogJson();
    }



}
