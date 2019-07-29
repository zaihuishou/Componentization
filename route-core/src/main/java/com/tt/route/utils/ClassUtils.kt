package com.tt.route.utils

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import com.tt.route.executor.DefaultPoolExecutor


import java.io.IOException
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import java.util.concurrent.CountDownLatch

import dalvik.system.DexFile

/**
 * Author: 夏胜明
 * Date: 2018/4/3 0003
 * Email: xiasem@163.com
 * Description:
 */

object ClassUtils {

    /**
     * 获得程序所有的apk(instant run会产生很多split apk)
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    @Throws(PackageManager.NameNotFoundException::class)
    private fun getSourcePaths(context: Context): List<String> {
        val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        val sourcePaths = ArrayList<String>()
        sourcePaths.add(applicationInfo.sourceDir)
        //instant run
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null != applicationInfo.splitSourceDirs) {
                sourcePaths.addAll(Arrays.asList(*applicationInfo.splitSourceDirs))
            }
        }
        return sourcePaths
    }

    /**
     * 得到路由表的类名
     * @param context
     * @param packageName
     * @return
     * @throws PackageManager.NameNotFoundException
     * @throws InterruptedException
     */
    @Throws(PackageManager.NameNotFoundException::class, InterruptedException::class)
    fun getFileNameByPackageName(context: Application, packageName: String): Set<String> {
        val classNames = HashSet<String>()
        val paths = getSourcePaths(context)
        //使用同步计数器判断均处理完成
        val countDownLatch = CountDownLatch(paths.size)
        val threadPoolExecutor = DefaultPoolExecutor.newDefaultPoolExecutor(paths.size)
        for (path in paths) {
            threadPoolExecutor?.execute {
                var dexFile: DexFile? = null
                try {
                    //加载 apk中的dex 并遍历 获得所有包名为 {packageName} 的类
                    dexFile = DexFile(path)
                    val dexEntries = dexFile.entries()
                    while (dexEntries.hasMoreElements()) {
                        val className = dexEntries.nextElement()
                        if (!TextUtils.isEmpty(className) && className.startsWith(packageName)) {
                            classNames.add(className)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (null != dexFile) {
                        try {
                            dexFile.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                    //释放一个
                    countDownLatch.countDown()
                }
            }
        }
        //等待执行完成
        countDownLatch.await()
        return classNames
    }


}
