package com.tt.route.utils

import java.util.TreeMap

/**
 * @author: luoxiaohui
 * @date: 2019-06-17 15:38
 * @desc: 主要用于拦截器优先级
 */
class UniqueKeyTreeMap<K, V> : TreeMap<K, V>() {

    override fun put(key: K, value: V): V? {
        return if (containsKey(key)) {

            throw RuntimeException("优先级为" + key + "的拦截器已经存在，不允许再次添加同级别的拦截器！")
        } else {

            super.put(key, value)
        }
    }
}
