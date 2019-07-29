package com.tt.route

import com.tt.route.annotation.RouteMeta
import com.tt.route.core.IInterceptor
import com.tt.route.core.IRouteGroup
import com.tt.route.core.IService
import com.tt.route.utils.UniqueKeyTreeMap

object Warehouse {

    // root 映射表 保存分组信息
     var groupsIndex: MutableMap<String, Class<out IRouteGroup>> = HashMap()

    // group 映射表 保存组中的所有数据
    internal var routes: MutableMap<String, RouteMeta> = HashMap()

    // group 映射表 保存组中的所有数据
    internal var services: MutableMap<Class<*>, IService> = HashMap()
    // TestServiceImpl.class , TestServiceImpl 没有再反射

    /**
     * 以键值对优先级的方式保存拦截器对象
     */
    var interceptorsIndex: Map<Int, Class<out IInterceptor>> = UniqueKeyTreeMap()
    /**
     * 以集合的方式保存所有拦截器对象
     */
    var interceptors: ArrayList<IInterceptor> = ArrayList()
}
