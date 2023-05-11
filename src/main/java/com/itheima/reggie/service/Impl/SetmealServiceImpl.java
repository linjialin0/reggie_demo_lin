package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.pojo.Setmeal;
import com.itheima.reggie.pojo.SetmealDish;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {
    @Autowired
    SetmealDishService setmealDishService;
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);
        //保存套餐菜品并为每个菜品添加套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
       setmealDishes.forEach(item->item.setSetmealId(setmealDto.getId()));
       setmealDishService.saveBatch(setmealDishes);
    }
    @Override
    @Transactional
    //多表操作，保持数据一致性
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，是否可删除
        //如果有处于起售状态套餐时则抛出异常
        //select count(*) from setmeal where id in(1,2,3) and status=1
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        //1为起售
        int count = this.count(lambdaQueryWrapper);
        if (count>0) {
            throw new CustomException("当前删除套餐处于正在售卖状态，删除失败");
        }
        //如果数量大于0则抛出异常
        //如果可以删除，删除套餐表数据
        this.removeByIds(ids);
        //删除关系表数据
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //先查询基础数据
        Setmeal byId = this.getById(id);
        SetmealDto setmealDto=new SetmealDto();

        BeanUtils.copyProperties(byId,setmealDto);
        //通过id查询菜品
        LambdaQueryWrapper<SetmealDish>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,byId.getId());
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }
}
