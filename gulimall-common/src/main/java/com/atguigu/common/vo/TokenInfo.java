package com.atguigu.common.vo;

import lombok.Data;


/**
 * 用以封装社交登录认证后换回的令牌等信息
 */
@Data
public class TokenInfo {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;
    private long created_at;

}
