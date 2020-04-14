package com.xia.router_annotation.model;

import com.xia.router_annotation.Router;

import javax.lang.model.element.Element;

public class RouterMeta {


    public enum Type {
        ACTIVITY,
        ISERVICE
    }

    private Type type;
    /**
     * 节点(Activity)
     */
    private Element element;
    /**
     * 注解使用的类
     */
    private Class<?> destination;
    /**
     * 路由路径
     */
    private String path;
    //路由组
    private String group;

    public RouterMeta() {
    }

    public static RouterMeta build(Type type, Class<?> destination, String path, String
            group) {
        return new RouterMeta(type, null, destination, path, group);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public RouterMeta(Type type, Element element, Class<?> destination, String path, String group) {
        this.type = type;
        this.element = element;
        this.destination = destination;
        this.path = path;
        this.group = group;
    }

    public RouterMeta(Type type, Router router, Element element) {
        this(type, element, null, router.path(), router.group());
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
