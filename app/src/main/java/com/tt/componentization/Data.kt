/**
 *
 *@author tanzhiqiang
 */

package com.tt.componentization

import android.os.Parcel
import android.os.Parcelable

data class Data(val name: String) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Data> = object : Parcelable.Creator<Data> {
            override fun createFromParcel(source: Parcel): Data = Data(source)
            override fun newArray(size: Int): Array<Data?> = arrayOfNulls(size)
        }
    }
}