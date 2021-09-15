package com.atguigu.gulimall.order.service;


import com.atguigu.gulimall.order.vo.SearchParam;
import com.atguigu.gulimall.order.vo.SearchResult;

/**
 * <p>Title: MasllService</p>
 * Description：
 * date：2020/6/12 23:05
 */
public interface MallSearchService {

	/**
	 * 检索所有参数
	 */
	SearchResult search(SearchParam Param);
}
