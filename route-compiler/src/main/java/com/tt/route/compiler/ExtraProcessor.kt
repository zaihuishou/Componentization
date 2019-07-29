/**
 *
 *@author tanzhiqiang
 */

package com.tt.route.compiler

import com.google.auto.service.AutoService
import com.tt.route.annotation.Extra
import com.tt.route.compiler.utils.RouteConstants
import java.util.HashMap
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(RouteConstants.K_APT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes(RouteConstants.ANN_TYPE_EXTRA)
class ExtraProcessor : AbstractProcessor() {


    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private lateinit var elementUtils: Elements

    /**
     * type(类信息)工具类
     */
    private lateinit var typeUtils: Types
    /**
     * 类/资源生成器
     */
    private var filerUtils: Filer? = null

    /**
     * 记录所有需要注入的属性 key:类节点 value:需要注入的属性节点集合
     */
    private val parentAndChild = HashMap<TypeElement, ArrayList<Element>>()

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnvironment.typeUtils
        filerUtils = processingEnv.filer
    }

    override fun process(elements: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        if (elements?.isNotEmpty()!!) {
            val extras = p1?.getElementsAnnotatedWith(Extra::class.java)
            if (extras?.isNotEmpty()!!) {
                categories(extras)
                generateAutoWired()
                return true
            }
        }
        return false
    }

    private fun generateAutoWired() {
//        val typeActivity = elementUtils.getTypeElement(RouteConstants.ACTIVITY).asType()
//        var typeExtra = elementUtils.getTypeElement(RouteConstants.IEXTRA)
//
//        if (parentAndChild.isNotEmpty()) {
//
//            parentAndChild.forEach { (key, value) ->
//                if (!typeUtils.isSubtype(key.asType(), typeActivity))
//                    throw RuntimeException("Extra no activity filed:$key")
//
//
//            }
//        }

    }

    private fun categories(extras: Set<Element>) {
        extras.forEach { element ->
            val typeElement = element.enclosingElement as TypeElement
            if (parentAndChild.containsKey(typeElement)) {
                parentAndChild[typeElement]?.add(element)
            } else {
                val arrayListOf = arrayListOf(element)
                parentAndChild[typeElement] = arrayListOf
            }
        }
    }

}