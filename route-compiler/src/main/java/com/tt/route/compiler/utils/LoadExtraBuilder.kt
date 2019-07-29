package com.tt.route.compiler.utils

import com.squareup.kotlinpoet.*
import com.tt.route.annotation.Extra

import javax.lang.model.element.Element
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Author: 夏胜明
 * Date: 2018/8/20 0020
 * Email: xiasem@163.com
 * Description:
 */
class LoadExtraBuilder(parameterSpec: ParameterSpec) {
    private val builder: FunSpec.Builder = FunSpec.builder(RouteConstants.METHOD_LOAD_EXTRA)
        .addAnnotation(Override::class.java)
        .addParameter("target", ClassName("kotlin", "Any"))
    private var elementUtils: Elements? = null
    private var typeUtils: Types? = null

    private var parcelableType: TypeMirror? = null
    private var iServiceType: TypeMirror? = null

    fun setElementUtils(elementUtils: Elements) {
        this.elementUtils = elementUtils
        parcelableType = elementUtils.getTypeElement(RouteConstants.PARCELABLE).asType()
        iServiceType = elementUtils.getTypeElement(RouteConstants.ISERVICE).asType()
    }

    fun setTypeUtils(typeUtils: Types) {
        this.typeUtils = typeUtils
    }

    fun buildStatement(element: Element) {
        val typeMirror = element.asType()
        val type = typeMirror.kind.ordinal
        //属性名 String text 获得text
        val fieldName = element.simpleName.toString()
        //获得注解 name值
        var extraName = element.getAnnotation(Extra::class.java).name
        extraName = if (extraName.isMEmpty()) fieldName else extraName
        val defaultValue = "t.$fieldName"
        var statement = "$defaultValue = t.getIntent()."
        when (type) {
            TypeKind.BOOLEAN.ordinal -> statement += "getBooleanExtra(%S, $defaultValue)"
            TypeKind.BYTE.ordinal -> statement += "getByteExtra(%S, $defaultValue)"
            TypeKind.SHORT.ordinal -> statement += "getShortExtra(%S, $defaultValue)"
            TypeKind.INT.ordinal -> statement += "getIntExtra(%S, $defaultValue)"
            TypeKind.LONG.ordinal -> statement += "getLongExtra(%S, $defaultValue)"
            TypeKind.CHAR.ordinal -> statement += "getCharExtra(%S, $defaultValue)"
            TypeKind.FLOAT.ordinal -> statement += "getFloatExtra(%S, $defaultValue)"
            TypeKind.DOUBLE.ordinal -> statement += "getDoubleExtra(%S, $defaultValue)"
            else -> {
                //数组类型
                if (type == TypeKind.ARRAY.ordinal) {
                    addArrayStatement(statement, fieldName, extraName, typeMirror, element)
                } else {
                    //Object
                    addObjectStatement(statement, fieldName, extraName, typeMirror, element)
                }
                return
            }
        }
        builder.addStatement(statement, extraName)
    }

    /**
     * 添加对象 String/List/Parcelable
     *
     * @param statement
     * @param extraName
     * @param typeMirror
     * @param element
     */
    private fun addObjectStatement(
        oldStatement: String, fieldName: String, extraName: String,
        typeMirror: TypeMirror,
        element: Element
    ) {
        var statement = oldStatement
        //Parcelable
        if (typeUtils!!.isSubtype(typeMirror, parcelableType)) {
            statement += "getParcelableExtra(%S)"
        } else if (typeMirror.toString() == RouteConstants.STRING) {
            statement += "getStringExtra(%S)"
        } else if (typeUtils!!.isSubtype(typeMirror, iServiceType)) {
            //如果@Extra 注解的属性是一个IService子类，注入一个
//            statement = "t.$fieldName = (%T) %T.getInstance().build(%S).navigation()"
//            builder.addStatement(
//                statement,
//                element.asType().asTypeName(),
//                ClassName(),
//                extraName
//            )
            return
        } else {
            //List
            val typeName = typeMirror.asTypeName()
            //泛型
            if (typeName is ParameterizedTypeName) {
                //list 或 arraylist
                val rawType = typeName.rawType
                //泛型类型
                val typeArguments = typeName.typeArguments
                if (rawType.toString() != "kotlin.collections.ArrayList" && rawType.toString() != "kotlin.collections.List"
                ) {
                    throw RuntimeException(
                        "Not Support Inject Type:" + typeMirror + " " +
                                element
                    )
                }
                if (typeArguments.isEmpty() || typeArguments.size != 1) {
                    throw RuntimeException("List Must Specify Generic Type:$typeArguments")
                }
                val typeArgumentName = typeArguments.get(0)
                val typeElement = elementUtils!!.getTypeElement(
                    typeArgumentName
                        .toString()
                )
                // Parcelable 类型
                if (typeUtils!!.isSubtype(typeElement.asType(), parcelableType)) {
                    statement += "getParcelableArrayListExtra(%S)"
                } else if (typeElement.asType().toString() == RouteConstants.STRING) {
                    statement += "getStringArrayListExtra(%S)"
                } else if (typeElement.asType().toString() == RouteConstants.INTEGER) {
                    statement += "getIntegerArrayListExtra(%S)"
                } else {
                    throw RuntimeException(
                        "Not Support Generic Type : " + typeMirror + " " +
                                element
                    )
                }
            } else {
                throw RuntimeException(
                    "Not Support Extra Type : " + typeMirror + " " +
                            element
                )
            }
        }
        builder.addStatement(statement, extraName)
    }

    /**
     * 添加数组
     *
     * @param statement
     * @param fieldName
     * @param typeMirror
     * @param element
     */
    private fun addArrayStatement(
        oldStatement: String,
        fieldName: String,
        extraName: String,
        typeMirror: TypeMirror,
        element: Element
    ) {
        var statement = oldStatement
        //数组
        when (typeMirror.toString()) {
            "BooleanArray" -> statement += "getBooleanArrayExtra(%S)"
            "IntArray" -> statement += "getIntArrayExtra(%S)"
            "ShortArray" -> statement += "getShortArrayExtra(%S)"
            "FloatArray" -> statement += "getFloatArrayExtra(%S)"
            "DoubleArray" -> statement += "getDoubleArrayExtra(%S)"
            "ByteArray" -> statement += "getByteArrayExtra(%S)"
            "CharArray" -> statement += "getCharArrayExtra(%S)"
            "LongArray" -> statement += "getLongArrayExtra(%S)"
            "StringArray" -> statement += "getStringArrayExtra(%S)"
//            else -> {
//                //Parcelable 数组
//                val defaultValue = "t.$fieldName"
//                //object数组 componentType获得object类型
//
////                val arrayTypeName = typeMirror.asTypeName()
////
////                elementUtils!!.
////                val typeElement = elementUtils!!.getTypeElement(
////                    arrayTypeName.
////                )
//                //是否为 Parcelable 类型
//                if (!typeUtils!!.isSubtype(element.asType(), parcelableType)) {
//                    throw RuntimeException(
//                        "Not Support Extra Type:" + typeMirror + " " +
//                                element
//                    )
//                }
//                statement = "%T[] " + fieldName + " = t.getIntent()" +
//                        ".getParcelableArrayExtra" + "(%S)"
//                builder.addStatement(statement, parcelableType, extraName)
//                builder.beginControlFlow("if( null != %L)", fieldName)
//                statement = "$defaultValue = new %T[$fieldName.length]"
//                builder.addStatement(statement, arrayTypeName.componentType)
//                    .beginControlFlow(
//                        "for (int i = 0; i < " + fieldName + "" +
//                                ".length; " +
//                                "i++)"
//                    )
//                    .addStatement(
//                        "$defaultValue[i] = (%T)$fieldName[i]",
//                        arrayTypeName.componentType
//                    )
//                    .endControlFlow()
//                builder.endControlFlow()
//                return
//            }
        }
        builder.addStatement(statement, extraName)
    }

    /**
     * 加入 $T t = ($T)target
     *
     * @param className
     */
    fun injectTarget(className: ClassName) {
        builder.addStatement(INJECT_TARGET, className, className)

    }

    fun build(): FunSpec {
        return builder.build()
    }

    companion object {
        private val INJECT_TARGET = "%T t = (%T)target"
    }
}
