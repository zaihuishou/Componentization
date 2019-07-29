package com.tt.route.compiler.utils

fun String?.isMEmpty():Boolean= this == null || this.isEmpty()

fun Collection<Any>?.isEmpty():Boolean=this == null || this.isEmpty()
