package com.weedow.searchy.query.querytype

import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.Visitor
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.SimpleExpression
import com.weedow.searchy.utils.MAP_KEY
import com.weedow.searchy.utils.MAP_VALUE
import java.lang.reflect.AnnotatedElement

/**
 * [QEntityJoin] implementation.
 *
 * @param qEntity [QEntity]
 * @param propertyInfos [PropertyInfos]
 */
class QEntityJoinImpl<T>(
    private val qEntity: QEntity<out T>,
    private val propertyInfos: PropertyInfos
) : QEntityJoin<T> {

    override fun get(fieldName: String): QPath<*> {
        return if (propertyInfos.elementType == ElementType.MAP) {
            val alias = qEntity.metadata.element.toString()
            when (fieldName) {
                MAP_KEY -> createQPath(
                    ElementType.MAP_KEY,
                    propertyInfos.parameterizedTypes[0],
                    PathMetadataFactory.forVariable("key($alias)"),
                    propertyInfos
                )
                MAP_VALUE -> createQPath(
                    ElementType.MAP_VALUE,
                    propertyInfos.parameterizedTypes[1],
                    PathMetadataFactory.forVariable(alias),
                    propertyInfos
                )
                else -> qEntity.get(fieldName)
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

    private fun createQPath(
        elementType: ElementType,
        parameterizedType: Class<*>,
        pathMetadata: PathMetadata,
        propertyInfos: PropertyInfos
    ): QPathImpl<*> {
        return QPathImpl(
            Expressions.path(parameterizedType, pathMetadata),
            getParameterizedPropertyInfos(parameterizedType, elementType, propertyInfos)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun getParameterizedPropertyInfos(parameterizedType: Class<*>, elementType: ElementType, propertyInfos: PropertyInfos): PropertyInfos {
        return propertyInfos.copy(
            parentClass = propertyInfos.parentClass,
            fieldName = propertyInfos.fieldName,
            elementType = elementType,
            type = parameterizedType,
            parameterizedTypes = emptyList(),
            annotations = emptyList(),
            queryType = elementType.pathClass as Class<out SimpleExpression<*>>
        )
    }
}