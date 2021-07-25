package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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

//    @GetMapping("index/json/catalog.json")
//    @ResponseBody
//    public Map<String, List<Catalog2Vo>> getCategoryMap() {
//        return categoryService.getCatalogJsonDbWithSpringCache();
//    }
}
