package com.tt.route.callback


import com.tt.route.Postcard

interface InterceptorCallback {

    /**
     * 未拦截，走正常流程
     * @author luoxiaohui
     * @createTime 2019-05-23 20:50
     */
    fun onNext(postcard: Postcard)

    /**
     * 拦截器拦截成功，中断流程
     * @author luoxiaohui
     * @createTime 2019-05-23 20:42
     */
    fun onInterrupt(interruptMsg: String)
}
