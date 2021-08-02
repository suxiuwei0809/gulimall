package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {
   @Autowired
    MallSearchService mallSearchService;
    @RequestMapping("/list.html")
    public  String   listpage(SearchParam searchParam, Model model){
        SearchResult searchResult = mallSearchService.search(searchParam);
        model.addAttribute("result",searchResult);
        return "list";
    }

}
