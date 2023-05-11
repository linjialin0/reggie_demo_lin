package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.pojo.Category;
import com.itheima.reggie.pojo.Employee;
import com.itheima.reggie.pojo.R;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService c;

    @GetMapping("/page")
    public R<Page> categoryList(Integer page, Integer pageSize) {
        log.info("page={},pageSize={}", page, pageSize);
        Page pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        c.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info(category.toString());
        c.updateById(category);

    /*    if (category.getType()==1){
            return R.success("菜品分类信息修改成功...");
        } else if (category.getType()==2) {
           return R.success("套餐分类信息修改成功...");
        }

        return R.error("未知错误...");*/
        return R.success("分类信息修改成功...");
    }

    @PostMapping
    public R<String> categoryAdd(@RequestBody Category category) {
        log.info("添加分类{}", category);
        c.save(category);
        return R.success("分类添加成功...");

    }
@DeleteMapping
    public R<String> deleteCategory(Long ids){
    log.info("删除id为{}的数据", ids);
    c.delete(ids);
    //service层判断是否满足删除条件，没有关联时则满足条件，否则抛出异常

    return R.success("分类信息删除成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("查询type为{}的菜品", category.getType());
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //如果没有接收到type数据，则不执行这条语句，查询所有套餐菜品
        lambdaQueryWrapper.eq(Category::getIsDeleted,0);
        //没有被逻辑删除的
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = c.list(lambdaQueryWrapper);
        return R.success(list);

    }

}
