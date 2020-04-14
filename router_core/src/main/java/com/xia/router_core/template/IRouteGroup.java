package com.xia.router_core.template;



import com.xia.router_annotation.model.RouterMeta;

import java.util.Map;

public interface IRouteGroup {

    void loadInto(Map<String, RouterMeta> atlas);
}
