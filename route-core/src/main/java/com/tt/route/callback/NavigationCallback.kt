package com.tt.route.callback


import com.tt.route.Postcard

/**
 * Author: 夏胜明
 * Date: 2018/4/25 0025
 * Email: xiasem@163.com
 * Description:
 */

interface NavigationCallback {

    /**
     * 找到跳转页面
     * @param postcard
     */
    fun onFound(postcard: Postcard)

    /**
     * 未找到
     * @param postcard
     */
    fun onLost(postcard: Postcard)

    /**
     * 成功跳转
     * @param postcard
     */
    fun onArrival(postcard: Postcard)

    /**
     * 中断了路由跳转
     * @author luoxiaohui
     * @createTime 2019-06-18 17:00
     */
    fun onInterrupt(throwable: Throwable)
}
