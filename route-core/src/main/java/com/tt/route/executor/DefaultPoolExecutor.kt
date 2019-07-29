package com.tt.route.executor

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Author: 夏胜明
 * Date: 2018/4/3 0003
 * Email: xiasem@163.com
 * Description:
 */

object DefaultPoolExecutor {

    lateinit var executor: ThreadPoolExecutor
    private val sThreadFactory = object : ThreadFactory {
        private val mCount = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            return Thread(r, "EasyRouter #" + mCount.getAndIncrement())
        }
    }

    //核心线程和最大线程都是cpu核心数+1
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val MAX_CORE_POOL_SIZE = CPU_COUNT + 1
    //存活30秒 回收线程
    private const val SURPLUS_THREAD_LIFE = 30L

    fun newDefaultPoolExecutor(corePoolSize: Int): ThreadPoolExecutor? {
        if (corePoolSize == 0) {
            return null
        }
        val newCorePoolSize = Math.min(corePoolSize, MAX_CORE_POOL_SIZE)
        executor = ThreadPoolExecutor(
            newCorePoolSize,
            newCorePoolSize,
            SURPLUS_THREAD_LIFE,
            TimeUnit.SECONDS,
            ArrayBlockingQueue(64),
            sThreadFactory
        )
        //核心线程也会被销毁
        executor.allowCoreThreadTimeOut(true)
        return executor
    }


}

























