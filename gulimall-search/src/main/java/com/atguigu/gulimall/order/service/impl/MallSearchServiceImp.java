package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.config.ElasticSearchConfig;
import com.atguigu.gulimall.order.constant.EsConstant;
import com.atguigu.gulimall.order.feign.ProductFeignService;
import com.atguigu.gulimall.order.service.MallSearchService;
import com.atguigu.gulimall.order.vo.AttrResponseVo;
import com.atguigu.gulimall.order.vo.SearchParam;
import com.atguigu.gulimall.order.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MallSearchServiceImp implements MallSearchService {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    ProductFeignService productFeignService;
    @Override
    public SearchResult search(SearchParam param) {
        //????????????????????????
        SearchRequest searchRequest = buildSearchRequest(param);
        SearchResult searchResult=null;
        try {
            SearchResponse response = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
             searchResult = buildSearchResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResult;
    }

    /**
     * ??????qsl ??????
     *
     * @param param
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        //????????????
        //1?????????????????????????????????(??????????????????????????????????????????????????????)
        //2???????????????????????????
        //3???????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //must????????????
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        //bool filter ????????????
        if (param.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        //bool filter ??????????????????????????????
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            for (String attr : param.getAttrs()) {
                //attr=1_1???:3???:5???
                String[] s = attr.split("_");
                String attrId = s[0];//??????Id
                String[] attrValues = s[1].split(":");///?????????
                BoolQueryBuilder nestBoolQuery = QueryBuilders.boolQuery();
                nestBoolQuery.must(QueryBuilders.termsQuery("attrs.attrId", attrId));
                nestBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestBoolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }

        }
        //bool filter ??????id
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        //bool filter ???????????????
        if(param.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        //bool filter  ????????????
        if (!StringUtils.isEmpty(param.getSkuPrice())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2) {
                rangeQueryBuilder.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(s[0]);
                }
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);
        //2.1??????
        if (!StringUtils.isEmpty(param.getSort())) {
            //sort=??????_??????
            String sort = param.getSort();
            String[] s = sort.split("_");
            String s0 = s[0];
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s0, sortOrder);
        }
        //2.2?????????
        searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        //2.3 ?????????
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        //????????????
        //????????????
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        //???????????? ????????????
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brand_agg);
        //????????????
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalog_agg);
        //???????????? ???????????????
        NestedAggregationBuilder nested_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nested_agg.subAggregation(attrIdAgg);
        SearchSourceBuilder sourceBuilder = searchSourceBuilder.aggregation(nested_agg);
        log.info("sourceBuilder"+sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * ??????????????????
     *
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {
        SearchResult searchResult = new SearchResult();
        SearchHits hits = response.getHits();//??????????????????
        ArrayList<SkuEsModel> skuEsModels = new ArrayList<>();
        if (hits != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSONObject.parseObject(sourceAsString, SkuEsModel.class);
                if(StringUtils.isNotEmpty(param.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    Text[] fragments = skuTitle.getFragments();
                    String highLightSkuTitle = fragments[0].string();
                    skuEsModel.setSkuTitle(highLightSkuTitle);
                }
                skuEsModels.add(skuEsModel);
            }
        }
        //???????????????????????????
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        ArrayList<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> catalogBuckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : catalogBuckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }

        //???????????????????????????
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        for (Terms.Bucket bucket : brandBuckets) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //id
            String keyAsString = bucket.getKeyAsString();
            brandVo.setBrandId(Long.parseLong(keyAsString));
            //name
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //img
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        //??????
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ArrayList<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        List<? extends Terms.Bucket> attrBuckets = attr_id_agg.getBuckets();
        for (Terms.Bucket attrBucket : attrBuckets) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            long attrId = attrBucket.getKeyAsNumber().longValue();
            ParsedStringTerms attr_name_agg = attrBucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms attr_value_agg = attrBucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attr_value_agg.getBuckets().stream().map(item -> {
                String keyAsString = item.getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());
            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        searchResult.setProduct(skuEsModels);
        searchResult.setCatalogs(catalogVos);
        searchResult.setBrands(brandVos);
        searchResult.setAttrs(attrVos);
        // 5.????????????-??????
        searchResult.setPageNum(param.getPageNum());

        // ????????????
        long total = hits.getTotalHits().value;

        searchResult.setTotal(total);

        // ????????????????????????
        Integer totalPages = (int)total % EsConstant.PRODUCT_PAGESIZE == 0 ?
                (int)total / EsConstant.PRODUCT_PAGESIZE : (int)total / EsConstant.PRODUCT_PAGESIZE + 1;
        searchResult.setTotalPages(totalPages);
        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <=totalPages ; i++) {
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        // 6. ?????????????????????
        List<String> attrs = param.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            List<SearchResult.NavVo> navVos = attrs.stream().map(attr -> {
                String[] split = attr.split("_");
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //6.1 ???????????????
                navVo.setNavValue(split[1]);
                //6.2 ????????????????????????
                try {
                    R r = productFeignService.getAttrsInfo(Long.parseLong(split[0]));
                    if (r.getCode() == 0) {
                        AttrResponseVo attrResponseVo = JSON.parseObject(JSON.toJSONString(r.get("attr")), new TypeReference<AttrResponseVo>() {
                        });
                        navVo.setNavName(attrResponseVo.getAttrName());
                    }
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????", e);
                }
                //6.3 ???????????????????????????
                String queryString = param.get_queryString();
                String replace = queryString.replace("&attrs=" + attr, "").replace("attrs=" + attr+"&", "").replace("attrs=" + attr, "");
                navVo.setLink("http://search.gulimall.com/list.html" + (replace.isEmpty()?"":"?"+replace));
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }
        return searchResult;
    }

    /**
     * ????????????
     * key ??????????????????key
     */
    private String replaceQueryString(SearchParam Param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value,"UTF-8");
            // ??????????????????????????????java????????????
            encode = encode.replace("+","%20");
            encode = encode.replace("%28", "(").replace("%29",")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Param.get_queryString().replace("&" + key + "=" + encode, "");
    }
}
