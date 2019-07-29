/**
 *
 *@author tanzhiqiang
 */

package com.tt.route.annotation

annotation class Interceptor(
    /**
     * 优先级
     */
    val priority: Int,
    /**
     * 拦截器名称
     */
    val name: String = ""
)