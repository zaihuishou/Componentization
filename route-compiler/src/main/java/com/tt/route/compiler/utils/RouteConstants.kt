package com.tt.route.compiler.utils

class RouteConstants private constructor() {
    companion object {

        const val ACTIVITY = "android.app.Activity"
        const val K_APT_KOTLIN_GENERATED_OPTION_NAME = "moduleName"
        const val K_APT_OPTION = "kapt.kotlin.generated"
        const val METHOD_LOAD_INFO = "loadInfo"
        const val METHOD_PARAMETER_DATA = "data"
        const val FILE_OUTPUT_PACKAGE_NAME = "org.tt.route"
        const val IROUTEGROUP_PACKAGE_NAME = "com.tt.route.core"
        const val IROUTEGROUP = "IRouteGroup"
        const val IROUTE_ROOT = "IRouteRoot"
        const val ROUTE = "Route"
        const val GROUP = "Group"
        const val ROOT = "Root"

        const val ANN_TYPE_EXTRA = "com.tt.route.annotation.Extra"

        const val IEXTRA = "com.tt.route.core.IExtra"
        const val METHOD_LOAD_EXTRA = "loadExtra"

        const val PARCELABLE = "android.os.Parcelable"
        const val ISERVICE = "com.tt.route.core.IService"

        private const val LANG = "kotlin"
        const val STRING = "$LANG.String"
        const val INTEGER = "$LANG.Int"

        const val ROUTER_PACKAGE = "com.tt.route"
        const val ROUTER_NAME = "TRouter"
    }
}