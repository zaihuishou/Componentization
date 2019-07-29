/**
 *
 *@author tanzhiqiang
 */

package com.tt.route

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.tt.route.annotation.RouteMeta
import com.tt.route.annotation.RouteMeta.Companion.ROUTE_TYPE_ACTIVITY
import com.tt.route.annotation.RouteMeta.Companion.ROUTE_TYPE_SERVICE
import com.tt.route.exception.NoRouteFoundException
import com.tt.route.callback.InterceptorCallback
import com.tt.route.callback.NavigationCallback
import com.tt.route.core.IInterceptorGroup
import com.tt.route.core.IRouteGroup
import com.tt.route.core.IRouteRoot
import com.tt.route.core.IService
import com.tt.route.imps.InterceptorImpl
import com.tt.route.utils.ClassUtils
import java.lang.reflect.InvocationTargetException

class EasyRouter {
    private val routeRootPackage = "org.tt.route"
    private val sdkName = "Route"
    private val separator = "_"
    private val suffixRoot = "Root"
    private val suffixInterceptor = "Interceptor"
    private lateinit var mContext: Application
    private lateinit var mHandler: Handler

    private fun EasyRouter() {
        mHandler = Handler(Looper.getMainLooper())
    }

    companion object {
        fun instance() = Holder.instance
    }

    object Holder {
        val instance = EasyRouter()
    }


    fun initRouter(app: Application) {
        this.mContext = app

        try {
            loadInfo()
            InterceptorImpl.init(app.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "初始化失败!", e)
        }

    }
    /**
     * 分组表制作
     */
    @Throws(
        PackageManager.NameNotFoundException::class,
        InterruptedException::class,
        ClassNotFoundException::class,
        NoSuchMethodException::class,
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    private fun loadInfo() {
        //获得所有 apt生成的路由类的全类名 (路由表)
        val routerMap = ClassUtils.getFileNameByPackageName(mContext, routeRootPackage)
        for (className in routerMap) {
            if (className.startsWith("$routeRootPackage.$sdkName$separator$suffixRoot")) {
                //root中注册的是分组信息 将分组信息加入仓库中
                (Class.forName(className).getConstructor().newInstance() as IRouteRoot).loadInfo(
                    Warehouse.groupsIndex
                )
            } else if (className.startsWith("$routeRootPackage.$sdkName$separator$suffixInterceptor")) {

                (Class.forName(className).getConstructor().newInstance() as IInterceptorGroup).loadInto(
                    Warehouse.interceptorsIndex
                )
            }
        }

        for (stringClassEntry in Warehouse.groupsIndex.entries) {
            Log.d(TAG, "Root映射表[ " + stringClassEntry.key + " : " + stringClassEntry.value + "]")
        }

    }


    fun navigation(
        context: Context?,
        postcard: Postcard,
        requestCode: Int,
        callback: NavigationCallback?
    ): Any? {

        if (callback != null) {

            InterceptorImpl.onInterceptions(postcard, object : InterceptorCallback {

                override fun onInterrupt(interruptMsg: String) {
                    callback.onInterrupt(Throwable(interruptMsg))
                }

                override fun onNext(postcard: Postcard) {
                    _navigation(context, postcard, requestCode, callback)
                }

            })
        } else {

            return _navigation(context, postcard, requestCode, callback)
        }

        return null
    }

    private fun _navigation(
        context: Context?,
        postcard: Postcard,
        requestCode: Int,
        callback: NavigationCallback?
    ): Any? {
        try {
            prepareCard(postcard)
        } catch (e: NoRouteFoundException) {
            e.printStackTrace()
            //没找到
            callback?.onLost(postcard)
            return null
        }

        callback?.onFound(postcard)

        when (postcard.type) {
            ROUTE_TYPE_ACTIVITY -> {
                val currentContext = context ?: mContext
                val intent = Intent(currentContext, postcard.destination)
                intent.putExtras(postcard.extras)
                if (-1 != postcard.flags) {
                    intent.flags = postcard.flags
                } else if (currentContext !is Activity) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                mHandler.post(Runnable {
                    //可能需要返回码
                    if (requestCode > 0) {
                        ActivityCompat.startActivityForResult(
                            currentContext as Activity, intent,
                            requestCode, postcard.optionsBundle
                        )
                    } else {
                        ActivityCompat.startActivity(
                            currentContext, intent, postcard.optionsBundle
                        )
                    }

                    if ((0 != postcard.enterAnim || 0 != postcard.exitAnim) && currentContext is Activity) {
                        //老版本
                        currentContext.overridePendingTransition(
                            postcard.enterAnim, postcard.exitAnim
                        )
                    }
                    //跳转完成
                    callback?.onArrival(postcard)
                })
            }
            ROUTE_TYPE_SERVICE -> return postcard.service
            else -> {
            }
        }
        return null
    }

    /**
     * 准备卡片
     *
     * @param card
     */
    private fun prepareCard(card: Postcard) {
        val routeMeta = Warehouse.routes[card.path]
        if (null == routeMeta) {
            val groupMeta = Warehouse.groupsIndex[card.group]
                ?: throw NoRouteFoundException("没找到对应路由：分组=" + card.group + "   路径=" + card.path)
            val iGroupInstance: IRouteGroup
            try {
                iGroupInstance = groupMeta.getConstructor().newInstance()
            } catch (e: Exception) {
                throw RuntimeException("路由分组映射表记录失败.", e)
            }

            iGroupInstance.loadInfo(Warehouse.routes)
            //已经准备过了就可以移除了 (不会一直存在内存中)
            Warehouse.groupsIndex.remove(card.group)
            //再次进入 else
            prepareCard(card)
        } else {
            //类 要跳转的activity 或IService实现类
            card.destination = routeMeta.destination
            card.type = routeMeta.type
            if (routeMeta.type == RouteMeta.ROUTE_TYPE_SERVICE) {
                val destination = routeMeta.destination
                var service = Warehouse.services[destination]
                if (null == service) {
                    try {
                        service = destination!!::class.java.getConstructor().newInstance() as IService
                        Warehouse.services[destination] = service
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                card.service = service
            }
        }
    }

    /**
     * 注入
     *
     * @param instance
     */
    fun inject(instance: Activity) {
        ExtraManager.getInstance().loadExtras(instance)
    }
}