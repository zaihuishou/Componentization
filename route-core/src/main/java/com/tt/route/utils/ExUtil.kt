package com.tt.route.utils

fun String?.isEmpty(): Boolean {
    return this == null || this == "" || this.isEmpty()
}

fun Collection<*>?.isMEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun Map<*, *>?.isEmpty(): Boolean {
    return this == null || this.isEmpty()
}