/**
 *
 *@author tanzhiqiang
 */

package com.tt.route

import android.app.Activity
import androidx.collection.LruCache
import com.tt.route.core.IExtra

class ExtraManager private constructor() {

    private var classCache: LruCache<String, IExtra> = LruCache(66)
    private val suffixAutowired = "_Extra"

    companion object {
        fun getInstance() = ExtraManagerHolder.instance
    }

    private object ExtraManagerHolder {
        val instance = ExtraManager()
    }


    fun loadExtras(instance: Activity) {
        val className = instance.javaClass.name
        var extra = classCache.get(className)
        try {
            if (null == extra) {
                extra =
                    Class.forName("$className$suffixAutowired").getConstructor().newInstance() as IExtra
            }
            extra.loadExtra(instance)
            classCache.put(className, extra)
        } catch (e: Exception) {

        }
    }
}