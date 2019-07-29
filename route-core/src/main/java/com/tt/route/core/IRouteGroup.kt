package com.tt.route.core

import com.tt.route.annotation.RouteMeta

interface IRouteGroup {
    fun loadInfo(data: MutableMap<String, RouteMeta>)
}