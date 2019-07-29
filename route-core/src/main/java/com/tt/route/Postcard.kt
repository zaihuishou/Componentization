package com.tt.route

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.core.app.ActivityOptionsCompat

import com.tt.route.annotation.RouteMeta
import com.tt.route.callback.NavigationCallback
import com.tt.route.core.IService

import java.util.ArrayList


class Postcard @JvmOverloads constructor(path: String, group: String, bundle: Bundle? = null) :
    RouteMeta() {
    val extras: Bundle = bundle ?: Bundle()
     var flags = -1
    //新版风格
     var optionsBundle: Bundle? = null
    //老版
     var enterAnim: Int = 0
     var exitAnim: Int = 0

    //服务
    var service: IService? = null

    /**
     * Intent.FLAG_ACTIVITY**
     * @param flag
     * @return
     */
    fun withFlags(flag: Int): Postcard {
        this.flags = flag
        return this
    }

    /**
     * 跳转动画
     *
     * @param enterAnim
     * @param exitAnim
     * @return
     */
    fun withTransition(enterAnim: Int, exitAnim: Int): Postcard {
        this.enterAnim = enterAnim
        this.exitAnim = exitAnim
        return this
    }

    /**
     * 转场动画
     *
     * @param compat
     * @return
     */
    fun withOptionsCompat(compat: ActivityOptionsCompat?): Postcard {
        if (null != compat) {
            this.optionsBundle = compat.toBundle()
        }
        return this
    }

    fun withString(key: String?, value: String?): Postcard {
        extras.putString(key, value)
        return this
    }


    fun withBoolean(key: String?, value: Boolean): Postcard {
        extras.putBoolean(key, value)
        return this
    }


    fun withShort(key: String?, value: Short): Postcard {
        extras.putShort(key, value)
        return this
    }


    fun withInt(key: String?, value: Int): Postcard {
        extras.putInt(key, value)
        return this
    }


    fun withLong(key: String?, value: Long): Postcard {
        extras.putLong(key, value)
        return this
    }


    fun withDouble(key: String?, value: Double): Postcard {
        extras.putDouble(key, value)
        return this
    }


    fun withByte(key: String?, value: Byte): Postcard {
        extras.putByte(key, value)
        return this
    }


    fun withChar(key: String?, value: Char): Postcard {
        extras.putChar(key, value)
        return this
    }


    fun withFloat(key: String?, value: Float): Postcard {
        extras.putFloat(key, value)
        return this
    }


    fun withParcelable(key: String?, value: Parcelable?): Postcard {
        extras.putParcelable(key, value)
        return this
    }


    fun withStringArray(key: String?, value: Array<String>?): Postcard {
        extras.putStringArray(key, value)
        return this
    }


    fun withBooleanArray(key: String?, value: BooleanArray): Postcard {
        extras.putBooleanArray(key, value)
        return this
    }


    fun withShortArray(key: String?, value: ShortArray): Postcard {
        extras.putShortArray(key, value)
        return this
    }


    fun withIntArray(key: String?, value: IntArray): Postcard {
        extras.putIntArray(key, value)
        return this
    }


    fun withLongArray(key: String?, value: LongArray): Postcard {
        extras.putLongArray(key, value)
        return this
    }


    fun withDoubleArray(key: String?, value: DoubleArray): Postcard {
        extras.putDoubleArray(key, value)
        return this
    }


    fun withByteArray(key: String?, value: ByteArray): Postcard {
        extras.putByteArray(key, value)
        return this
    }


    fun withCharArray(key: String?, value: CharArray): Postcard {
        extras.putCharArray(key, value)
        return this
    }


    fun withFloatArray(key: String?, value: FloatArray): Postcard {
        extras.putFloatArray(key, value)
        return this
    }


    fun withParcelableArray(key: String?, value: Array<Parcelable>?): Postcard {
        extras.putParcelableArray(key, value)
        return this
    }

    fun withParcelableArrayList(key: String?, value: ArrayList<out Parcelable>?): Postcard {
        extras.putParcelableArrayList(key, value)
        return this
    }

    fun withIntegerArrayList(key: String?, value: ArrayList<Int>?): Postcard {
        extras.putIntegerArrayList(key, value)
        return this
    }

    fun withStringArrayList(key: String?, value: ArrayList<String>?): Postcard {
        extras.putStringArrayList(key, value)
        return this
    }

    fun navigation(): Any? {
        return EasyRouter.instance().navigation(null, this, -1, null)
    }

    fun navigation(context: Context): Any? {
        return EasyRouter.instance().navigation(context, this, -1, null)
    }


    fun navigation(context: Context, callback: NavigationCallback): Any? {
        return EasyRouter.instance().navigation(context, this, -1, callback)
    }

    fun navigation(context: Context, requestCode: Int): Any? {
        return EasyRouter.instance().navigation(context, this, requestCode, null)
    }

    fun navigation(context: Context, requestCode: Int, callback: NavigationCallback): Any? {
        return EasyRouter.instance().navigation(context, this, requestCode, callback)
    }


}
