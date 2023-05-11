package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.pojo.R;
import com.itheima.reggie.pojo.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.GetCurrentId();
        shoppingCart.setUserId(userId);
        log.info("添加购物车{}", shoppingCart.toString());
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        if (dishId != null) {
            //如果是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            //如果是套餐
            lambdaQueryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if (one != null) {
            //如果数据存在，就在原先基础上数量加1
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);

        } else {
            //如果不存在，就储存
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("根据id查找购物车菜品或套餐{}", BaseContext.GetCurrentId());
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.GetCurrentId());
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        //排序，最后添加排在前面
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);

    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("数量减少{}", shoppingCart);
        Long dishId = shoppingCart.getDishId();
        //查询
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.GetCurrentId());
        if (dishId != null) {
            //菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //套餐
            lambdaQueryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = one.getNumber();
        one.setNumber(number - 1);
        if (one.getNumber() == 0) {
            //throw new CustomException("请使用清空选项重新选择");
            shoppingCartService.removeById(one.getId());
            return R.success(one);
        } else {
            shoppingCartService.updateById(one);
            return R.success(one);
        }





    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        log.info("清空购物车");
        LambdaQueryWrapper<ShoppingCart>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.GetCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("清空购物车成功");
    }


}
