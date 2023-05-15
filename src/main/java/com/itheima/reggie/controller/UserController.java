package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.pojo.R;
import com.itheima.reggie.pojo.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //获取随机4位数验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);

            //使用阿里云短信服务发送验证码
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //将验证码存储到session
            //session.setAttribute(phone, code);

            //将验证码存储到redis中，有效期为5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.success("手机短信验证码发送成功");
        } else {
            return R.error("短信发送失败");
        }

    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        String phone = map.get("phone").toString();
        //手机号
        String code = map.get("code").toString();
        //验证码
        //Object code1 = session.getAttribute(phone);
        //从session中获取的验证码

        Object code1 = redisTemplate.opsForValue().get(phone);
        //从redis中获取的验证码
        //验证码进行比对
        if (code1 != null && code1.equals(code)) {
            //根据手机号判断是否为新用户，如果是则自动创建完成注册
            //将数据存入数据库
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                userService.save(user);

                //没有设置id，由MP默认直接生成使用，会使用雪花算法生成id存入，并自动set到实体类的id字段
                //这也是为什么没有给实体类setId数据库有id，且实体类能get到的原因
            }
            session.setAttribute("user", user.getId());
            //查询到数据时有id，查询不到时save的时候也会自动生成id，也能获取到

            //用户登录成功，删除redis验证码数据
            redisTemplate.delete(phone);

            return R.success(user);

        }

        return R.error("登录失败");

    }

    @PostMapping("/loginout")
    public R<String> exit(HttpSession session) {
        log.info("退出登录");
        //清空session数据
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}


