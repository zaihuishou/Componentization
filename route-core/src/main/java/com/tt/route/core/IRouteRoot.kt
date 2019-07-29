package com.tt.route.core

interface IRouteRoot {
    fun loadInfo(data: MutableMap<String,Class<out IRouteGroup>>)
}