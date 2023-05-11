package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.pojo.OrderDetail;
import com.itheima.reggie.pojo.Orders;
import com.itheima.reggie.pojo.R;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("下单{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");

    }

    @GetMapping("/userPage")
    public R<Page> page(Integer page,Integer pageSize) {
        log.info(page+","+pageSize);
        Page<Orders>ordersPage=new Page<>();
        Page<OrdersDto>ordersDtoPage=new Page<>();
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        LambdaQueryWrapper<Orders>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId, BaseContext.GetCurrentId());
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage,lambdaQueryWrapper);
        List<Orders> records = ordersPage.getRecords();
        List<OrdersDto> collect = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            //根据订单id获取订单菜品

            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> list = orderDetailService.list(lambdaQueryWrapper1);
            ordersDto.setOrderDetails(list);
            return ordersDto;

        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(collect);

        return R.success(ordersDtoPage);

    }
}
