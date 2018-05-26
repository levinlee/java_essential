package com.levinston.exec.reflection;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

@Retention(RetentionPolicy.RUNTIME) // 保持在运行时,才可以通过反射获取
@interface MyAnn {
    String name() default "张三";
    int age() default 30;
    //TODO : Java注解和注解类
}

// 创建Person类，为完成反射提供一个简单的类
class Person {

    public String gender;
    public int height;
    public double weight;

    private void privateTest() {
        System.out.println("Person.privateTest()");
    }
}

// 创建Teacher类继承Person类
@MyAnn
public class Teacher extends Person implements Serializable, Comparable {

    private static int counter = 100;

    public static void test() {
        System.out.println("static test()");
    }

    public String name;
    private int age;
    private double salary;

    public Teacher() {
    }

    private Teacher(String name, int age, double salary) {
        super();
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    public void eat(String something) {
        System.out.println("老师在吃" + something);
    }

    public void eat(String something, int time) {
        System.out.println("老师在吃" + something + "吃了" + time + "小时");
    }

    @MyAnn(name="李四", age=40)
    private static final String concat(int i, double d, char c, boolean b) {
        return "" + i + d + c + b;
    }

    @Override
    public String toString() {
        return "Teacher [name=" + name + ", age=" + age + ", salary=" + salary + "]";
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
