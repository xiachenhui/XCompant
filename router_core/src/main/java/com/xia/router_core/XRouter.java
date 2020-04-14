package com.xia.router_core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;


import com.xia.router_annotation.model.RouterMeta;
import com.xia.router_core.callback.NavigationCallback;
import com.xia.router_core.exception.NoRouteFoundException;
import com.xia.router_core.template.IRouteGroup;
import com.xia.router_core.template.IRouteRoot;
import com.xia.router_core.template.IService;
import com.xia.router_core.utils.ClassUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static com.xia.router_annotation.model.RouterMeta.Type.ACTIVITY;


public class XRouter {
    private static final String TAG = "XRouter";
    private static final String ROUTE_ROOT_PAKCAGE = "com.dn_alan.router_core";
    private static final String SDK_NAME = "XRouter";
    private static final String SEPARATOR = "$$";
    private static final String SUFFIX_ROOT = "Root";
    private static Application mContext;

    private static XRouter instance;


    public static XRouter getInstance() {
        synchronized (XRouter.class) {
            if (instance == null) {
                instance = new XRouter();
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param application
     */
    public static void init(Application application) {
        mContext = application;
        try {
            loadInfo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "初始化失败!", e);
        }
    }


    /**
     * 分组表制作
     * @throws InterruptedException
     * @throws IOException
     * @throws PackageManager
    .NameNotFoundException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void loadInfo() throws InterruptedException, IOException, PackageManager
            .NameNotFoundException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        //获得所有 apt生成的路由类的全类名 (路由表)
        Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
        for (String className : routerMap) {
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR +
                    SUFFIX_ROOT)) {
                // root中注册的是分组信息 将分组信息加入仓库中
                ((IRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInto
                        (Warehouse.groupsIndex);
            }
        }
//        for (Map.Entry<String, Class<? extends IRouteGroup>> stringClassEntry : Warehouse
//                .groupsIndex.entrySet()) {
//            Log.e(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry
//                    .getValue() + "]");
//        }

    }


    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(path, extractGroup(path));
        }
    }

    //路由信息  和組名
    public Postcard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return new Postcard(path, group);
        }
    }


    /**
     * 获得path中的组名
     *
     * @param path
     * @return
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException(path + " : 不能提取group.");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new RuntimeException(path + " : 不能提取group.");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据跳卡跳转页面
     *
     * @param context
     * @param postcard
     * @param requestCode
     * @param callback
     */
    protected Object navigation(Context context, final Postcard postcard, final int requestCode,
                                final NavigationCallback callback) {
        try {
            prepareCard(postcard);
        } catch (NoRouteFoundException e) {
            e.printStackTrace();
            //没找到
            if (null != callback) {
                callback.onLost(postcard);
            }
            return null;
        }
        if (null != callback) {
            callback.onFound(postcard);
        }

        switch (postcard.getType()) {
            case ACTIVITY:
                final Context currentContext = null == context ? mContext : context;
                //postcard.getDestination = Module1Activity.class
                final Intent intent = new Intent(currentContext, postcard.getDestination());
                intent.putExtras(postcard.getExtras());
                int flags = postcard.getFlags();
                if (-1 != flags) {
                    intent.setFlags(flags);
                } else if (!(currentContext instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //可能需要返回码
                        if (requestCode > 0) {
                            ActivityCompat.startActivityForResult((Activity) currentContext, intent,
                                    requestCode, postcard.getOptionsBundle());
                        } else {
                            ActivityCompat.startActivity(currentContext, intent, postcard
                                    .getOptionsBundle());
                        }

                        if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) &&
                                currentContext instanceof Activity) {
                            //老版本
                            ((Activity) currentContext).overridePendingTransition(postcard
                                            .getEnterAnim()
                                    , postcard.getExitAnim());
                        }
                        //跳转完成
                        if (null != callback) {
                            callback.onArrival(postcard);
                        }
                    }
                });
                break;
            case ISERVICE:
                return postcard.getService();
            default:
                break;
        }
        return null;
    }

    /**
     * 准备卡片
     *
     * @param card
     */
    private void prepareCard(Postcard card) {
        RouterMeta routeMeta = Warehouse.routes.get(card.getPath());
        //还没准备的
        if (null == routeMeta) {
            //创建并调用 loadInto 函数,然后记录在仓库
            Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(card
                    .getGroup());
            if (null == groupMeta) {
                throw new NoRouteFoundException("没找到对应路由: " + card.getGroup() + " " +
                        card.getPath());
            }
            IRouteGroup iGroupInstance;
            try {
                iGroupInstance = groupMeta.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("路由分组映射表记录失败.", e);
            }
            iGroupInstance.loadInto(Warehouse.routes);
            //已经准备过了就可以移除了 (不会一直存在内存中)
            Warehouse.groupsIndex.remove(card.getGroup());
            //再次进入 else
            prepareCard(card);
        } else {
            //类 要跳转的activity 或IService实现类
            card.setDestination(routeMeta.getDestination());
            card.setType(routeMeta.getType());
            switch (routeMeta.getType()) {
                case ISERVICE:
                    //TestServiceImpl
                    Class<?> destination = routeMeta.getDestination();
                    IService service = Warehouse.services.get(destination);
                    if (null == service) {
                        try {
                            service = (IService) destination.getConstructor().newInstance();
                            Warehouse.services.put(destination, service);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    card.setService(service);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 注入
     *
     * @param instance
     */
    public void inject(Activity instance) {
        ExtraManager.getInstance().loadExtras(instance);
    }


}
