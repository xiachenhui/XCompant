package com.xia.router_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//元注解 作用在类上
@Target(ElementType.TYPE)
//编译时
@Retention(RetentionPolicy.CLASS)
public @interface  Router {
    /**
     * 指定路由的路径
     * @return
     */
    String path();

    /**
     * 将路由节点进行分组，可以实现按组动态加载
     * @return
     */
    String group() default "";
}
