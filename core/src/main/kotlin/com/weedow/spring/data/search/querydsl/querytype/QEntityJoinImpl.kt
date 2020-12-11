package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.Visitor
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.SimpleExpression
import com.weedow.spring.data.search.utils.MAP_KEY
import com.weedow.spring.data.search.utils.MAP_VALUE
import java.lang.reflect.AnnotatedElement

class QEntityJoinImpl<T>(
    private val qEntity: QEntity<out T>,
    private val qPath: QPath<*>
) : QEntityJoin<T> {

    override fun get(fieldName: String): QPath<*> {
        val propertyInfos = qPath.propertyInfos
        return if (propertyInfos.elementType == ElementType.MAP) {
            when (fieldName) {
                MAP_KEY -> createQPath(0, ElementType.MAP_KEY, propertyInfos)
                MAP_VALUE -> createQPath(1, ElementType.MAP_VALUE, propertyInfos)
                else -> throw IllegalArgumentException("The attribute name '$fieldName' is not authorized for a parent Map")
            }
        } else {
            qEntity.get(fieldName)
        }
    }

    override fun <R : Any?, C : Any?> accept(v: Visitor<R, C>?, context: C?): R? = qEntity.accept(v, context)

    override fun getType(): Class<out T> = qEntity.type

    override fun getMetadata(property: Path<*>?): Any = qEntity.getMetadata(property)

    override fun getMetadata(): PathMetadata = qEntity.metadata

    override fun getRoot(): Path<*> = qEntity.root

    override fun getAnnotatedElement(): AnnotatedElement = qEntity.annotatedElement

    private fun createQPath(idx: Int, elementType: ElementType, propertyInfos: PropertyInfos) = QPathImpl(
        Expressions.path(propertyInfos.parametrizedTypes[idx], PathMetadataFactory.forVariable(propertyInfos.fieldName)),
        getParameterizedPropertyInfos(idx, elementType, propertyInfos)
    )

    @Suppress("UNCHECKED_CAST")
    private fun getParameterizedPropertyInfos(idx: Int, elementType: ElementType, propertyInfos: PropertyInfos): PropertyInfos {
        val parameterizedType = propertyInfos.parametrizedTypes[idx]
        return propertyInfos.copy(
            parentClass = propertyInfos.parentClass,
            fieldName = propertyInfos.fieldName,
            elementType = elementType,
            type = parameterizedType,
            parametrizedTypes = emptyList(),
            annotations = emptyList(),
            queryType = elementType.pathClass as Class<out SimpleExpression<*>>
        )
    }
}