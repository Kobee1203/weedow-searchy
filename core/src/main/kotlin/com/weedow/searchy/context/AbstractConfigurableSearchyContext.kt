package com.weedow.searchy.context

import com.querydsl.core.types.dsl.SimpleExpression
import com.weedow.searchy.query.querytype.ElementType
import com.weedow.searchy.query.querytype.PropertyInfos
import com.weedow.searchy.query.querytype.QEntity
import com.weedow.searchy.query.querytype.QEntityImpl
import com.weedow.searchy.utils.EntityUtils
import org.apache.commons.lang3.reflect.FieldUtils
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Convenient superclass for [SearchyContext] implementations.
 *
 * It contains the main processing logic. The subclasses just provide the specific configurations.
 */
abstract class AbstractConfigurableSearchyContext : ConfigurableSearchyContext {

    companion object {
        private const val JOIN_NAME_SEPARATOR = "."
    }

    private val qEntities = mutableMapOf<Class<*>, QEntity<*>>()

    @Suppress("UNCHECKED_CAST")
    override fun <E> add(entityClass: Class<E>): QEntity<E> {
        return qEntities.getOrPut(entityClass) {
            QEntityImpl(this, entityClass, entityClass.simpleName.decapitalize())
        } as QEntity<E>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E> get(entityClass: Class<E>, default: (entityClazz: Class<E>) -> QEntity<E>): QEntity<E> {
        return qEntities.getOrElse(entityClass) { default(entityClass) } as QEntity<E>
    }

    override fun getAllPropertyInfos(entityClass: Class<*>): List<PropertyInfos> {
        return FieldUtils.getAllFieldsList(entityClass)
            .filter { field -> !Modifier.isStatic(field.modifiers) }
            .map { field -> getPropertyInfos(entityClass, field) }
    }

    override fun isEntity(clazz: Class<*>): Boolean {
        return entityAnnotations.any { clazz.getAnnotation(it) != null }
    }

    override fun isJoinAnnotation(annotationClass: Class<out Annotation>): Boolean {
        return joinAnnotations.contains(annotationClass)
    }

    private fun getPropertyInfos(parentClass: Class<*>, field: Field): PropertyInfos {
        val fieldName = field.name
        val fieldType = field.type.kotlin.javaObjectType
        val elementType = ElementType.get(fieldType, this)
        val parameterizedTypes = getParameterizedTypes(elementType, field)
        val annotations = field.annotations.toList()
        val queryType = getQueryType(elementType, parameterizedTypes)
        val qName = getQName(parentClass, fieldType, parameterizedTypes, elementType, fieldName)
        return PropertyInfos(qName, parentClass, fieldName, elementType, fieldType, parameterizedTypes, annotations, queryType)
    }

    private fun getParameterizedTypes(elementType: ElementType, field: Field) =
        when (elementType) {
            ElementType.LIST,
            ElementType.SET,
            ElementType.COLLECTION,
            ElementType.MAP,
            ElementType.ARRAY
            -> {
                EntityUtils.getParameterizedTypes(field)
            }
            else -> emptyList()
        }

    @Suppress("UNCHECKED_CAST")
    private fun getQueryType(elementType: ElementType, parameterizedTypes: List<Class<*>>): Class<out SimpleExpression<*>> =
        when (elementType) {
            ElementType.LIST,
            ElementType.SET,
            ElementType.COLLECTION,
            ElementType.ARRAY
            -> {
                ElementType.get(parameterizedTypes[0], this).pathClass
            }
            ElementType.MAP -> {
                ElementType.get(parameterizedTypes[1], this).pathClass
            }
            else -> elementType.pathClass
        } as Class<out SimpleExpression<*>>

    private fun getQName(
        entityClass: Class<*>,
        fieldType: Class<*>,
        parameterizedTypes: List<Class<*>>,
        elementType: ElementType,
        fieldName: String
    ): String {
        val fieldClass = when (elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            ElementType.ARRAY
            -> {
                parameterizedTypes[0]
            }
            else -> fieldType
        }

        return if (isEntity(fieldClass)) fieldClass.canonicalName else entityClass.canonicalName + JOIN_NAME_SEPARATOR + fieldName
    }

}