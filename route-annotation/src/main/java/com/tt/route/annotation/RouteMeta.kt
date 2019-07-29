/**
 *
 *@author tanzhiqiang
 */

package com.tt.route.annotation

import javax.lang.model.element.Element

open class RouteMeta {

    var type: Int = ROUTE_TYPE_ACTIVITY

    /**
     * 节点（Activity）
     */
    var element: Element? = null
    /**
     * 注解使用的类对象
     */
    var destination: Class<*>? = null

    /**
     * 路由地址
     */
    var path: String? = null

    /**
     * 路由组
     */
    var group: String? = null

//    enum class Type {
//        ACTIVITY, SERVICE
//    }

    constructor() {}
    constructor(type: Int, route: Route, element: Element) : this(type, element, null, route.path, route.group)
    constructor(type: Int, element: Element?, destination: Class<*>?, path: String, group: String) {
        this.type = type
        this.destination = destination
        this.element = element
        this.path = path
        this.group = group
    }

    companion object {
        const val ROUTE_TYPE_ACTIVITY = 1
        const val ROUTE_TYPE_SERVICE= 2
        fun build(type: Int, destination: Class<*>, path: String, group: String): RouteMeta {
            return RouteMeta(type, null, destination, path, group)
        }
    }
}
