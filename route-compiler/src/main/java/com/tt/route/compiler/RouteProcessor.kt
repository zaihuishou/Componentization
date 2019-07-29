/**
 *
 *@author tanzhiqiang
 */

package com.tt.route.compiler

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.tt.route.annotation.Route
import com.tt.route.annotation.RouteMeta
import com.tt.route.annotation.RouteMeta.Companion.ROUTE_TYPE_ACTIVITY
import com.tt.route.compiler.utils.RouteConstants
import com.tt.route.compiler.utils.isEmpty
import com.tt.route.compiler.utils.isMEmpty
import java.io.File
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.collections.ArrayList

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(RouteConstants.K_APT_KOTLIN_GENERATED_OPTION_NAME)
class RouteProcessor : AbstractProcessor() {

    /**
     * key:组名 value:类名
     */
    private val rootMap = TreeMap<String, ClassName>()

    private val mGroupMap = mutableMapOf<String, ArrayList<RouteMeta>>()
    private var mModuleName: String? = null
    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        p0?.apply {
            if (options != null && options.isEmpty().not()) {
                mModuleName = options[RouteConstants.K_APT_KOTLIN_GENERATED_OPTION_NAME]
            }

            if (mModuleName.isMEmpty()) {
                throw RuntimeException("Not set module Name option!")
            }
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Route::class.java.canonicalName)
    }

    private val generatedSourcesRoot by lazy { processingEnv.options[RouteConstants.K_APT_OPTION].orEmpty() }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        if (annotations.isNotEmpty()) {
            val rootElements = roundEnv.getElementsAnnotatedWith(Route::class.java)
            if (rootElements != null && rootElements.isNotEmpty()) {
                process(rootElements)
            }
            return true
        }

        return false
    }

    private fun process(rootElements: Set<Element>) {
        val activityElement = processingEnv.elementUtils.getTypeElement(RouteConstants.ACTIVITY)

        rootElements.forEach { element ->
            val asType = element.asType()
            val route = element.getAnnotation(Route::class.java)
            val mRouteData: RouteMeta
            if (processingEnv.typeUtils.isSubtype(asType, activityElement.asType())) {
                mRouteData = RouteMeta(ROUTE_TYPE_ACTIVITY, route, element)
            } else throw RuntimeException("Route annotation just support Activity")
            collectRouteMetaData(mRouteData)
        }
        val groupClassName =
            ClassName(RouteConstants.IROUTEGROUP_PACKAGE_NAME, RouteConstants.IROUTEGROUP)

        val rootClassName =
            ClassName(RouteConstants.IROUTEGROUP_PACKAGE_NAME, RouteConstants.IROUTE_ROOT)
        generateGroupRoute(groupClassName)
        generateRootRoute(rootClassName, groupClassName)

    }

    private fun generateRootRoute(rootClassName: ClassName, groupClassName: ClassName) {
//构建 MutableMap<String,Class<out IRouteGroup>>
        val mapParameter = ClassName(
            "kotlin.collections",
            "MutableMap"
        ).parameterizedBy(
            String::class.asClassName(),
            ClassName("java.lang", "Class").parameterizedBy(
                WildcardTypeName.producerOf(groupClassName)
            )
        )

       val loadInfoFun =  FunSpec.builder(RouteConstants.METHOD_LOAD_INFO)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter( RouteConstants.METHOD_PARAMETER_DATA,mapParameter)

        rootMap.forEach { (group, name) ->
            loadInfoFun.addStatement("data[%S] = %T::class.java", group,name)
        }

        val mRootClassName = "${RouteConstants.ROUTE}_${RouteConstants.ROOT}_$mModuleName"

        val classType = TypeSpec.classBuilder(mRootClassName).addFunction(loadInfoFun.build())
            .addSuperinterface(rootClassName).build()
        FileSpec.builder(RouteConstants.FILE_OUTPUT_PACKAGE_NAME,mRootClassName).addType(classType).build()
            .writeTo(File(generatedSourcesRoot))
    }

    private fun generateGroupRoute(iGroupClassName: ClassName) {
        val mapParameter = ClassName(
            "kotlin.collections",
            "MutableMap"
        ).parameterizedBy(String::class.asClassName(), RouteMeta::class.asClassName())
        val loadDataFun =
            FunSpec.builder(RouteConstants.METHOD_LOAD_INFO).addModifiers(KModifier.OVERRIDE)
                .addParameter(
                    ParameterSpec.builder(
                        RouteConstants.METHOD_PARAMETER_DATA,
                        mapParameter
                    ).build()
                )
        mGroupMap.forEach { (group, routeData) ->
            routeData.forEach { r ->
                val asClassName = (r.element as TypeElement).asClassName()
                val routeClassType = ClassName(asClassName.packageName, asClassName.simpleName)
                loadDataFun.addStatement(
                    "data[%S]=%T.build(%L,%T::class.java,%S,%S)",
                    "${r.path}",
                    RouteMeta::class,
                    r.type,
                    routeClassType,
                    "${r.path}",
                    "${r.group}"
                )
            }
            val className = "${RouteConstants.ROUTE}_${RouteConstants.GROUP}_$group"
            val classType = TypeSpec.classBuilder(className).addFunction(loadDataFun.build())
                .addSuperinterface(
                    iGroupClassName
                ).build()
            FileSpec.builder(RouteConstants.FILE_OUTPUT_PACKAGE_NAME, className).addType(classType)
                .build().writeTo(File(generatedSourcesRoot))

            rootMap[group] = ClassName(RouteConstants.FILE_OUTPUT_PACKAGE_NAME,className)

        }

    }

    private fun collectRouteMetaData(mRouteData: RouteMeta) {
        if (routeVerify(mRouteData)) {
            val group = mGroupMap[mRouteData.group]
            if (group.isEmpty()) {
                val routeMetaList = arrayListOf(mRouteData)
                mGroupMap[mRouteData.group!!] = routeMetaList
            } else {
                group!!.add(mRouteData)
            }
        }
    }

    private fun checkCamelVariable(classElement: TypeElement) {
        classElement.enclosedElements.filter {
            !it.simpleName.toString().isDefinedCamelCase()
        }.forEach {
            printWarning("Detected non-camelcase name: ${it.simpleName}.")
        }
    }

    /**
     * 验证path路由地址的合法性
     * @param routeMeta
     * @return
     */
    private fun routeVerify(routeMeta: RouteMeta): Boolean {
        val path = routeMeta.path
        val group = routeMeta.group
        // 必须以 / 开头来指定路由地址
        if (path.isMEmpty().not() && !path!!.startsWith("/")) {
            return false
        }
        //如果group没有设置 我们从path中获得group
        if (group.isMEmpty()) {
            val defaultGroup = path!!.substring(1, path.indexOf("/", 1))
            //截取出的group还是空
            if (defaultGroup.isEmpty()) {
                return false
            }
            routeMeta.group = defaultGroup
        }
        return true
    }

    private fun String.isDefinedCamelCase(): Boolean {
        val toCharArray = toCharArray()
        return toCharArray
            .mapIndexed { index, current -> current to toCharArray.getOrNull(index + 1) }
            .none { it.first.isUpperCase() && it.second?.isUpperCase() ?: false }
    }

    private fun printError(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message)
    }

    private fun printWarning(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, message)
    }

}