package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.pojo.Category;
import com.itheima.reggie.pojo.Dish;
import com.itheima.reggie.pojo.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService d;
    @Autowired
    private SetmealService s;

    @Override
    public void delete(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //判断dish和setMeal表中是否有关联分类id的数据(根据分类id进行查询表或套餐的数量)，有则抛出异常，该分类下含有套餐或者菜品，无法删除
        //实现逻辑删除
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);

        int dishCount = d.count(dishLambdaQueryWrapper);

        if (dishCount > 0) {
            //已经关联菜品，抛出一个业务异常

            throw new CustomException("当前分类关联了菜品，无法删除");

        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int setmealCount = s.count(setmealLambdaQueryWrapper);

        if (setmealCount > 0) {
            //已经关联套餐，抛出一个业务异常

            throw new CustomException("当前分类关联了套餐，无法删除");
        }
        super.removeById(ids);
    }
}
