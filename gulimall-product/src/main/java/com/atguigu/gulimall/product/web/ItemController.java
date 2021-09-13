package com.atguigu.gulimall.product.web;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo  skuItemVo = skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        String s = JSON.toJSONString(skuItemVo);
        System.out.println("详情="+s);
        return "item";
    }
}
