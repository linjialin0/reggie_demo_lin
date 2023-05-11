package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.pojo.AddressBook;
import com.itheima.reggie.pojo.R;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
   private AddressBookService addressBookService;

    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.GetCurrentId());
        log.info("保存地址{}", addressBook);
        addressBookService.save(addressBook);
        return R.success("保存成功");
    }

    @GetMapping("/list")
    public R<List<AddressBook>> list() {

        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId, BaseContext.GetCurrentId());

        List<AddressBook> list = addressBookService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    //设置默认地址
    @PutMapping("/default")
    public R<AddressBook> updateNumber(@RequestBody AddressBook addressBook) {

        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId, BaseContext.GetCurrentId());
        lambdaUpdateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(lambdaUpdateWrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);

    }

    //数据回显
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable String id) {

        AddressBook byId = addressBookService.getById(id);

        return R.success(byId);
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {

        addressBookService.updateById(addressBook);

        return R.success("修改成功");
    }

    //默认地址
    @GetMapping("/default")
    public R<AddressBook> getAddressBook() {
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId, BaseContext.GetCurrentId());
        lambdaQueryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook one = addressBookService.getOne(lambdaQueryWrapper);
        return R.success(one);


    }

}
