package com.weedow.searchy.mongodb.query.querytype

import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.Visitor
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.core.types.dsl.SimpleExpression
import com.weedow.searchy.query.querytype.*
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
            when (fieldName) {
                MAP_KEY -> {
                    QPathImpl(
                        createMapPathWrapper(
                            propertyInfos.parameterizedTypes[0],
                            propertyInfos.parameterizedTypes[1],
                            qEntity.metadata,
                            ElementType.MAP_KEY
                        ),
                        getParameterizedPropertyInfos(propertyInfos.parameterizedTypes[0], ElementType.MAP_KEY, propertyInfos)
                    )
                }
                MAP_VALUE -> {
                    QPathImpl(
                        createMapPathWrapper(
                            propertyInfos.parameterizedTypes[0],
                            propertyInfos.parameterizedTypes[1],
                            PathMetadataFactory.forProperty(qEntity, fieldName),
                            ElementType.MAP_VALUE
                        ),
                        getParameterizedPropertyInfos(propertyInfos.parameterizedTypes[1], ElementType.MAP_VALUE, propertyInfos)
                    )
                }
                else -> qEntity.get(fieldName)
            }
        } else if (propertyInfos.elementType == ElementType.MAP_VALUE) {
            val qPath = qEntity.get(fieldName)
            QPathImpl(
                PathWrapperImpl(qPath.path, ElementType.MAP_VALUE),
                getParameterizedPropertyInfos(qPath.propertyInfos.type, ElementType.MAP_VALUE, qPath.propertyInfos)
            )
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

    private fun <K, V, E : SimpleExpression<V>> createMapPathWrapper(
        keyType: Class<K>,
        valueType: Class<V>,
        metadata: PathMetadata,
        elementType: ElementType
    ) = MapPathWrapperImpl<K, V, E>(Expressions.mapPath(keyType, valueType, PathBuilder::class.java as Class<E>, metadata), elementType)

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