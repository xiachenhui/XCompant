package com.xia.router_compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.xia.router_annotation.Router;
import com.xia.router_annotation.model.RouterMeta;
import com.xia.router_compiler.utils.Contants;
import com.xia.router_compiler.utils.Log;
import com.xia.router_compiler.utils.Utils;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2019/12/6/006 16:11
 * desc : 自定义注解处理器 ,需添加依赖auto-service
 * 添加AutoService 注解，是为了自动生成META-INF\services\java.lang.Process
 **/
@AutoService(Processor.class)
//指定SDK的版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
//注册给哪些注解
@SupportedAnnotationTypes(Contants.ANN_TYPE_ROUTE)
public class RouterProcessor extends AbstractProcessor {
    private static final String TAG = RouterProcessor.class.getSimpleName();
    //文件生成器
    private Filer mFilerUtils;
    /**
     * 日志工具
     */
    private Log log;
    /**
     * 类工具
     */
    private Types typeUtils;
    /**
     * 节点工具
     */
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        log = Log.newLog(processingEnvironment.getMessager());
        mFilerUtils = processingEnvironment.getFiler();
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
    }

    /**
     * 返回true 表示后续不再处理
     *
     * @param set
     * @param roundEnvironment 当前或者之前运行的环境
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        /*MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet! xia chen hui  ")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();

        try {
            javaFile.writeTo(mFilerUtils);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //不为空，就进行处理注解
        if (!Utils.isEmpty(set)) {
            //获取所有被Router注解的元素集合
            Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Router.class);
            //处理Router注解
            if (!Utils.isEmpty(elementsAnnotatedWith)) {
                log.i(TAG + "--" + elementsAnnotatedWith.size());
                parseRoutes(elementsAnnotatedWith);
            }
            return true;
        }

        return false;
    }

    private void parseRoutes(Set<? extends Element> elementsAnnotatedWith) {
        for (Element element : elementsAnnotatedWith) {
            //获取使用注解的类的信息(Activity/Service)
            TypeMirror typeMirror = element.asType();
            //通过节点工具传入全类名，获取节点
            TypeElement typeElement = elementUtils.getTypeElement(Contants.ACTIVITY);
            //节点的描述信息
            TypeMirror type_activity = typeElement.asType();
            Router router = element.getAnnotation(Router.class);
            //判断注解使用在什么类上
            RouterMeta routerMeta;
            if (typeUtils.isSameType(typeMirror, type_activity)) {
                //是Activity
                routerMeta = new RouterMeta(RouterMeta.Type.ACTIVITY, router, element);

            } else {
                throw new RuntimeException("Just Support Activity/Service " + element);
            }
        }
    }
}
