package com.tt.route.core

/**
 * @author: luoxiaohui
 * @date: 2019-05-23 20:36
 * @desc:
 */
interface IInterceptorGroup {

    /**
     * key为拦截器的优先级，value为拦截器
     * @author luoxiaohui
     * @createTime 2019-05-23 20:54
     * @param map
     */
    fun loadInto(map: Map<Int, Class<out IInterceptor>>)
}
