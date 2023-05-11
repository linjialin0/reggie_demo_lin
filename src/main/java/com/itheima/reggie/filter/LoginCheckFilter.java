package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.pojo.R;
import com.itheima.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    //路径匹配器
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {



        //获取请求url路径
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //需要放行的路径

        String[] urls = {
                "/employee/login",
                "/employee/logout",
                //请求路径
                "/backend/**",
                "/front/**",
                //文件路径

                "/user/sendMsg",
                "/user/login"


        };


        String requestURI = request.getRequestURI();
        //url路径
        log.info("拦截到请求{}", requestURI);

        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //如果是定义在数组里的路径则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态;

        if (request.getSession().getAttribute("employee")!=null) {
        //emp不为空时则已登录，直接放行
            log.info("用户已登录，用户id为{}", request.getSession().getAttribute("employee"));
            //测试
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            /////////////////////
            filterChain.doFilter(request,response);
            return;
        }

        //客户端登录校验
        if (request.getSession().getAttribute("user")!=null) {
            //emp不为空时则已登录，直接放行
            log.info("用户已登录，用户id为{}", request.getSession().getAttribute("user"));
            //测试
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            /////////////////////
            filterChain.doFilter(request,response);
            return;
        }




        //运行到此则代表未登录，返回未登录响应
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));



    }

    public boolean check(String[] urls, String requestURI) {

        for (String url : urls) {
            //路径比较
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
