package com.itheima.reggie.utils;

/**
 * 基于threadLocal封装的类，用于保存和获取当前登录用户id
 *
 * 使用线程的方式进行解决用户id获取的问题，使每条线程都拥有一个共享的存储空间
 * 通过存储空间进行存储与获取
 *
 *
 *客户端每次发起请求，在服务端都会分配一个新的线程来处理
 *客户端发起请求，在服务端所涉及到的类中的方法都属于同一线程
 *
 * 例如发起了一个数据修改（controller）请求，设置了过滤器（filter）和自动填充（metaObjectHandler），
 * 这些操作都会被视为一次请求，请求中包含这些操作
 * 服务端这时会分配一个线程去处理请求，处理的顺序为filter->controller->metaObjectHandler
 * ps:为啥顺序是这样的？你总不能执行完了再拦截把，又或者先填充在执行，那样就没数据了
 * 在对应顺序类中所使用到的方法就是同一条线程（线程会去使用方法）
 *
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();


    public static void setCurrentId(Long id) {

        threadLocal.set(id);

    }

    public static Long GetCurrentId() {

        return threadLocal.get();

    }
}



