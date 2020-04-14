package com.xia.router_compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xia.router_annotation.Router;
import com.xia.router_annotation.model.RouterMeta;
import com.xia.router_compiler.utils.Constants;
import com.xia.router_compiler.utils.Log;
import com.xia.router_compiler.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@SupportedAnnotationTypes(Constants.ANN_TYPE_ROUTE)
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


    /**
     * key:组名 value：对应的路由信息
     */
    private Map<String, List<RouterMeta>> mGroupMap = new HashMap<>();

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
            TypeElement typeElement = elementUtils.getTypeElement(Constants.ACTIVITY);
            //节点的描述信息
            TypeMirror type_activity = typeElement.asType();
            Router router = element.getAnnotation(Router.class);
            //判断注解使用在什么类上
            RouterMeta routerMeta;
            if (typeUtils.isSubtype(typeMirror, type_activity)) {
                //是Activity
                routerMeta = new RouterMeta(RouterMeta.Type.ACTIVITY, router, element);

            } else {
                throw new RuntimeException("Just Support Activity/Service " + element);
            }
            log.i("RouterMeta Info :" +routerMeta.getPath());
            //路由信息的记录
            categories(routerMeta);
            //生成类需要实现的接口
            TypeElement iRouteGroup = elementUtils.getTypeElement(Constants.IROUTE_GROUP);
            //生成group类
            generatedGroup(iRouteGroup);
        }
    }

    /**
     * 生成group类
     *
     * @param iRouteGroup
     */
    private void generatedGroup(TypeElement iRouteGroup) {
        //获取方法,创建方法的参数类型  Map<String,RouteMeta>
        ParameterizedTypeName parameterizedType = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterMeta.class));
        //创建参数名字 Map<String,RouteMeta>  parameterizedType
        ParameterSpec spec = ParameterSpec.builder(parameterizedType, "parameterizedType").build();
        //遍历分组,没一个分组创建一个类
        for (Map.Entry<String, List<RouterMeta>> stringListEntry : mGroupMap.entrySet()) {
            //方法public void loadInto(Map<String,RouteMeta>  parameterizedType){}
            MethodSpec.Builder builder = MethodSpec.methodBuilder(Constants.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addParameter(spec);

            String groupName = stringListEntry.getKey();
            List<RouterMeta> groupData = stringListEntry.getValue();
            for (RouterMeta routerMeta : groupData) {
                //添加代码块
                builder.addStatement("parameterizedType.put($S,$T.build($T.$L,$T.class,$S,$S)",
                        routerMeta.getPath(),
                        ClassName.get(RouterMeta.class),
                        ClassName.get(RouterMeta.Type.class),
                        routerMeta.getType(),
                        ClassName.get((TypeElement) routerMeta.getElement()),
                        routerMeta.getPath().toLowerCase(),
                        routerMeta.getGroup().toLowerCase());
            }
            //创建java类
            String groupClassName = Constants.NAME_OF_GROUP + groupName;
            try {
                JavaFile.builder(Constants.PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupClassName)
                                .addSuperinterface(ClassName.get(iRouteGroup))
                                .addModifiers(Modifier.PUBLIC)
                                .addMethod(builder.build()).build()).build().writeTo(mFilerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 路由信息的记录
     *
     * @param routerMeta
     */
    private void categories(RouterMeta routerMeta) {
        //检测路由的地址是否正确
        if (routeVerify(routerMeta)) {
            //判断当前routeMeta是否在分组集合中
            List<RouterMeta> routerMetas = mGroupMap.get(routerMeta.getGroup());
            if (Utils.isEmpty(routerMetas)) {
                //没有分组，就添加到分组集合中
                List<RouterMeta> routeMetaList = new ArrayList<>();
                routeMetaList.add(routerMeta);
                mGroupMap.put(routerMeta.getGroup(), routeMetaList);
            } else {
                //如果已经有group了，不添加到分组集合中，直接更新分组集合的信息
                routerMetas.add(routerMeta);
            }
        } else {
            log.i("Group Info Error :" + routerMeta.getPath());
        }
    }

    /**
     * 检验路由地址
     */

    private boolean routeVerify(RouterMeta routerMeta) {
        String path = routerMeta.getPath();
        String group = routerMeta.getGroup();
        if (Utils.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }
        //如果没有写group，就从path 中截取group
        if (Utils.isEmpty(group)) {
            String substring = path.substring(1, path.indexOf("/", 1));
            //如果中间的是空格，返回false
            if (Utils.isEmpty(substring)) {
                return false;
            }
            //设置group
            routerMeta.setGroup(substring);
            return true;
        }

        return false;
    }
}
