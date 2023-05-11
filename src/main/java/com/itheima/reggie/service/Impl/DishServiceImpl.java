package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.pojo.Dish;
import com.itheima.reggie.pojo.DishFlavor;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    //操作多张表，保持数据一致性
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        //保存菜品表
        Long id = dishDto.getId();
        //获取菜品表id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(dishFlavor-> dishFlavor.setDishId(id));
        //遍历存入口味实体类，存入菜品表id
        dishFlavorService.saveBatch(flavors);
        //保存口味表，批量保存
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish byId = this.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(byId,dishDto);
        LambdaQueryWrapper<DishFlavor>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,byId.getId());
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //根据id更新菜品数据
        this.updateById(dishDto);


        //根据菜品id修改口味表数据
        //删除再添加
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        //添加，获取对象中的口味集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将菜品id赋值给集合的每一个元素
 /*       flavors.stream().map(new Function<DishFlavor, Object>() {
            @Override
            public Object apply(DishFlavor dishFlavor) {
                dishFlavor.setDishId(dishDto.getId());
                return dishFlavor;
            }
        }).close();*/
        flavors.forEach(dishFlavor->dishFlavor.setDishId(dishDto.getId()));

        dishFlavorService.saveBatch(flavors);



    }
}
