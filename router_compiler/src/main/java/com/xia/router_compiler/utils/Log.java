package com.xia.router_compiler.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2019/12/6/006 20:06
 * desc : 注解处理器中无法使用Log
 **/
public class Log {
    private Messager messager;

    private Log(Messager messager) {
        this.messager = messager;
    }
    public static Log newLog(Messager messager){
        return new Log(messager);
    }
    public void i(String msg){
        messager.printMessage(Diagnostic.Kind.NOTE,msg);
    }
}
