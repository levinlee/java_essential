package com.levinston.exec.reflection;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class ReflectTest {
    // 正常方式创建对象，调用方法
    @Test
    public void test1() {
        /*
        Teacher t1 = new Teacher(); // 由JVM自动 加载类, 再创建对象.
		t1.name = "小明";
		t1.age = 20; // FIXME : Error Here as age is private
		t1.salary = 1000; // FIXME : Error Here as salary is private
		System.out.println(t1.name);
		System.out.println(t1.age);
		System.out.println(t1.salary);
		t1.eat("回锅肉");
		System.out.println(t1);
		*/
    }

    // 通过反射获取类的属性，以及私有属性的获取
    @Test
    public void test2() {
        try {
            Class clazz = Class.forName("com.levinston.exec.reflection.Teacher");// 根据类的全限定名称加载类并创建类模板对象,
            // 加载类的过程就是由类加载器读取Teacher.class二进制文件,由类加载器生成Class对象
            // 类模板对象中包含了类中的属性的定义(Field对象)和方法的代码(Method对象)
            Object object = clazz.newInstance(); // 对象的创建由方法来完成, 调用类的无参构造器创建对象
            System.out.println(object);

            // ((Teacher)object).name = "小红";
            // Field ageField = clazz.getField("age"); // 只能获取公共的属性
            Field ageField = clazz.getDeclaredField("age"); // 只在在本类中声明了的属性, 就可以获取到!!!, 不受访问控制修饰
            ageField.setAccessible(true);
            ageField.set(object, 40);// 相当于object.age = 40;
            System.out.println(ageField.get(object)); // System.out.println(object.age);

            Field salaryField = clazz.getDeclaredField("salary");
            System.out.println(salaryField);
            salaryField.setAccessible(true); // 突破封装,实现私有成员的访问, 不set为true, 则报错: can not access a member of ... with modifiers private
            salaryField.set(object, 5000);
            System.out.println(salaryField.get(object));

            Field nameField = clazz.getDeclaredField("name");
            nameField.setAccessible(true); // 突破封装,实现私有成员的访问
            nameField.set(object, "小红");

            System.out.println(object);

        } catch (ClassNotFoundException e) { // 类名不正确, 根据类名找到.class文件
            e.printStackTrace();
        } catch (InstantiationException e) { // 构造器调用错误!!
            e.printStackTrace();
        } catch (IllegalAccessException e) { // 构造器封装
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // 通过反射获取类实现了哪些接口
    @Test
    public void test3() {
        try {
            Class clazz = Class.forName("com.levinston.exec.reflection.Teacher");
            Class superClazz = clazz.getSuperclass();
            System.out.println(superClazz);
            Class[] interfaceArr = clazz.getInterfaces();
            for (Class anInterfaceArr : interfaceArr) {
                System.out.println("实现接口:" + anInterfaceArr);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //  创建对象的4种方法
    @Test
    public void test4() {
        try {
            //1) 常规使用, 软编码
            Class clazz1 = Class.forName("com.levinston.exec.reflection.Teacher");

            //2) 最简单, 硬编码
            Class clazz2 = Teacher.class;

            System.out.println(clazz1 == clazz2);

            //3) 比较灵活, 硬编码
            Class clazz3 = new Teacher().getClass();
            System.out.println(clazz2 == clazz3);

            //4) 比较复杂, 先获取到类加载器对象,再手工加载类, 软编码
            ClassLoader classLoader = this.getClass().getClassLoader();
            Class clazz4 = classLoader.loadClass("com.levinston.exec.reflection.Teacher");
            System.out.println(clazz3 == clazz4);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 通过反射获取类加载器
    // TODO : 类加载器的不同
    @Test
    public void test5() {
        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(appClassLoader);

        ClassLoader extClassLoader = appClassLoader.getParent();
        System.out.println(extClassLoader);

        ClassLoader bootstapClassLoader = extClassLoader.getParent();
        System.out.println(bootstapClassLoader);

        System.out.println("" + Object.class.getClassLoader());// 引导类加载器加载核心类
        System.out.println("" + Person.class.getClassLoader());
    }

    @Test
    public void test6() throws IOException {
        // 通类加载器读取的资源文件的当前目录不是项目目录, 而是src目录
        // src目录就是项目的classpath中的
        // 可以直接从jar包中读取资源文件
        InputStream is_res = this.getClass().getClassLoader().getResourceAsStream("resource.properties");
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/sun/corba/se/impl/logging/LogStrings.properties");
        Properties properties = new Properties();
        Properties properties_res = new Properties();
        properties.load(is);
        properties_res.load(is_res);
        System.out.println(properties.getProperty("IOR.nullPoa"));
        System.out.println(properties_res.getProperty("com.levinston.exec.reflection"));
    }

    // 通过反射获取类的方法，并实现方法
    @Test
    public void test7() {
        try {
            Class clazz = Class.forName("com.levinston.exec.reflection.Teacher");
            Object object = clazz.newInstance();

            Field nameField = clazz.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(object, "小明");

            System.out.println(object);

            // 调用方法
            // 1) 从类模板对象中获取方法对象, getMethod会获取本类或者从父类继承的所有公共方式
            Method eatMethod1 = clazz.getMethod("eat", String.class);
            // 2) 通过方法对象间接调用自己
            Object retValue = eatMethod1.invoke(object, "肉包子"); // object.eat("肉包子");
            System.out.println("方法返回值:" + retValue);

            Method eatMethod2 = clazz.getMethod("eat", String.class, int.class);
            eatMethod2.invoke(object, "紫菜包饭", 5);

            Method toStringMethod = clazz.getMethod("toString");
            Object retValue2 = toStringMethod.invoke(object);
            System.out.println(retValue2);

            Method hashCodeMethod = clazz.getMethod("hashCode");//继承自父类的hashCode方法
            Object retValue3 = hashCodeMethod.invoke(object);
            System.out.println(retValue3);

            // 可以获取本类所有方法,包括私有方法
            Method concatMethod = clazz.getDeclaredMethod("concat", int.class, double.class, char.class, boolean.class);
            concatMethod.setAccessible(true);
            Object retValue4 = concatMethod.invoke(object, 100, 3.14, '你', true);
            System.out.println(retValue4);

            System.out.println("修饰符:" + Modifier.toString(concatMethod.getModifiers()));
            Annotation annotation = concatMethod.getAnnotation(Test.class); // 获取注解 TODO : ??
            System.out.println(annotation);

            // getDeclaredMethod, 只能获取本类中声明的方法,从父类继承的无法拿到
            //Method getClassMethod = clazz.getDeclaredMethod("getClass"); FIXME : getDeclaredMethod 无法获取父类的方法
            //System.out.println(getClassMethod);

            // 获取父类的私有方法,并通过子类对象来调用, 也是可以成功的, 说明子类继承父类的所有成员!!
            Method method = clazz.getSuperclass().getDeclaredMethod("privateTest");
            method.setAccessible(true);
            method.invoke(object);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // 通过反射获取构造器对象，从而完成对象的创建
    @Test
    public void test8() {
        try {
            Class clazz = Class.forName("com.levinston.exec.reflection.Teacher");
            // 使用其他构造器创建对象
            // 1) 先获取到构造器对象
            //Constructor constructor = clazz.getConstructor(String.class, int.class, double.class);
            Constructor constructor = clazz.getDeclaredConstructor(String.class, int.class, double.class);
            int modifiers = constructor.getModifiers(); // 获取修饰符
            Class[] typesClasses = constructor.getParameterTypes();
            for (Class typesClass : typesClasses) {
                System.out.println("构造器参数:" + typesClass);
            }
            System.out.println(Modifier.toString(modifiers));

            constructor.setAccessible(true);
            // 2) 间接调用构造器完成对象创建
            Object object = constructor.newInstance("立超", 19, 10000);
            System.out.println(object);

            Constructor runtimeConstructor = Runtime.class.getDeclaredConstructor();
            System.out.println(runtimeConstructor);
            runtimeConstructor.setAccessible(true);
            Object object2 = runtimeConstructor.newInstance();
            System.out.println(object2);
            System.out.println(Runtime.getRuntime());

            System.out.println(Object.class.getSuperclass());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // 通过反射获取私有方法，注解
    @Test
    public void test9() {
        try {
            Class clazz = Class.forName("com.levinston.exec.reflection.Teacher");
            Annotation classannotation = clazz.getAnnotation(MyAnn.class);
            System.out.println(classannotation);

            // 可以获取本类所有方法,包括私有方法
            Method concatMethod = clazz.getDeclaredMethod("concat", int.class, double.class, char.class, boolean.class);
            System.out.println("修饰符:" + Modifier.toString(concatMethod.getModifiers()));
            Annotation annotation = concatMethod.getAnnotation(MyAnn.class); // 获取注解
            System.out.println(annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 通过反射获取一个类的构造器，属性，方法
    @Test
    public void test10() throws Exception {
        String className = "java.util.HashMap";
        Class clazz = Class.forName(className);
        Constructor[] allConstructors = clazz.getDeclaredConstructors(); // 本类中声明的所有构造器
        for (int i = 0; i < allConstructors.length; i++) {
            System.out.println(allConstructors[i]);
        }
        System.out.println("----------------------");
        Field[] allFields = clazz.getDeclaredFields(); // 本类中声明的所有的属性
        for (int i = 0; i < allFields.length; i++) {
            System.out.println(allFields[i]);
        }
        Field[] allPublicFields = clazz.getFields(); // 获取本类及从父类继承的所有公共属性
        for (int i = 0; i < allPublicFields.length; i++) {
            System.out.println(allPublicFields[i]);
        }
        System.out.println("-----------------------------");
        Method[] allMethods = clazz.getDeclaredMethods(); // 本类声明的所有方法
        for (int i = 0; i < allMethods.length; i++) {
            System.out.println(allMethods[i]);
        }
        Method[] allPublicMethods = clazz.getMethods(); // 获取本类及从父类继承的所有公共方法
        for (int i = 0; i < allPublicMethods.length; i++) {
            System.out.println(allPublicMethods[i]);
        }
    }

    // 写一个方法printClassDetails(String className)
    // 先打印类名和父类(getSuperClass())及实现的接口(getInterfaces())
    // 在方法中打印类模板中的所有构造器,所有属性(包含从父类继承的公共属性), 所有方法(包含从父类继承的公共方法) 要求不要重复打印
    // 打印的时候注意一下缩进
    public void printClassDetails(String className) throws ClassNotFoundException {
        Class clazz = Class.forName(className);
        System.out.print("public class " + clazz.getSimpleName() + " extends " + clazz.getSuperclass().getSimpleName());
        System.out.print(" implements ");
        Class[] allInterface = clazz.getInterfaces();
        for (int i = 0; i < allInterface.length; i++) {
            System.out.print(allInterface[i].getSimpleName() + ",");
        }
        System.out.println("{");
        System.out.println();
        System.out.println("All Fields : ");
        Set<Field> allFields = new HashSet<Field>();
        for (Field field : clazz.getFields()) {
            allFields.add(field);
        }
        for (Field field : clazz.getDeclaredFields()) {
            allFields.add(field);
        }

        Iterator<Field> iterator = allFields.iterator();
        while (iterator.hasNext()) {
            System.out.println("\t" + iterator.next());
        }

        System.out.println();
        System.out.println("Declared Constructors : ");

        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            System.out.println("\t" + constructor);
        }
        System.out.println();
        System.out.println("All Methods : ");
        Set<Method> allMethods = new HashSet<Method>();
        for (Method method : clazz.getMethods()) {
            allMethods.add(method);
        }
        for (Method method : clazz.getDeclaredMethods()) {
            allMethods.add(method);
        }
        System.out.println();
        for (Method method : allMethods) {
            System.out.println("\t" + method);
        }
        System.out.println("}");
    }

    // 调用printClassDetails方法
    @Test
    public void exer1() {
        try {
            printClassDetails("java.io.ObjectOutputStream");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 静态属性、静态方法的访问
    @Test
    public void test11() {
        try {
            Class clazz = Class.forName("com.levinston.exec.reflection.Teacher");
            //Object object = clazz.newInstance();
            Field counterField = clazz.getDeclaredField("counter");
            System.out.println(counterField);
            counterField.setAccessible(true);
            System.out.println(counterField.get(null)); // 静态属性的访问不需要对象, 所以传入null也可以
            counterField.set(null, 10000); // 静态的设置也不需要对象
            System.out.println(counterField.get(null)); // 静态属性的访问不需要对象, 所以传入null也可以

            Method testMethod = clazz.getMethod("test");
            testMethod.invoke(null); //静态属性的调用也不需要对象， 所以传入null也可以

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 反射在自定义泛型中的应用
    // 通过反射获取子类的泛型类型->创建父类对象</span>
    @Test
    public void test12() throws InstantiationException, IllegalAccessException {
        Parent parent1 = new Child1();
        Parent parent2 = new Child2();
        //Parent parent3 = new Child3(); FIXME: 出错，java.lang.NoSuchMethodException: java.io.PrintStream.<init>()
    }

    // 上面test12测试所需要的类，其中父类为自定义泛型类，子类确定泛型的类型，在父类无参构造器中通过反射获取到子类的泛型
    // 类型从而确定父类的泛型类型，然后创建对象，从而实现对象的创建
    class Parent<T> {
        T t;
        Class clazz;

        public Parent() {
            Type type = this.getClass().getGenericSuperclass(); // TODO : getGenericSuperclass
            if (!(type instanceof ParameterizedType)) { // 在子类的声明中并没有任何的泛型信息
                clazz = Object.class;
            } else {
                ParameterizedType parameteredType = (ParameterizedType) type;
                Type[] types = parameteredType.getActualTypeArguments();
                clazz = (Class) types[0];
            }
            System.out.println(clazz);
            try {
                t = (T) clazz.newInstance();
                System.out.println(t);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public T getT() {
            return t;
        }
    }

    class Child1 extends Parent<Person> {
    }

    class Child2 extends Parent {
    }

    class Child3 extends Parent<PrintStream> {
    }
    // 反射可以应用在泛型类中，但是更加凸显出去其作用是在动态中

}
