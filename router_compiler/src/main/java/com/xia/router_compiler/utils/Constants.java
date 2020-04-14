package com.xia.router_compiler.utils;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2019/12/6/006 17:59
 * desc : 配置常量
 **/
public class Constants {
    public static final String MODULE_NAME = "module name";
    public static final String ANN_TYPE_ROUTE ="com.xia.router_annotation.Router";
    public static final String ACTIVITY ="android.app.Activity";
    public static final String ANN_TYPE_EXTRA ="com.xia.router_annotation.Extra";

    public static final String IROUTE_GROUP = "com.xia.router_core.template.IRouteGroup";
    public static final String IROUTE_ROOT = "com.xia.router_core.template.IRouteRoot";
    public static final String IEXTRA = "com.xia.router_core.template.IExtra";

    public static final String METHOD_LOAD_INTO = "loadInto";
    public static final String METHOD_LOAD_EXTRA = "loadExtra";

    public static final String ISERVICE = "com.xia.router_core.template.IService";


    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String STRING = LANG + ".String";
    public static final String ARRAY = "ARRAY";

    public static final String BYTEARRAY = "byte[]";
    public static final String SHORTARRAY = "short[]";
    public static final String BOOLEANARRAY = "boolean[]";
    public static final String CHARARRAY = "char[]";
    public static final String DOUBLEARRAY = "double[]";
    public static final String FLOATARRAY = "float[]";
    public static final String INTARRAY = "int[]";
    public static final String LONGARRAY = "long[]";
    public static final String STRINGARRAY = "java.lang.String[]";

    public static final String ARRAYLIST = "java.util.ArrayList";
    public static final String LIST = "java.util.List";


    public static final String PARCELABLE = "android.os.Parcelable";


    public static final String SEPARATOR = "$$";
    public static final String PROJECT = "XCompant";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root" + SEPARATOR;
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;
    public static final String NAME_OF_EXTRA = SEPARATOR + "Extra";

    public static final String PACKAGE_OF_GENERATE_FILE = "com.xia.router_core";
}
