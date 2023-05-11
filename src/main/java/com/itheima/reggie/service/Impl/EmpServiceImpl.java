package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.mapper.EmpMapper;
import com.itheima.reggie.pojo.Employee;
import com.itheima.reggie.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Employee> implements EmpService {
    @Autowired
    private EmpMapper e;

}
