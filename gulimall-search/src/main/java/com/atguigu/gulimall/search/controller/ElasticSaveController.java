package com.atguigu.gulimall.search.controller;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.common.utils.exception.BizCodeEnum;
import com.atguigu.gulimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;
    @PostMapping("/product")
    public R ProductStatusUp(@RequestBody List<SkuEsModel> skuEsModelList){
        Boolean b=false;
        try {
            b=productSaveService.productStatusUp(skuEsModelList);
        }catch (Exception e){
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if(b){
            return R.ok();
        }else{
            return R.error();
        }

    }
}
