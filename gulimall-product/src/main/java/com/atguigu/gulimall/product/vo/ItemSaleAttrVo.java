package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ItemSaleAttrVo{
	private Long attrId;

	private String attrName;

	private String attrValues;
}