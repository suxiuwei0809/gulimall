package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.order.config.ElasticSearchConfig;
import com.atguigu.gulimall.order.constant.EsConstant;
import com.atguigu.gulimall.order.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImp implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException {
        /**
         *1、 增加mapping
         * 2、保存数据
         */
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel:skuEsModelList) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String  a   = JSON.toJSONString(skuEsModel);
            indexRequest.source(a, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        boolean b = bulk.hasFailures();//是否出错
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.error("商品上架出错："+collect);
        return !b;
    }
}
