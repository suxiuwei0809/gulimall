package com.atguigu.gulimall.order.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.order.service.MallSearchService;
import com.atguigu.gulimall.order.vo.SearchParam;
import com.atguigu.gulimall.order.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class SearchController {
   @Autowired
    MallSearchService mallSearchService;
    @RequestMapping("/list.html")
    public  String   listpage(SearchParam searchParam, Model model, HttpServletRequest request){
        String queryString = request.getQueryString();//获取请求拼接
        searchParam.set_queryString(queryString);
        SearchResult searchResult = mallSearchService.search(searchParam);
        model.addAttribute("result",searchResult);
        String s = JSON.toJSONString(searchResult);
        log.info("搜索结果"+s);
        return "list";
    }

}
