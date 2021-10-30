package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;

    @RequestMapping("/toTrade")
    public String toTrade() {
        //订单确认页 详情
        OrderConfirmVo confirmOrder = orderService.confirmOrder();
        System.out.println("aaaaaaaaaa");
        return "confirm";
    }
}
