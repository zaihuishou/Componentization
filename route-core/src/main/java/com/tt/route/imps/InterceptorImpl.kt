package com.tt.route.imps

import android.content.Context
import com.tt.route.Postcard
import com.tt.route.Warehouse
import com.tt.route.callback.InterceptorCallback

import com.tt.route.executor.DefaultPoolExecutor
import com.tt.route.utils.CancelableCountDownLatch
import com.tt.route.utils.isEmpty
import java.util.concurrent.TimeUnit

object InterceptorImpl {

    /**
     * 初始化路由时，需要轮询每个拦截器中的init()方法
     *
     */
    fun init(context: Context) {

        DefaultPoolExecutor.executor.execute {
            if (Warehouse.interceptorsIndex.isNotEmpty()) {

                for (entry in Warehouse.interceptorsIndex.entries) {
                    val interceptorClass = entry.value
                    try {
                        val iInterceptor = interceptorClass.getConstructor().newInstance()
                        iInterceptor.init(context)
                        Warehouse.interceptors.add(iInterceptor)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    /**
     * 执行拦截逻辑
     *
     * @author luoxiaohui
     * @createTime 2019-06-18 14:56
     */
    fun onInterceptions(postcard: Postcard, callback: InterceptorCallback) {

        if (Warehouse.interceptors.isNotEmpty()) {
            DefaultPoolExecutor.executor.execute {
                val countDownLatch = CancelableCountDownLatch(Warehouse.interceptors.size)
                execute(0, countDownLatch, postcard)
                try {
                    countDownLatch.await(300, TimeUnit.SECONDS)
                    if (countDownLatch.count > 0) {

                        callback.onInterrupt("拦截器处理超时")
                    } else if (countDownLatch.msg.isEmpty().not()) {

                        callback.onInterrupt(countDownLatch.msg)
                    } else {

                        callback.onNext(postcard)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        } else {

            callback.onNext(postcard)
        }
    }


    /**
     * 以递归的方式走完所有拦截器的process()方法
     *
     * @author luoxiaohui
     * @createTime 2019-06-18 15:22
     */
    private fun execute(index: Int, countDownLatch: CancelableCountDownLatch, postcard: Postcard) {
        if (index < Warehouse.interceptors.size) {

            val iInterceptor = Warehouse.interceptors.get(index)
            iInterceptor.process(postcard, object : InterceptorCallback {

                override fun onNext(postcard: Postcard) {
                    countDownLatch.countDown()
                    execute(index + 1, countDownLatch, postcard)
                }

                override fun onInterrupt(interruptMsg: String) {
                    countDownLatch.cancel(interruptMsg)
                }

            })
        }
    }
}
































