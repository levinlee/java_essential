package com.levinston.exec.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class SellTest {

    public static void main(String[] args) {

        MaoTai maoTai = new MaoTai();
        WuLiangYe wuLiangYe = new WuLiangYe();

        InvocationHandler goods_01 = new CounterA(maoTai);
        InvocationHandler goods_02 = new CounterA(wuLiangYe);

        /* 并没有创建代理类(Proxy),但仍然通过调用MaoTai的sellWine()方法， 实现了茅台酒的销售
         *
         * Proxy 动态产生的代理会调用 InvocationHandler 实现类，所以 InvocationHandler 是实际执行者 - 在这里就是CounterA
         * CounterA 实现了InvocationHandler 的invoke方法。
         *
         */

        SellWine dynamicProxy_01 = (SellWine) Proxy.newProxyInstance(MaoTai.class.getClassLoader(), MaoTai.class.getInterfaces(), goods_01);
        // FIXME： 这里用MaoTai的classLoader 和 Interfaces 但仍调用WuLiangYe的sellWine()
        SellWine dynamicProxy_02 = (SellWine) Proxy.newProxyInstance(MaoTai.class.getClassLoader(), MaoTai.class.getInterfaces(), goods_02);

        dynamicProxy_01.sellWine();
        dynamicProxy_02.sellWine();

        FuRongWang fuRongWang = new FuRongWang();

        InvocationHandler goods_03 = new CounterA(fuRongWang);

        // TODO : 同样是通过 Proxy.newProxyInstance() 方法，却产生了 SellWine 和 SellCigarette 两种接口的实现类代理，这就是动态代理的魔力
        SellCigarette dynamicProxy_03 = (SellCigarette) Proxy.newProxyInstance(FuRongWang.class.getClassLoader(), FuRongWang.class.getInterfaces(), goods_03);

        dynamicProxy_03.sellCigarette();

        //动态生成的代理类的名字是: 包名+$Proxy+id序号。
        System.out.println("dynamicProxy_01 class name:" + dynamicProxy_01.getClass().getName());
        System.out.println("dynamicProxy_02 class name:" + dynamicProxy_02.getClass().getName());
        System.out.println("dynamicProxy_03 class name:" + dynamicProxy_03.getClass().getName());


    }
}
