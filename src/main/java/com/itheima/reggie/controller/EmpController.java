package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.pojo.Employee;
import com.itheima.reggie.pojo.R;
import com.itheima.reggie.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmpController {
    @Autowired
    private EmpService e;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee emp) {

        /**
         * 1.将界面传递密码进行md5加密
         * 2.根据界面2提交的用户名username查询数据
         * 3.没有查询到返回失败
         * 4.查看员工状态，禁用返回失败
         * 5.登陆成功，将员工id存入session，返回成功
         **/
        log.info("登录请求...");
        String password = DigestUtils.md5DigestAsHex(emp.getPassword().getBytes());

        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, emp.getUsername())
                .eq(Employee::getPassword, password);
        Employee getEmp = e.getOne(lambdaQueryWrapper);

        if (getEmp == null) {

            return R.error("用户名或密码错误...");
        }

        if (getEmp.getStatus() == 0) {
            return R.error("该员工已禁用...");
        }

        request.getSession().setAttribute("employee", getEmp.getId());
        return R.success(getEmp);
    }

    @PostMapping("/logout")
    public R<String> exited(HttpServletRequest request) {
        log.info("退出请求...");
        request.getSession().removeAttribute("employee");
        //清除session
        return R.success("退出...");
    }

    @PostMapping
    public R<String> empAdd(@RequestParam(required=false)HttpServletRequest request, @RequestBody Employee employee) {
        log.info("添加名字为{}的员工", employee.getName());
        //设置初始密码
        //md5加密
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

   /*     employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //修改与创建时间


        Long employeeId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(employeeId);
        employee.setUpdateUser(employeeId);
        //操作人id*/


        e.save(employee);
        //将员工信息储存数据库

        return R.success("添加员工成功");
        //返回成功信息

    }

    //员工数据显示
    @GetMapping("/page")
    public R<Page> empList(Integer page, Integer pageSize, String name) {

        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            //当name不为null时调用
            //过滤条件
            lambdaQueryWrapper.like(Employee::getName, name);
        }
        //排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        e.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);

    }

    @PutMapping
    public R<String> update(@RequestParam(required=false)HttpServletRequest request, @RequestBody Employee emp) {

        log.info(emp.toString());
        //获取此次操作的用户id
       /* Long employee = (Long) request.getSession().getAttribute("employee");
        emp.setUpdateTime(LocalDateTime.now());
        emp.setUpdateUser(employee);*/
        e.updateById(emp);
        return R.success("员工信息修改成功...");
    }
    //修改员工信息


    //编辑数据回显
    @GetMapping("/{id}")

    public R<Employee> getDataById(@PathVariable Long id) {
        log.info("根据id查询员工数据...");
        Employee emp = e.getById(id);
        if (emp != null) {
            return R.success(emp);
        }

        return R.error("没有查询到对应员工信息...");
    }

}
