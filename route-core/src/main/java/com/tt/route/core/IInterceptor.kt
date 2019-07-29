package com.tt.route.core

import android.content.Context
import com.tt.route.Postcard
import com.tt.route.callback.InterceptorCallback


/**
 * @author: luoxiaohui
 * @date: 2019-05-23 20:52
 * @desc:
 */
interface IInterceptor {

    /**
     * 拦截器流程
     * @author luoxiaohui
     * @createTime 2019-05-23 20:53
     */
    fun process(postcard: Postcard, callback: InterceptorCallback)

    /**
     * 在调用EasyRouter.init()初始化时，会调用到此方法
     * @author luoxiaohui
     * @createTime 2019-06-18 10:39
     */
    fun init(context: Context)
}
