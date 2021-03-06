package com.gulimall;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.order.bean.User;
import com.atguigu.gulimall.order.config.ElasticSearchConfig;
import org.apache.shiro.authc.Account;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class MallSearchApplicationTests {

    @Resource
    private RestHighLevelClient client;

    @Test
    public void esTest() {
        System.out.println(client);
    }

    @Test
    public void Test() {
        try {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("user", "kimchy");
            jsonMap.put("postDate", new Date());
            jsonMap.put("message", "trying out Elasticsearch");
            IndexRequest indexRequest = new IndexRequest("posts").id("1").source(jsonMap);
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     */
    @Test
    public void searchDataTest() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        // ????????????
        searchRequest.indices("newbank");
        // 1.?????????????????? DSL
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 1.1 ??????????????????
        builder.query(QueryBuilders.matchQuery("address", "mill"));
        // 1.2 ?????????????????????
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        builder.aggregation(ageAgg);
        // 1.3 ??????????????????
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        builder.aggregation(balanceAvg);
        System.out.println("???????????????" + builder.toString());
        searchRequest.source(builder);
        // 2.????????????
        SearchResponse search = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        // 3.????????????
//		Map map = JSON.parseObject(search.toString(), Map.class);
//		System.out.println(map);
        // 3.1 ??????????????????
        SearchHits hits = search.getHits();
        // ????????????
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
//			String index = hit.getIndex();
//			String id = hit.getId();
            String source = hit.getSourceAsString();
            Account account = JSON.parseObject(source, Account.class);
            System.out.println(account);
        }
        // ??????????????????
        Aggregations aggregations = search.getAggregations();
//		List<Aggregation> list = aggregations.asList();
//		for (Aggregation aggregation : list) {
//			Terms agg = aggregations.get(aggregation.getName());
//			System.out.println(agg.getBuckets());
//		}
        Terms agg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : agg.getBuckets()) {
            System.out.println("??????: " + bucket.getKeyAsString() + "-->" + bucket.getDocCount() + "???");
        }
        Avg avg = aggregations.get("balanceAvg");
        System.out.println("??????????????? " + avg.getValue());
    }

    /**
     * ????????????
     */
    @Test
    public void indexTest() throws IOException {
        IndexRequest request = new IndexRequest("users");
        // ????????????id
        request.id("2");
        // ???1?????????
//		request.source("userName","firenay","age","20","gender","???");
        // ???2?????????
        User user = new User();
        user.setUserName("firenay");
        user.setAge("20");
        user.setGender("???");
        String jsonString = JSON.toJSONString(user);
        // ??????json??? ????????????
        request.source(jsonString, XContentType.JSON);
        // ??????????????????
        IndexResponse response = client.index(request, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(response);
        System.out.println(response.status());
    }
}
