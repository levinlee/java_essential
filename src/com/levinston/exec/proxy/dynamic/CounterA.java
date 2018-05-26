package com.levinston.exec.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CounterA implements InvocationHandler {

    private Object brand;

    public CounterA(Object brand) {
        this.brand = brand;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("销售开始 柜台 ： " + this.getClass().getSimpleName());
        method.invoke(brand, args);
        System.out.println("销售结束");
        return null;
    }
}
