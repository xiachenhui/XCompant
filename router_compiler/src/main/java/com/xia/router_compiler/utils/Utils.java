package com.xia.router_compiler.utils;

import java.util.Collection;
import java.util.Map;

public class Utils {

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length()==0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
