package com.tt.route.utils

import java.util.concurrent.CountDownLatch

class CancelableCountDownLatch(count: Int) : CountDownLatch(count) {

    var msg = ""
        private set

    /**
     * 当遇到特殊情况时，需要将计步器清0
     *
     * @author luoxiaohui
     * @createTime 2019-06-18 14:47
     */
    fun cancel(msg: String) {
        this.msg = msg
        while (count > 0) {
            countDown()
        }
    }
}
