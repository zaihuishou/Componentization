package com.tt.route.annotation


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Route (val path:String,val group:String ="")