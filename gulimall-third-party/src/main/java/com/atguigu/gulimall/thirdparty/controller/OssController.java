package com.atguigu.gulimall.thirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.atguigu.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
@RestController
public class OssController {
    @Autowired
    private OSS  ossClient;
    @Value("${alibaba.cloud.oss.endpoint}")
    private  String endpoint;
    @Value("${alibaba.cloud.bucket}")
    private  String bucket;
    @Value("${alibaba.cloud.access-key}")
    private  String accessId;



    @RequestMapping("/oss/policy")
    public  R policy(){
//        String accessId = "<yourAccessKeyId>"; // 请填写您的AccessKeyId。
//        String accessKey = "<yourAccessKeySecret>"; // 请填写您的AccessKeySecret。
//        String endpoint = "oss-cn-hangzhou.aliyuncs.com"; // 请填写您的 endpoint。
//        String bucket = "bucket-name"; // 请填写您的 bucketname 。

        System.out.println("bucket="+bucket);
        System.out.println("endpoint="+endpoint);
        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        String   date  = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = date+"/"; // 用户上传文件时指定的前缀。
        HashMap<String, String> respMap=null;
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            respMap = new HashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        }
        return R.ok().put("data",respMap);
    }
}
