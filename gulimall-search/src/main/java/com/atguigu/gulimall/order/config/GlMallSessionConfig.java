package com.atguigu.gulimall.order.config;

import org.springframework.context.annotation.Configuration;


/**
 * <p>Title: GlMallSessionConfig</p>
 * Description：设置Session作用域、自定义cookie序列化机制
 * date：2020/6/26 21:44
 */
@Configuration
public class GlMallSessionConfig {

//	@Bean
//	public CookieSerializer cookieSerializer(){
//		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
//		// 明确的指定Cookie的作用域
//		cookieSerializer.setDomainName("glmall.com");
//		cookieSerializer.setCookieName("FIRESESSION");
//		return cookieSerializer;
//	}
//
//	/**
//	 * 自定义序列化机制
//	 * 这里方法名必须是：springSessionDefaultRedisSerializer
//	 */
//	@Bean
//	public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
//		return new GenericJackson2JsonRedisSerializer();
//	}
}